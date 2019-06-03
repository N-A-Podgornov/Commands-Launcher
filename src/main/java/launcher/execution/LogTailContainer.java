package launcher.execution;

import java.util.ArrayDeque;
import java.util.Queue;

class LogTailContainer {

    private final int capacity;
    private final Queue<String> lines;


    public LogTailContainer(int capacity) {
        this.capacity = capacity;
        this.lines = new ArrayDeque<>(capacity);
    }


    public synchronized void addLine(String line) {
        if (lines.size() >= capacity) {
            lines.poll();
        }
        lines.offer(line);
    }

    public synchronized String getTail(int length) {
        if (length > capacity) {
            length = capacity;
        }

        StringBuilder builder = new StringBuilder();
        int writePoint = lines.size() - length;
        int i = 0;
        for (String line : lines) {
            if (i < writePoint) {
                i++;
                continue;
            }
            builder.append(' ').append(line).append(';');
        }
        return builder.toString();
    }

}