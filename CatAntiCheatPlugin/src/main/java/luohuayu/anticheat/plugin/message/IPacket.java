package luohuayu.anticheat.plugin.message;

import io.netty.buffer.ByteBuf;
import luohuayu.anticheat.plugin.PlayerHandler;

import java.io.IOException;

public interface IPacket {
    IPacket onMessage(PlayerHandler player);
    void read(ByteBuf buf) throws IOException;
    void write(ByteBuf buf) throws IOException;
}
