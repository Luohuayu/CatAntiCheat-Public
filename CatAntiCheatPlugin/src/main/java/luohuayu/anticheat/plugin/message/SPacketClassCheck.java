package luohuayu.anticheat.plugin.message;

import io.netty.buffer.ByteBuf;
import luohuayu.anticheat.plugin.PlayerHandler;
import luohuayu.anticheat.plugin.Utils;

import java.io.IOException;
import java.util.List;

public class SPacketClassCheck implements IPacket {
    private List<String> classList;

    public SPacketClassCheck() {}

    public SPacketClassCheck(List<String> classList) {
        this.classList = classList;
    }

    public IPacket onMessage(PlayerHandler player) {
        return null;
    }

    public void read(ByteBuf buf) throws IOException { }

    public void write(ByteBuf buf) throws IOException {
        buf.writeShort(classList.size());
        for (String className : classList) {
            Utils.writeUTF8String(buf, className);
        }
    }
}
