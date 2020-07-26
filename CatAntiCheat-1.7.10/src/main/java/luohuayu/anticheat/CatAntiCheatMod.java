package luohuayu.anticheat;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import luohuayu.anticheat.message.*;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = "catanticheat", version = "1.2.7", name = "CatAntiCheat")
@SideOnly(Side.CLIENT)
public class CatAntiCheatMod {
    public static CatAntiCheatMod instance;
    public static int version = 2;
    public SimpleNetworkWrapper networkChannel;

    static {
        RuntimeInjectCheck.monitorLaunchClassLoader();
    }

    @Mod.EventHandler
    public void preInit(final FMLPreInitializationEvent event) {
        instance = this;
        MinecraftForge.EVENT_BUS.register(this);
        networkChannel = NetworkRegistry.INSTANCE.newSimpleChannel("CatAntiCheat");

        networkChannel.registerMessage(new AntiCheatPacketMessageHandler.SPacketHelloHandler(), SPacketHello.class, 0, Side.CLIENT);
        networkChannel.registerMessage(new AntiCheatPacketMessageHandler.SPacketFileCheckHandler(), SPacketFileCheck.class, 1, Side.CLIENT);
        networkChannel.registerMessage(new AntiCheatPacketMessageHandler.SPacketClassCheckHandler(), SPacketClassCheck.class, 2, Side.CLIENT);
        networkChannel.registerMessage(new AntiCheatPacketMessageHandler.SPacketScreenshotHandler(), SPacketScreenshot.class, 3, Side.CLIENT);
        networkChannel.registerMessage(new AntiCheatPacketMessageHandler.CPacketHelloReplyHandler(), CPacketHelloReply.class, 4, Side.SERVER);
        networkChannel.registerMessage(new AntiCheatPacketMessageHandler.CPacketFileHashHandler(), CPacketFileHash.class, 5, Side.SERVER);
        networkChannel.registerMessage(new AntiCheatPacketMessageHandler.CPacketClassFoundHandler(), CPacketClassFound.class, 6, Side.SERVER);
        networkChannel.registerMessage(new AntiCheatPacketMessageHandler.CPacketInjectDetectHandler(), CPacketInjectDetect.class, 7, Side.SERVER);
        networkChannel.registerMessage(new AntiCheatPacketMessageHandler.CPacketImageDataHandler(), CPacketImageData.class, 8, Side.SERVER);
        networkChannel.registerMessage(new AntiCheatPacketMessageHandler.SPacketDataCheckHandler(), SPacketDataCheck.class, 9, Side.CLIENT);
        networkChannel.registerMessage(new AntiCheatPacketMessageHandler.CPacketVanillaDataHandler(), CPacketVanillaData.class, 10, Side.SERVER);
    }
}
