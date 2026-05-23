import Modelo.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * Clase principal del sistema Finanz.
 * Demuestra el uso completo de todas las clases del proyecto.
 */
public class Main {

    public static void main(String[] args) {

        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║       SISTEMA FINANZ - v1.0          ║");
        System.out.println("╚══════════════════════════════════════╝\n");

        // ── 1. Crear la empresa ────────────────────────────────────────────────
        Empresa empresa = new Empresa("Tech PYME S.A.S", "900-123-456-7");
        System.out.println("✔ Modelo.Empresa creada: " + empresa.getNombre() + "\n");

        // ── 2. Crear empleados ─────────────────────────────────────────────────
        EmpleadoFijo ana   = new EmpleadoFijo(1, "Ana García",    "1001234567", "Gerente",          4_500_000);
        EmpleadoFijo     luis  = new EmpleadoFijo(2, "Luis Martínez", "1009876543", "Contador",         3_200_000);
        EmpleadoPorHoras sofia = new EmpleadoPorHoras(3, "Sofía Ríos", "1005551234", "Diseñadora", 35_000, 120);
        EmpleadoPorHoras juan  = new EmpleadoPorHoras(4, "Juan Pérez",  "1007778899", "Desarrollador", 45_000, 160);

        empresa.agregarEmpleado(ana);
        empresa.agregarEmpleado(luis);
        empresa.agregarEmpleado(sofia);
        empresa.agregarEmpleado(juan);

        System.out.println("── Empleados registrados ──────────────");
        for (Empleado e : empresa.getEmpleados()) {
            System.out.println("  " + e);
        }

        // ── 3. Registrar transacciones ─────────────────────────────────────────
        System.out.println("\n── Registrando transacciones ──────────");

        // Ingresos
        empresa.agregarTransaccion(new Ingreso(1, "Venta proyecto web",      15_000_000, LocalDate.of(2025, 1, 10), "Ventas"));
        empresa.agregarTransaccion(new Ingreso(2, "Consultoría empresarial",  8_500_000, LocalDate.of(2025, 2, 14), "Servicios"));
        empresa.agregarTransaccion(new Ingreso(3, "Mantenimiento mensual",    3_200_000, LocalDate.of(2025, 3,  1), "Servicios"));
        empresa.agregarTransaccion(new Ingreso(4, "Licencias de software",    6_000_000, LocalDate.of(2025, 3, 20), "Ventas"));

        // Gastos
        empresa.agregarTransaccion(new Gasto(5,  "Arriendo oficina",          2_500_000, LocalDate.of(2025, 1,  5), "Arriendo"));
        empresa.agregarTransaccion(new Gasto(6,  "Servicios públicos",          350_000, LocalDate.of(2025, 2,  5), "Servicios"));
        empresa.agregarTransaccion(new Gasto(7,  "Compra equipos",            5_800_000, LocalDate.of(2025, 2, 20), "Equipos"));
        empresa.agregarTransaccion(new Gasto(8,  "Papelería y suministros",     120_000, LocalDate.of(2025, 3, 10), "Suministros"));

        System.out.println("  " + empresa.getTransacciones().size() + " transacciones registradas.");

        // ── 4. Reporte de nómina (polimorfismo) ────────────────────────────────
        System.out.println("\n" + empresa.generarReporteNomina());

        // ── 5. Balance financiero ──────────────────────────────────────────────
        System.out.println("── Balance Financiero ─────────────────");
        double balance = empresa.calcularBalance();
        String estado  = balance >= 0 ? "✔ POSITIVO" : "✘ NEGATIVO";
        System.out.printf("  Balance neto: $%,.2f  [%s]%n%n", balance, estado);

        // ── 6. Filtrar transacciones por rango de fechas ───────────────────────
        System.out.println("── Transacciones de Febrero 2025 ──────");
        List<Transaccion> febrero = empresa.filtrarTransacciones(
                LocalDate.of(2025, 2, 1),
                LocalDate.of(2025, 2, 28));
        if (febrero.isEmpty()) {
            System.out.println("  Sin resultados.");
        } else {
            for (Transaccion t : febrero) System.out.println("  " + t);
        }

        // ── 7. Filtrar transacciones por categoría ─────────────────────────────
        System.out.println("\n── Transacciones categoría 'Servicios' ");
        List<Transaccion> servicios = empresa.filtrarTransacciones("Servicios");
        if (servicios.isEmpty()) {
            System.out.println("  Sin resultados.");
        } else {
            for (Transaccion t : servicios) System.out.println("  " + t);
        }

        // ── 8. Persistencia: guardar en CSV ────────────────────────────────────
        System.out.println("\n── Guardando datos en archivos CSV ────");
        try {
            GestorArchivos.guardarEmpresa(empresa);
            System.out.println("  ✔ Archivos guardados correctamente.");
        } catch (IOException e) {
            System.err.println("  ✘ Error al guardar: " + e.getMessage());
        }

        // ── 9. Persistencia: cargar desde CSV ─────────────────────────────────
        System.out.println("\n── Cargando datos desde archivos CSV ──");
        Empresa empresaCargada = new Empresa("Tech PYME S.A.S", "900-123-456-7");
        try {
            GestorArchivos.cargarEmpresa(empresaCargada);
            System.out.printf("  ✔ Empleados cargados:     %d%n", empresaCargada.getEmpleados().size());
            System.out.printf("  ✔ Transacciones cargadas: %d%n", empresaCargada.getTransacciones().size());
            System.out.printf("  ✔ Balance verificado:     $%,.2f%n", empresaCargada.calcularBalance());
        } catch (IOException e) {
            System.err.println("  ✘ Error al cargar: " + e.getMessage());
        }

        // ── 10. Prueba de validaciones ─────────────────────────────────────────
        System.out.println("\n── Prueba de validaciones ─────────────");
        try {
            new EmpleadoFijo(99, "Test", "000", "Prueba", -1000);
        } catch (IllegalArgumentException e) {
            System.out.println("  ✔ Salario negativo rechazado: " + e.getMessage());
        }

        try {
            new Ingreso(99, "Test", -500, LocalDate.now(), "Error");
        } catch (IllegalArgumentException e) {
            System.out.println("  ✔ Monto negativo rechazado:   " + e.getMessage());
        }

        try {
            empresa.filtrarTransacciones(LocalDate.of(2025, 5, 1), LocalDate.of(2025, 1, 1));
        } catch (IllegalArgumentException e) {
            System.out.println("  ✔ Rango de fechas inválido:   " + e.getMessage());
        }

        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║        FIN DE LA DEMOSTRACIÓN        ║");
        System.out.println("╚══════════════════════════════════════╝");
    }
}
