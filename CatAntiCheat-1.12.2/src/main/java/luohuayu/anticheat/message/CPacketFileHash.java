package luohuayu.anticheat.message;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPOutputStream;

public class CPacketFileHash implements IMessage {
    private List<String> fileHashList;
    private byte salt;

    public CPacketFileHash() {}

    public CPacketFileHash(List<String> fileHashList, byte salt) {
        this.fileHashList = fileHashList;
        this.salt = salt;
    }

    @Override
    public void fromBytes(final ByteBuf buf) { }

    @Override
    public void toBytes(final ByteBuf buf) {
        if (fileHashList == null || fileHashList.size() == 0) throw new RuntimeException("Hash is empty");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            GZIPOutputStream gzipOutputStream = new GZIPOutputStream(out);
            gzipOutputStream.write(salt);

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(gzipOutputStream, StandardCharsets.UTF_8));
            for (int i = 0; i < fileHashList.size(); i++) {
                if (i > 0) writer.newLine();
                writer.write(fileHashList.get(i));
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        byte[] gzipData = out.toByteArray();

        buf.writeShort(gzipData.length);
        buf.writeBytes(gzipData);
        buf.writeInt(Arrays.hashCode(gzipData));
    }
}
