/*
 * DeluxeCoinflip Plugin
 * Copyright (c) 2021 - 2025 Zithium Studios. All rights reserved.
 */

package net.zithium.deluxecoinflip.storage;

import net.zithium.deluxecoinflip.api.data.IPlayerData;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PlayerData implements IPlayerData {

    private final UUID uuid;
    private int wins, losses;
    private double moneyGained, moneyLost, totalGambled;
    private boolean displayBroadcastMessages;

    public PlayerData(@NotNull UUID uuid, int wins, int losses, long moneyGained, long moneyLost, long moneyGambled, boolean displayBroadcastMessages) {
        this.uuid = uuid;
        this.losses = losses;
        this.wins = wins;
        this.moneyGained = moneyGained;
        this.moneyLost = moneyLost;
        this.totalGambled = moneyGambled;
        this.displayBroadcastMessages = displayBroadcastMessages;
    }

    public PlayerData(@NotNull UUID uuid) {
        this(uuid, 0, 0, 0, 0, 0, true);
    }

    @NotNull
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public int getWins() {
        return wins;
    }

    @Override
    public void setWins(int wins) {
        this.wins = wins;
    }

    @Override
    public void addWin() {
        this.wins++;
    }

    @Override
    public int getLosses() {
        return losses;
    }

    @Override
    public void setLosses(int losses) {
        this.losses = losses;
    }

    @Override
    public void addLoss() {
        this.losses++;
    }

    @Override
    public double getMoneyGained() {
        return moneyGained;
    }

    @Override
    public void setMoneyGained(double moneyGained) {
        this.moneyGained = moneyGained;
    }

    @Override
    public void addMoneyGained(double moneyGained) {
        this.moneyGained += moneyGained;
    }

    @Override
    public double getMoneyLost() {
        return moneyLost;
    }

    @Override
    public void setMoneyLost(double moneyLost) {
        this.moneyLost = moneyLost;
    }

    @Override
    public void addMoneyLost(double moneyLost) {
        this.moneyLost += moneyLost;
    }

    @Override
    public double getMoneyGambled() {
        return totalGambled;
    }

    @Override
    public void setMoneyGambled(double moneyGambled) {
        this.totalGambled = moneyGambled;
    }

    @Override
    public void addMoneyGambled(double moneyGambled) {
        this.totalGambled += moneyGambled;
    }

    @Override
    public int getTotalGames() {
        return getWins() + getLosses();
    }

    @Override
    public boolean shouldDisplayBroadcastMessages() {
        return displayBroadcastMessages;
    }

    @Override
    public void shouldDisplayBroadcastMessages(boolean displayBroadcastMessages) {
        this.displayBroadcastMessages = displayBroadcastMessages;
    }
}
