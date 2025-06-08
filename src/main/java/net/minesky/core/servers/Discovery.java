package net.minesky.core.servers;

import net.minesky.core.CoreMain;
import net.minesky.core.messaging.MessagingSender;

public class Discovery {

    public static void findServers() {
        MessagingSender.sendMessage("server-discovery", "");
    }

    public static void registerServer(ServerData data) {
        Server server = new Server(data);
        boolean can = true;

        for(Server sv : ServerRegistry.getServers()) {
            final ServerData dat = sv.getServerData();

            if( dat.id().equals(data.id()) || dat.name().equals(data.name()) || dat.uuid().equals(data.uuid()) ) {
                can = false;
            }
        }

        if(can) {
            ServerRegistry.getServers().add(server);
            CoreMain.logger.info("New server discovered! "+data.name() + " with the Id "+data.id());
        }
    }

}
