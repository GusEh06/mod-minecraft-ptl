# ⏳ LimitTimer — Minecraft Fabric Mod

![Minecraft Version](https://img.shields.io/badge/Minecraft-1.21.1-brightgreen)
![Fabric](https://img.shields.io/badge/Loader-Fabric-blue)
![License](https://img.shields.io/badge/License-MIT-yellow)

**LimitTimer** es un mod desarrollado para servidores de Minecraft (Fabric 1.21.1) diseñado para gestionar el tiempo de juego diario de los usuarios. Es ideal para torneos, servidores survival con límites de tiempo o control parental.


## 🚀 Características Principales

* **Límite Diario Personalizable**: Configurado por defecto a una hora y media (5400 segundos).
* **Sistema de Acumulación**: Si un jugador no utiliza todo su tiempo, este se acumula para el día siguiente (con un tope máximo configurable).
* **Persistencia Robusta**: Los datos se guardan en tiempo real en archivos JSON para evitar pérdidas tras reinicios del servidor.
* **Configuración Dinámica**: Mensajes, colores del Scoreboard y tiempos editables sin necesidad de reiniciar el servidor.
* **Gestión por Consola**: Comandos accesibles tanto por administradores (OP) como desde la consola del servidor.

## 🛠️ Comandos y Permisos

| Comando | Descripción | Permiso |
|:---|:---|:---|
| `/lt add <player> <segundos>` | Suma o resta tiempo a un jugador. | Admin (Nivel 2) |
| `/lt reload` | Recarga la configuración del archivo JSON. | Admin (Nivel 2) |

> **Nota**: El comando `/lt add` también puede ejecutarse desde la consola de comandos del servidor sin el prefijo `/`.

## ⚙️ Configuración (`limittimer_config.json`)

El mod genera automáticamente un archivo en la carpeta `config/` del servidor. Ejemplo de personalización:

```json
{
  "tiempoDiarioSegundos": 5400,
  "maxAcumulacionSegundos": 16200,
  "acumulacionActiva": true,
  "tituloScoreboard": "§6§lTIEMPO RESTANTE",
  "mensajeExpulsion": "&c&l¡TIEMPO AGOTADO!\n\n&7Vuelve mañana para seguir jugando."
}
```

## 🏗️ Estructura del Proyecto

El desarrollo sigue buenas prácticas de programación modular:

* **ConfigManager**: Manejo de archivos de configuración JSON.
* **TimerManager**: Lógica de persistencia y cálculo de acumulación de días.
* **ScoreboardHandler**: Interfaz visual dinámica para el jugador.

