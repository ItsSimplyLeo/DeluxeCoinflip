package net.zithium.deluxecoinflip.api.data;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface IPlayerData {

    @NotNull
    UUID getUUID();

    int getWins();

    void setWins(int wins);

    void addWin();

    int getLosses();

    void setLosses(int losses);

    void addLoss();

    double getMoneyGained();

    void setMoneyGained(double moneyGained);

    void addMoneyGained(double moneyGained);

    double getMoneyLost();

    void setMoneyLost(double moneyLost);

    void addMoneyLost(double moneyLost);

    double getMoneyGambled();

    void setMoneyGambled(double moneyGambled);

    void addMoneyGambled(double moneyGambled);

    default double getWinPercentage() {
        if (getWins() == 0 || (getWins() + getLosses() == 0)) return 0.0;
        return Math.round((getWins() * 100.0) / (getWins() + getLosses()) * 100.0) / 100.0;
    }

    int getTotalGames();

    boolean shouldDisplayBroadcastMessages();

    void shouldDisplayBroadcastMessages(boolean displayBroadcastMessages);

}
