package launcher.execution;

import java.time.LocalDateTime;

public interface Execution {

    enum Stage {RUNNING, ERROR, INTERRUPTED, FINISHED}


    void terminate();

    String getCommand();
    Stage getStage();
    int getExitCode();

    String getOutLogTail(int length);
    String getErrLogTail(int length);

    LocalDateTime getStartTime();
    LocalDateTime getFinishTime();
}