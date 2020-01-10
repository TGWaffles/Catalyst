package rocks.milspecsg.msessentials;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import rocks.milspecsg.msessentials.commands.MSEssentialsCommandManager;
import rocks.milspecsg.msessentials.listeners.*;
import rocks.milspecsg.msrepository.ApiVelocityModule;
import rocks.milspecsg.msrepository.CommonConfigurationModule;
import rocks.milspecsg.msrepository.api.config.ConfigurationService;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;

@Plugin(
        id = MSEssentialsPluginInfo.id,
        name = MSEssentialsPluginInfo.name,
        version = MSEssentialsPluginInfo.version,
        authors = MSEssentialsPluginInfo.authors,
        description = MSEssentialsPluginInfo.description,
        url = MSEssentialsPluginInfo.url
)
public class MSEssentials {

    @Override
    public String toString() {
        return MSEssentialsPluginInfo.id;
    }

    @Inject
    Logger logger;

    @Inject
    private Injector velocityRootInjector;

    @Inject
    private ProxyServer proxyServer;


    public static ProxyServer server;

    public static MSEssentials plugin = null;
    private Injector injector = null;

    public static LuckPerms api;

    private boolean alreadyLoadedOnce = false;

    @Subscribe
    public void onInit(ProxyInitializeEvent event) {
        plugin = this;
        initServices();
        initCommands();
        initListeners();
        loadConfig();
        server = proxyServer;
    }

    public void initServices() {
        api = LuckPermsProvider.get();
        injector = velocityRootInjector.createChildInjector(new VelocityModule(), new MSEssentialsConfigurationModule(), new ApiVelocityModule());
    }

    private void initCommands() {
        if (!alreadyLoadedOnce) {
            injector.getInstance(Key.get(MSEssentialsCommandManager.class)).register(this);
            alreadyLoadedOnce = true;
        }
    }

    public static ProxyServer getServer() {
        return server;
    }

    private void initListeners() {
        logger.info("Injecting listeners");
        proxyServer.getEventManager().register(this, injector.getInstance(ProxyJoinListener.class));
        proxyServer.getEventManager().register(this, injector.getInstance(ProxyLeaveListener.class));
        proxyServer.getEventManager().register(this, injector.getInstance(ProxyChatListener.class));
        proxyServer.getEventManager().register(this, injector.getInstance(ProxyStaffChatListener.class));
        proxyServer.getEventManager().register(this, injector.getInstance(ProxyTeleportRequestListener.class));
        proxyServer.getEventManager().register(this, injector.getInstance(PluginMessageListener.class));
        proxyServer.getEventManager().register(this, injector.getInstance(PingEventListener.class));
    }

    public void loadConfig() {
        logger.info("Loading config");
        injector.getInstance(ConfigurationService.class).load(this);
    }

    private static class MSEssentialsConfigurationModule extends CommonConfigurationModule {
        @Override
        protected void configure() {
            super.configure();
            File configFilesLocation = Paths.get("plugins/" + MSEssentialsPluginInfo.id).toFile();
            if (!configFilesLocation.exists()) {
                if (!configFilesLocation.mkdirs()) {
                    throw new IllegalStateException("Unable to create config directory");
                }
            }
            bind(new TypeLiteral<ConfigurationLoader<CommentedConfigurationNode>>() {
            }).toInstance(HoconConfigurationLoader.builder().setPath(Paths.get(configFilesLocation + "/msessentials.conf")).build());

        }
    }

    @Subscribe
    public void onPluginMessage(PluginMessageEvent event) {
        System.out.println("Plugin messaged");
        System.out.println(event.getIdentifier());
        if (event.getIdentifier().equals("MSE-Starting")) {
            System.out.println(Arrays.toString(event.getData()));
        }
    }
}