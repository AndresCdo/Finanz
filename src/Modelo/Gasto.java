package Modelo;/* Representa un gasto financiero. Su monto resta al balance total de la empresa.
 */
import java.time.LocalDate;
public class Gasto extends Transaccion {

    public Gasto(int id, String descripcion, double monto,
                 LocalDate fecha, String categoria) {
        super(id, descripcion, monto, fecha, categoria);
    }

    @Override
    public String toString() {
        return String.format("Modelo.Gasto{id=%d, descripcion='%s', monto=-%.2f, fecha=%s, categoria='%s'}",
                getId(), getDescripcion(), getMonto(), getFecha(), getCategoria());
    }
}
