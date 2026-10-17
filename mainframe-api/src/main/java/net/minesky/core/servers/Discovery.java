package net.minesky.core.servers;

import net.minesky.core.CoreMain;
import net.minesky.core.messaging.MessagingSender;

public final class Discovery {

    private Discovery() {
    }

    public static void findServers() {
        MessagingSender.publish("server-discovery", "");
    }

    public static void registerServer(ServerData data) {
        boolean duplicate = ServerRegistry.getServers().stream().anyMatch(sv -> {
            ServerData dat = sv.getServerData();
            return dat.id().equals(data.id()) || dat.name().equals(data.name()) || dat.uuid().equals(data.uuid());
        });

        if (!duplicate) {
            ServerRegistry.getServers().add(new Server(data));
            CoreMain.logger.info("Server discovered: " + data.name() + " (" + data.id() + ")");
        }
    }
}
