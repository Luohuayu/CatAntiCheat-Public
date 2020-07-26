package luohuayu.anticheat;

import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ScreenShotHelper;

import java.awt.image.BufferedImage;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class OpenGLHelper {
    public static BufferedImage createScreenshot(int width, int height, Framebuffer framebufferIn) {
        IntBuffer field_74293_b = ReflectionHelper.getPrivateValue(ScreenShotHelper.class, null, "field_74293_b");
        int[] field_74294_c = ReflectionHelper.getPrivateValue(ScreenShotHelper.class, null, "field_74294_c");
        if (OpenGlHelper.func_148822_b()) {
            width = framebufferIn.field_147622_a;
            height = framebufferIn.field_147620_b;
        }

        int k = width * height;
        if (field_74293_b == null || field_74293_b.capacity() < k) {
            field_74293_b = BufferUtils.createIntBuffer(k);
            ReflectionHelper.setPrivateValue(ScreenShotHelper.class, null, new int[k], "field_74294_c");
        }

        GL11.glPixelStorei(3333, 1);
        GL11.glPixelStorei(3317, 1);
        field_74293_b.clear();
        if (OpenGlHelper.func_148822_b()) {
            GL11.glBindTexture(3553, framebufferIn.field_147617_g);
            GL11.glGetTexImage(3553, 0, 32993, 33639, field_74293_b);
        } else {
            GL11.glReadPixels(0, 0, width, height, 32993, 33639, field_74293_b);
        }

        field_74293_b.get(field_74294_c);
        TextureUtil.func_147953_a(field_74294_c, width, height);
        BufferedImage bufferedimage = null;
        if (OpenGlHelper.func_148822_b()) {
            bufferedimage = new BufferedImage(framebufferIn.field_147621_c, framebufferIn.field_147618_d, 1);
            int l = framebufferIn.field_147620_b - framebufferIn.field_147618_d;

            for(int i1 = l; i1 < framebufferIn.field_147620_b; ++i1) {
                for(int j1 = 0; j1 < framebufferIn.field_147621_c; ++j1) {
                    bufferedimage.setRGB(j1, i1 - l, field_74294_c[i1 * framebufferIn.field_147622_a + j1]);
                }
            }
        } else {
            bufferedimage = new BufferedImage(width, height, 1);
            bufferedimage.setRGB(0, 0, width, height, field_74294_c, 0, width);
        }

        return bufferedimage;
    }
}
