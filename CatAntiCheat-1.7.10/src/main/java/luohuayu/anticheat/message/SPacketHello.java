package luohuayu.anticheat.message;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class SPacketHello implements IMessage {
    public byte salt;

    @Override
    public void fromBytes(ByteBuf buf) {
        salt = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf) { }
}
