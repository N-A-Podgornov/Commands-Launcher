package launcher.executions;

import launcher.execution.CommandExecution;
import launcher.execution.Execution;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CommandsExecutions implements Executions {

    private Map<UUID, CommandExecution> executionsMap = new ConcurrentHashMap<>();


    @Override
    public UUID start(String command) throws IOException {
        UUID id = UUID.randomUUID();
        executionsMap.put(id, new CommandExecution(command));
        return id;
    }

    @Override
    public void stop(UUID id) {
        executionsMap.get(id).terminate();
    }


    @Override
    public StatusContainer getStatus(UUID id) {
        return new StatusContainer(id, executionsMap.get(id));
    }

    @Override
    public String getOutput(UUID id, int length) {
        return executionsMap.get(id).getOutLogTail(length);
    }

    @Override
    public String getErrors(UUID id, int length) {
        return executionsMap.get(id).getErrLogTail(length);
    }


    @Override
    public Map<Execution.Stage, List<StatusContainer>> getStatuses() {

        Map<Execution.Stage, List<StatusContainer>> result = new EnumMap<>(Execution.Stage.class);
        Arrays.asList(Execution.Stage.values()).forEach(stage -> result.put(stage, new ArrayList<>()));

        executionsMap.entrySet().stream()
                .map(execution -> new StatusContainer(execution.getKey(), execution.getValue()))
                .sorted((o1, o2) -> {
                    LocalDateTime t1 = o1.getStartTime();
                    LocalDateTime t2 = o2.getStartTime();
                    if (t1.isBefore(t2)) {
                        return -1;
                    } else if (t1.isAfter(t2)) {
                        return 1;
                    } else {
                        return 0;
                    }
                })
                .forEach(statusContainer -> result.get(statusContainer.getStage()).add(statusContainer));

        return result;
    }


    @Override
    public boolean deleteFinished(UUID id) {
        CommandExecution execution = executionsMap.get(id);
        if (execution.getStage() != Execution.Stage.RUNNING) {
            executionsMap.remove(id);
            return true;
        }
        return false;
    }

}