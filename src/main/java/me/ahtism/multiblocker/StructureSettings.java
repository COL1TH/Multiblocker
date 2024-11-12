package me.ahtism.multiblocker;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class StructureSettings {
    private static final StructureSettings instance = new StructureSettings();

    private File file;
    private static YamlConfiguration config;

    private StructureSettings() {}

    public void load() {
        file = new File(Multiblocker.getInstance().getDataFolder(), "settings.yml");

        if (!file.exists()) {
            Multiblocker.getInstance().saveResource("settings.yml", false);
        }

        config = new YamlConfiguration();
        config.options().parseComments(true);

        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void set(String path, Object value) {
        config.set(path, value);
        save();
    }

    public static StructureSettings getInstance() {
        return instance;
    }

    public static YamlConfiguration getConfig() {
        return config;
    }
}
