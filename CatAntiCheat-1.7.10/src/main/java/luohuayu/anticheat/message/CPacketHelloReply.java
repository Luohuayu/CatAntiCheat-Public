package luohuayu.anticheat.message;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

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
