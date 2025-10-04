package modelo;

// Interfaz funcional para calcular intereses en una cuenta bancaria
@FunctionalInterface
public interface LiquidadorInteres {

    // Metodo abstracto para calcular el interés basado en el saldo
    double calcular(double saldo);
}