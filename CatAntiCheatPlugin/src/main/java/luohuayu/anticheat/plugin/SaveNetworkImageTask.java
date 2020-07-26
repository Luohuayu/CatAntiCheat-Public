package luohuayu.anticheat.plugin;

import org.apache.commons.io.FileUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

public class SaveNetworkImageTask implements Runnable {
    private final File file;
    private final byte[] data;

    public SaveNetworkImageTask(File file, byte[] data) {
        this.file = file;
        this.data = data;
    }

    @Override
    public void run() {
        try {
            ByteArrayOutputStream imageOutputStream = new ByteArrayOutputStream();
            GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(data));
            byte[] buffer = new byte[256];
            int n;
            while ((n = gzipInputStream.read(buffer)) >= 0) {
                imageOutputStream.write(buffer, 0, n);
            }
            gzipInputStream.close();

            FileUtils.writeByteArrayToFile(file, imageOutputStream.toByteArray());
            imageOutputStream.close();
            System.out.println("截图已保存到 " + file.getAbsolutePath());
        } catch (IOException e) {
            CatAntiCheat.plugin.logInfo("[截图]保存截图 " + file.getName() + " 失败: " + e.toString() + " (玩家可能在破解反作弊)");
        }
    }
}
