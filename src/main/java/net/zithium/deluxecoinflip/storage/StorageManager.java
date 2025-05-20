/*
 * DeluxeCoinflip Plugin
 * Copyright (c) 2021 - 2025 Zithium Studios. All rights reserved.
 */

package net.zithium.deluxecoinflip.storage;

import net.zithium.deluxecoinflip.DeluxeCoinflipPlugin;
import net.zithium.deluxecoinflip.config.Messages;
import net.zithium.deluxecoinflip.exception.InvalidStorageHandlerException;
import net.zithium.deluxecoinflip.game.CoinflipGame;
import net.zithium.deluxecoinflip.storage.handler.StorageHandler;
import net.zithium.deluxecoinflip.storage.handler.impl.SQLiteHandler;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class StorageManager {

    private final DeluxeCoinflipPlugin plugin;
    private final Map<UUID, PlayerData> playerDataMap;
    private StorageHandler storageHandler;

    public StorageManager(DeluxeCoinflipPlugin plugin) {
        this.plugin = plugin;
        this.playerDataMap = new HashMap<>();
    }

    public void onEnable() {
        if (plugin.getConfig().getString("storage.type", "SQLITE").equalsIgnoreCase("SQLITE")) {
            storageHandler = new SQLiteHandler();
        } else {
            throw new InvalidStorageHandlerException("Invalid storage handler specified");
        }

        if (!storageHandler.onEnable(plugin)) {
            plugin.getServer().getPluginManager().disablePlugin(plugin);
            return;
        }

        Bukkit.getOnlinePlayers().forEach(player -> loadPlayerData(player.getUniqueId()));
    }

    public void onDisable(boolean shutdown) {
        plugin.getLogger().info("Saving player data to database...");
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.execute(() -> {
            for (PlayerData player : new ArrayList<>(playerDataMap.values())) {
                storageHandler.savePlayer(player);
            }

            if (shutdown) {
                playerDataMap.clear();
                storageHandler.onDisable();
            }

        });
        scheduler.shutdown();
    }

    public Optional<PlayerData> getPlayer(UUID uuid) {
        return Optional.ofNullable(playerDataMap.get(uuid));
    }

    public void loadPlayerData(@NotNull UUID uuid) {
        DeluxeCoinflipPlugin.getInstance().getScheduler().runTaskAsynchronously(() -> {
            playerDataMap.put(uuid, storageHandler.getPlayer(uuid));

            // Load any previous unclosed games and refund and delete
            CoinflipGame game = storageHandler.getCoinflipGame(uuid);
            if (game != null) {
                plugin.getScheduler().runTask(() -> {
                    OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
                    plugin.getEconomyManager().getEconomyProvider(game.getProvider()).deposit(player, game.getAmount());
                    Messages.GAME_REFUNDED.send(player.getPlayer(), "{AMOUNT}", game.getAmount(), "{PROVIDER}", game.getProvider());
                });

                storageHandler.deleteCoinfip(uuid);
                plugin.getGameManager().removeCoinflipGame(uuid);
            }
        });
    }

    public void savePlayerData(PlayerData player, boolean removeCache) {
        UUID uuid = player.getUUID();
        DeluxeCoinflipPlugin.getInstance().getScheduler().runTaskAsynchronously(() -> {
            storageHandler.savePlayer(player);
            if (removeCache) playerDataMap.remove(uuid);
        });
    }

    public StorageHandler getStorageHandler() {
        return storageHandler;
    }
}
