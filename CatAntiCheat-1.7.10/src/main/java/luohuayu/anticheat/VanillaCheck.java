package luohuayu.anticheat;

import luohuayu.anticheat.message.CPacketVanillaData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.SimpleResource;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class VanillaCheck {
    private static IResource resourceCache = null;

    public static void checkVanilla() {
        boolean isLighting = Minecraft.func_71410_x().field_71474_y.func_74296_a(GameSettings.Options.GAMMA) > 1.5;
        boolean isTransparentTexture = isTransparentTexture();
        CatAntiCheatMod.instance.networkChannel.sendToServer(new CPacketVanillaData(isLighting, isTransparentTexture));
    }

    private static boolean isTransparentTexture() {
        try {
            IResource resource = Minecraft.func_71410_x().func_110442_L().func_110536_a(new ResourceLocation("minecraft", "textures/blocks/stone.png"));
            if (resourceCache == resource) return false;

            if (resource instanceof SimpleResource) {
                BufferedImage png = ImageIO.read(resource.func_110527_b());

                int width = png.getWidth();
                int height = png.getHeight();

                int point = 0;
                for (int w = 0; w < width; w++) {
                    for (int h = 0; h < height; h++) {
                        final int color = png.getRGB(w, h);
                        if (color == 0) {
                            point++;
                        }
                    }
                }

                if ((float)point / (float) (width * height) > 0.5) {
                    return true;
                } else {
                    resourceCache = resource;
                    return false;
                }
            }
        } catch (Exception ignored) { }
        return false;
    }
}
