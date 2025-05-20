/*
 * DeluxeCoinflip Plugin
 * Copyright (c) 2021 - 2025 Zithium Studios. All rights reserved.
 */

package net.zithium.deluxecoinflip.game;

import net.zithium.deluxecoinflip.DeluxeCoinflipPlugin;
import net.zithium.deluxecoinflip.api.data.StorageManager;
import net.zithium.deluxecoinflip.api.game.GameManager;
import net.zithium.deluxecoinflip.storage.PlayerData;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GameManagerImpl implements GameManager {

    private final DeluxeCoinflipPlugin plugin;
    private final Map<UUID, CoinflipGame> coinflipGames;
    private final StorageManager storageManager;

    public GameManagerImpl(DeluxeCoinflipPlugin plugin) {
        this.plugin = plugin;
        this.coinflipGames = new HashMap<>();
        this.storageManager = plugin.getStorageManager();
    }
    /**
     * Add a coinflip game
     *
     * @param uuid The UUID of the player creating the game
     * @param game The coinflip game object
     */
    public void addCoinflipGame(@NotNull UUID uuid, @NotNull CoinflipGame game) {
        coinflipGames.put(uuid, game);
        if (Bukkit.isPrimaryThread()) {
            plugin.getScheduler().runTaskAsynchronously(() -> storageManager.getStorageHandler().saveCoinflip(game));
        } else {
            storageManager.getStorageHandler().saveCoinflip(game);
        }
    }

    /**
     * Delete an existing coinflip game
     *
     * @param uuid The UUID of the player removing the game
     */
    public void removeCoinflipGame(@NotNull UUID uuid) {
        coinflipGames.remove(uuid);
        if (Bukkit.isPrimaryThread()) {
            plugin.getScheduler().runTaskAsynchronously(() -> storageManager.getStorageHandler().deleteCoinfip(uuid));
        } else {
            storageManager.getStorageHandler().deleteCoinfip(uuid);
        }
    }

    @Override
    public void handleGameWin(@NotNull UUID uuid, long winnings, long winningsBeforeTax) {
        PlayerData playerData = storageManager.getPlayer(uuid).orElse(storageManager.getStorageHandler().getPlayer(uuid));
        playerData.addWin();
        playerData.addMoneyGained(winnings);
        playerData.addMoneyGambled(winningsBeforeTax);
        storageManager.savePlayerData(playerData, false);
    }

    @Override
    public void handleGameLoss(@NotNull UUID uuid, long loss, long lossBeforeTax) {
        PlayerData playerData = storageManager.getPlayer(uuid).orElse(storageManager.getStorageHandler().getPlayer(uuid));
        playerData.addLoss();
        playerData.addMoneyLost(lossBeforeTax);
        playerData.addMoneyGambled(lossBeforeTax);
        storageManager.savePlayerData(playerData, false);
    }

    @Override
    public boolean handleGameCancel(@NotNull UUID uuid) {
        CoinflipGame game = getCoinflipGame(uuid);
        if (game == null) {
            return false;
        }

        this.plugin.getEconomyManager().getEconomyProvider(game.getProvider()).deposit(Bukkit.getOfflinePlayer(uuid), game.getAmount());
        this.removeCoinflipGame(uuid);

        return true;
    }

    @Nullable
    public CoinflipGame getCoinflipGame(@NotNull UUID uuid) {
        return coinflipGames.get(uuid);
    }

    /**
     * Get all coinflip games
     *
     * @return Map of UUID and CoinflipGame object
     */
    public @NotNull Map<UUID, CoinflipGame> getCoinflipGames() {
        return coinflipGames;
    }
}
