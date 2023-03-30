package me.bottleofglass.tebexrotation;

import com.google.inject.Inject;
import me.bottleofglass.tebexrotation.commands.TebexRotate;
import me.bottleofglass.tebexrotation.commands.TimeUntilRotation;
import me.bottleofglass.tebexrotation.managers.HttpManager;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

@Plugin(
        id = "tebexrotation",
        name = "TebexRotation"
)
public class TebexRotation {
    private HttpManager httpManager;
    @Inject
    @DefaultConfig(sharedRoot = false)
    private Path defaultConfig;

    private HoconConfigurationLoader loader;

    private CommentedConfigurationNode rootNode;

    @Inject
    private Logger logger;

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        loadConfigs();
        httpManager = new HttpManager(this);
        loadCommands();
    }

    private void loadConfigs() {
        try {
            Sponge.getAssetManager().getAsset(this, "default.conf").get().copyToFile(defaultConfig, false, true);
            loader = HoconConfigurationLoader.builder().setPath(defaultConfig).build();
            rootNode = loader.load();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadCommands() {
        CommandSpec rotateCommandSpec = CommandSpec.builder()
                .description(Text.of("Rotate Command"))
                .permission("tebexrotation.tebexrotate")
                .executor(new TebexRotate(this))
                .build();

        CommandSpec timeUntilRotationCommandSpec = CommandSpec.builder()
                        .description(Text.of("Get time until rotation"))
                                .permission("tebexrotation.timeuntilrotation")
                                        .executor(new TimeUntilRotation(this))
                                                .build();

        Sponge.getCommandManager().register(this, rotateCommandSpec, "tebexrotate");
        Sponge.getCommandManager().register(this, timeUntilRotationCommandSpec, "timeuntilrotation");


    }
    public CommentedConfigurationNode getConfig() {
        return rootNode;
    }
    public HoconConfigurationLoader getLoader() {
        return loader;
    }


    public Logger getLogger() {
        return logger;
    }

    public HttpManager getHttpManager() {
        return httpManager;
    }
}
