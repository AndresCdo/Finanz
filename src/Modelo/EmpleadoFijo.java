/**
 * Representa un empleado con salario fijo mensual.
 * Extiende de {@link Empleado}.
 */
public class EmpleadoFijo extends Empleado {

    // ── Atributo específico ────────────────────────────────────────────────────
    private double salarioBase;

    // ── Constructor ────────────────────────────────────────────────────────────
    public EmpleadoFijo(int id, String nombre, String documento, String cargo,
                        double salarioBase) {
        super(id, nombre, documento, cargo);
        if (salarioBase < 0) {
            throw new IllegalArgumentException("El salario base no puede ser negativo.");
        }
        this.salarioBase = salarioBase;
    }

    // ── Implementación del método abstracto ────────────────────────────────────
    @Override
    public double calcularSalario() {
        return salarioBase;
    }

    // ── Getters y Setters ──────────────────────────────────────────────────────
    public double getSalarioBase() { return salarioBase; }
    public void setSalarioBase(double salarioBase) {
        if (salarioBase < 0) {
            throw new IllegalArgumentException("El salario base no puede ser negativo.");
        }
        this.salarioBase = salarioBase;
    }

    // ── toString ───────────────────────────────────────────────────────────────
    @Override
    public String toString() {
        return String.format("EmpleadoFijo{id=%d, nombre='%s', cargo='%s', salarioBase=%.2f}",
                getId(), getNombre(), getCargo(), salarioBase);
    }
}

