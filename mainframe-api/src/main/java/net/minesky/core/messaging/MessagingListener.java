package net.minesky.core.messaging;

public abstract class MessagingListener {

    private final String channel;
    private final boolean listenToOther;

    /**
     * Prefer overriding this for typed payloads.
     * Default implementation delegates to {@link #onMessage(String, String)} for compatibility.
     */
    public void onMessage(NetworkMessage message) {
        onMessage(message.subchannel(), message.legacyValue());
    }

    /** Legacy string listener — still supported for existing MineSky plugins. */
    public abstract void onMessage(String subchannel, String value);

    public String getChannel() {
        return channel;
    }

    public boolean canListenToOtherChannels() {
        return listenToOther;
    }

    protected MessagingListener(String channel) {
        this.channel = channel;
        this.listenToOther = false;
    }

    protected MessagingListener(String channel, boolean listenToOtherChannels) {
        this.channel = channel;
        this.listenToOther = listenToOtherChannels;
    }
}
