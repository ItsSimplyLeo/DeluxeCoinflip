/*
 * DeluxeCoinflip Plugin
 * Copyright (c) 2021 - 2025 Zithium Studios. All rights reserved.
 */

package net.zithium.deluxecoinflip;

import co.aikar.commands.PaperCommandManager;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import me.nahu.scheduler.wrapper.FoliaWrappedJavaPlugin;
import net.zithium.deluxecoinflip.api.DeluxeCoinflipAPI;
import net.zithium.deluxecoinflip.api.data.StorageManager;
import net.zithium.deluxecoinflip.api.game.GameManager;
import net.zithium.deluxecoinflip.command.CoinflipCommand;
import net.zithium.deluxecoinflip.config.ConfigHandler;
import net.zithium.deluxecoinflip.config.ConfigType;
import net.zithium.deluxecoinflip.config.Messages;
import net.zithium.deluxecoinflip.economy.EconomyManager;
import net.zithium.deluxecoinflip.api.economy.EconomyProvider;
import net.zithium.deluxecoinflip.game.CoinflipGame;
import net.zithium.deluxecoinflip.game.GameManagerImpl;
import net.zithium.deluxecoinflip.hook.DiscordHook;
import net.zithium.deluxecoinflip.hook.PlaceholderAPIHook;
import net.zithium.deluxecoinflip.listener.PlayerChatListener;
import net.zithium.deluxecoinflip.listener.PlayerConnectionListener;
import net.zithium.deluxecoinflip.menu.InventoryManager;
import net.zithium.deluxecoinflip.storage.PlayerData;
import net.zithium.deluxecoinflip.storage.StorageManagerImpl;
import org.bstats.bukkit.Metrics;
import net.zithium.deluxecoinflip.menu.DupeProtection;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import net.zithium.deluxecoinflip.utility.ItemStackBuilder;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class DeluxeCoinflipPlugin extends FoliaWrappedJavaPlugin implements DeluxeCoinflipAPI {

    private static DeluxeCoinflipPlugin instance;

    private Map<ConfigType, ConfigHandler> configMap;
    private StorageManager storageManager;
    private GameManager gameManager;
    private InventoryManager inventoryManager;
    private EconomyManager economyManager;
    private DiscordHook discordHook;

    private Cache<UUID, CoinflipGame> listenerCache;

    public void onEnable() {
        instance = this;
        long start = System.currentTimeMillis();

        getLogger().log(Level.INFO, "");
        getLogger().log(Level.INFO, " __ __    DeluxeCoinflip v" + getDescription().getVersion());
        getLogger().log(Level.INFO, "/  |_     Author: " + getDescription().getAuthors().get(1));
        getLogger().log(Level.INFO, "\\_ |      (c) Zithium Studios 2021 - 2025. All rights reserved.");
        getLogger().log(Level.INFO, "");

        enableMetrics();

        listenerCache = CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.SECONDS).maximumSize(500).build();

        // Register configurations
        configMap = new HashMap<>();
        registerConfig(ConfigType.CONFIG);
        registerConfig(ConfigType.MESSAGES);
        Messages.setConfiguration(configMap.get(ConfigType.MESSAGES).getConfig());

        // Load storage
        storageManager = new StorageManagerImpl(this);
        try {
            storageManager.onEnable();
        } catch (Exception ex) {
            getLogger().log(Level.SEVERE, "There was an issue attempting to load the storage handler.", ex);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        discordHook = new DiscordHook(this);

        economyManager = new EconomyManager(this);
        economyManager.onEnable();

        gameManager = new GameManagerImpl(this);

        inventoryManager = new InventoryManager();
        inventoryManager.load(this);
        new DupeProtection(this);
        ItemStackBuilder.setPlugin(this);

        List<String> aliases = getConfigHandler(ConfigType.CONFIG).getConfig().getStringList("settings.command_aliases");

        PaperCommandManager paperCommandManager = new PaperCommandManager(this);
        paperCommandManager.getCommandCompletions().registerAsyncCompletion("providers", c -> economyManager.getEconomyProviders().values().stream().map(EconomyProvider::getDisplayName).collect(Collectors.toList()));
        paperCommandManager.getCommandReplacements().addReplacement("main", "coinflip|" + String.join("|", aliases));
        paperCommandManager.registerCommand(new CoinflipCommand(this).setExceptionHandler((command, registeredCommand, sender, args, t) -> {
            Messages.NO_PERMISSION.send(sender.getIssuer());
            return true;
        }));

        // Register listeners
        this.registerEvents(
                new PlayerChatListener(this),
                new PlayerConnectionListener(this)
        );

        // PlaceholderAPI Hook
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderAPIHook(this).register();
            getLogger().log(Level.INFO, "Hooked into PlaceholderAPI successfully");
        }

        getLogger().log(Level.INFO, "");
        getLogger().log(Level.INFO, "Successfully loaded in " + (System.currentTimeMillis() - start) + "ms");
        getLogger().log(Level.INFO, "");
    }

    private void enableMetrics() {
        if (getConfig().getBoolean("metrics", true)) {
            getLogger().log(Level.INFO, "Loading bStats metrics");
            new Metrics(this, 20887);
        } else {
            getLogger().log(Level.INFO, "Metrics are disabled");
        }
    }

    @Override
    public void onDisable() {
        if (storageManager != null) storageManager.onDisable(true);
    }

    // Plugin reload handling
    public void reload() {
        configMap.values().forEach(ConfigHandler::reload);
        Messages.setConfiguration(configMap.get(ConfigType.MESSAGES).getConfig());

        inventoryManager.load(this);
        economyManager.onEnable();
    }

    // Method to register a configuration file
    private void registerConfig(ConfigType type) {
        ConfigHandler handler = new ConfigHandler(this, type.toString().toLowerCase());
        handler.saveDefaultConfig();
        configMap.put(type, handler);
    }

    public StorageManager getStorageManager() {
        return storageManager;
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    public ConfigHandler getConfigHandler(ConfigType type) {
        return configMap.get(type);
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public EconomyManager getEconomyManager() {
        return economyManager;
    }

    public DiscordHook getDiscordHook() {
        return discordHook;
    }

    public Cache<UUID, CoinflipGame> getListenerCache() {
        return listenerCache;
    }

    public NamespacedKey getKey(String key) {
        return new NamespacedKey(this, key);
    }

    private void registerEvents(Listener... listeners) {
        for (Listener listener : listeners) getServer().getPluginManager().registerEvents(listener, this);
    }

    // API methods
    @Override
    public void registerEconomyProvider(@NotNull EconomyProvider provider, @NotNull String requiredPlugin) {
        economyManager.registerEconomyProvider(provider, requiredPlugin);
    }

    @Override
    public Optional<PlayerData> getPlayerData(@NotNull Player player) {
        return storageManager.getPlayer(player.getUniqueId());
    }

    public static DeluxeCoinflipPlugin getInstance() {
        return instance;
    }
}
