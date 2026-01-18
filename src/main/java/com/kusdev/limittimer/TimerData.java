package com.kusdev.limittimer;

import java.time.LocalDate;
import java.util.UUID;

public class TimerData {
    public int timeLeft;           // Tiempo en ticks
    public String lastLoginDate;   // Guardamos como String para el JSON (ej: "2026-01-17")

    public TimerData(int defaultTime) {
        this.timeLeft = defaultTime;
        this.lastLoginDate = LocalDate.now().toString();
    }
}
