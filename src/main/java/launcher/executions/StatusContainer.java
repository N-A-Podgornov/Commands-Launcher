package launcher.executions;

import launcher.execution.CommandExecution;
import launcher.execution.Execution;

import java.time.LocalDateTime;
import java.util.UUID;

public class StatusContainer {

    private final UUID id;
    private final String command;
    private final Execution.Stage stage;
    private final int exitCode;
    private final LocalDateTime startTime;
    private final LocalDateTime finishTime;


    public StatusContainer(UUID id, CommandExecution execution) {
        this.id = id;
        this.command = execution.getCommand();
        this.stage = execution.getStage();
        this.exitCode = execution.getExitCode();
        this.startTime = execution.getStartTime();
        this.finishTime = execution.getFinishTime();
    }


    public UUID getId() {
        return id;
    }

    public String getCommand() {
        return command;
    }

    public Execution.Stage getStage() {
        return stage;
    }

    public int getExitCode() {
        return exitCode;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getFinishTime() {
        return finishTime;
    }
}
