package luohuayu.anticheat.message;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

import java.util.List;

public class CPacketClassFound implements IMessage {
    private List<String> foundClassList;
    private byte salt;

    public CPacketClassFound() {}

    public CPacketClassFound(List<String> foundClassList, byte salt) {
        this.foundClassList = foundClassList;
        this.salt = salt;
    }

    @Override
    public void fromBytes(final ByteBuf buf) { }

    @Override
    public void toBytes(final ByteBuf buf) {
        buf.writeShort(foundClassList.size());
        for (String s : foundClassList) {
            ByteBufUtils.writeUTF8String(buf, s);
        }
        buf.writeByte(salt);
    }
}
