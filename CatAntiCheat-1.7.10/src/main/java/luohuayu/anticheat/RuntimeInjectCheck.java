package luohuayu.anticheat;

import catserver.server.remapper.ReflectionUtils;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.gameevent.TickEvent;
import io.netty.util.internal.ConcurrentSet;
import net.minecraft.launchwrapper.LaunchClassLoader;
import cpw.mods.fml.common.eventhandler.EventBus;
import cpw.mods.fml.common.eventhandler.IEventListener;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraftforge.common.MinecraftForge;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RuntimeInjectCheck {
    private final static ConcurrentSet<String> injectClass = new ConcurrentSet<String>();

    public static ConcurrentSet<String> getInjectClass() {
        MonitorHashMap<String, Class<?>> cachedClasses = ReflectionHelper.getPrivateValue(LaunchClassLoader.class, (LaunchClassLoader) RuntimeInjectCheck.class.getClassLoader(), "cachedClasses");
        Map<Object, ArrayList<IEventListener>> forgeListeners = ReflectionHelper.getPrivateValue(EventBus.class, MinecraftForge.EVENT_BUS, "listeners");
        Map<Object, ArrayList<IEventListener>> fmlListeners = ReflectionHelper.getPrivateValue(EventBus.class, FMLCommonHandler.instance().bus(), "listeners");

        Collection<Class<?>> loadedClasses = cachedClasses.values();
        for (Object obj : forgeListeners.keySet()) {
            Class<?> objClass = (obj.getClass() == Class.class ? (Class<?>)obj : obj.getClass());
            if (!loadedClasses.contains(objClass)) {
                addInjectClass(objClass.getName());
            }
        }

        for (String detectClass : cachedClasses.detectClasses) {
            addInjectClass(detectClass);
        }

        for (int i = 0; i < 4; i++) {
            for (IEventListener listener : new TickEvent.PlayerTickEvent(TickEvent.Phase.START, null).getListenerList().getListeners(i)) {
                if (!loadedClasses.contains(listener.getClass())) {
                    addInjectClass(listener.getClass().getName());
                }
            }
        }

        return injectClass;
    }

    private static void addInjectClass(String name) {
        if (injectClass.size() < 100) {
            injectClass.add(name.length() < 100 ? name : name.substring(0, 100));
        }
    }

    public static void monitorLaunchClassLoader() {
        try {
            Field fieldCachedClasses = LaunchClassLoader.class.getDeclaredField("cachedClasses");
            fieldCachedClasses.setAccessible(true);
            ConcurrentHashMap<String, Class<?>> cachedClasses = (ConcurrentHashMap<String, Class<?>>) fieldCachedClasses.get(RuntimeInjectCheck.class.getClassLoader());
            fieldCachedClasses.set(RuntimeInjectCheck.class.getClassLoader(), new MonitorHashMap<String, Class<?>>(cachedClasses));
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    private static boolean isCallFromLaunchClassLoader() {
        return ReflectionUtils.getCallerClass(4) == RuntimeInjectCheck.class.getClassLoader().getClass();
    }

    static class MonitorHashMap<K, V> extends ConcurrentHashMap<K, V> {
        private final ConcurrentHashMap<K, V> oldMap;
        protected final Set<String> detectClasses = new ConcurrentSet<>();

        public MonitorHashMap(ConcurrentHashMap<K, V> oldMap) {
            this.oldMap = oldMap;
        }

        @Override
        public int size() {
            return oldMap.size();
        }

        @Override
        public boolean isEmpty() {
            return oldMap.isEmpty();
        }

        @Override
        public boolean containsKey(Object key) {
            return oldMap.containsKey(key);
        }

        @Override
        public boolean containsValue(Object value) {
            return oldMap.containsValue(value);
        }

        @Override
        public V get(Object key) {
            return oldMap.get(key);
        }

        @Override
        public V put(K key, V value) {
            if (!isCallFromLaunchClassLoader()) detectClasses.add(key.toString());
            return oldMap.put(key, value);
        }

        @Override
        public V remove(Object key) {
            if (!isCallFromLaunchClassLoader()) detectClasses.add(key.toString());
            return oldMap.remove(key);
        }

        @Override
        public void putAll(Map<? extends K, ? extends V> m) {
            if (!isCallFromLaunchClassLoader()) detectClasses.add(m.toString());
            oldMap.putAll(m);
        }

        @Override
        public void clear() {
            oldMap.clear();
        }

        public Enumeration<K> keys() {
            return oldMap.keys();
        }

        @Override
        public KeySetView<K,V> keySet() {
            return oldMap.keySet();
        }

        @Override
        public Collection<V> values() {
            return oldMap.values();
        }

        @Override
        public Set<Entry<K, V>> entrySet() {
            return oldMap.entrySet();
        }
    }
}
