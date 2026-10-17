package net.minesky.core.messaging;

import net.minesky.core.CoreMain;
import net.minesky.core.runtime.MainframeRuntime;

public class DefaultListener extends MessagingListener {

    public DefaultListener() {
        super("mainframe:default");
    }

    @Override
    public void onMessage(String subchannel, String value) {
        onTyped(new NetworkMessage(
                getChannel(),
                subchannel,
                MessageType.STRING,
                value,
                null,
                System.currentTimeMillis(),
                null
        ));
    }

    private void onTyped(NetworkMessage message) {
        switch (message.subchannel()) {
            case "test-speed-results" -> {
                if (CoreMain.isOnVelocity() && MainframeRuntime.speedTest() != null) {
                    for (String line : message.asString().split("☺")) {
                        if (!line.isEmpty()) {
                            MainframeRuntime.speedTest().onSpeedResultsLine(line);
                        }
                    }
                }
                break;
            }
            case "server-discovery" -> MessagingSender.publish("discovery-result", "Me");
            case "test-speed" -> {
                if (!CoreMain.isOnVelocity() && MainframeRuntime.speedTest() != null) {
                    MainframeRuntime.speedTest().onMongoSpeedProbe(message.asLong());
                }
                break;
            }
            default -> {
            }
        }
    }
}
