package net.minesky.api.mineskymod;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.minesky.api.MineSkyPlayer;
import net.minesky.spigot.SpigotMain;

public class MineSkyModPlayer {

    public static final String LAST_MINESKYMOD_VERSION = "1.0.1";

    private final MineSkyPlayer msPlayer;
    private final String modVersion;

    public MineSkyModPlayer(MineSkyPlayer msn, String modV) {
        this.modVersion = modV;
        this.msPlayer = msn;
    }

    public MineSkyPlayer getMineSkyPlayer() {
        return msPlayer;
    }

    public String getModVersion() {
        return modVersion;
    }

    public void sendChannelPacket(String stringInfo, int intInfo) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        if (intInfo > 0) {
            out.writeInt(intInfo);
        }
        if (!stringInfo.isEmpty()) {
            out.writeUTF(stringInfo);
        }
        msPlayer.getSpigotPlayer().sendPluginMessage(SpigotMain.getInstance(), "minesky:main", out.toByteArray());
    }

    public boolean isTheModUpdated() {
        return modVersion.equals(LAST_MINESKYMOD_VERSION);
    }
}
