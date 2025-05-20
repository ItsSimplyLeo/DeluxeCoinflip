/*
 * DeluxeCoinflip Plugin
 * Copyright (c) 2021 - 2025 Zithium Studios. All rights reserved.
 */

package net.zithium.deluxecoinflip.economy.providers;

import net.zithium.deluxecoinflip.api.economy.EconomyProvider;
import me.realized.tokenmanager.api.TokenManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.OptionalLong;

public class TokenManagerProvider extends EconomyProvider {

    private TokenManager tokenManager;

    public TokenManagerProvider() {
        super("TokenManager");
    }

    @Override
    public void onEnable() {
        tokenManager = (TokenManager) Bukkit.getServer().getPluginManager().getPlugin("TokenManager");
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        OptionalLong tokens = tokenManager.getTokens(player.getPlayer());
        if (tokens.isEmpty()) return 0.0;
        return (double) tokens.getAsLong();
    }

    @Override
    public void withdraw(OfflinePlayer player, double amount) {
        tokenManager.removeTokens(player.getPlayer(), (long) amount);
    }

    @Override
    public void deposit(OfflinePlayer player, double amount) {
        tokenManager.addTokens(player.getPlayer(), (long) amount);
    }
}
