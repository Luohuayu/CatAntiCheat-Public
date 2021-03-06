package luohuayu.anticheat.message;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class CPacketImageData implements IMessage {
    private boolean eof;
    private byte[] bytes;

    public CPacketImageData() {}

    public CPacketImageData(boolean eof, byte[] bytes) {
        this.eof = eof;
        this.bytes = bytes;
        if (bytes.length > 32763) throw new RuntimeException("Image data size > 32K");
    }

    @Override
    public void fromBytes(final ByteBuf buf) { }

    @Override
    public void toBytes(final ByteBuf buf) {
        buf.writeBoolean(eof);
        buf.writeBytes(bytes);
    }
}
