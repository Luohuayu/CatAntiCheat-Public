package luohuayu.anticheat.plugin;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class Config {
    public String kickmessage_error;
    public String kickmessage_violation;
    public String kickmessage_banned;
    public int timeout_handshake;
    public int timeout_file;
    public int timeout_keepalive;
    public int timer_file;
    public int timer_class;
    public int autoScreenshot;
    public boolean banTryBypass;
    public String banCommand;
    public List<String> blackListClass;
    public boolean disallowLighting;
    public boolean disallowTransparentTexture;
    public boolean ignoreDuplicateMods;
    public boolean ignoreUncheckMods;

    public Config(FileConfiguration config) {
        kickmessage_error = config.getString("kickmessage.error");
        kickmessage_violation = config.getString("kickmessage.violation");
        kickmessage_banned = config.getString("kickmessage.banned");
        timeout_handshake = config.getInt("timeout.handshake");
        timeout_file = config.getInt("timeout.file");
        timeout_keepalive = config.getInt("timeout.keepalive");
        timer_file = config.getInt("timer.file");
        timer_class = config.getInt("timer.class");
        autoScreenshot = config.getInt("auto_screenshot");
        banTryBypass = config.getBoolean("ban_try_bypass");
        banCommand = config.getString("ban_command");
        blackListClass = config.getStringList("blacklist_class");
        disallowLighting = config.getBoolean("disallow_lighting");
        disallowTransparentTexture = config.getBoolean("disallow_transparent_texture");
        ignoreDuplicateMods = config.getBoolean("ignore_duplicate_mods");
        ignoreUncheckMods = config.getBoolean("ignore_uncheck_mods");
    }
}
