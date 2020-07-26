package luohuayu.anticheat;

import com.google.common.collect.Sets;
import luohuayu.anticheat.asm.AntiCheatTransformer;
import luohuayu.anticheat.message.*;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

public class AntiCheatPacketMessageHandler implements IMessageHandler<IMessage, IMessage> {
    private static byte salt;

    @Override
    public IMessage onMessage(IMessage message, MessageContext ctx) {
        if (!Minecraft.getMinecraft().isCallingFromMinecraftThread()) { Minecraft.getMinecraft().addScheduledTask(() -> onMessage(message, ctx)); return null;}
        if (message instanceof SPacketHello) {
            salt = ((SPacketHello) message).salt;
            CatAntiCheatMod.instance.networkChannel.sendToServer(new CPacketHelloReply(CatAntiCheatMod.version, salt));

            Set<String> injectClass = Sets.union(AntiCheatTransformer.getInjectClass(), RuntimeInjectCheck.getInjectClass());
            if (injectClass.size() > 0) {
                CatAntiCheatMod.instance.networkChannel.sendToServer(new CPacketInjectDetect(new ArrayList<>(injectClass)));
            }
            RuntimeInjectCheck.getInjectClass();
        } else if (message instanceof SPacketFileCheck) {
            checkFileAsync();
        } else if (message instanceof SPacketClassCheck) {
            SPacketClassCheck packet = (SPacketClassCheck) message;
            CatAntiCheatMod.instance.networkChannel.sendToServer(new CPacketClassFound(CheckUtils.checkClass(packet.classList), salt));
        } else if (message instanceof SPacketScreenshot) {
            ByteArrayInputStream in = new ByteArrayInputStream(CheckUtils.screenshot());
            try {
                byte[] networkData = new byte[32763];
                int size;
                while ((size = in.read(networkData)) >= 0) {
                    if (networkData.length == size) {
                        CatAntiCheatMod.instance.networkChannel.sendToServer(new CPacketImageData(in.available() == 0, networkData));
                    } else {
                        CatAntiCheatMod.instance.networkChannel.sendToServer(new CPacketImageData(in.available() == 0, Arrays.copyOf(networkData, size)));
                    }
                }
            } catch (IOException e) {
                ctx.getClientHandler().onDisconnect(new TextComponentString(e.toString()));
            }
        } else if (message instanceof SPacketDataCheck) {
            VanillaCheck.checkVanilla();

            Set<String> injectClass = Sets.union(AntiCheatTransformer.getInjectClass(), RuntimeInjectCheck.getInjectClass());
            if (injectClass.size() > 0) {
                CatAntiCheatMod.instance.networkChannel.sendToServer(new CPacketInjectDetect(new ArrayList<>(injectClass)));
            }
        }
        return null;
    }

    public void checkFileAsync() {
        Collection<String> loadedList = AntiCheatTransformer.getLoadedFileHash().values();
        new Thread(() -> {
            final Set<String> inGameList = CheckUtils.checkFile();
            for (String s : loadedList) {
                if (!s.isEmpty()) inGameList.add(s);
            }
            Minecraft.getMinecraft().addScheduledTask(() -> CatAntiCheatMod.instance.networkChannel.sendToServer(new CPacketFileHash(new ArrayList<>(inGameList), salt)));
        }).start();
    }
}
