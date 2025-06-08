package net.minesky.core.servers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ServerRegistry {

    private static ArrayList<Server> servers = new ArrayList<>();

    public static Server getServerById(String id) {
        return servers.stream().filter(s -> s.getServerData().id().equals(id)).findFirst()
                .orElse(null);
    }

    public static Server getServerByName(String name) {
        return servers.stream().filter(s -> s.getServerData().name().equalsIgnoreCase(name)).findFirst()
                .orElse(null);
    }

    public static List<Server> getServers() {
        return servers;
    }

    public static Server getServerByUUID(UUID u) {
        return servers.stream().filter(s -> s.getServerData().uuid().equals(u)).findFirst()
                .orElse(null);
    }

}
