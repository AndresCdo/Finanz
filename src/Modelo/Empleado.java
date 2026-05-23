package Modelo;

/**
 * Clase abstracta que representa un empleado dentro del sistema Finanz.
 * Sirve como base para los diferentes tipos de empleados de la empresa.
 */
public abstract class Empleado {

    // ── Atributos ──────────────────────────────────────────────────────────────
    private int    id;
    private String nombre;
    private String documento;
    private String cargo;

    // ── Constructor ────────────────────────────────────────────────────────────
    public Empleado(int id, String nombre, String documento, String cargo) {
        this.id        = id;
        this.nombre    = nombre;
        this.documento = documento;
        this.cargo     = cargo;
    }

    // ── Método abstracto ───────────────────────────────────────────────────────
    /**
     * Calcula el salario del empleado según su modalidad de contratación.
     * @return monto del salario calculado
     */
    public abstract double calcularSalario();

    // ── Getters y Setters ──────────────────────────────────────────────────────
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }

    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }

    // ── toString ───────────────────────────────────────────────────────────────
    @Override
    public String toString() {
        return String.format("Modelo.Empleado{id=%d, nombre='%s', documento='%s', cargo='%s'}",
                id, nombre, documento, cargo);
    }
}