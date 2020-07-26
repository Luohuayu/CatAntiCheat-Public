package luohuayu.anticheat.asm;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import java.util.*;

@IFMLLoadingPlugin.SortingIndex(32000)
public class AntiCheatCorePlugin implements IFMLLoadingPlugin
{
    public String[] getASMTransformerClass() {
        return new String[] { AntiCheatTransformer.class.getCanonicalName() };
    }

    public String getAccessTransformerClass() { return null; }

    public String getModContainerClass() { return null; }

    public String getSetupClass() { return null; }

    public void injectData(final Map<String, Object> arg0) { }
}
