package luohuayu.anticheat.plugin.message;

import io.netty.buffer.ByteBuf;
import luohuayu.anticheat.plugin.PlayerHandler;

import java.io.IOException;

public class SPacketHello implements IPacket {
    private byte salt;

    public SPacketHello() {}

    public SPacketHello(byte salt) {
        this.salt = salt;
    }

    public IPacket onMessage(PlayerHandler player) {
        return null;
    }

    public void read(ByteBuf buf) throws IOException { }

    public void write(ByteBuf buf) throws IOException {
        buf.writeByte(salt);
    }
}
