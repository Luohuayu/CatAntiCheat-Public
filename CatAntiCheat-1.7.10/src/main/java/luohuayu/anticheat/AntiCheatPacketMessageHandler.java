package luohuayu.anticheat;

import com.google.common.collect.Sets;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import luohuayu.anticheat.asm.AntiCheatTransformer;
import luohuayu.anticheat.message.*;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;

public class AntiCheatPacketMessageHandler {
    private static byte salt;

    public static class SPacketHelloHandler implements IMessageHandler<SPacketHello, IMessage> {
        public IMessage onMessage(final SPacketHello message, final MessageContext ctx) {
            if (!Minecraft.func_71410_x().func_152345_ab()) {
                Minecraft.func_71410_x().func_152344_a(new Runnable() {
                    @Override
                    public void run() {
                        onMessage(message, ctx);
                    }
                });
                return null;
            }

            salt = message.salt;
            CatAntiCheatMod.instance.networkChannel.sendToServer(new CPacketHelloReply(CatAntiCheatMod.version, salt));

            Set<String> injectClass =  Sets.union(AntiCheatTransformer.getInjectClass(), RuntimeInjectCheck.getInjectClass());
            if (injectClass.size() > 0) {
                CatAntiCheatMod.instance.networkChannel.sendToServer(new CPacketInjectDetect(new ArrayList<>(injectClass)));
            }
            return null;
        }
    }

    public static class SPacketFileCheckHandler implements IMessageHandler<SPacketFileCheck, IMessage> {
        public IMessage onMessage(final SPacketFileCheck message, final MessageContext ctx) {
            checkFileAsync();
            return null;
        }
    }

    public static class SPacketClassCheckHandler implements IMessageHandler<SPacketClassCheck, IMessage> {
        public IMessage onMessage(final SPacketClassCheck message, final MessageContext ctx) {
            if (!Minecraft.func_71410_x().func_152345_ab()) {
                Minecraft.func_71410_x().func_152344_a(new Runnable() {
                    @Override
                    public void run() {
                        onMessage(message, ctx);
                    }
                });
                return null;
            }

            CatAntiCheatMod.instance.networkChannel.sendToServer(new CPacketClassFound(CheckUtils.checkClass(message.classList), salt));
            return null;
        }
    }

    public static class SPacketScreenshotHandler implements IMessageHandler<SPacketScreenshot, IMessage> {
        public IMessage onMessage(final SPacketScreenshot message, final MessageContext ctx) {
            if (!Minecraft.func_71410_x().func_152345_ab()) {
                Minecraft.func_71410_x().func_152344_a(new Runnable() {
                    @Override
                    public void run() {
                        onMessage(message, ctx);
                    }
                });
                return null;
            }

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
                ctx.getClientHandler().func_147231_a(new ChatComponentText(e.toString()));
            }
            return null;
        }
    }

    public static class SPacketDataCheckHandler implements IMessageHandler<SPacketDataCheck, IMessage> {
        public IMessage onMessage(final SPacketDataCheck message, final MessageContext ctx) {
            if (!Minecraft.func_71410_x().func_152345_ab()) {
                Minecraft.func_71410_x().func_152344_a(new Runnable() {
                    @Override
                    public void run() {
                        onMessage(message, ctx);
                    }
                });
                return null;
            }

            VanillaCheck.checkVanilla();

            Set<String> injectClass = Sets.union(AntiCheatTransformer.getInjectClass(), RuntimeInjectCheck.getInjectClass());
            if (injectClass.size() > 0) {
                CatAntiCheatMod.instance.networkChannel.sendToServer(new CPacketInjectDetect(new ArrayList<>(injectClass)));
            }
            return null;
        }
    }

    public static class CPacketHelloReplyHandler implements IMessageHandler<CPacketHelloReply, IMessage> {
        public IMessage onMessage(final CPacketHelloReply message, final MessageContext ctx) { return null; }
    }

    public static class CPacketFileHashHandler implements IMessageHandler<CPacketFileHash, IMessage> {
        public IMessage onMessage(final CPacketFileHash message, final MessageContext ctx) { return null; }
    }

    public static class CPacketClassFoundHandler implements IMessageHandler<CPacketClassFound, IMessage> {
        public IMessage onMessage(final CPacketClassFound message, final MessageContext ctx) { return null; }
    }

    public static class CPacketInjectDetectHandler implements IMessageHandler<CPacketInjectDetect, IMessage> {
        public IMessage onMessage(final CPacketInjectDetect message, final MessageContext ctx) { return null; }
    }

    public static class CPacketImageDataHandler implements IMessageHandler<CPacketImageData, IMessage> {
        public IMessage onMessage(final CPacketImageData message, final MessageContext ctx) { return null; }
    }

    public static class CPacketVanillaDataHandler implements IMessageHandler<CPacketVanillaData, IMessage> {
        public IMessage onMessage(final CPacketVanillaData message, final MessageContext ctx) { return null; }
    }

    public static void checkFileAsync() {
        final Collection<String> loadedList = AntiCheatTransformer.getLoadedFileHash().values();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Set<String> inGameList = CheckUtils.checkFile();
                for (String s : loadedList) {
                    if (!s.isEmpty()) inGameList.add(s);
                }
                Minecraft.func_71410_x().func_152344_a(new Runnable() {
                    @Override
                    public void run() {
                        CatAntiCheatMod.instance.networkChannel.sendToServer(new CPacketFileHash(new ArrayList<>(inGameList), salt));
                    }
                });
            }
        }).start();
    }
}
