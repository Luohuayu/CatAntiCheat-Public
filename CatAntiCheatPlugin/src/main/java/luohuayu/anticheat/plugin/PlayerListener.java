package luohuayu.anticheat.plugin;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        SpigotChannelInject.injectChannel(player, CatAntiCheat.channelName);
        if (!CatAntiCheat.plugin.isInstallClientMod(player)) {
            CatAntiCheat.plugin.handleViolation(player, "客户端未安装反作弊", "fastcheck", 1);
            return;
        }
        PlayerManager.addPlayer(player);
        if (!CatAntiCheat.plugin.isConfiged()) {
            player.sendMessage("反作弊MOD列表未配置, 请在后台使用/cac指令进行配置!");
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        PlayerManager.removePlayer(event.getPlayer());
    }
}
