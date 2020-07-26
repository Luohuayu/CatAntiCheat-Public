package luohuayu.anticheat.plugin;

import net.minecraftforge.fml.common.network.handshake.NetworkDispatcher;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class CatServerFastCheck {
    static {
        NetworkDispatcher.class.getName();
    }

    public static boolean isInstallClientMod(Player player) {
        return NetworkDispatcher.get(((CraftPlayer) player).getHandle().field_71135_a.field_147371_a).getModList().containsKey("catanticheat");
    }
}
