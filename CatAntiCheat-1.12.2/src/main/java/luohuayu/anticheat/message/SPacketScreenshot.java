package luohuayu.anticheat.message;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class SPacketScreenshot implements IMessage {
    public int timestamp;

    @Override
    public void fromBytes(ByteBuf buf) {
        timestamp = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) { }
}
