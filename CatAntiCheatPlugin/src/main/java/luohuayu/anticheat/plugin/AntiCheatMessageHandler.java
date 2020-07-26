package luohuayu.anticheat.plugin;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import luohuayu.anticheat.plugin.message.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AntiCheatMessageHandler implements PluginMessageListener {
    private List<Class<? extends IPacket>> packets = new ArrayList<Class<? extends IPacket>>();

    public AntiCheatMessageHandler() {
        packets.add(SPacketHello.class);
        packets.add(SPacketFileCheck.class);
        packets.add(SPacketClassCheck.class);
        packets.add(SPacketScreenshot.class);
        packets.add(CPacketHelloReply.class);
        packets.add(CPacketFileHash.class);
        packets.add(CPacketClassFound.class);
        packets.add(CPacketInjectDetect.class);
        packets.add(CPacketImageData.class);
        packets.add(SPacketDataCheck.class);
        packets.add(CPacketVanillaData.class);
    }

    public void onPluginMessageReceived(String channel, Player player, byte[] bytes) {
        if (!CatAntiCheat.channelName.equals(channel) || bytes.length == 0) return;
        ByteBuf buffer = Unpooled.wrappedBuffer(bytes);
        try {
            int id = buffer.readByte();
            IPacket packet = packets.get(id).newInstance();
            packet.read(buffer);

            PlayerHandler playerHandle = PlayerManager.getPlayerData(player);
            if (playerHandle != null) {
                IPacket retPacket = packet.onMessage(playerHandle);
                if (retPacket != null) sendPacket(player, retPacket);
            }
        } catch (Exception e) {
            CatAntiCheat.plugin.handleViolation(player, "服务器内部错误", e.toString(), 0);
            e.printStackTrace();
        } finally {
            buffer.release();
        }
    }

    public void sendPacket(Player player, IPacket packet) {
        ByteBuf buffer = Unpooled.buffer();
        try {
            buffer.writeByte(packets.indexOf(packet.getClass()));
            packet.write(buffer);
            player.sendPluginMessage(CatAntiCheat.plugin, CatAntiCheat.channelName, buffer.array());
        } catch (IOException e) {
            CatAntiCheat.plugin.handleViolation(player, "服务器内部错误", e.toString(), 0);
            e.printStackTrace();
        } finally {
            buffer.release();
        }
    }
}
