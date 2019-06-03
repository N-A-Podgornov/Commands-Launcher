package launcher.execution;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CommandExecution implements Execution {

    private final Logger log = LoggerFactory.getLogger(CommandExecution.class);

    private final String command;
    private final Process process;
    private final LocalDateTime startTime;
    private volatile LocalDateTime finishTime;
    private volatile boolean wasKilled;

    private static final int CAPACITY = 10000;
    private volatile LogTailContainer inputContainer = new LogTailContainer(CAPACITY);
    private volatile LogTailContainer errorContainer = new LogTailContainer(CAPACITY);


    public CommandExecution(String command) throws IOException {
        this.command = command;

        ProcessBuilder builder = new ProcessBuilder(prepareCommand(command));

        process = builder.start();
        this.wasKilled = false;
        this.startTime = LocalDateTime.now();

        new InputActualizes().start();
        new ErrorActualizes().start();
    }

    private class InputActualizes extends Thread {
        @Override
        public void run() {
            this.setName("InputActualizes");

            actualizeContainer(inputContainer, process.getInputStream());
            finishTime = LocalDateTime.now();
        }
    }

    private class ErrorActualizes extends Thread {
        @Override
        public void run() {
            this.setName("ErrorActualizes");

            actualizeContainer(errorContainer, process.getErrorStream());
            finishTime = LocalDateTime.now();
        }
    }


    @Override
    public void terminate() {
        process.destroy();
        this.wasKilled = true;
        if (finishTime == null) {
            finishTime = LocalDateTime.now();
        }
    }


    @Override
    public String getCommand() {
        return command;
    }

    @Override
    public Stage getStage() {
        if (process.isAlive()) {
            return Stage.RUNNING;
        }
        if (wasKilled) {
            return Stage.INTERRUPTED;
        }
        return process.exitValue() == 0 ? Stage.FINISHED : Stage.ERROR;
    }

    @Override
    public int getExitCode() {
        if (process.isAlive()) return -1;
        return process.exitValue();
    }


    @Override
    public String getOutLogTail(int length) {
        return inputContainer.getTail(length);
    }

    @Override
    public String getErrLogTail(int length) {
        return errorContainer.getTail(length);
    }


    @Override
    public LocalDateTime getStartTime() {
        return startTime;
    }

    @Override
    public LocalDateTime getFinishTime() {
        return finishTime;
    }


    private void actualizeContainer(LogTailContainer container, InputStream inStream) {
        if (process.isAlive()) {
            try (BufferedReader buffRead = new BufferedReader(new InputStreamReader(inStream))) {
                String line;
                while ((line = buffRead.readLine()) != null) {
                    container.addLine(line);
                }
            } catch (IOException e) {
                log.warn("Error stream read", e);
            }
        }
    }

    private static List<String> prepareCommand(String command) {
        return Arrays.stream(command.split("[\\s\\t]+"))
                .map(String::trim)
                .collect(Collectors.toList());
    }

}