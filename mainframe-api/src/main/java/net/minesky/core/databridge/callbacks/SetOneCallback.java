package net.minesky.core.databridge.callbacks;

public interface SetOneCallback {
    void onSetDone();

    void onSetError(ErrorType errorType);
}
