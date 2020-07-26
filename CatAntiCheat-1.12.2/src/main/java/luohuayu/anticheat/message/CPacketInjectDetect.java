package luohuayu.anticheat.message;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import java.util.List;

public class CPacketInjectDetect implements IMessage {
    public List<String> foundClassList;

    public CPacketInjectDetect() {}

    public CPacketInjectDetect(List<String> foundClassList) {
        this.foundClassList = foundClassList;
    }

    @Override
    public void fromBytes(final ByteBuf buf) { }

    @Override
    public void toBytes(final ByteBuf buf) {
        buf.writeShort(foundClassList.size());
        for (String s : foundClassList) {
            ByteBufUtils.writeUTF8String(buf, s);
        }
    }
}
