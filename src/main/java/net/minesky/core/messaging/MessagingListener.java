package net.minesky.core.messaging;

public abstract class MessagingListener {

    private final String channel;
    private final boolean listenToOther;

    public abstract void onMessage(final String subchannel, final String value);

    public String getChannel() {
        return channel;
    }

    public boolean canListenToOtherChannels() {
        return listenToOther;
    }

    public MessagingListener(String channel) {
        this.channel = channel;
        this.listenToOther = false;
    }

    public MessagingListener(String channel, boolean listenToOtherChannels) {
        this.channel = channel;
        this.listenToOther = true;
    }

}
