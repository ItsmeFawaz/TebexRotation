package me.bottleofglass.tebexrotation.commands;

import me.bottleofglass.tebexrotation.TebexRotation;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;

import java.util.concurrent.TimeUnit;

public class TimeUntilRotation implements CommandExecutor {
    private TebexRotation plugin;

    public TimeUntilRotation(TebexRotation plugin) {
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        src.sendMessage(Text.of("Time remaining: " + parseMillis(plugin.getHttpManager().getTime())));
        return CommandResult.success();
    }

    public static String parseMillis(long time) {

        long days = TimeUnit.MILLISECONDS.toDays(time);
        time -= TimeUnit.DAYS.toMillis(days);

        long hours = TimeUnit.MILLISECONDS.toHours(time);
        time -= TimeUnit.HOURS.toMillis(hours);

        long minutes = TimeUnit.MILLISECONDS.toMinutes(time);
        time -= TimeUnit.MINUTES.toMillis(minutes);

        long seconds = TimeUnit.MILLISECONDS.toSeconds(time);

        return ((days != 0) ? days + " days " : "") + ((hours != 0) ? hours + " hours " : "") + ((minutes != 0) ? minutes + " minutes " : "") + ((seconds != 0) ? seconds + " seconds " : "");
    }
}
