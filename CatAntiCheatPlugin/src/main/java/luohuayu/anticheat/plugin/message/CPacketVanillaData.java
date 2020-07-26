package luohuayu.anticheat.plugin.message;

import io.netty.buffer.ByteBuf;
import luohuayu.anticheat.plugin.PlayerHandler;

import java.io.IOException;

public class CPacketVanillaData implements IPacket {
    private boolean lighting;
    private boolean transparentTexture;

    public IPacket onMessage(PlayerHandler player) {
        player.onVanillaData(lighting, transparentTexture);
        return null;
    }

    public void read(ByteBuf buf) throws IOException {
        lighting = buf.readBoolean();
        transparentTexture = buf.readBoolean();
    }

    public void write(ByteBuf buf) throws IOException { }
}
