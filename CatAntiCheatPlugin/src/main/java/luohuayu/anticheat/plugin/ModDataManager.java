package luohuayu.anticheat.plugin;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ModDataManager {
    private File dataFile;
    private YamlConfiguration config;
    private List<String> modList;

    public ModDataManager(File dataFile) {
        this.dataFile = dataFile;
        this.config = YamlConfiguration.loadConfiguration(dataFile);
        modList = config.getStringList("modList");
    }

    public boolean save() {
        try {
            config.set("modList", modList);
            config.save(dataFile);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<String> getModList() {
        return modList;
    }

    public void setModList(List<String> modList) {
        this.modList = modList;
    }
}
