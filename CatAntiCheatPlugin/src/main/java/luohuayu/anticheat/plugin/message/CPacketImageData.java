package luohuayu.anticheat.plugin.message;

import io.netty.buffer.ByteBuf;
import luohuayu.anticheat.plugin.PlayerHandler;

import java.io.IOException;

public class CPacketImageData implements IPacket {
    private boolean eof;
    private byte[] bytes;

    public IPacket onMessage(PlayerHandler player) {
        player.onImageData(bytes, eof);
        return null;
    }

    public void read(ByteBuf buf) throws IOException {
        eof = buf.readBoolean();
        int size = buf.readableBytes();
        if (size >= 0 && size <= 32765) {
            bytes = new byte[size];
            buf.readBytes(bytes);
        } else {
            throw new IOException("Image data size > 32K");
        }

    }

    public void write(ByteBuf buf) throws IOException { }
}
