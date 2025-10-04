package exception;

// Excepción personalizada para indicar que no hay suficiente saldo
// en una cuenta para realizar una operación financiera.
public class SaldoInsuficienteException extends Exception {

    // Constructor que recibe el saldo actual y el monto requerido
    // para generar un mensaje detallado.
    public SaldoInsuficienteException(double saldoActual, double montoRequerido) {
        super(String.format(
                "Fondos insuficientes. Saldo disponible: $%.2f, Monto solicitado: $%.2f",
                saldoActual, montoRequerido
        ));
    }

    // Constructor que recibe un mensaje personalizado.
    public SaldoInsuficienteException(String mensaje) {
        super(mensaje);
    }
}