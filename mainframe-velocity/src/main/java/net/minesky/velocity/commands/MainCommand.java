package net.minesky.velocity.commands;

import com.velocitypowered.api.command.SimpleCommand;
import net.minesky.core.commands.CoreCommands;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MainCommand implements SimpleCommand {

    @Override
    public void execute(Invocation invocation) {
        CoreCommands.runCommand(new VelocitySender(invocation.source()), invocation.arguments());
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        return CoreCommands.subcommands;
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(Invocation invocation) {
        return CompletableFuture.completedFuture(CoreCommands.subcommands);
    }
}
