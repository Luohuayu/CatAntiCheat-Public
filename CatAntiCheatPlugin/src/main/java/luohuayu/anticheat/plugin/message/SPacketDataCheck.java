package luohuayu.anticheat.plugin.message;

import io.netty.buffer.ByteBuf;
import luohuayu.anticheat.plugin.PlayerHandler;

import java.io.IOException;

public class SPacketDataCheck implements IPacket {

    public IPacket onMessage(PlayerHandler player) {
        return null;
    }

    public void read(ByteBuf buf) throws IOException { }

    public void write(ByteBuf buf) throws IOException { }
}
