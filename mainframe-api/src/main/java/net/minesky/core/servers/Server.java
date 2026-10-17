package net.minesky.core.servers;

import net.minesky.core.CoreMain;

public class Server {

    private final ServerData data;
    private boolean alreadyRegistered;

    public Server(ServerData data) {
        this.data = data;
    }

    public boolean alreadyRegistered() {
        return alreadyRegistered;
    }

    public void setAlreadyRegistered(boolean registered) {
        this.alreadyRegistered = registered;
    }

    public ServerData getServerData() {
        return data;
    }

    public boolean isVelocity() {
        return getServerData().platform() == CoreMain.PlatformType.VELOCITY;
    }

    public boolean isSpigot() {
        return !isVelocity();
    }
}
