package servicio;

import modelo.Cliente;
import modelo.Cuenta;

import java.util.*;
import java.util.stream.Collectors;

// Servicio para generar reportes financieros basados en una lista de clientes y sus cuentas
public class ReporteServicio {
    private final List<Cliente> clientes;

    // Constructor que recibe una lista de clientes
    public ReporteServicio(List<Cliente> clientes) {
        if (clientes == null) {
            throw new IllegalArgumentException("La lista de clientes no puede ser nula");
        }
        this.clientes = new ArrayList<>(clientes); // Copia defensiva
    }

    // Calcula el saldo total de un cliente sumando los saldos de todas sus cuentas
    private double calcularSaldoTotalCliente(Cliente cliente) {
        return cliente.getCuentas().stream()
                .mapToDouble(Cuenta::getSaldo)
                .sum();
    }

    // Obtiene clientes con saldo total superior al umbral especificado
    public List<Cliente> obtenerClientesSaldoSuperior(double umbral) {
        return clientes.stream()
                .filter(cliente -> calcularSaldoTotalCliente(cliente) > umbral)
                .collect(Collectors.toList());
    }

    // Obtiene clientes con saldo total inferior al umbral especificado
    public List<Cliente> obtenerClientesSaldoInferior(double umbral) {
        return clientes.stream()
                .filter(cliente -> calcularSaldoTotalCliente(cliente) < umbral)
                .collect(Collectors.toList());
    }

    // Calcula el capital total en todas las cuentas de todos los clientes
    public double calcularCapitalTotal() {
        return clientes.stream()
                .flatMap(cliente -> cliente.getCuentas().stream())
                .mapToDouble(Cuenta::getSaldo)
                .sum();
    }

    // Identifica clientes sin cuentas
    public List<Cliente> identificarClientesSinCuentas() {
        return clientes.stream()
                .filter(cliente -> cliente.getCuentas().isEmpty())
                .collect(Collectors.toList());
    }

    // Identifica clientes con múltiples cuentas
    public List<Cliente> identificarClientesConMultiplesCuentas() {
        return clientes.stream()
                .filter(cliente -> cliente.getCuentas().size() > 1)
                .collect(Collectors.toList());
    }

    // Calcula el saldo promedio por cliente
    public double calcularSaldoPromedioPorCliente() {
        if (clientes.isEmpty()) return 0.0;

        double saldoTotal = clientes.stream()
                .mapToDouble(this::calcularSaldoTotalCliente)
                .sum();
        return saldoTotal / clientes.size();
    }

    // Genera un reporte de clientes premium (top 20% por saldo total)
    public List<Cliente> generarReporteClientesPremium() {
        if (clientes.isEmpty()) {
            return List.of();
        }

        // Obtener saldos y ordenar para encontrar el percentil 80
        List<Double> saldos = clientes.stream()
                .map(this::calcularSaldoTotalCliente)
                .sorted(Collections.reverseOrder()) // De mayor a menor
                .collect(Collectors.toList());

        // Calcular índice del top 20% (al menos 1 cliente)
        int topCount = Math.max(1, (int) Math.ceil(clientes.size() * 0.2));
        double umbral = saldos.get(Math.min(topCount - 1, saldos.size() - 1));

        return clientes.stream()
                .filter(cliente -> calcularSaldoTotalCliente(cliente) >= umbral)
                .collect(Collectors.toList());
    }
}