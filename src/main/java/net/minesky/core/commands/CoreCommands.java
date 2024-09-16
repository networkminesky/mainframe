package net.minesky.core.commands;

import net.minesky.api.database.UpdatedData;
import net.minesky.api.database.ValueType;
import net.minesky.api.database.PlayerDatabase;
import net.minesky.core.CoreMain;
import net.minesky.core.speedtest.SpeedTest;
import net.minesky.core.databridge.*;
import net.minesky.core.databridge.callbacks.ErrorType;
import net.minesky.core.databridge.callbacks.FindOneCallback;
import net.minesky.core.databridge.callbacks.FindValueCallback;
import net.minesky.core.databridge.callbacks.SetOneCallback;
import net.minesky.core.messaging.MessagingSender;
import org.bson.Document;

import java.util.Arrays;
import java.util.List;

public class CoreCommands {

    public static final List<String> subcommands = Arrays.asList("isconnected", "forcefirstconnect", "getplayervalue",
            "setplayervaluewithcallback", "playerexists", "debug-messaging");

    public static void runCommand(Sender s, String[] args) {
        if(!s.hasPermission("mainframe.admin")) {
            s.sendMessage("&cYou don't have permission to do that.");
            return;
        }

        if(args.length == 0) {
            s.sendMessage("&c/mainframe isconnected\n" +
                    "&c/mainframe forcefirstconnect\n" +
                    "&c/mainframe getplayervalue &f(player) (key)\n" +
                    "&c/mainframe setplayervaluewithcallback &f(player) (key) (value)\n" +
                    "&c/mainframe playerexists &f(player)\n" +
                    "&c/mainframe debug-messaging");
            return;
        }

        if(args[0].equalsIgnoreCase("testspeed")) {
            if(CoreMain.currentPlatform != CoreMain.PlatformType.VELOCITY) {
                s.sendMessage("You can only run this command on Velocity!");
                return;
            }

            if(args.length == 1) {
                s.sendMessage("You need to specify a server name to send the bungee message. This server needs to have Mainframe installed spigot-side!");
                return;
            }

            s.sendMessage("§cFor the test to work, it needs at least 1 online player in the target server! Bungee messaging only accepts plugin message from players.");

            s.sendMessage("§eThis will test the speed taken in both methods: MongoDB Messaging and Bungee Plugin Messaging");

            MessagingSender.sendMessage("test-speed", String.valueOf(System.currentTimeMillis()));

            SpeedTest.sendVelocityMessage(args[1]);

            s.sendMessage("§eTest sent. The results will be shown at this server's console and in here!");

        }

        if(args[0].equalsIgnoreCase("isconnected")) {
            s.sendMessage(MineSkyDB.isConnected ? "Connected and operating!" : "Disconnected. Force first connection with /mainframe forcefirstconnect");
        }

        if(args[0].equalsIgnoreCase("forcefirstconnect")) {
            s.sendMessage("Forcing first connection method and trying to reconnect to the Database...");
            MineSkyDB.firstConnect();
        }

        if(args[0].equalsIgnoreCase("playerexists")) {
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

        if(args[0].equalsIgnoreCase("getplayervalue")) {
            PlayerDatabase.getPlayerSpecificDataAsync(args[1], ValueType.OBJECT, args[2], new FindValueCallback() {
                @Override
                public void onQueryDone(Document player, Object value, boolean found) {
                    if(found)
                        s.sendMessage("Player found, value found: " +value);
                    else
                        s.sendMessage("Player found, value not found.");
                }

                @Override
                public void onQueryError(ErrorType type) {
                    s.sendMessage("Player not found.");
                }
            });
        }

        if(args[0].equalsIgnoreCase("setplayervaluewithcallback")) {
            PlayerDatabase.setPlayerData(args[1], new UpdatedData(args[2], args[3]), new SetOneCallback() {
                @Override
                public void onSetDone() {
                    s.sendMessage("Value changed.");
                }

                @Override
                public void onSetError(ErrorType t) {
                    s.sendMessage("Value not changed, error: "+t);
                }
            });
        }

        if(args[0].equalsIgnoreCase("debug-messaging")) {

            s.sendMessage("Sending custom message listener to debug.");

            MessagingSender.sendMessage("testchannel", "Test value! It can be a string, number, or anything.");

        }

    }

}
