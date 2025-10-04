package modelo;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

// Clase que representa un cliente bancario
// Un cliente puede tener múltiples cuentas
// Se asegura que no haya cuentas duplicadas por número
// Proporciona métodos para agregar, eliminar y buscar cuentas
// También permite calcular el saldo total del cliente sumando todas sus cuentas
// Incluye validaciones para datos nulos o inválidos
public class Cliente {
    private final String nombre;
    private final String documento;
    private final List<Cuenta> cuentas;

    // Constructor
    public Cliente(String nombre, String documento) {
        validarNombre(nombre);
        validarDocumento(documento);

        this.nombre = nombre.trim();
        this.documento = documento.trim();
        this.cuentas = new ArrayList<>();
    }

    // Métodos
    public void agregarCuenta(Cuenta cuenta) {
        validarCuentaNoNula(cuenta);

        if (!tieneCuenta(cuenta.getNumero())) {
            cuentas.add(cuenta);
        }
    }

    // Elimina una cuenta por su número
    public boolean eliminarCuenta(String numeroCuenta) {
        validarNumeroCuenta(numeroCuenta);
        return cuentas.removeIf(cuenta -> cuenta.getNumero().equals(numeroCuenta));
    }

   // Busca una cuenta por su número y la devuelve envuelta en un Optional
    public Optional<Cuenta> buscarCuenta(String numeroCuenta) {
        validarNumeroCuenta(numeroCuenta);
        return cuentas.stream()
                .filter(cuenta -> cuenta.getNumero().equals(numeroCuenta))
                .findFirst();
    }

    // Verifica si el cliente tiene una cuenta con el número dado
    public boolean tieneCuenta(String numeroCuenta) {
        return buscarCuenta(numeroCuenta).isPresent();
    }

    // Calcula el saldo total sumando los saldos de todas las cuentas del cliente
    public double calcularSaldoTotal() {
        return cuentas.stream()
                .mapToDouble(Cuenta::getSaldo)
                .sum();
    }

    // Getters
    public String getNombre() {
        return nombre;
    }

    public String getDocumento() {
        return documento;
    }

    public List<Cuenta> getCuentas() {
        return new ArrayList<>(cuentas);
    }

    public int getCantidadCuentas() {
        return cuentas.size();
    }

    // Validaciones

    private void validarNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del cliente no puede estar vacío");
        }
    }

    // Valida que el documento no sea nulo o vacío
    private void validarDocumento(String documento) {
        if (documento == null || documento.trim().isEmpty()) {
            throw new IllegalArgumentException("El documento del cliente no puede estar vacío");
        }
    }

    private void validarCuentaNoNula(Cuenta cuenta) {
        if (cuenta == null) {
            throw new IllegalArgumentException("La cuenta no puede ser nula");
        }
    }

    private void validarNumeroCuenta(String numeroCuenta) {
        if (numeroCuenta == null || numeroCuenta.trim().isEmpty()) {
            throw new IllegalArgumentException("El número de cuenta no puede estar vacío");
        }
    }

    // equals y hashCode basados en documento (identificador único)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cliente)) return false;
        Cliente cliente = (Cliente) o;
        return Objects.equals(documento, cliente.documento);
    }

    @Override
    public int hashCode() {
        return Objects.hash(documento);
    }

    @Override
    public String toString() {
        return String.format("Cliente[%s - %s - Cuentas: %d]",
                nombre, documento, cuentas.size());
    }
}