package net.zithium.deluxecoinflip.listener;

import net.zithium.deluxecoinflip.DeluxeCoinflipPlugin;
import net.zithium.deluxecoinflip.api.data.StorageManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerConnectionListener implements Listener {

    private final DeluxeCoinflipPlugin plugin;

    public PlayerConnectionListener(@NotNull DeluxeCoinflipPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        this.getStorageManager().loadPlayerData(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        this.getStorageManager().getPlayer(event.getPlayer().getUniqueId()).ifPresent(data -> getStorageManager().savePlayerData(data, true));
    }

    private StorageManager getStorageManager() {
        return this.plugin.getStorageManager();
    }
}
