package com.kusdev.limittimer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModConfig {
    public int tiempoDiarioSegundos = 5400;
    public int maxAcumulacionSegundos = 16200;
    public boolean acumulacionActiva = true; // <--- ESTA ES LA QUE FALTA

    public String tituloScoreboard = "§6§lTIEMPO RESTANTE";
    public String prefijoLinea = "§fQuedan: §e";
    public String mensajeExpulsion = "&c&l¡TIEMPO AGOTADO!\n\n&7Vuelve mañana para seguir jugando.";
    public String mensajeComandoExito = "&a[LimitTimer] &7Tiempo ajustado para &e";
    public String mensajeSoloJugadores = "Este comando solo puede ser usado por jugadores.";
    public String mensajeSinPermiso = "&cNo tienes permiso para usar este comando.";

    public List<String> lineasExtra = new ArrayList<>();
    public Map<String, String> notificationsMap = new HashMap<>();


    public ModConfig() {
        // Usamos '&' para que el admin pueda editarlo fácil en el JSON
        lineasExtra.add("&7-----------------");
        lineasExtra.add("&b¡Suerte en tu partida!");
        lineasExtra.add("&7-----------------");

        notificationsMap.put("1800", "&6[!] &e30 minutos restantes.");
        notificationsMap.put("600", "&c[!] &610 minutos restantes.");
        notificationsMap.put("300", "&4[!] &l¡ALERTA! &e5 minutos restantes.");
    }
}