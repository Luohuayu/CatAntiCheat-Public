package luohuayu.anticheat.message;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class SPacketHello implements IMessage {
    public byte salt;

    @Override
    public void fromBytes(ByteBuf buf) {
        salt = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf) { }
}
