package luohuayu.anticheat.plugin.message;

import io.netty.buffer.ByteBuf;
import luohuayu.anticheat.plugin.PlayerHandler;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class CPacketFileHash implements IPacket {
    private List<String> fileHashList;
    private byte salt;

    public IPacket onMessage(PlayerHandler player) {
        player.onFileCheck(fileHashList, salt);
        return null;
    }

    public void read(ByteBuf buf) throws IOException {
        int gzipSize = buf.readShort();
        byte[] gzipData = new byte[gzipSize];
        buf.readBytes(gzipData);

        GZIPInputStream gzipIn = new GZIPInputStream(new ByteArrayInputStream(gzipData));
        salt = (byte) gzipIn.read();

        BufferedReader reader = new BufferedReader(new InputStreamReader(gzipIn, StandardCharsets.UTF_8));
        fileHashList = new ArrayList<String>();
        String lineStr;
        while((lineStr = reader.readLine()) != null)
        {
            fileHashList.add(lineStr);
        }
        reader.close();

        if (Arrays.hashCode(gzipData) != buf.readInt()) fileHashList.clear();
    }

    public void write(ByteBuf buf) throws IOException { }

}
