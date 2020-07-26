package luohuayu.anticheat.plugin.message;

import io.netty.buffer.ByteBuf;
import luohuayu.anticheat.plugin.PlayerHandler;
import luohuayu.anticheat.plugin.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CPacketInjectDetect implements IPacket {
    private List<String> foundClassList;

    public IPacket onMessage(PlayerHandler player) {
        player.onInjectDetect(foundClassList);
        return null;
    }

    public void read(ByteBuf buf) throws IOException {
        int size = buf.readShort();
        foundClassList = new ArrayList<String>();
        for (int i = 0; i < size; i++) {
            foundClassList.add(Utils.readUTF8String(buf));
        }
    }

    public void write(ByteBuf buf) throws IOException { }
}
