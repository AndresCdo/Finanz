# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run

This is a plain IntelliJ IDEA project (Java 25, no Maven/Gradle). The only external dependency is `libs/flatlaf-3.5.1.jar`.

**Compile model + CLI entry point:**
```powershell
javac -cp "libs\flatlaf-3.5.1.jar" -d out\production\Finanz src\Modelo\*.java src\Main.java
```

**Compile GUI layer (requires model compiled first):**
```powershell
javac -cp "libs\flatlaf-3.5.1.jar;out\production\Finanz" -d out\UI src\UI\*.java
```

**Run CLI backend demo:**
```powershell
java -cp "out\production\Finanz" Main
```

**Run GUI application:**
```powershell
java -cp "libs\flatlaf-3.5.1.jar;out\UI;out\production\Finanz" UI.FinanzApp
```

CSV data files (`empleados.csv`, `transacciones.csv`) are written to the working directory at runtime (project root).

## Architecture

The project has two independent entry points and two source packages:

- `src/Main.java` — standalone CLI demo that exercises the full backend
- `src/UI/FinanzApp.java` — Swing GUI entry point; initialises FlatDarkLaf and opens `MainWindow`

### `Modelo` package — domain + persistence

Two inheritance hierarchies:

```
Empleado (abstract, calcularSalario())
  ├── EmpleadoFijo        — fixed salary
  └── EmpleadoPorHoras    — tarifaHora × horasTrabajadas
  
Transaccion (abstract)
  ├── Ingreso             — adds to balance
  └── Gasto               — subtracts from balance
```

`Empresa` is the aggregate root. It holds `List<Empleado>` and `List<Transaccion>` and exposes the three core business methods: `calcularBalance()`, `generarReporteNomina()` (polymorphic via `calcularSalario()`), and `filtrarTransacciones()` (overloaded: by date range or by category string).

`GestorArchivos` is a static-only class that reads/writes the two CSV files using `java.io` only (no external libraries). CSV format:
- Empleados: `FIJO,id,nombre,documento,cargo,salarioBase` or `HORAS,id,nombre,documento,cargo,tarifaHora,horasTrabajadas`
- Transacciones: `INGRESO|GASTO,id,descripcion,monto,yyyy-MM-dd,categoria`

Lines starting with `#` are skipped (header comments).

### `UI` package — Swing GUI

`MainWindow` owns a single `Empresa` instance (loaded from CSV on startup). It uses a `CardLayout` in the center with a hand-built sidebar for navigation. Each navigation button calls `CardLayout.show()` with a string key.

Current panel status:
- `DashboardPanel` — fully implemented; shows 4 metric cards, surplus/deficit alert, last 4 transactions, and first 4 employees. Refreshed on each Dashboard navigation via `refrescar(empresa)`.
- `EmpleadosPanel` — fully implemented; `JTable` with custom `ButtonRenderer`/`ButtonEditor` inner classes for the inline "Eliminar" button. Saves to CSV on every add/delete.
- `NominaPanel`, `IngresosPanel`, `GastosPanel`, `ReportesPanel` — placeholders ("En construcción").

### Key constraints from the spec (README.md)
- All attributes must be `private` with getters/setters; constructors must use `super()` in subclasses.
- Amounts (monto, salarioBase, tarifaHora) must reject negative values via `IllegalArgumentException`.
- `toString()` must be overridden on all domain entities.
- Persistence uses only `java.io` — no external DB libraries.
- JavaDoc is required on `Empresa`; basic exception handling across all classes.
