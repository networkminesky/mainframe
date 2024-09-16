package net.minesky.api.messaging;

import net.minesky.core.messaging.MessagingObserver;

public class Messaging {

    private static MessagingObserver messagingObserver;

    public static MessagingObserver getMessagingObserver() {
        return messagingObserver;
    }

    public static void setMessagingObserver(MessagingObserver msg) {
        messagingObserver = msg;
    }

}
