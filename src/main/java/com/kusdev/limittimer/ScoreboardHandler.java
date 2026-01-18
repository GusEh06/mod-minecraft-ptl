package com.kusdev.limittimer;

import net.minecraft.scoreboard.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ScoreboardHandler {

    private static final String OBJECTIVE_NAME = "limittimer_sidebar";
    // Guardamos el último texto enviado a cada jugador para poder borrarlo
    private final Map<UUID, String> lastSentLines = new HashMap<>();

    // Añadimos ModConfig config al final de los parámetros
    public void updateScoreboard(ServerPlayerEntity player, int secondsLeft, ModConfig config) {
        Scoreboard scoreboard = player.getScoreboard();
        ScoreboardObjective objective = scoreboard.getNullableObjective(OBJECTIVE_NAME);

        if (objective == null) {
            objective = scoreboard.addObjective(
                    OBJECTIVE_NAME,
                    ScoreboardCriterion.DUMMY,
                    Text.literal(config.tituloScoreboard),
                    ScoreboardCriterion.RenderType.INTEGER,
                    false,
                    net.minecraft.scoreboard.number.BlankNumberFormat.INSTANCE
            );
        }

        // Limpiamos todo para redibujar
        for (ScoreHolder holder : scoreboard.getKnownScoreHolders()) {
            scoreboard.removeScore(holder, objective);
        }

        scoreboard.setObjectiveSlot(ScoreboardDisplaySlot.SIDEBAR, objective);

        // 1. Línea del tiempo (Score 100 para que esté arriba)
        String timeLine = config.prefijoLinea + formatTime(secondsLeft);
        scoreboard.getOrCreateScore(ScoreHolder.fromName(timeLine.replace("&", "§")), objective).setScore(100);

        // 2. Líneas extra del JSON
        int currentScore = 99;
        for (String extraLine : config.lineasExtra) {
            String formatted = extraLine.replace("&", "§");
            scoreboard.getOrCreateScore(ScoreHolder.fromName(formatted), objective).setScore(currentScore);
            currentScore--; // Bajamos el score para la siguiente línea
        }
    }

    private String formatTime(int totalSeconds) {
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;
        return String.format("%dh %dm %ds", hours, minutes, seconds);
    }
}
