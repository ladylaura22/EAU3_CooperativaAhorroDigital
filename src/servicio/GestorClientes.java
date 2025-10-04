package servicio;
import modelo.Cliente;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Clase singleton que gestiona los clientes del sistema bancario
public class GestorClientes {
    private static GestorClientes instancia;
    private final List<Cliente> clientes;

    // Constructor privado para evitar instanciación externa
    private GestorClientes() {
        this.clientes = new ArrayList<>();
    }

    // Metodo para obtener la instancia única del gestor de clientes
    public static GestorClientes getInstance() {
        if (instancia == null) {
            instancia = new GestorClientes();
        }
        return instancia;
    }

    //
    public void registrarCliente(Cliente cliente) {
        validarClienteNoNulo(cliente);
        validarDocumentoNoDuplicado(cliente.getDocumento());
        clientes.add(cliente);
    }

    // Busca un cliente por su documentos
    public Optional<Cliente> buscarCliente(String documento) {
        validarDocumentoValido(documento);
        return clientes.stream()
                .filter(cliente -> cliente.getDocumento().equals(documento))
                .findFirst();
    }

    // Verifica si un cliente existe por su documento
    public boolean existeCliente(String documento) {
        validarDocumentoValido(documento);
        return buscarCliente(documento).isPresent();
    }

    // Elimina un cliente por su documento
    public boolean eliminarCliente(String documento) {
        validarDocumentoValido(documento);
        return clientes.removeIf(cliente -> cliente.getDocumento().equals(documento));
    }

    // Obtiene la lista de todos los clientes
    public List<Cliente> obtenerClientes() {
        return new ArrayList<>(clientes);
    }

    // Cuenta la cantidad de clientes registrados
    public int contarClientes() {
        return clientes.size();
    }

    // Validaciones privadas
    private void validarClienteNoNulo(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente no puede ser nulo");
        }
    }

    // Valida que el documento no sea nulo o vacío
    private void validarDocumentoValido(String documento) {
        if (documento == null || documento.trim().isEmpty()) {
            throw new IllegalArgumentException("El documento no puede estar vacío");
        }
    }

    private void validarDocumentoNoDuplicado(String documento) {
        if (existeCliente(documento)) {
            throw new IllegalArgumentException("Ya existe un cliente con documento: " + documento);
        }
    }
}