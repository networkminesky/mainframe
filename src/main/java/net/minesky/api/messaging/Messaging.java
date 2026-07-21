package net.minesky.api.messaging;

import net.minesky.core.messaging.MessagingObserver;
import org.jetbrains.annotations.Nullable;

public class Messaging {

    private static MessagingObserver messagingObserver;

    public static @Nullable MessagingObserver getMessagingObserver() {
        return messagingObserver;
    }

    public static boolean hasObserver() {
        return messagingObserver != null;
    }

    public static void setMessagingObserver(MessagingObserver msg) {
        messagingObserver = msg;
    }

}
