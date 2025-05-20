/*
 * DeluxeCoinflip Plugin
 * Copyright (c) 2021 - 2025 Zithium Studios. All rights reserved.
 */

package net.zithium.deluxecoinflip.hook;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.zithium.deluxecoinflip.DeluxeCoinflipPlugin;
import net.zithium.deluxecoinflip.api.data.StorageManager;
import net.zithium.deluxecoinflip.storage.PlayerData;
import net.zithium.deluxecoinflip.utility.FormatUtil;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@SuppressWarnings("deprecation")
public class PlaceholderAPIHook extends PlaceholderExpansion {

    private final DeluxeCoinflipPlugin plugin;
    private final StorageManager storageManager;

    public PlaceholderAPIHook(DeluxeCoinflipPlugin plugin) {
        this.plugin = plugin;
        this.storageManager = plugin.getStorageManager();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @NotNull
    @Override
    public String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @NotNull
    @Override
    public String getIdentifier() {
        return "deluxecoinflip";
    }

    @NotNull
    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (player == null) return "";

        Optional<PlayerData> playerDataOptional = storageManager.getPlayer(player.getUniqueId());
        if (playerDataOptional.isEmpty()) {
            return "N/A";
        }

        PlayerData playerData = playerDataOptional.get();
        return switch (identifier) {
            case "games_played" -> FormatUtil.formatNumber(playerData.getTotalGames());
            case "wins" -> FormatUtil.formatNumber(playerData.getWins());
            case "win_percentage" -> FormatUtil.formatNumber(playerData.getWinPercentage());
            case "losses" -> String.valueOf(playerData.getLosses());
            case "profit" -> String.valueOf(playerData.getMoneyGained());
            case "profit_formatted" -> FormatUtil.formatNumber(playerData.getMoneyGained());
            case "total_losses" -> String.valueOf(playerData.getMoneyLost());
            case "total_losses_formatted" -> FormatUtil.formatNumber(playerData.getMoneyLost());
            case "total_gambled" -> String.valueOf(playerData.getMoneyGambled());
            case "total_gambled_formatted" -> FormatUtil.formatNumber(playerData.getMoneyGambled());
            case "display_broadcast_messages" -> String.valueOf(playerData.shouldDisplayBroadcastMessages());
            case "total_games" -> String.valueOf(plugin.getGameManager().getCoinflipGames().size());
            default -> null;
        };
    }
}
