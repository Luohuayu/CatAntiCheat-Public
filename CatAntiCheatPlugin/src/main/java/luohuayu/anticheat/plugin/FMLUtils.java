package luohuayu.anticheat.plugin;

import java.util.ArrayList;

public class FMLUtils {
    private static String[] fmlClasses = new String[] { "net.minecraft.launchwrapper.ITweaker" , "net.minecraft.launchwrapper.LaunchClassLoader"};

    static {
        ArrayList<String> modClasses = new ArrayList<>();

        try {
            Class.forName("ic2.core.IC2");
            modClasses.add("ic2.core.IC2");
        } catch (Exception ignored) {}

        try {
            Class.forName("noppes.npcs.CustomNpcs");
            modClasses.add("noppes.npcs.CustomNpcs");
        } catch (Exception ignored) {}

        try {
            Class.forName("slimeknights.tconstruct.TConstruct");
            modClasses.add("slimeknights.tconstruct.TConstruct");
        } catch (Exception ignored) {}

        try {
            Class.forName("mekanism.common.Mekanism");
            modClasses.add("mekanism.common.Mekanism");
        } catch (Exception ignored) {}

        try {
            Class.forName("com.pixelmonmod.pixelmon.Pixelmon");
            modClasses.add("com.pixelmonmod.pixelmon.Pixelmon");
        } catch (Exception ignored) {}

        try {
            Class.forName("cpw.mods.ironchest.IronChest");
            modClasses.add("cpw.mods.ironchest.IronChest");
        } catch (Exception ignored) {}

        if (modClasses.size() > 0) {
            fmlClasses = modClasses.toArray(new String[0]);
        }
    }

    public static String getFmlClass() {
        return fmlClasses[Utils.randomInt(fmlClasses.length - 1)];
    }
}
