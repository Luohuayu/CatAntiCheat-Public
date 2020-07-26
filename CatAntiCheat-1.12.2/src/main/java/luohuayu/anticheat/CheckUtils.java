package luohuayu.anticheat;

import net.minecraft.client.Minecraft;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraft.util.ScreenShotHelper;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URL;
import java.net.UnknownServiceException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.GZIPOutputStream;
import javax.imageio.ImageIO;

public class CheckUtils {
    public static List<String> checkClass(List<String> classList) {
        List<String> foundClass = new ArrayList<String>();
        for (String className : classList) {
            try {
                Class.forName(className);
                foundClass.add(className);
            } catch (ClassNotFoundException e) {
                try {
                    Class.forName(className, true, ClassLoader.getSystemClassLoader());
                    foundClass.add(className);
                } catch (ClassNotFoundException ignored) { }
            }
        }
        return foundClass;
    }

    public static Set<String> checkFile() {
        Set<String> fileHash = new HashSet<>();
        LaunchClassLoader lwClassloader = (LaunchClassLoader) CheckUtils.class.getClassLoader();
        for (URL source : lwClassloader.getSources()) {
            String hash = getFileHash(source);
            if (hash != null) fileHash.add(hash);
        }
        return fileHash;
    }

    public static byte[] screenshot() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            GZIPOutputStream gzipOutputStream = new GZIPOutputStream(out);
            BufferedImage bufferedImage = ScreenShotHelper.createScreenshot(Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight, Minecraft.getMinecraft().getFramebuffer());
            ImageIO.write(bufferedImage, "png", gzipOutputStream);
            gzipOutputStream.flush();
            gzipOutputStream.close();
        } catch (Exception ignored) {}
        return out.toByteArray();
    }

    private static String getFileHash(URL url) {
        String fileName = new File(url.getFile()).getName();
        try {
            try (InputStream in = url.openStream()) {
                return calcHash(in) + "\0" + fileName;
            }
        } catch (UnknownServiceException e) {
            return null;
        } catch (IOException e) {
            System.out.println(e.toString());
            return "0000000000000000000000000000000000000000\0" + (fileName.isEmpty() ? "unknown" : fileName);
        }
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
