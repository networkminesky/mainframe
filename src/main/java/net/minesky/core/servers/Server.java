package net.minesky.core.servers;

import net.minesky.core.CoreMain;

import java.util.UUID;

public class Server {

    private final ServerData data;

    private boolean alreadyRegistered = false;

    public boolean alreadyRegistered() {
        return this.alreadyRegistered;
    }

    public void setAlreadyRegistered(boolean b) {
        this.alreadyRegistered = b;
    }

    public ServerData getServerData() {
        return data;
    }

    public Server(ServerData data) {
        this.data = data;
    }

    public boolean isVelocity() {
        return getServerData().platform() == CoreMain.PlatformType.VELOCITY;
    }

    public boolean isSpigot() {
        return !isVelocity();
    }
}
