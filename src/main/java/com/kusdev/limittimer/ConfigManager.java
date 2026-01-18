package com.kusdev.limittimer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;

public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final File configFile;
    private ModConfig config;

    public ConfigManager(File gameDir) {
        // Usamos getAbsoluteFile() para asegurarnos de que la ruta sea /server y no una ruta vacía
        File base = gameDir.getAbsoluteFile();
        File configDir = new File(base, "config");

        // Cambiamos getPath() por getAbsolutePath() para ver la verdad en el log
        System.out.println("[LimitTimer] Buscando en: " + configDir.getAbsolutePath());

        if (!configDir.exists()) {
            configDir.mkdirs();
        }

        this.configFile = new File(configDir, "limittimer_config.json");
        loadConfig();
    }

    public void loadConfig() {
        try {
            if (configFile.exists()) {
                try (FileReader reader = new FileReader(configFile)) {
                    this.config = GSON.fromJson(reader, ModConfig.class);
                    if (this.config == null) throw new Exception("Archivo vacío o corrupto");
                    System.out.println("[LimitTimer] Configuración cargada desde disco.");
                }
            } else {
                System.out.println("[LimitTimer] Config no encontrada. Generando valores iniciales...");
                this.config = new ModConfig();
                saveConfig(); // Esto DEBERÍA crear el archivo ahora
            }
        } catch (Exception e) {
            System.err.println("[LimitTimer] Error cargando configuración: " + e.getMessage());
            this.config = new ModConfig();
            saveConfig();
        }
    }

    public void saveConfig() {
        try {
            // Aseguramos que la carpeta exista antes de intentar escribir
            File parent = configFile.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }

            try (FileWriter writer = new FileWriter(configFile)) {
                GSON.toJson(config, writer);
                writer.flush(); // Obliga al disco a guardar ahora mismo
                System.out.println("[LimitTimer] ¡ÉXITO! Configuración creada en: " + configFile.getPath());
            }
        } catch (IOException e) {
            System.err.println("[LimitTimer] ERROR FATAL: " + e.getMessage());
        }
    }

    public ModConfig getConfig() {
        return config;
    }
}
