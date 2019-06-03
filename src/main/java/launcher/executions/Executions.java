package launcher.executions;

import launcher.execution.Execution;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface Executions {

    UUID start(String command) throws IOException;
    void stop(UUID id);

    StatusContainer getStatus(UUID id);
    String getOutput(UUID id, int length);
    String getErrors(UUID id, int length);

    Map<Execution.Stage, List<StatusContainer>> getStatuses();

    boolean deleteFinished(UUID id);
}