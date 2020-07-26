package luohuayu.anticheat.plugin;

import luohuayu.anticheat.plugin.message.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class PlayerHandler {
    private final Player player;
    private byte salt;
    private Map<String, Integer> tasks = new HashMap<String, Integer>();
    private List<String> modList = new ArrayList<>();
    private ByteArrayOutputStream imageNetworkDataOutput;
    private int networkNoTask = 0;
    private int tick = 0;

    private String checkClass;

    public PlayerHandler(Player player) {
        this.player = player;
        this.salt = Utils.randomByte();
        this.checkClass = FMLUtils.getFmlClass();
        sendHandshake();
        sendDataCheck();
    }

    public boolean addTask(String name, int timeoutTime) {
        if (tasks.containsKey(name)) return false;
        tasks.put(name, timeoutTime);
        return true;
    }

    public boolean completeTask(String name) {
        return (tasks.remove(name) != null);
    }

    public void onUpdate() {
        tick++;
        int time = Utils.getTimestamp();
        for (Map.Entry<String, Integer> entry : tasks.entrySet()) {
            String k = entry.getKey();
            int v = entry.getValue();
            if (time >= v) {
                if ("handshake".equals(k)) {
                    CatAntiCheat.plugin.handleViolation(player, "客户端未安装反作弊或验证超时", "", 1);
                } else if (!CatAntiCheat.plugin.hasBypassPermission(player, "timeout")) {
                    CatAntiCheat.plugin.handleViolation(player, "反作弊验证超时", k, 1);
                }
                return;
            }
        }

        if (CatAntiCheat.config.timer_class > 0 && tick % CatAntiCheat.config.timer_class == 0) sendClassCheck();
        if (CatAntiCheat.config.timer_file > 0 && tick % CatAntiCheat.config.timer_file == 0) sendFileCheck();
        if (tick % 45 == 0) sendDataCheck();
        if (CatAntiCheat.config.autoScreenshot > 0 && tick == CatAntiCheat.config.autoScreenshot) sendScreenshot();
    }

    public void sendHandshake() {
        addTask("handshake", Utils.getTimestamp() + CatAntiCheat.config.timeout_handshake);
        CatAntiCheat.plugin.sendPacket(player, new SPacketHello(salt));
    }

    public void onHandshake(int version, byte salt) {
        if (!checkSalt(salt)) return;
        if (completeTask("handshake")) {
            if (version == CatAntiCheat.version) {
                sendFileCheck();
            } else {
                CatAntiCheat.plugin.handleViolation(player, "反作弊版本不一致", "client:" + version, 1);
            }
        } else {
            if (!CatAntiCheat.plugin.isBungee()) {
                CatAntiCheat.plugin.handleViolation(player, "数据包异常", "notask:handshake", 1);
            }
        }
    }

    public void sendFileCheck() {
        if (addTask("file", Utils.getTimestamp() + CatAntiCheat.config.timeout_file)) {
            CatAntiCheat.plugin.sendPacket(player, new SPacketFileCheck());
        }
    }

    public void onFileCheck(List<String> fileHashList, byte salt) {
        if (!checkSalt(salt)) return;
        if (completeTask("file")) {
            if (fileHashList.size() <= 5) {
                CatAntiCheat.plugin.handleViolation(player, "数据包异常", "type4", 2);
                return;
            }
            List<String> oldModList = new ArrayList<String>(modList);
            modList.clear();
            List<String> allowMods = new ArrayList<>();
            List<String> rejectMods = new ArrayList<>();
            List<String> duplicateMods = new ArrayList<>();
            List<String> uncheckMods = new ArrayList<>();
            for (String fileHash : fileHashList) {
                String[] data = fileHash.split("\0");
                if (data.length == 2 && data[0].length() == 40) {
                    if ("0000000000000000000000000000000000000000".equals(data[0])) {
                        uncheckMods.add(fileHash);
                        continue;
                    }
                    if (!allowMods.contains(data[0])) {
                        if (CatAntiCheat.plugin.isAllowMod(data[0])) {
                            allowMods.add(data[0]);
                        } else {
                            rejectMods.add(fileHash);
                        }
                        modList.add(data[0] + "|" + data[1]);
                    } else {
                        duplicateMods.add(fileHash);
                    }
                } else {
                    CatAntiCheat.plugin.handleViolation(player, "数据包异常", "type3", 2);
                    return;
                }
            }
            if (uncheckMods.size() > 0 && !CatAntiCheat.config.ignoreUncheckMods) {
                CatAntiCheat.plugin.handleViolation(player, "客户端存在无法读取的文件 (请尝试更换启动器)", Arrays.toString(uncheckMods.toArray()), 1);
                return;
            }
            if (duplicateMods.size() > 0 && !CatAntiCheat.config.ignoreDuplicateMods) {
                CatAntiCheat.plugin.handleViolation(player, "请勿安装重复的MOD", Arrays.toString(duplicateMods.toArray()), 1);
                return;
            }
            if (rejectMods.size() > 0 && oldModList.size() > 0 && modList.size() > oldModList.size() && !CatAntiCheat.plugin.hasBypassPermission(player, "inject")) {
                List<String> injectModList = new ArrayList<>();
                for (String s : modList) {
                    if (!oldModList.contains(s)) {
                        injectModList.add(s);
                    }
                }
                CatAntiCheat.plugin.handleViolation(player, "检测到注入", Arrays.toString(injectModList.toArray()), 2);
                return;
            }
            if (rejectMods.size() > 0 && !CatAntiCheat.plugin.hasBypassPermission(player, "file")) {
                CatAntiCheat.plugin.handleViolation(player, "请勿自行安装MOD", Arrays.toString(rejectMods.toArray()), 1);
                return;
            }
        } else {
            if (++networkNoTask > 2) {
                CatAntiCheat.plugin.handleViolation(player, "数据包异常", "notask:file", 1);
            }
        }
    }

    public void sendClassCheck() {
        if (addTask("class", Utils.getTimestamp() + CatAntiCheat.config.timeout_keepalive)) {
            List<String> blackListClass = new ArrayList<>(CatAntiCheat.config.blackListClass);
            blackListClass.add(checkClass);
            CatAntiCheat.plugin.sendPacket(player, new SPacketClassCheck(blackListClass));
        }
    }

    public void onClassFound(List<String> classList, byte salt) {
        if (!checkSalt(salt)) return;
        if (completeTask("class")) {
            if (!classList.remove(checkClass)) {
                CatAntiCheat.plugin.handleViolation(player, "客户端反作弊被修改", "", 2);
                return;
            }
            if (classList.size() > 0 && !CatAntiCheat.plugin.hasBypassPermission(player, "class")) {
                CatAntiCheat.plugin.handleViolation(player, "检测到被禁止的MOD", Arrays.toString(classList.toArray()), 2);
            }
        }
    }

    public void onInjectDetect(List<String> classList) {
        if (classList.size() > 0 && !CatAntiCheat.plugin.hasBypassPermission(player, "inject")) {
            CatAntiCheat.plugin.handleViolation(player, "检测到注入", Arrays.toString(classList.toArray()), 2);
        }
    }

    public void sendScreenshot() {
        if (addTask("screenshot", Utils.getTimestamp() + CatAntiCheat.config.timeout_keepalive)) {
            imageNetworkDataOutput = new ByteArrayOutputStream();
            CatAntiCheat.plugin.sendPacket(player, new SPacketScreenshot());
        }
    }

    public void onImageData(byte[] bytes, boolean eof) {
        if (imageNetworkDataOutput != null) {
            try {
                imageNetworkDataOutput.write(bytes);

                if (eof) {
                    byte[] networkBytes = imageNetworkDataOutput.toByteArray();
                    File saveDir = new File(CatAntiCheat.plugin.getDataFolder(), "screenshot");
                    if (!saveDir.exists()) saveDir.mkdir();
                    Bukkit.getScheduler().runTaskAsynchronously(CatAntiCheat.plugin, new SaveNetworkImageTask(new File(saveDir, player.getName() + "-" + Utils.getTimestamp() + ".png"), networkBytes));
                    closeImageIO();
                    completeTask("screenshot");
                } else {
                    if (imageNetworkDataOutput.size() > 16777216 /* 16M */) {
                        CatAntiCheat.plugin.handleViolation(player, "数据包异常", "type6", 2);
                    }
                }
            } catch (IOException e) {
                closeImageIO();
                System.out.println("截图玩家 " + player.getName() + " 失败: " + e.toString());
            }
        }
    }

    public void closeImageIO() {
        if (imageNetworkDataOutput != null) try { imageNetworkDataOutput.close(); } catch (IOException ignored) { } finally { imageNetworkDataOutput = null; }
    }

    public List<String> getModList() {
        return modList;
    }

    private boolean checkSalt(byte salt) {
        if (salt != this.salt) {
            if (!CatAntiCheat.plugin.isBungee()) {
                CatAntiCheat.plugin.handleViolation(player, "数据包异常", "salt", 1);
            }
            return false;
        }
        return true;
    }

    public void sendDataCheck() {
        if (addTask("datacheck", Utils.getTimestamp() + CatAntiCheat.config.timeout_keepalive)) {
            CatAntiCheat.plugin.sendPacket(player, new SPacketDataCheck());
        }
    }

    public void onVanillaData(boolean lighting, boolean transparentTexture) {
        if (completeTask("datacheck")) {
            if (CatAntiCheat.config.disallowLighting && lighting && !CatAntiCheat.plugin.hasBypassPermission(player, "vanilla")) {
                CatAntiCheat.plugin.handleViolation(player, "请勿修改亮度值", "", 1);
                return;
            }
            if (CatAntiCheat.config.disallowTransparentTexture && transparentTexture && !CatAntiCheat.plugin.hasBypassPermission(player, "vanilla")) {
                CatAntiCheat.plugin.handleViolation(player, "请勿使用透明材质", "", 1);
            }
        }
    }
}
