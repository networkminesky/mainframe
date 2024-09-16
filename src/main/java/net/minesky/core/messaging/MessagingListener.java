package net.minesky.core.messaging;

public interface MessagingListener {

    void onDocumentUpdated(String channel, String value);

}
