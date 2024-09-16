package net.minesky.velocity.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import net.kyori.adventure.text.Component;
import net.minesky.core.commands.CoreCommands;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MainCommand implements SimpleCommand {

    @Override
    public void execute(final Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();

        CoreCommands.runCommand(new VelocitySender(source), args);
    }

    /*@Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source().hasPermission("mainframe.admin");
    }*/

    @Override
    public List<String> suggest(final Invocation invocation) {
        return CoreCommands.subcommands;
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(final Invocation invocation) {
        return CompletableFuture.completedFuture(CoreCommands.subcommands);
    }

}
