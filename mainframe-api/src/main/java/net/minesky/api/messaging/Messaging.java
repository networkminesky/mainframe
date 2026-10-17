package net.minesky.api.messaging;

import net.minesky.core.messaging.MessageType;
import net.minesky.core.messaging.MessagingListener;
import net.minesky.core.messaging.MessagingObserver;
import net.minesky.core.messaging.MessagingSender;
import net.minesky.core.messaging.NetworkMessage;
import org.jetbrains.annotations.Nullable;

/**
 * Public messaging API for MineSky plugins (Velocity, Folia/Paper, or plain JVM).
 */
public final class Messaging {

    private static MessagingObserver messagingObserver;

    private Messaging() {
    }

    public static @Nullable MessagingObserver getMessagingObserver() {
        return messagingObserver;
    }

    public static boolean hasObserver() {
        return messagingObserver != null;
    }

    public static void setMessagingObserver(MessagingObserver observer) {
        messagingObserver = observer;
    }

    public static void registerListener(String pluginId, MessagingListener listener) {
        if (messagingObserver == null) {
            throw new IllegalStateException("Messaging is not enabled on this server");
        }
        messagingObserver.addListener(pluginId, listener);
    }

    public static void unregisterListener(MessagingListener listener) {
        if (messagingObserver != null) {
            messagingObserver.removeListener(listener);
        }
    }

    public static void publish(NetworkMessage message) {
        MessagingSender.publish(message);
    }

    public static void publish(String channel, String subchannel, MessageType type, Object payload) {
        MessagingSender.publish(channel, subchannel, type, payload);
    }

    public static void publish(String subchannel, String value) {
        MessagingSender.publish(subchannel, value);
    }
}
