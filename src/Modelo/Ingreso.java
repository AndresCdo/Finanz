import java.time.LocalDate;
class Ingreso extends Transaccion {

    public Ingreso(int id, String descripcion, double monto,
                   LocalDate fecha, String categoria) {
        super(id, descripcion, monto, fecha, categoria);
    }

    @Override
    public String toString() {
        return String.format("Ingreso{id=%d, descripcion='%s', monto=+%.2f, fecha=%s, categoria='%s'}",
                getId(), getDescripcion(), getMonto(), getFecha(), getCategoria());
    }
}