package me.bottleofglass.tebexrotation.commands;

import me.bottleofglass.tebexrotation.TebexRotation;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

public class TebexRotate implements CommandExecutor {
    private TebexRotation plugin;

    public TebexRotate(TebexRotation plugin) {
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        plugin.getHttpManager().getURL();
        return CommandResult.success();
    }
}
