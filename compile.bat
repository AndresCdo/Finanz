@echo off
cd /d "%~dp0"
echo Compilando archivos Java...
echo.

javac -cp libs\flatlaf-3.5.jar Empleado.java EmpleadoFijo.java EmpleadoPorHoras.java Transaccion.java Ingreso.java Gasto.java Empresa.java GestorArchivos.java Main.java FinanzApp.java MainWindow.java DashboardPanel.java IngresosPanel.java GastosPanel.java EmpleadosPanel.java NominaPanel.java ReportesPanel.java

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ✓ Compilación exitosa!
    echo.
    pause
) else (
    echo.
    echo ✗ Error en la compilación
    echo.
    pause
)