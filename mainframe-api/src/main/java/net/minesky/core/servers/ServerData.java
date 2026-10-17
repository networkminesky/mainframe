package net.minesky.core.servers;

import net.minesky.core.CoreMain;

import java.util.UUID;

public record ServerData(CoreMain.PlatformType platform, String id, String name, UUID uuid) {
}
