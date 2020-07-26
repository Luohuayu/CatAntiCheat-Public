package luohuayu.anticheat.plugin;

import luohuayu.anticheat.plugin.message.IPacket;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;

public class CatAntiCheat extends JavaPlugin implements Listener {
    public static CatAntiCheat plugin;
    public static String channelName;
    public static int version = 2;
    public static Config config;
    private AntiCheatMessageHandler messageHandler;
    public ModDataManager modDataManager;
    private boolean enableFastCheck;
    private Class<?> fastCheck;
    private Writer logWriter;

    @Override
    public void onEnable() {
        plugin = this;
        saveDefaultConfig();
        reloadConfig();
        config = new Config(getConfig());
        messageHandler = new AntiCheatMessageHandler();
        modDataManager = new ModDataManager(new File(getDataFolder(), "modlist.yml"));

        channelName = !isLater1_13() ? "CatAntiCheat" : "catanticheat:data";

        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getMessenger().registerIncomingPluginChannel(this, channelName, messageHandler);
        getServer().getMessenger().registerOutgoingPluginChannel(this, channelName);

        hotLoadCheck();

        getServer().getScheduler().runTaskTimer(this, PlayerManager::onUpdate, 1 , 20);
        getCommand("catanticheat").setExecutor(new CommandHandler());

        try {
            Class.forName("net.minecraftforge.fml.common.network.handshake.NetworkDispatcher");
            enableFastCheck = true;
            try {
                fastCheck = Class.forName("luohuayu.anticheat.plugin." + getServer().getName() + "FastCheck");
            } catch (Exception e) {
                getLogger().info("Forge兼容模块加载失败! (" + Integer.toHexString(getServer().getName().hashCode()) + ")");
                getLogger().info("请尝试更换支持的服务端: Spigot/Paper/CatServer/Uranium/KCauldron/Thermos");
                while (true) Thread.sleep(1000);
            }
        } catch (Exception ignored) {}

        if (isBungee()) {
            getLogger().info("使用BungeeCord兼容模式");
        }
    }

    @Override
    public void onDisable() {
        try { if (logWriter != null) logWriter.close(); } catch (IOException ignored) { }
    }

    public void handleViolation(Player player, String reason, String log, int level) {
        String kickUUID = UUID.randomUUID().toString();
        if (level == 0) {
            logInfo(String.format("[错误][%s][%s]%s - %s", player.getName(), kickUUID, reason, log));
            player.kickPlayer(config.kickmessage_error.replace("%reason%", reason).replace("%uuid%", kickUUID));
        } else if (level == 1) {
            logInfo(String.format("[踢出][%s][%s]%s - %s", player.getName(), kickUUID, reason, log));
            player.kickPlayer(config.kickmessage_violation.replace("%reason%", reason).replace("%uuid%", kickUUID));
        } else if (level == 2) {
            logInfo(String.format("[封禁][%s][%s]%s - %s", player.getName(), kickUUID, reason, log));
            player.kickPlayer(config.kickmessage_banned.replace("%reason%", reason).replace("%uuid%", kickUUID));
            if (config.banTryBypass) {
                getServer().dispatchCommand(getServer().getConsoleSender(), config.banCommand.replace("%player%", player.getName()).replace("%reason%", reason).replace("%uuid%", kickUUID));
            }
        }
        PlayerManager.removePlayer(player);
    }

    public void sendPacket(Player player, IPacket packet) {
        messageHandler.sendPacket(player, packet);
    }

    public boolean isConfiged() {
        return !modDataManager.getModList().isEmpty();
    }

    public boolean isAllowMod(String hash) {
        if (!isConfiged()) return true;
        for (String s : modDataManager.getModList()) {
            if (s.split("\\|")[0].equals(hash)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasBypassPermission(Player player, String perm) {
        return player.hasPermission("CatAntiCheat.bypass." + perm);
    }

    public boolean isInstallClientMod(Player player) {
        if (enableFastCheck) {
            try {
                return (Boolean) fastCheck.getMethod("isInstallClientMod", Player.class).invoke(null, player);
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    private void hotLoadCheck() {
        Collection<? extends Player> players = getServer().getOnlinePlayers();
        for (Player player : players) {
            PlayerManager.addPlayer(player);
        }
    }

    public void logInfo(String log) {
        getLogger().info(log);
        try {
            if (logWriter == null) logWriter = new FileWriter(new File(getDataFolder(), "CatAntiCheat.log"), true);
            logWriter.write(String.format("[%s]%s\n", new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()), log));
            logWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isBungee() {
        try {
            return (boolean) Class.forName("org.spigotmc.SpigotConfig").getField("bungee").get(null);
        } catch (Exception ignored) { }
        return false;
    }

    public boolean isLater1_13() {
        try {
            String nmsVersion = getServer().getClass().getName().split("\\.")[3];
            return Integer.parseInt(nmsVersion.split("_")[1]) >= 13;
        } catch (Exception ignored) {}
        return false;
    }
}
