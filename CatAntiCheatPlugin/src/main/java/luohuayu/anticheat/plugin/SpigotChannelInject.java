package luohuayu.anticheat.plugin;

import org.bukkit.entity.Player;

public class SpigotChannelInject {
    public static void injectChannel(Player player, String channelName) {
        try {
            player.getClass().getMethod("addChannel", String.class).invoke(player, channelName);
        } catch (Exception ignored) { }
    }
}
