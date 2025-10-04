package modelo;

// Clase que representa una cuenta de ahorros con interés
public class CuentaAhorros extends Cuenta {
    private static final double TASA_INTERES = 0.02; // 2% anual
    private static final LiquidadorInteres LIQUIDADOR_AHORROS = saldo -> saldo * TASA_INTERES;

    // Constructor que inicializa la cuenta de ahorros con un número de cuenta
    public CuentaAhorros(String numero) {
        super(numero, LIQUIDADOR_AHORROS);
    }

    //
    @Override
    public void aplicarInteres() {
        double interes = liquidador.calcular(this.saldo);
        if (interes > 0) {
            this.saldo += interes;
        }
    }

    // Obtiene la tasa de interés aplicada a esta cuenta
    public double getTasaInteres() {
        return TASA_INTERES;
    }

    // Calcula el interés proyectado basado en el saldo actual
    public double calcularInteresProyectado() {
        return liquidador.calcular(this.saldo);
    }

    @Override
    public String toString() {
        return String.format("CuentaAhorros[%s - Saldo: $%.2f - Tasa: %.1f%%]",
                getNumero(), getSaldo(), TASA_INTERES * 100);
    }
}