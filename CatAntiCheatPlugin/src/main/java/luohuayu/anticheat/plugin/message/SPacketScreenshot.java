package luohuayu.anticheat.plugin.message;

import io.netty.buffer.ByteBuf;
import luohuayu.anticheat.plugin.PlayerHandler;
import luohuayu.anticheat.plugin.Utils;

import java.io.IOException;

public class SPacketScreenshot implements IPacket {

    public IPacket onMessage(PlayerHandler player) {
        return null;
    }

    public void read(ByteBuf buf) throws IOException { }

    public void write(ByteBuf buf) throws IOException {
        buf.writeInt(Utils.getTimestamp());
    }
}
