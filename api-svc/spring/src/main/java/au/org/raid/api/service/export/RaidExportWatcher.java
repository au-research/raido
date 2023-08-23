package au.org.raid.api.service.export;

import au.org.raid.api.util.Log;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static au.org.raid.api.util.Log.to;

class RaidExportWatcher<T> {
    private static final Log log = to(RaidExportWatcher.class);
    private static final ScheduledExecutorService executorService =
            Executors.newScheduledThreadPool(1, new DaemonThreadFactory());
    private Consumer<T> writer;
    private int exportedRecords = 0;

    public RaidExportWatcher(Consumer<T> writer) {
        this.writer = writer;
    }

    public void watch(T value) {
        writer.accept(value);
        exportedRecords++;
    }

    public void startProgressLogging(int logPeriodSeconds) {
        executorService.scheduleAtFixedRate(this::logProgress,
                logPeriodSeconds, logPeriodSeconds, TimeUnit.SECONDS);
    }

    public void stopProgressLogging() {
        executorService.shutdown();
    }

    public void logProgress() {
        log.with("exportedRecords", exportedRecords).
                info("open-data-export exporting");
    }

    public void logSummary() {
        log.with("exportedRecords", exportedRecords).
                info("open-data-export finished");
    }

    /**
     * So that the thread doesn't keep a main() method process alive if the caller
     * forgot to call stopProgressLogging().
     */
    static class DaemonThreadFactory implements ThreadFactory {
        @Override
        public Thread newThread(Runnable runnable) {
            Thread thread = Executors.defaultThreadFactory().newThread(runnable);
            thread.setDaemon(true);
            return thread;
        }
    }

}
