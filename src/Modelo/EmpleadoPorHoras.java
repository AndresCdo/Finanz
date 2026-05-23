/**
 * Representa un empleado que cobra según las horas trabajadas.
 * Extiende de {@link Empleado}.
 */
public class EmpleadoPorHoras extends Empleado {

    // ── Atributos específicos ──────────────────────────────────────────────────
    private double tarifaHora;
    private double horasTrabajadas;

    // ── Constructor ────────────────────────────────────────────────────────────
    public EmpleadoPorHoras(int id, String nombre, String documento, String cargo,
                            double tarifaHora, double horasTrabajadas) {
        super(id, nombre, documento, cargo);
        if (tarifaHora < 0 || horasTrabajadas < 0) {
            throw new IllegalArgumentException("La tarifa y las horas no pueden ser negativas.");
        }
        this.tarifaHora      = tarifaHora;
        this.horasTrabajadas = horasTrabajadas;
    }

    // ── Implementación del método abstracto ────────────────────────────────────
    @Override
    public double calcularSalario() {
        return tarifaHora * horasTrabajadas;
    }

    // ── Getters y Setters ──────────────────────────────────────────────────────
    public double getTarifaHora() { return tarifaHora; }
    public void setTarifaHora(double tarifaHora) {
        if (tarifaHora < 0) throw new IllegalArgumentException("La tarifa no puede ser negativa.");
        this.tarifaHora = tarifaHora;
    }

    public double getHorasTrabajadas() { return horasTrabajadas; }
    public void setHorasTrabajadas(double horasTrabajadas) {
        if (horasTrabajadas < 0) throw new IllegalArgumentException("Las horas no pueden ser negativas.");
        this.horasTrabajadas = horasTrabajadas;
    }

    // ── toString ───────────────────────────────────────────────────────────────
    @Override
    public String toString() {
        return String.format(
                "EmpleadoPorHoras{id=%d, nombre='%s', cargo='%s', tarifaHora=%.2f, horas=%.1f, salario=%.2f}",
                getId(), getNombre(), getCargo(), tarifaHora, horasTrabajadas, calcularSalario());
    }
}
