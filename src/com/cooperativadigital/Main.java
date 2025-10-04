package com.cooperativadigital;
import modelo.Cliente;
import modelo.CuentaAhorros;
import modelo.Transaccion;
import servicio.GestorClientes;
import servicio.ReporteServicio;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

// Sistema bancario Cooperativa Digital - Aplicación principal
// Gestiona clientes, cuentas y operaciones financieras
public class Main {
    private static final Scanner consola = new Scanner(System.in);
    private static final GestorClientes gestor = GestorClientes.getInstance();
    private static final ReporteServicio reportes = new ReporteServicio();

    public static void main(String[] args) {
        System.out.println("*** BIENVENIDO A COOPERATIVA DIGITAL ***");
        registrarClientesIniciales();
        mostrarMenuPrincipal();
    }

    // Registra los clientes iniciales del sistema
    private static void registrarClientesIniciales() {
        System.out.print("\n¿Cuántos clientes desea registrar?: ");
        int cantidad = leerEnteroPositivo();
        int registrados = 0;

        for (int i = 0; i < cantidad; i++) {
            System.out.println("\n*** Registro Cliente " + (i + 1) + " ***");

            boolean clienteRegistrado = false;
            while (!clienteRegistrado) {
                if (registrarCliente(registrados + 1)) {
                    registrados++;
                    clienteRegistrado = true;
                } else {
                    System.out.print("¿Desea intentar nuevamente con este cliente? (s/n): ");
                    String respuesta = consola.nextLine().trim().toLowerCase();
                    if (!respuesta.equals("s")) {
                        System.out.println("Saltando al siguiente cliente...");
                        break;
                    }
                }
            }
        }

        System.out.printf("\nSe registraron %d de %d clientes exitosamente.%n", registrados, cantidad);
    }

    // Registra un cliente individual con validación
    private static boolean registrarCliente(int numeroCliente) {
        try {
            System.out.print("Nombre: ");
            String nombre = consola.nextLine().trim();
            validarNombre(nombre);

            System.out.print("Documento: ");
            String documento = consola.nextLine().trim();
            validarDocumento(documento);

            Cliente cliente = new Cliente(nombre, documento);
            String numeroCuenta = generarNumeroCuenta(numeroCliente);
            CuentaAhorros cuenta = new CuentaAhorros(numeroCuenta);
            cliente.agregarCuenta(cuenta);

            gestor.registrarCliente(cliente);
            System.out.println("Cliente registrado - Cuenta: " + numeroCuenta);
            return true;

        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
            return false;
        }
    }

    // Valida que el nombre solo contenga letras y espacios
    private static void validarNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }

        // Permite letras, espacios, acentos y ñ
        if (!nombre.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) {
            throw new IllegalArgumentException("El nombre solo puede contener letras y espacios");
        }

        // Validación adicional: mínimo 2 caracteres
        if (nombre.length() < 2) {
            throw new IllegalArgumentException("El nombre debe tener al menos 2 caracteres");
        }

        // Validación adicional: máximo 50 caracteres
        if (nombre.length() > 50) {
            throw new IllegalArgumentException("El nombre no puede exceder 50 caracteres");
        }

        // Validación adicional: no permite solo espacios
        if (nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede contener solo espacios");
        }
    }

    // Valida que el documento solo contenga números
    private static void validarDocumento(String documento) {
        if (documento == null || documento.trim().isEmpty()) {
            throw new IllegalArgumentException("El documento no puede estar vacío");
        }

        // Solo permite dígitos numéricos
        if (!documento.matches("^[0-9]+$")) {
            throw new IllegalArgumentException("El documento solo puede contener números");
        }
    }

    // Genera un número de cuenta secuencial con ceros a la izquierda
    private static String generarNumeroCuenta(int numeroSecuencial) {
        return String.format("CTA-%04d", numeroSecuencial);
    }

    // Menú principal del sistema
    private static void mostrarMenuPrincipal() {
        while (true) {
            mostrarOpciones();
            int opcion = leerOpcionValida(1, 6);

            try {
                switch (opcion) {
                    case 1 -> realizarDeposito();
                    case 2 -> realizarRetiro();
                    case 3 -> consultarSaldo();
                    case 4 -> consultarHistorial();
                    case 5 -> generarReportes();
                    case 6 -> salir();
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }

    // Realiza un depósito de fondos en una cuenta específica
    private static void realizarDeposito() {
        System.out.print("Ingrese documento del cliente: ");
        String documento = consola.nextLine().trim();

        gestor.buscarCliente(documento).ifPresentOrElse(
                cliente -> {
                    // Mostrar información del cliente encontrado
                    System.out.println("Cliente encontrado: " + cliente.getNombre());

                    seleccionarCuenta(cliente).ifPresent(cuenta -> {
                        // Mostrar información de la cuenta
                        System.out.printf("Cuenta seleccionada: %s - Saldo actual: $%.2f%n",
                                cuenta.getNumero(), cuenta.getSaldo());

                        double monto = leerMontoValido("Ingrese monto a depositar: $");

                        // Confirmación con nombre y monto
                        System.out.printf("\n| *** RESUMEN DE OPERACIÓN: *** | %n");
                        System.out.printf("   Cliente: %s%n", cliente.getNombre());
                        System.out.printf("   Documento: %s%n", cliente.getDocumento());
                        System.out.printf("   Cuenta: %s%n", cuenta.getNumero());
                        System.out.printf("   Monto a depositar: $%.2f%n", monto);

                        System.out.print("¿Confirmar depósito? (s/n): ");
                        String confirmacion = consola.nextLine().trim().toLowerCase();

                        if (confirmacion.equals("s") || confirmacion.equals("si")) {
                            try {
                                cuenta.depositar(monto);
                                System.out.printf("DEPÓSITO EXITOSO%n");
                                System.out.printf("   Cliente: %s%n", cliente.getNombre());
                                System.out.printf("   Cuenta: %s%n", cuenta.getNumero());
                                System.out.printf("   Monto depositado: $%.2f%n", monto);
                                System.out.printf("   Nuevo saldo: $%.2f%n", cuenta.getSaldo());
                            } catch (IllegalArgumentException e) {
                                System.err.println("Error en depósito: " + e.getMessage());
                            }
                        } else {
                            System.out.println("Depósito cancelado por el usuario");
                        }
                    });
                },
                () -> System.out.print("Cliente no encontrado. Verifique el documento.\n")
        );
    }

    private static void realizarRetiro() {
        Optional<Cliente> clienteOpt = obtenerClienteValido();
        if (clienteOpt.isEmpty()) return;

        Cliente cliente = clienteOpt.get();
        System.out.println("Cliente encontrado: " + cliente.getNombre());

        seleccionarCuenta(cliente).ifPresent(cuenta -> {
            System.out.printf("Cuenta seleccionada: %s - Saldo disponible: $%.2f%n",
                    cuenta.getNumero(), cuenta.getSaldo());

            double monto = leerMontoValido("Monto a retirar: $");

            System.out.printf("\nRESUMEN DE RETIRO:%n");
            System.out.printf("   Cliente: %s%n", cliente.getNombre());
            System.out.printf("   Documento: %s%n", cliente.getDocumento());
            System.out.printf("   Cuenta: %s%n", cuenta.getNumero());
            System.out.printf("   Monto a retirar: $%.2f%n", monto);
            System.out.printf("   Saldo después del retiro: $%.2f%n", cuenta.getSaldo() - monto);

            System.out.print("¿Confirmar retiro? (s/n): ");
            String confirmacion = consola.nextLine().trim().toLowerCase();

            if (confirmacion.equals("s") || confirmacion.equals("si")) {
                try {
                    cuenta.retirar(monto);
                    System.out.printf("RETIRO EXITOSO%n");
                    System.out.printf("   Cliente: %s%n", cliente.getNombre());
                    System.out.printf("   Cuenta: %s%n", cuenta.getNumero());
                    System.out.printf("   Monto retirado: $%.2f%n", monto);
                    System.out.printf("   Saldo disponible: $%.2f%n", cuenta.getSaldo());
                } catch (IllegalArgumentException e) {
                    System.out.println("Error en retiro: " + e.getMessage());
                }
            } else {
                System.out.println("Retiro cancelado por el usuario");
            }
        });
    }

    // Nuevo metodo reutilizable para obtener cliente válido
    private static Optional<Cliente> obtenerClienteValido() {
        System.out.print("Documento del cliente: ");
        String documento = consola.nextLine().trim();

        Optional<Cliente> clienteOpt = gestor.buscarCliente(documento);
        if (clienteOpt.isEmpty()) {
            System.out.println("Cliente no encontrado. Verifique el documento.");
        }

        return clienteOpt;
    }

    private static void consultarSaldo() {
        System.out.print("Documento del cliente: ");
        String documento = consola.nextLine().trim();

        gestor.buscarCliente(documento).ifPresentOrElse(
                cliente -> {
                    // Encabezado profesional
                    System.out.println("\n" + "=".repeat(50));
                    System.out.println("          CONSULTA DE SALDOS BANCARIOS");
                    System.out.println("=".repeat(50));
                    System.out.println("CLIENTE: " + cliente.getNombre().toUpperCase());
                    System.out.println("DOCUMENTO: " + cliente.getDocumento());
                    System.out.println("FECHA: " + java.time.LocalDate.now());
                    System.out.println("=".repeat(50));

                    if (cliente.getCuentas().isEmpty()) {
                        System.out.println("El cliente no tiene cuentas activas");
                        return;
                    }

                    // Encabezado de tabla
                    System.out.printf("%-15s %-20s %12s%n",
                            "NÚMERO CUENTA", "TIPO", "SALDO");
                    System.out.println("-".repeat(50));

                    // Lista de cuentas con formato profesional
                    cliente.getCuentas().forEach(cuenta -> {
                        String tipoCuenta = cuenta.getClass().getSimpleName()
                                .replace("Cuenta", "")
                                .toUpperCase();

                        System.out.printf("%-15s %-20s $%10.2f%n",
                                cuenta.getNumero(),
                                tipoCuenta,
                                cuenta.getSaldo());
                    });

                    // Resumen financiero
                    System.out.println("-".repeat(50));
                    double saldoTotal = cliente.calcularSaldoTotal();
                    int totalCuentas = cliente.getCuentas().size();

                    System.out.printf("Total de cuentas: %d%n", totalCuentas);
                    System.out.printf("SALDO TOTAL DISPONIBLE: $%.2f%n", saldoTotal);

                    // Información adicional
                    if (saldoTotal > 10000) {
                        System.out.println("Cliente categoría PREMIUM");
                    } else if (saldoTotal > 5000) {
                        System.out.println("Cliente categoría PLUS");
                    }

                    System.out.println("=".repeat(50));
                },
                () -> System.out.println("Cliente no encontrado. Verifique el documento.")
        );
    }

    // Consulta y muestra el historial de transacciones de un cliente
    private static void consultarHistorial() {
        System.out.print("Documento del cliente: ");
        String documento = consola.nextLine().trim();

        gestor.buscarCliente(documento).ifPresentOrElse(
                cliente -> {
                    if (cliente.getCuentas().isEmpty()) {
                        System.out.println("El cliente no tiene cuentas");
                        return;
                    }

                    // Encabezado profesional del estado de cuenta
                    System.out.println("\n" + "=".repeat(65));
                    System.out.println("               ESTADO DE CUENTA BANCARIO");
                    System.out.println("=".repeat(65));
                    System.out.println("CLIENTE: " + cliente.getNombre().toUpperCase());
                    System.out.println("DOCUMENTO: " + cliente.getDocumento());
                    System.out.println("FECHA DE CONSULTA: " + java.time.LocalDate.now());
                    System.out.println("=".repeat(65));

                    cliente.getCuentas().forEach(cuenta -> {
                        System.out.println("\nCUENTA: " + cuenta.getNumero());
                        System.out.printf("SALDO ACTUAL: $%.2f%n", cuenta.getSaldo());
                        System.out.println("-".repeat(65));

                        List<Transaccion> historial = cuenta.obtenerHistorial();

                        if (historial.isEmpty()) {
                            System.out.println("No hay transacciones registradas");
                        } else {
                            // Encabezado de columnas alineadas
                            System.out.printf("%-12s %-8s %-18s %12s%n",
                                    "FECHA", "HORA", "TIPO", "MONTO");
                            System.out.println("-".repeat(65));

                            historial.forEach(transaccion -> {
                                // Usa getFechaLegible() y separa fecha/hora
                                String fechaCompleta = transaccion.getFechaLegible();
                                String[] partesFecha = fechaCompleta.split(" ");
                                String fecha = partesFecha[0];  // dd/MM/yyyy
                                String hora = partesFecha[1];   // HH:mm

                                System.out.printf("%-12s %-8s %-18s $%10.2f%n",
                                        fecha,
                                        hora,
                                        transaccion.getTipo(),
                                        transaccion.getMonto());
                            });

                            System.out.println("-".repeat(65));
                            System.out.printf("Total de transacciones: %d%n", historial.size());

                            // Resumen financiero detallado
                            double totalDepositos = historial.stream()
                                    .filter(t -> t.getTipo().equals("DEPÓSITO"))
                                    .mapToDouble(Transaccion::getMonto)
                                    .sum();

                            double totalRetiros = historial.stream()
                                    .filter(t -> t.getTipo().equals("RETIRO"))
                                    .mapToDouble(Transaccion::getMonto)
                                    .sum();

                            System.out.printf("Total depositado: $%.2f%n", totalDepositos);
                            System.out.printf("Total retirado: $%.2f%n", totalRetiros);
                            System.out.printf("Saldo disponible: $%.2f%n", totalDepositos - totalRetiros);
                        }
                        System.out.println(); // Espacio entre cuentas
                    });
                },
                () -> System.out.println("Cliente no encontrado")
        );
    }

    // Genera y muestra reportes financieros del sistema
    private static void generarReportes() {
        System.out.println("\n*** REPORTES FINANCIEROS ***");
        System.out.printf("Capital total: $%.2f%n", reportes.calcularCapitalTotal());
        System.out.printf("Saldo promedio: $%.2f%n", reportes.calcularSaldoPromedioPorCliente());
        System.out.printf("Total clientes: %d%n", gestor.contarClientes());
        System.out.printf("Clientes sin cuentas: %d%n", reportes.identificarClientesSinCuentas().size());

        System.out.print("\n¿Ver clientes premium? (s/n): ");
        if (consola.nextLine().trim().equalsIgnoreCase("s")) {
            System.out.println("| *** CLIENTES PREMIUM *** |");
            reportes.generarReporteClientesPremium().forEach(cliente ->
                    System.out.printf("%s - Saldo: $%.2f%n",
                            cliente.getNombre(), cliente.calcularSaldoTotal())
            );
        }
    }

    // Sale del sistema de manera segura
    private static void salir() {
        System.out.println("\n¡Gracias por usar Cooperativa Digital!");
        System.out.println("¡Hasta pronto!");
        consola.close();
        System.exit(0);
    }

    // Muestra las opciones del menú principal
    private static void mostrarOpciones() {
        System.out.println("\n" + "=" .repeat(48));
        System.out.println(" *** COOPERATIVA DIGITAL - MENÚ PRINCIPAL *** ");
        System.out.println("=" .repeat(48));
        System.out.println("1. Depositar fondos");
        System.out.println("2. Retirar efectivo");
        System.out.println("3. Consultar saldo");
        System.out.println("4. Ver historial");
        System.out.println("5. Reportes financieros");
        System.out.println("6. Salir del sistema");
        System.out.print("Seleccione una opción: ");
    }

    // Permite al usuario seleccionar una cuenta si tiene varias
    private static java.util.Optional<modelo.Cuenta> seleccionarCuenta(Cliente cliente) {
        if (cliente.getCuentas().isEmpty()) {
            System.err.println("El cliente no tiene cuentas");
            return java.util.Optional.empty();
        }

        if (cliente.getCuentas().size() == 1) {
            return java.util.Optional.of(cliente.getCuentas().get(0));
        }

        System.out.println("\n| *** Seleccione cuenta *** |");
        for (int i = 0; i < cliente.getCuentas().size(); i++) {
            modelo.Cuenta cuenta = cliente.getCuentas().get(i);
            System.out.printf("%d. %s - Saldo: $%.2f%n", i + 1, cuenta.getNumero(), cuenta.getSaldo());
        }

        System.out.print("Opción: ");
        try {
            int seleccion = Integer.parseInt(consola.nextLine().trim()) - 1;
            if (seleccion >= 0 && seleccion < cliente.getCuentas().size()) {
                return java.util.Optional.of(cliente.getCuentas().get(seleccion));
            }
        } catch (NumberFormatException e) {
            // Continuar con la primera cuenta por defecto
        }

        System.out.println("Usando primera cuenta por defecto");
        return java.util.Optional.of(cliente.getCuentas().get(0));
    }

    // Lee un entero positivo desde la consola
    private static int leerEnteroPositivo() {
        while (true) {
            try {
                int valor = Integer.parseInt(consola.nextLine().trim());
                if (valor > 0) return valor;
                System.out.print("Ingrese un número positivo: ");
            } catch (NumberFormatException e) {
                System.out.print("Ingrese un número válido: ");
            }
        }
    }

    // Lee una opción válida dentro de un rango
    private static int leerOpcionValida(int min, int max) {
        while (true) {
            try {
                int opcion = Integer.parseInt(consola.nextLine().trim());
                if (opcion >= min && opcion <= max) return opcion;
                System.out.printf("Ingrese opción válida (%d-%d): ", min, max);
            } catch (NumberFormatException e) {
                System.out.print("Ingrese un número: ");
            }
        }
    }

    // Lee un monto válido (positivo) desde la consola
    private static double leerMontoValido(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                double valor = Double.parseDouble(consola.nextLine().trim());
                if (valor > 0) return valor;
                System.out.println("El monto debe ser positivo");
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un monto válido");
            }
        }
    }
}