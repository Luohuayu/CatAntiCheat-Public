package luohuayu.anticheat.asm;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import io.netty.util.internal.ConcurrentSet;
import net.minecraft.launchwrapper.IClassTransformer;


import io.netty.util.internal.ConcurrentSet;
import net.minecraft.launchwrapper.IClassTransformer;

public class AntiCheatTransformer implements IClassTransformer {
    private final static ConcurrentSet<String> injectClass = new ConcurrentSet<String>();
    private final static Map<URL, String> loadedFileHash = new ConcurrentHashMap<URL, String>();

    private static Method getURLsMethod;
    private static Object ucp;

    static {
        try {
            Field ucpField = URLClassLoader.class.getDeclaredField("ucp");
            ucpField.setAccessible(true);
            ucp = ucpField.get(AntiCheatTransformer.class.getClassLoader());
            getURLsMethod = Class.forName("sun.misc.URLClassPath").getMethod("getURLs");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null) return null;
        Thread currentThread = Thread.currentThread();
        if ("Attach Listener".equals(currentThread.getName())) {
            addInjectClass(currentThread.getClass().getName());
        } else if (currentThread.getClass().getName().startsWith("com.sun.proxy.$")) {
            addInjectClass(currentThread.getClass().getName());
        } else if (transformedName != null) {
            checkUCP();
        }
        return basicClass;
    }

    private static void addInjectClass(String name) {
        if (injectClass.size() < 100) {
            injectClass.add(name.length() < 100 ? name : name.substring(0, 100));
        }
    }

    public static ConcurrentSet<String> getInjectClass() {
        return injectClass;
    }

    public static Map<URL, String> getLoadedFileHash() {
        return loadedFileHash;
    }

    public static void checkUCP() {
        try {
            URL[] urls = (URL[]) getURLsMethod.invoke(ucp);
            for (URL url : urls) {
                if (loadedFileHash.containsKey(url)) continue;
                String hash = getFileHash(url);
                loadedFileHash.put(url, hash != null ? hash : "");
            }
        } catch (Exception ignored) { }
    }

    private static String getFileHash(URL url) {
        try {
            try (InputStream in = url.openStream()) {
                return calcHash(in) + "\0" + new File(url.getFile()).getName();
            }
        } catch (IOException ignored) { }
        return null;
    }

    private static String calcHash(InputStream in) throws IOException {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");

            final byte[] buffer = new byte[4096];
            int read = in.read(buffer, 0, 4096);

            while (read > -1) {
                md.update(buffer, 0, read);
                read = in.read(buffer, 0, 4096);
            }

            byte[] digest = md.digest();
            return String.format("%0" + (digest.length << 1) + "x", new BigInteger(1, digest)).toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
