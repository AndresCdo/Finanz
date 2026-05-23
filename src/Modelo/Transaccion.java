package Modelo;

import java.time.LocalDate;

/**
 * Clase abstracta que representa una transacción financiera en el sistema Finanz.
 */
public abstract class Transaccion {

    // ── Atributos ──────────────────────────────────────────────────────────────
    private int       id;
    private String    descripcion;
    private double    monto;
    private LocalDate fecha;
    private String    categoria;

    // ── Constructor ────────────────────────────────────────────────────────────
    public Transaccion(int id, String descripcion, double monto,
                       LocalDate fecha, String categoria) {
        if (monto < 0) {
            throw new IllegalArgumentException("El monto de una transacción no puede ser negativo.");
        }
        this.id          = id;
        this.descripcion = descripcion;
        this.monto       = monto;
        this.fecha       = fecha;
        this.categoria   = categoria;
    }

    // ── Getters y Setters ──────────────────────────────────────────────────────
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public double getMonto() { return monto; }
    public void setMonto(double monto) {
        if (monto < 0) throw new IllegalArgumentException("El monto no puede ser negativo.");
        this.monto = monto;
    }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    // ── toString ───────────────────────────────────────────────────────────────
    @Override
    public String toString() {
        return String.format("Modelo.Transaccion{id=%d, descripcion='%s', monto=%.2f, fecha=%s, categoria='%s'}",
                id, descripcion, monto, fecha, categoria);
    }
}
