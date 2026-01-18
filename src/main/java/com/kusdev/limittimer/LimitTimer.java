package com.kusdev.limittimer;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import java.io.File;
import java.nio.file.Path;
import net.minecraft.server.network.ServerPlayerEntity;

import static net.minecraft.server.command.CommandManager.*;

public class LimitTimer implements ModInitializer {

    private ConfigManager configManager;
    private TimerManager timerManager;
    private final ScoreboardHandler scoreboardHandler = new ScoreboardHandler();
    private int tickCounter = 0;

    @Override
    public void onInitialize() {
        // 1. Registro de Comandos
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> registerCommands(dispatcher));


        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            // Obtenemos la carpeta donde está el servidor (ej: /home/container/)
            File baseDir = server.getRunDirectory().toFile();

            // IMPORTANTE: Imprimimos para confirmar en el log de Exaroton
            System.out.println("[LimitTimer] RAÍZ DEL SERVIDOR: " + baseDir.getAbsolutePath());

            this.configManager = new ConfigManager(baseDir);
            this.timerManager = new TimerManager(baseDir);
        });


        // 3. Guardado preventivo al cerrar
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            if (timerManager != null) {
                timerManager.saveData();
                System.out.println("[LimitTimer] Persistencia final completada.");
            }
        });

        // 4. Bucle principal (cada 1 segundo)
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            tickCounter++;
            if (tickCounter >= 20) {
                actualizarLogicaGlobal(server);
                tickCounter = 0;
            }
        });
    }

    private void actualizarLogicaGlobal(MinecraftServer server) {
        ModConfig config = configManager.getConfig();

        server.getPlayerManager().getPlayerList().forEach(player -> {
            // La acumulación se verifica aquí (Punto 3)
            timerManager.checkAndAddAccumulatedTime(player, config);
            TimerData data = timerManager.getPlayerData(player.getUuid(), config.tiempoDiarioSegundos);

            if (data.timeLeft > 0) {
                data.timeLeft--;

                // Notificaciones (Punto 2)
                String currentSec = String.valueOf(data.timeLeft);
                if (config.notificationsMap.containsKey(currentSec)) {
                    String msg = config.notificationsMap.get(currentSec).replace("&", "§");
                    player.sendMessage(Text.literal(msg), false);
                    player.playSound(net.minecraft.sound.SoundEvents.BLOCK_NOTE_BLOCK_BELL.value(), 1.0f, 1.0f);
                }
            } else {
                // Usamos el mensaje del config y reemplazamos colores
                String kickMsg = config.mensajeExpulsion.replace("&", "§");
                player.networkHandler.disconnect(Text.literal(kickMsg));
            }

            scoreboardHandler.updateScoreboard(player, data.timeLeft, config);
        });

        // GUARDADO ÚNICO: Una vez por segundo para todos los cambios
        timerManager.saveData();
    }

    public void registerCommands(com.mojang.brigadier.CommandDispatcher<net.minecraft.server.command.ServerCommandSource> dispatcher) {
        dispatcher.register(literal("lt")
                .requires(source -> source.hasPermissionLevel(2))
                .then(literal("reload")
                        .executes(context -> {
                            configManager.loadConfig(); // Ahora sí existe
                            context.getSource().sendFeedback(() -> Text.literal("§a[LimitTimer] ¡Configuración recargada!"), true);
                            return 1;
                        })
                )
                        // Comando para AÑADIR/RESTAR tiempo
                        // Uso: /lt add <jugador> 60 (añade un minuto)
                        // Uso: /lt add <jugador> -60 (resta un minuto)
                .then(literal("add")
                        .then(argument("player", EntityArgumentType.player())
                                .then(argument("seconds", IntegerArgumentType.integer())
                                        .requires(source -> source.hasPermissionLevel(2)) // Permiso para admins y consola
                                        .executes(context -> {
                                            // Obtenemos el jugador objetivo del argumento
                                            ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "player");
                                            int seconds = IntegerArgumentType.getInteger(context, "seconds");
                                            ModConfig config = configManager.getConfig();

                                            TimerData data = timerManager.getPlayerData(target.getUuid(), config.tiempoDiarioSegundos);
                                            data.timeLeft += seconds;
                                            if (data.timeLeft < 0) data.timeLeft = 0;

                                            timerManager.saveData();

                                            // Feedback que funciona tanto en chat como en consola
                                            context.getSource().sendFeedback(() -> Text.literal(
                                                    config.mensajeComandoExito.replace("&", "§") + target.getName().getString()
                                            ), true);

                                            return 1;
                                        })
                                )
                        )
                )
        );
    }
}