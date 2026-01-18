package com.kusdev.limittimer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;

public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final File configFile;
    private ModConfig config;

    public ConfigManager(File gameDir) {
        // Al usar el objeto gameDir, Java sabe que es "dentro" de la carpeta del servidor
        File configDir = new File(gameDir, "config");

        // Imprimimos para verificar (verás que ya no sale la / sola)
        System.out.println("[LimitTimer] Buscando en: " + configDir.getAbsolutePath());

        if (!configDir.exists()) {
            configDir.mkdirs();
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
