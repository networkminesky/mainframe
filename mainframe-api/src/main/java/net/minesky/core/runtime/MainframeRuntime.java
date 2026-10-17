package net.minesky.core.runtime;

import net.minesky.core.CoreMain;

import java.io.File;
import java.util.Objects;
import java.util.logging.Logger;

public final class MainframeRuntime {

    private static volatile AsyncExecutor asyncExecutor = Runnable::run;
    private static volatile SpeedTestBridge speedTestBridge;

    private MainframeRuntime() {
    }

    public static void bind(AsyncExecutor executor) {
        asyncExecutor = Objects.requireNonNull(executor, "executor");
    }

    public static void bindSpeedTest(SpeedTestBridge bridge) {
        speedTestBridge = bridge;
    }

    public static AsyncExecutor async() {
        return asyncExecutor;
    }

    public static SpeedTestBridge speedTest() {
        return speedTestBridge;
    }

    public static void bootstrap(
            CoreMain.PlatformType platform,
            File dataDirectory,
            Logger logger,
            AsyncExecutor executor,
            Class<?> resourceAnchor
    ) {
        bind(executor);
        CoreMain.simpleInitialization(platform, dataDirectory, logger, resourceAnchor);
    }
}
