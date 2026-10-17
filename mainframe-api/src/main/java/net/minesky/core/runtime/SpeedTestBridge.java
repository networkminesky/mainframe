package net.minesky.core.runtime;

public interface SpeedTestBridge {

    void sendPluginChannelSpeedProbe(String serverName, long timestampMillis);

    void onMongoSpeedProbe(long originTimestampMillis);

    void onSpeedResultsLine(String line);
}
