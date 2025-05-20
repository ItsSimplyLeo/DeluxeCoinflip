package net.zithium.deluxecoinflip.api.data;

import net.zithium.deluxecoinflip.storage.PlayerData;
import net.zithium.deluxecoinflip.storage.handler.StorageHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface StorageManager {

    void onEnable();

    void onDisable(boolean shutdown);

    Map<UUID, PlayerData> getPlayerCache();

    Optional<PlayerData> getPlayer(@NotNull UUID uuid);

    void loadPlayerData(@NotNull UUID uuid);

    void savePlayerData(@NotNull PlayerData uuid, boolean removeCache);

    @NotNull
    StorageHandler getStorageHandler();

}
