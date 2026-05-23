package Modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase principal gestora del sistema Finanz.
 *
 * <p>Representa una empresa PYME y centraliza la lógica de negocio:
 * administración de empleados, registro de transacciones financieras
 * y generación de reportes.</p>
 *
 * <p>Utiliza agregación para mantener colecciones de {@link Empleado}
 * y {@link Transaccion}, aprovechando el polimorfismo para calcular
 * salarios y balances de forma genérica.</p>
 *
 * @author  Finanz System
 * @version 1.0
 */
public class Empresa {

    // ── Atributos ──────────────────────────────────────────────────────────────
    private String           nombre;
    private String           nit;
    private List<Empleado>   empleados;
    private List<Transaccion> transacciones;

    // ── Constructor ────────────────────────────────────────────────────────────

    /**
     * Crea una nueva instancia de Modelo.Empresa.
     *
     * @param nombre nombre comercial de la empresa
     * @param nit    número de identificación tributaria
     */
    public Empresa(String nombre, String nit) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la empresa no puede estar vacío.");
        }
        if (nit == null || nit.isBlank()) {
            throw new IllegalArgumentException("El NIT no puede estar vacío.");
        }
        this.nombre        = nombre;
        this.nit           = nit;
        this.empleados     = new ArrayList<>();
        this.transacciones = new ArrayList<>();
    }

    // ── Gestión de Empleados ───────────────────────────────────────────────────

    /**
     * Agrega un empleado a la empresa.
     *
     * @param empleado instancia de {@link Empleado} a registrar
     * @throws IllegalArgumentException si el empleado es null
     */
    public void agregarEmpleado(Empleado empleado) {
        if (empleado == null) {
            throw new IllegalArgumentException("El empleado no puede ser null.");
        }
        empleados.add(empleado);
    }

    /**
     * Elimina un empleado de la empresa según su ID.
     *
     * @param id identificador del empleado a eliminar
     * @return {@code true} si fue eliminado, {@code false} si no se encontró
     */
    public boolean eliminarEmpleado(int id) {
        return empleados.removeIf(e -> e.getId() == id);
    }

    // ── Gestión de Transacciones ───────────────────────────────────────────────

    /**
     * Registra una transacción (ingreso o gasto) en la empresa.
     *
     * @param transaccion instancia de {@link Transaccion} a registrar
     * @throws IllegalArgumentException si la transacción es null
     */
    public void agregarTransaccion(Transaccion transaccion) {
        if (transaccion == null) {
            throw new IllegalArgumentException("La transacción no puede ser null.");
        }
        transacciones.add(transaccion);
    }

    // ── Métodos de Negocio ─────────────────────────────────────────────────────

    /**
     * Calcula el balance financiero de la empresa.
     *
     * <p>Recorre todas las transacciones: suma los {@link Ingreso} y
     * resta los {@link Gasto} para obtener el saldo neto.</p>
     *
     * @return balance total (ingresos − gastos)
     */
    public double calcularBalance() {
        double totalIngresos = 0;
        double totalGastos   = 0;

        for (Transaccion t : transacciones) {
            if (t instanceof Ingreso) {
                totalIngresos += t.getMonto();
            } else if (t instanceof Gasto) {
                totalGastos += t.getMonto();
            }
        }

        return totalIngresos - totalGastos;
    }

    /**
     * Genera un reporte de nómina completo de la empresa.
     *
     * <p>Itera la lista de empleados y aprovecha el polimorfismo de
     * {@code calcularSalario()} para obtener el costo total sin importar
     * el tipo concreto de empleado.</p>
     *
     * @return String con el detalle de cada empleado y el costo total
     */
    public String generarReporteNomina() {
        StringBuilder reporte = new StringBuilder();
        reporte.append("══════════════════════════════════════\n");
        reporte.append("   REPORTE DE NÓMINA - ").append(nombre).append("\n");
        reporte.append("══════════════════════════════════════\n");

        double costoTotal = 0;

        if (empleados.isEmpty()) {
            reporte.append("  No hay empleados registrados.\n");
        } else {
            for (Empleado e : empleados) {
                double salario = e.calcularSalario(); // polimorfismo
                costoTotal += salario;
                reporte.append(String.format("  %-25s | $%,.2f%n",
                        e.getNombre() + " (" + e.getCargo() + ")", salario));
            }
        }

        reporte.append("──────────────────────────────────────\n");
        reporte.append(String.format("  COSTO TOTAL NÓMINA: $%,.2f%n", costoTotal));
        reporte.append("══════════════════════════════════════\n");

        return reporte.toString();
    }

    /**
     * Filtra transacciones por rango de fechas.
     *
     * @param desde fecha de inicio (inclusive)
     * @param hasta fecha de fin (inclusive)
     * @return lista de transacciones dentro del rango
     * @throws IllegalArgumentException si {@code desde} es posterior a {@code hasta}
     */
    public List<Transaccion> filtrarTransacciones(LocalDate desde, LocalDate hasta) {
        if (desde.isAfter(hasta)) {
            throw new IllegalArgumentException("La fecha 'desde' no puede ser posterior a 'hasta'.");
        }
        List<Transaccion> resultado = new ArrayList<>();
        for (Transaccion t : transacciones) {
            if (!t.getFecha().isBefore(desde) && !t.getFecha().isAfter(hasta)) {
                resultado.add(t);
            }
        }
        return resultado;
    }

    /**
     * Filtra transacciones por categoría (sin distinguir mayúsculas).
     *
     * @param categoria nombre de la categoría a buscar
     * @return lista de transacciones que coincidan con la categoría
     */
    public List<Transaccion> filtrarTransacciones(String categoria) {
        if (categoria == null || categoria.isBlank()) {
            throw new IllegalArgumentException("La categoría no puede estar vacía.");
        }
        List<Transaccion> resultado = new ArrayList<>();
        for (Transaccion t : transacciones) {
            if (t.getCategoria().equalsIgnoreCase(categoria)) {
                resultado.add(t);
            }
        }
        return resultado;
    }

    // ── Getters y Setters ──────────────────────────────────────────────────────
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getNit() { return nit; }
    public void setNit(String nit) { this.nit = nit; }

    public List<Empleado> getEmpleados() { return new ArrayList<>(empleados); }
    public List<Transaccion> getTransacciones() { return new ArrayList<>(transacciones); }

    // ── toString ───────────────────────────────────────────────────────────────
    @Override
    public String toString() {
        return String.format("Modelo.Empresa{nombre='%s', nit='%s', empleados=%d, transacciones=%d, balance=$%,.2f}",
                nombre, nit, empleados.size(), transacciones.size(), calcularBalance());
    }
}
