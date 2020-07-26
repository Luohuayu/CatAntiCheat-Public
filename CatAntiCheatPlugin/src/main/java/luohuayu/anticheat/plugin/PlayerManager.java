package luohuayu.anticheat.plugin;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PlayerManager {
    private static Map<Player, PlayerHandler> players = new HashMap<Player, PlayerHandler>();

    public static void addPlayer(Player player) {
        players.put(player, new PlayerHandler(player));
    }

    public static void removePlayer(Player player) {
        PlayerHandler playerHandler = players.remove(player);
        if (playerHandler != null) {
            playerHandler.closeImageIO();
        }
    }

    public static PlayerHandler getPlayerData(Player player) {
        return players.get(player);
    }

    public static void onUpdate() {
        new ArrayList<>(players.values()).forEach(PlayerHandler::onUpdate);
    }
}
