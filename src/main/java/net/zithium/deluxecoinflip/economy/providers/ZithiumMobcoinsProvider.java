/*
 * DeluxeCoinflip Plugin
 * Copyright (c) 2021 - 2025 Zithium Studios. All rights reserved.
 */

package net.zithium.deluxecoinflip.economy.providers;

import net.zithium.deluxecoinflip.api.economy.EconomyProvider;
import net.zithium.mobcoins.ZithiumMobCoinsAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.Optional;

public class ZithiumMobcoinsProvider extends EconomyProvider {

    private ZithiumMobCoinsAPI api;

    public ZithiumMobcoinsProvider() {
        super("ZithiumMobcoins");
    }

    @Override
    public void onEnable() {
        api = (ZithiumMobCoinsAPI) Bukkit.getPluginManager().getPlugin("ZithiumMobcoins");
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        Optional<Long> balanceOptional = api.getUserBalance(player.getUniqueId());
        return balanceOptional.orElse(0L).doubleValue();
    }

    @Override
    public void withdraw(OfflinePlayer player, double amount) {
        api.subtractCoins(player.getUniqueId(), (int) amount);
    }

    @Override
    public void deposit(OfflinePlayer player, double amount) {
        api.addCoins(player.getUniqueId(), (int) amount);
    }
}