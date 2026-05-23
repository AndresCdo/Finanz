# Finanz - Interfaz Gráfica (GUI)

## 📋 Requisitos

La interfaz gráfica está construida con **Swing + FlatLaf** para un diseño moderno y responsivo.

### Dependencias necesarias:
- **JDK 25** o superior
- **FlatLaf** (Look and Feel moderno)

## 🔧 Instalación de FlatLaf

### Opción 1: Descargar FlatLaf JAR (Recomendado)

1. Descarga `flatlaf-3.5.jar` desde:
   ```
   https://www.formdev.com/flatlaf/
   ```

2. Coloca el JAR en una carpeta `libs/` en la raíz del proyecto:
   ```
   proyecto/
   ├── src/
   ├── libs/
   │   └── flatlaf-3.5.jar
   ├── *.java
   ```

### Opción 2: Compilar sin instalar (usando Maven)

Si tienes Maven instalado, puedes compilar así:

```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="FinanzApp"
```

## 🚀 Compilación y Ejecución

### Opción 1: Compilación manual con classpath

```bash
# Compilar (desde la carpeta raíz del proyecto)
javac -cp libs/flatlaf-3.5.jar *.java

# Ejecutar
java -cp libs/flatlaf-3.5.jar:. FinanzApp
```

**En Windows:**
```bash
javac -cp libs\flatlaf-3.5.jar *.java
java -cp libs\flatlaf-3.5.jar;. FinanzApp
```

### Opción 2: Crear archivo batch/shell

**Linux/Mac (run.sh):**
```bash
#!/bin/bash
javac -cp libs/flatlaf-3.5.jar *.java
java -cp libs/flatlaf-3.5.jar:. FinanzApp
```

**Windows (run.bat):**
```bat
@echo off
javac -cp libs\flatlaf-3.5.jar *.java
java -cp libs\flatlaf-3.5.jar;. FinanzApp
```

## 📁 Estructura del proyecto

```
finanz/
├── src/
│   ├── modelo/
│   │   ├── Empleado.java
│   │   ├── EmpleadoFijo.java
│   │   ├── EmpleadoPorHoras.java
│   │   ├── Transaccion.java
│   │   ├── Ingreso.java
│   │   ├── Gasto.java
│   │   ├── Empresa.java
│   │   └── GestorArchivos.java
│   │
│   ├── gui/
│   │   ├── FinanzApp.java
│   │   ├── MainWindow.java
│   │   ├── DashboardPanel.java
│   │   ├── IngresosPanel.java
│   │   ├── GastosPanel.java
│   │   ├── EmpleadosPanel.java
│   │   ├── NominaPanel.java
│   │   ├── ReportesPanel.java
│   │   └── PanelPlaceholders.java
│   │
│   └── datos/
│       ├── empleados.csv
│       └── transacciones.csv
│
├── libs/
│   └── flatlaf-3.5.jar
│
└── README.md
```

## 🎨 Características de la GUI

### Dashboard
- **Cards de métricas**: Ingresos, Gastos, Costo nómina, Balance
- **Alerta de estado**: Indicador de superávit/déficit
- **Últimos movimientos**: Lista de transacciones recientes
- **Empleados activos**: Vista rápida del equipo

### Navegación
- **Sidebar colapsable**: Optimizado para diferentes tamaños de pantalla
- **Pestañas**: Dashboard, Ingresos, Gastos, Empleados, Nómina, Reportes
- **Responsive**: Se adapta a diferentes resoluciones

### Tema
- **Oscuro moderno**: Basado en FlatLaf Dark
- **Paleta de colores**: Grises oscuros + acentos verdes/rojos/naranjas
- **Tipografía**: Segoe UI (profesional y limpia)

## 🔄 Siguiente paso

Una vez que la GUI esté funcionando, necesitas:

1. **Conectar los datos**: Modificar los paneles para cargar datos desde `Empresa`
2. **Generar CSV de prueba**: Ejecutar `Main.java` para crear empleados.csv y transacciones.csv
3. **Cargar datos en GUI**: Implementar métodos para inyectar `Empresa` en los paneles

## 📝 Notas

- FlatLaf es **100% Java** - no requiere instalación externa
- Los colores y fuentes se pueden customizar en cada panel
- La GUI es completamente responsiva gracias a `BorderLayout` y `BoxLayout`
- Los "placeholders" pueden ser reemplazados con componentes funcionales después

---

**¿Preguntas?** Revisa la documentación oficial de FlatLaf en https://www.formdev.com/flatlaf/