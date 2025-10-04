package modelo;
import servicio.ValidadorTransaccion;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// Clase abstracta que representa una cuenta bancaria
public abstract class Cuenta {
    protected final String numero;
    protected double saldo;
    private final List<Transaccion> transacciones;
    protected final LiquidadorInteres liquidador;

    // Constructor
    public Cuenta(String numero, LiquidadorInteres liquidador) {
        validarNumeroCuenta(numero);
        validarLiquidador(liquidador);

        this.numero = numero.trim();
        this.saldo = 0.0;
        this.transacciones = new ArrayList<>();
        this.liquidador = liquidador;
    }

    // Metodo abstracto para aplicar intereses, implementado en subclases
    public abstract void aplicarInteres();

    // Métodos públicos
    public void depositar(double monto) {
        ValidadorTransaccion.validarDeposito(monto);
        this.saldo += monto;
        registrarTransaccion("DEPÓSITO", monto);
    }

    // Retira dinero de la cuenta
    public void retirar(double monto) {
        ValidadorTransaccion.validarRetiro(this.saldo, monto);
        this.saldo -= monto;
        registrarTransaccion("RETIRO", monto);
    }

    // Transfiere dinero a otra cuenta
    public void transferir(Cuenta cuentaDestino, double monto) {
        ValidadorTransaccion.validarTransferencia(this.saldo, monto);
        validarCuentaDestino(cuentaDestino);

        this.retirar(monto);
        cuentaDestino.depositar(monto);
        registrarTransaccion("TRANSFERENCIA_ENVIADA", monto);
    }

    // Obtiene el historial de transacciones
    public List<Transaccion> obtenerHistorial() {
        return new ArrayList<>(transacciones);
    }


    public List<Transaccion> obtenerTransaccionesPorTipo(String tipo) {
        return transacciones.stream()
                .filter(t -> t.getTipo().equals(tipo))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    // Getters
    public String getNumero() {
        return numero;
    }

    public double getSaldo() {
        return saldo;
    }

    public int getCantidadTransacciones() {
        return transacciones.size();
    }

    // Métodos privados

    private void registrarTransaccion(String tipo, double monto) {
        transacciones.add(new Transaccion(tipo, monto));
    }

    private void validarNumeroCuenta(String numero) {
        if (numero == null || numero.trim().isEmpty()) {
            throw new IllegalArgumentException("El número de cuenta no puede estar vacío");
        }
    }

    private void validarLiquidador(LiquidadorInteres liquidador) {
        if (liquidador == null) {
            throw new IllegalArgumentException("El liquidador de intereses no puede ser nulo");
        }
    }

    private void validarCuentaDestino(Cuenta cuentaDestino) {
        if (cuentaDestino == null) {
            throw new IllegalArgumentException("La cuenta destino no puede ser nula");
        }
        if (cuentaDestino.equals(this)) {
            throw new IllegalArgumentException("No se puede transferir a la misma cuenta");
        }
    }

    // equals y hashCode basados en número de cuenta (identificador único)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cuenta)) return false;
        Cuenta cuenta = (Cuenta) o;
        return Objects.equals(numero, cuenta.numero);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numero);
    }

    @Override
    public String toString() {
        return String.format("Cuenta[%s - Saldo: $%.2f - %s]",
                numero, saldo, this.getClass().getSimpleName());
    }
}