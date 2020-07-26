package luohuayu.anticheat;

import io.netty.util.internal.ConcurrentSet;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.eventhandler.IEventListener;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class RuntimeInjectCheck {
    private final static Map<String, Class<?>> cachedClasses;
    private final static Map<Object, ArrayList<IEventListener>> listeners;
    private final static ConcurrentSet<String> injectClass = new ConcurrentSet<String>();

    static {
        cachedClasses = ReflectionHelper.getPrivateValue(LaunchClassLoader.class, (LaunchClassLoader) RuntimeInjectCheck.class.getClassLoader(), "cachedClasses");
        listeners = ReflectionHelper.getPrivateValue(EventBus.class, MinecraftForge.EVENT_BUS, "listeners");
    }

    public static ConcurrentSet<String> getInjectClass() {
        Collection<Class<?>> loadedClasses = cachedClasses.values();
        for (Object obj : listeners.keySet()) {
            Class<?> objClass = (obj.getClass() == Class.class ? (Class<?>)obj : obj.getClass());
            if (!loadedClasses.contains(objClass)) {
                addInjectClass(objClass.getName());
            }
        }
        return injectClass;
    }

    private static void addInjectClass(String name) {
        if (injectClass.size() < 100) {
            injectClass.add(name.length() < 100 ? name : name.substring(0, 100));
        }
    }
}
