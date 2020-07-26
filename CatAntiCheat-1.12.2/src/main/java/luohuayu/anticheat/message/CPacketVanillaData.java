package luohuayu.anticheat.message;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class CPacketVanillaData implements IMessage {
    private boolean lighting;
    private boolean transparentTexture;

    public CPacketVanillaData(boolean lighting, boolean transparentTexture) {
        this.lighting = lighting;
        this.transparentTexture = transparentTexture;
    }

    @Override
    public void fromBytes(ByteBuf buf) { }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(lighting);
        buf.writeBoolean(transparentTexture);
    }
}
