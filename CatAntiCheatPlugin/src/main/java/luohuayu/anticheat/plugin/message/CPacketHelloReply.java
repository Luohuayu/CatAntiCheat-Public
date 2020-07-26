package luohuayu.anticheat.plugin.message;

import io.netty.buffer.ByteBuf;
import luohuayu.anticheat.plugin.PlayerHandler;

import java.io.IOException;

public class CPacketHelloReply implements IPacket {
    private int version;
    private byte salt;

    public IPacket onMessage(PlayerHandler player) {
        player.onHandshake(version, salt);
        return null;
    }

    public void read(ByteBuf buf) throws IOException {
        version = buf.readShort();
        salt = buf.readByte();
    }

    public void write(ByteBuf buf) throws IOException { }
}
