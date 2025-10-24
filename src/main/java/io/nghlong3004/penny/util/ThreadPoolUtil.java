package io.nghlong3004.penny.util;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public final class ThreadPoolUtil {
    public static final ExecutorService BACKGROUND_EXECUTOR = Executors.newFixedThreadPool(8);

    public static void shutdown() {
        log.info("Shutting down background executor pool...");
        BACKGROUND_EXECUTOR.shutdown();
        try {
            if (!BACKGROUND_EXECUTOR.awaitTermination(5, TimeUnit.SECONDS)) {
                log.warn("Background pool did not terminate in 5 seconds!");
                BACKGROUND_EXECUTOR.shutdownNow();
            }
        } catch (InterruptedException e) {
            log.error("Background pool shutdown was interrupted");
            BACKGROUND_EXECUTOR.shutdownNow();
        }
        log.info("Background pool shutdown complete.");
    }
}
