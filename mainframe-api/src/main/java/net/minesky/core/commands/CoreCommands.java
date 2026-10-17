package net.minesky.core.commands;

import net.minesky.api.database.PlayerDatabase;
import net.minesky.api.database.UpdatedData;
import net.minesky.api.database.ValueType;
import net.minesky.core.CoreMain;
import net.minesky.core.databridge.MineSkyDB;
import net.minesky.core.databridge.callbacks.ErrorType;
import net.minesky.core.databridge.callbacks.FindOneCallback;
import net.minesky.core.databridge.callbacks.FindValueCallback;
import net.minesky.core.databridge.callbacks.SetOneCallback;
import net.minesky.core.messaging.MessageType;
import net.minesky.core.messaging.MessagingSender;
import net.minesky.core.runtime.MainframeRuntime;
import org.bson.Document;

import java.util.Arrays;
import java.util.List;

public final class CoreCommands {

    public static final List<String> subcommands = Arrays.asList(
            "isconnected", "forcefirstconnect", "getplayervalue",
            "setplayervaluewithcallback", "playerexists", "debug-messaging", "testspeed", "control", "servers"
    );

    private CoreCommands() {
    }

    public static void runCommand(Sender s, String[] args) {
        if (!s.hasPermission("mainframe.admin")) {
            s.sendMessage("&cYou don't have permission to do that.");
            return;
        }

        if (args.length == 0) {
            s.sendMessage("&c/mainframe isconnected");
            s.sendMessage("&c/mainframe forcefirstconnect");
            s.sendMessage("&c/mainframe getplayervalue &f(player) (key)");
            s.sendMessage("&c/mainframe setplayervaluewithcallback &f(player) (key) (value)");
            s.sendMessage("&c/mainframe playerexists &f(player)");
            s.sendMessage("&c/mainframe debug-messaging");
            return;
        }

        String subcmd = args[0].toLowerCase();

        switch (subcmd) {
            case "control", "servers" -> MessagingSender.publish("mainframe:list-servers", "asking-list");
            case "testspeed" -> handleSpeedTest(s, args);
            case "isconnected" -> s.sendMessage(MineSkyDB.isConnected
                    ? "§aConnected and operating!"
                    : "§cDisconnected. Use forcefirstconnect.");
            case "forcefirstconnect" -> {
                s.sendMessage("Reconnecting to MongoDB...");
                MineSkyDB.firstConnect();
            }
            case "playerexists" -> {
                if (args.length < 2) {
                    s.sendMessage("&cUsage: playerexists <player>");
                    return;
                }
                PlayerDatabase.getPlayerDataAsync(args[1], new FindOneCallback() {
                    @Override
                    public void onQueryError(ErrorType type) {
                        s.sendMessage("Player not found.");
                    }

                    @Override
                    public void onQueryDone(Document document) {
                        s.sendMessage("Player found!");
                    }
                });
            }
            case "getplayervalue" -> {
                if (args.length < 3) {
                    s.sendMessage("&cUsage: getplayervalue <player> <key>");
                    return;
                }
                PlayerDatabase.getPlayerSpecificDataAsync(args[1], ValueType.OBJECT, args[2], new FindValueCallback() {
                    @Override
                    public void onQueryDone(Document player, Object value, boolean found) {
                        s.sendMessage(found ? "Value: " + value : "Player found, value missing.");
                    }

                    @Override
                    public void onQueryError(ErrorType type) {
                        s.sendMessage("Player not found.");
                    }
                });
            }
            case "setplayervaluewithcallback" -> {
                if (args.length < 4) {
                    s.sendMessage("&cUsage: setplayervaluewithcallback <player> <key> <value>");
                    return;
                }
                PlayerDatabase.setPlayerData(args[1], new UpdatedData(args[2], args[3]), new SetOneCallback() {
                    @Override
                    public void onSetDone() {
                        s.sendMessage("Value changed.");
                    }

                    @Override
                    public void onSetError(ErrorType t) {
                        s.sendMessage("Error: " + t);
                    }
                });
            }
            case "debug-messaging" -> {
                s.sendMessage("Sending typed test message...");
                MessagingSender.publish(
                        "mainframe:default",
                        "testchannel",
                        MessageType.STRING,
                        "Test value — use MessageType for numbers, JSON, BSON, etc."
                );
            }
            default -> s.sendMessage("&cUnknown subcommand.");
        }
    }

    private static void handleSpeedTest(Sender s, String[] args) {
        if (CoreMain.currentPlatform != CoreMain.PlatformType.VELOCITY) {
            s.sendMessage("Run testspeed on Velocity.");
            return;
        }
        if (args.length < 2) {
            s.sendMessage("Usage: testspeed <backend-server-name>");
            return;
        }
        if (MainframeRuntime.speedTest() == null) {
            s.sendMessage("Speed test bridge not available.");
            return;
        }
        long now = System.currentTimeMillis();
        MessagingSender.publish("test-speed", MessageType.LONG, now);
        MainframeRuntime.speedTest().sendPluginChannelSpeedProbe(args[1], now);
        s.sendMessage("§eSpeed test dispatched (Mongo + plugin channel). Check console.");
    }
}
