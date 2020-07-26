package luohuayu.anticheat;

import luohuayu.anticheat.message.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = "catanticheat", version = "1.2.6", name = "CatAntiCheat")
@SideOnly(Side.CLIENT)
public class CatAntiCheatMod {
    public static CatAntiCheatMod instance;
    public static int version = 2;
    public SimpleNetworkWrapper networkChannel;

    @Mod.EventHandler
    public void preInit(final FMLPreInitializationEvent event) {
        instance = this;
        MinecraftForge.EVENT_BUS.register(this);
        networkChannel = NetworkRegistry.INSTANCE.newSimpleChannel("CatAntiCheat");

        networkChannel.registerMessage(new AntiCheatPacketMessageHandler(), SPacketHello.class, 0, Side.CLIENT);
        networkChannel.registerMessage(new AntiCheatPacketMessageHandler(), SPacketFileCheck.class, 1, Side.CLIENT);
        networkChannel.registerMessage(new AntiCheatPacketMessageHandler(), SPacketClassCheck.class, 2, Side.CLIENT);
        networkChannel.registerMessage(new AntiCheatPacketMessageHandler(), SPacketScreenshot.class, 3, Side.CLIENT);
        networkChannel.registerMessage(new AntiCheatPacketMessageHandler(), CPacketHelloReply.class, 4, Side.SERVER);
        networkChannel.registerMessage(new AntiCheatPacketMessageHandler(), CPacketFileHash.class, 5, Side.SERVER);
        networkChannel.registerMessage(new AntiCheatPacketMessageHandler(), CPacketClassFound.class, 6, Side.SERVER);
        networkChannel.registerMessage(new AntiCheatPacketMessageHandler(), CPacketInjectDetect.class, 7, Side.SERVER);
        networkChannel.registerMessage(new AntiCheatPacketMessageHandler(), CPacketImageData.class, 8, Side.SERVER);
        networkChannel.registerMessage(new AntiCheatPacketMessageHandler(), SPacketDataCheck.class, 9, Side.CLIENT);
        networkChannel.registerMessage(new AntiCheatPacketMessageHandler(), CPacketVanillaData.class, 10, Side.SERVER);
    }

}
