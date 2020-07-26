package luohuayu.anticheat.plugin;

import io.netty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;
import java.util.Random;

public class Utils {
    public static int getTimestamp() {
        return (int) (System.currentTimeMillis() / 1000);
    }

    public static int readVarInt(ByteBuf buf, int maxSize) {
        int i = 0;
        int j = 0;

        byte b0;
        do {
            b0 = buf.readByte();
            i |= (b0 & 127) << j++ * 7;
            if (j > maxSize) {
                throw new RuntimeException("VarInt too big");
            }
        } while((b0 & 128) == 128);

        return i;
    }

    public static void writeVarInt(ByteBuf to, int toWrite) {
        while((toWrite & -128) != 0) {
            to.writeByte(toWrite & 127 | 128);
            toWrite >>>= 7;
        }

        to.writeByte(toWrite);
    }

    public static String readUTF8String(ByteBuf from) {
        int len = readVarInt(from, 2);
        String str = from.toString(from.readerIndex(), len, StandardCharsets.UTF_8);
        from.readerIndex(from.readerIndex() + len);
        return str;
    }

    public static void writeUTF8String(ByteBuf to, String string) {
        byte[] utf8Bytes = string.getBytes(StandardCharsets.UTF_8);
        writeVarInt(to, utf8Bytes.length);
        to.writeBytes(utf8Bytes);
    }

    public static byte randomByte() {
        byte[] bytes = new byte[1];
        new Random().nextBytes(bytes);
        return bytes[0];
    }

    public static int randomInt(int max) {
        return new Random().nextInt(max + 1);
    }
}
