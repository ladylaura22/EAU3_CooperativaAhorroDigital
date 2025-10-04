package servicio;

// Clase para validar transacciones bancarias
// Incluye validaciones para retiros, depósitos y transferencias
// Lanza IllegalArgumentException con mensajes claros en caso de errores
public class ValidadorTransaccion {

    // Valida que el monto sea positivo
    // Lanza IllegalArgumentException si no lo es
    // Usado por otros métodos de validación
    public static void validarMontoPositivo(double monto) {
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto debe ser un número positivo.");
        }
    }

    // Valida condiciones para realizar un retiro
    // Verifica que el monto sea positivo y que haya fondos suficientes/
    // Lanza IllegalArgumentException si no se cumplen las condiciones
    public static void validarRetiro(double saldoActual, double montoRetiro) {
        validarMontoPositivo(montoRetiro);

        if (montoRetiro > saldoActual) {
            throw new IllegalArgumentException(
                    String.format("Fondos insuficientes. Disponible: $%.2f, Solicitado: $%.2f",
                            saldoActual, montoRetiro)
            );
        }
    }

    // Valida condiciones para realizar un depósito
    public static void validarDeposito(double montoDeposito) {
        validarMontoPositivo(montoDeposito);
    }

    //
    public static void validarTransferencia(double saldoOrigen, double montoTransferencia) {
        validarRetiro(saldoOrigen, montoTransferencia); // Mismas reglas que retiro
    }
}