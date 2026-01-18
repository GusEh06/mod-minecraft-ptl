package com.kusdev.limittimer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.server.network.ServerPlayerEntity;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.nio.file.Path;

public class TimerManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final File saveFile;
    // Mapa que guarda el UUID del jugador y sus datos
    private Map<UUID, TimerData> playerDataMap = new HashMap<>();

    public TimerManager(File gameDir) {
        // En lugar de "config/archivo.json", construimos la ruta por piezas
        Path configPath = gameDir.toPath().resolve("config");
        File configDir = configPath.toFile();

        if (!configDir.exists()) {
            configDir.mkdirs();
        }

        // Esta forma garantiza que no haya una "/" al inicio que confunda a Linux
        this.saveFile = configPath.resolve("limittimer_players.json").toFile();

        System.out.println("[LimitTimer] Archivo de datos asignado en: " + saveFile.getPath());
        loadData();
    }

    public void checkAndAddAccumulatedTime(ServerPlayerEntity player, ModConfig config) {
        UUID uuid = player.getUuid();
        TimerData data = getPlayerData(uuid, config.tiempoDiarioSegundos);

        LocalDate lastLogin = LocalDate.parse(data.lastLoginDate);
        LocalDate today = LocalDate.now();

        // Calculamos la diferencia de días
        long daysPassed = ChronoUnit.DAYS.between(lastLogin, today);

        if (daysPassed > 0 && config.acumulacionActiva) {
            int extraTime = (int) (daysPassed * config.tiempoDiarioSegundos);
            data.timeLeft += extraTime;

            if (data.timeLeft > config.maxAcumulacionSegundos) {
                data.timeLeft = config.maxAcumulacionSegundos;
            }

            data.lastLoginDate = today.toString();
            saveData();
        }
    }

    // Obtener datos de un jugador o crear nuevos si es la primera vez
    public TimerData getPlayerData(UUID uuid, int defaultTime) {
        return playerDataMap.computeIfAbsent(uuid, k -> new TimerData(defaultTime));
    }

    public void saveData() {
        // El FileWriter con try-with-resources asegura que el archivo se cierre
        try (java.io.FileWriter writer = new java.io.FileWriter(saveFile)) {
            GSON.toJson(playerDataMap, writer);
            writer.flush(); // ESTO obliga al sistema a escribir en el disco duro REAL
        } catch (java.io.IOException e) {
            System.err.println("[LimitTimer] Error crítico guardando datos: " + e.getMessage());
        }
    }

    private void loadData() {
        if (!saveFile.exists()) return;

        try (FileReader reader = new FileReader(saveFile)) {
            java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<HashMap<UUID, TimerData>>(){}.getType();
            HashMap<UUID, TimerData> loadedData = GSON.fromJson(reader, type);
            if (loadedData != null) {
                this.playerDataMap = loadedData;
                System.out.println("[LimitTimer] Datos cargados: " + playerDataMap.size() + " jugadores.");
            }
        } catch (IOException e) {
            System.err.println("[LimitTimer] Fallo al cargar: " + e.getMessage());
        }
    }
}
