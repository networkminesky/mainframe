package com.mongodb.internal.diagnostics.logging;

import net.minesky.core.CoreMain;

public final class Loggers {

    private Loggers() {
    }

    public static Logger getLogger(final String suffix) {
        return new LoggerWrapper(CoreMain.logger);
    }

    private static class LoggerWrapper implements Logger {
        private final java.util.logging.Logger logger;

        LoggerWrapper(java.util.logging.Logger logger) {
            this.logger = logger;
        }

        @Override
        public String getName() {
            return logger.getName();
        }

        @Override
        public boolean isWarnEnabled() {
            return true;
        }

        @Override
        public void warn(String msg) {
            logger.warning(msg);
        }

        @Override
        public void warn(String msg, Throwable t) {
            logger.warning(msg);
        }

        @Override
        public boolean isErrorEnabled() {
            return true;
        }

        @Override
        public void error(String msg) {
            logger.severe(msg);
        }

        @Override
        public void error(String msg, Throwable t) {
            logger.severe(msg);
        }
    }
}
