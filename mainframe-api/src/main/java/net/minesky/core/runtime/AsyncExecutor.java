package net.minesky.core.runtime;

/**
 * Runs database and IO work off the platform main thread.
 */
public interface AsyncExecutor {

    void execute(Runnable task);
}
