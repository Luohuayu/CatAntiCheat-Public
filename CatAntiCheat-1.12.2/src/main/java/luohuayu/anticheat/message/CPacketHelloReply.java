package luohuayu.anticheat.message;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class CPacketHelloReply implements IMessage {
    private int version;
    private byte salt;

    public CPacketHelloReply() {}

    public CPacketHelloReply(int version, byte salt) {
        this.version = version;
        this.salt = salt;
    }

    @Override
    public void fromBytes(final ByteBuf buf) { }

    @Override
    public void toBytes(final ByteBuf buf) {
        buf.writeShort(version);
        buf.writeByte(salt);
    }
}
