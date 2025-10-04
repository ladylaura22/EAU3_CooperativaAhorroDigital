package modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

// Clase inmutable que representa una transacción financiera
public final class Transaccion {
    private final String tipo;
    private final double monto;
    private final LocalDateTime fecha;
    private static final DateTimeFormatter FORMATEADOR =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // Constructor
    public Transaccion(String tipo, double monto) {
        validarDatos(tipo, monto);
        this.tipo = tipo.trim().toUpperCase();
        this.monto = monto;
        this.fecha = LocalDateTime.now();
    }

    // Getters básicos
    public String getTipo() { return tipo; }
    public double getMonto() { return monto; }
    public LocalDateTime getFecha() { return fecha; }

    // Formatea la fecha para presentación legible
    public String getFechaLegible() {
        return fecha.format(FORMATEADOR);
    }

    // Verifica si la transacción es del tipo especificado
    public boolean esTipo(String tipoConsulta) {
        return this.tipo.equals(tipoConsulta.toUpperCase());
    }

    @Override
    public String toString() {
        return String.format("[%s] %s - $%.2f", getFechaLegible(), tipo, monto);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transaccion)) return false;
        Transaccion that = (Transaccion) o;
        return Double.compare(that.monto, monto) == 0 &&
                Objects.equals(tipo, that.tipo) &&
                Objects.equals(fecha, that.fecha);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tipo, monto, fecha);
    }

    private void validarDatos(String tipo, double monto) {
        if (tipo == null || tipo.trim().isEmpty()) {
            throw new IllegalArgumentException("Tipo de transacción requerido");
        }
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto debe ser positivo");
        }
    }
}