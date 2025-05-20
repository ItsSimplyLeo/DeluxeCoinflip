package net.zithium.deluxecoinflip.api.game;

import net.zithium.deluxecoinflip.game.CoinflipGame;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

public interface GameManager {

    void addCoinflipGame(@NotNull UUID uuid, @NotNull CoinflipGame game);

    void removeCoinflipGame(@NotNull UUID uuid);

    void handleGameWin(@NotNull UUID uuid, long winnings, long winningsBeforeTax);

    void handleGameLoss(@NotNull UUID uuid, long loss, long lossBeforeTax);

    /**
     * Handle game cancel
     *
     * @param uuid The UUID of the player canceling the game
     * @return true if the game was canceled successfully, false otherwise
     */
    boolean handleGameCancel(@NotNull UUID uuid);

    @Nullable
    CoinflipGame getCoinflipGame(@NotNull UUID uuid);

    @NotNull
    Map<UUID, CoinflipGame> getCoinflipGames();
}
