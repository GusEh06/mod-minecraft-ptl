package com.kusdev.limittimer;

import net.minecraft.scoreboard.*;
import net.minecraft.scoreboard.number.BlankNumberFormat;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ScoreboardHandler {

    private static final String OBJECTIVE_NAME = "limittimer_sidebar";
    private final Map<UUID, String> lastSentLines = new HashMap<>();

    public void updateScoreboard(ServerPlayerEntity player, int secondsLeft, ModConfig config) {
        Scoreboard scoreboard = player.getScoreboard();
        ScoreboardObjective objective = scoreboard.getNullableObjective(OBJECTIVE_NAME);

        // Si el objetivo no existe, lo creamos
        if (objective == null) {
            objective = scoreboard.addObjective(
                    OBJECTIVE_NAME,
                    ScoreboardCriterion.DUMMY,
                    Text.literal(config.tituloScoreboard.replace("&", "§")), // Soporte para colores en el título
                    ScoreboardCriterion.RenderType.INTEGER,
                    false,
                    BlankNumberFormat.INSTANCE // Formato por defecto para el objetivo
            );
        }

        // Limpiamos las puntuaciones anteriores para evitar duplicados al cambiar el tiempo
        for (ScoreHolder holder : scoreboard.getKnownScoreHolders()) {
            scoreboard.removeScore(holder, objective);
        }

        // Aseguramos que se muestre en la barra lateral (Sidebar)
        scoreboard.setObjectiveSlot(ScoreboardDisplaySlot.SIDEBAR, objective);

        // --- 1. LÍNEA DEL TIEMPO ---
        String timeLine = config.prefijoLinea.replace("&", "§") + formatTime(secondsLeft);
        var timeScore = scoreboard.getOrCreateScore(ScoreHolder.fromName(timeLine), objective);

        // Esta es la clave para 1.21.1: oculta el número rojo de esta línea específica
        timeScore.setNumberFormat(BlankNumberFormat.INSTANCE);
        timeScore.setScore(100);

        // --- 2. LÍNEAS EXTRA DEL JSON ---
        int currentScore = 99;
        if (config.lineasExtra != null) {
            for (String extraLine : config.lineasExtra) {
                String formatted = extraLine.replace("&", "§");
                var extraScore = scoreboard.getOrCreateScore(ScoreHolder.fromName(formatted), objective);

                // También ocultamos el número para las líneas extra
                extraScore.setNumberFormat(BlankNumberFormat.INSTANCE);
                extraScore.setScore(currentScore);

                currentScore--; // Restamos para que aparezcan debajo de la anterior
            }
        }
    }

    private String formatTime(int totalSeconds) {
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        // Formato profesional: 1h 30m 00s
        return String.format("%dh %dm %ds", hours, minutes, seconds);
    }
}