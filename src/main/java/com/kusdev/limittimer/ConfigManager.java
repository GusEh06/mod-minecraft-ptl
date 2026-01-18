package com.kusdev.limittimer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;

public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final File configFile;
    private ModConfig config;

    public ConfigManager(File gameDir) {
        // Esto nos dirá en la consola de IntelliJ dónde se está intentando crear
        File configDir = new File(gameDir, "config");
        System.out.println("[LimitTimer] Buscando carpeta de configuración en: " + configDir.getAbsolutePath());

        if (!configDir.exists()) {
            boolean created = configDir.mkdirs();
            System.out.println("[LimitTimer] ¿Se creó la carpeta config?: " + created);
        }

        this.configFile = new File(configDir, "limittimer_config.json");
        loadConfig();
    }

    public void loadConfig() {
        if (configFile.exists()) {
            try (FileReader reader = new FileReader(configFile)) {
                config = GSON.fromJson(reader, ModConfig.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            config = new ModConfig();
            saveConfig();
        }
    }

    public void saveConfig() {
        try (FileWriter writer = new FileWriter(configFile)) {
            GSON.toJson(config, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ModConfig getConfig() {
        return config;
    }
}
