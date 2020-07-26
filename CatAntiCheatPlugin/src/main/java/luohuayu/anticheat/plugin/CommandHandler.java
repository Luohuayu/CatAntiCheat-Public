package luohuayu.anticheat.plugin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;

public class CommandHandler implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!sender.equals(Bukkit.getConsoleSender())) {
            sender.sendMessage("指令只能由后台执行!");
            return false;
        }
        if (args.length >= 1) {
            if ("setmodlist".equals(args[0]) && args.length >= 2) {
                Player player = Bukkit.getPlayer(args[1]);
                if (player != null) {
                    PlayerHandler playerHandler = PlayerManager.getPlayerData(player);
                    if (playerHandler != null && !playerHandler.getModList().isEmpty()) {
                        CatAntiCheat.plugin.modDataManager.setModList(playerHandler.getModList());
                        if (CatAntiCheat.plugin.modDataManager.save()) {
                            sender.sendMessage("设置MOD列表成功");
                        } else {
                            sender.sendMessage("无法保存MOD列表");
                        }
                        return true;
                    }
                }
                sender.sendMessage("无法获取玩家MOD列表, 请确认玩家在线");
                return true;
            } else if ("screenshot".equals(args[0]) && args.length >= 2) {
                Player player = Bukkit.getPlayer(args[1]);
                if (player != null) {
                    PlayerHandler playerHandler = PlayerManager.getPlayerData(player);
                    if (playerHandler != null) {
                        playerHandler.sendScreenshot();
                        sender.sendMessage("已向客户端发送截图请求");
                        return true;
                    }
                }
                sender.sendMessage("截图玩家失败, 请确认玩家在线");
                return true;
            } else if ("modlist".equals(args[0]) && args.length >= 2) {
                Player player = Bukkit.getPlayer(args[1]);
                if (player != null) {
                    PlayerHandler playerHandler = PlayerManager.getPlayerData(player);
                    if (playerHandler != null && !playerHandler.getModList().isEmpty()) {
                        sender.sendMessage(player.getName() + " 的MOD列表:");
                        for (String mod : playerHandler.getModList()) {
                            sender.sendMessage(mod);
                        }
                        return true;
                    }
                }
                sender.sendMessage("无法获取玩家MOD列表, 请确认玩家在线");
                return true;
            } else if ("reload".equals(args[0])) {
                CatAntiCheat.plugin.reloadConfig();
                CatAntiCheat.config = new Config(CatAntiCheat.plugin.getConfig());
                CatAntiCheat.plugin.modDataManager = new ModDataManager(new File(CatAntiCheat.plugin.getDataFolder(), "modlist.yml"));
                sender.sendMessage("配置文件重置成功");
                return true;
            }
        }

        sender.sendMessage("========== CatAntiCheat ==========");
        sender.sendMessage("/cac setmodlist <玩家> --- 设置MOD列表为该玩家的列表");
        sender.sendMessage("/cac screenshot <玩家> --- 截图该玩家");
        sender.sendMessage("/cac modlist <玩家> --- 查看该玩家的MOD列表");
        sender.sendMessage("/cac reload --- 重置配置文件和MOD列表");
        sender.sendMessage("==================================");

        return true;
    }
}
