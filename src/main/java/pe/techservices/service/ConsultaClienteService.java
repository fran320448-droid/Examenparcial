package pe.techservices.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.techservices.dto.ClienteResponseDTO;
import pe.techservices.dto.ConsultaClienteResponseDTO;
import pe.techservices.exception.ApiException;
import pe.techservices.model.Cliente;
import pe.techservices.model.UsuarioApi;
import pe.techservices.repository.ClienteRepository;
import pe.techservices.repository.UsuarioApiRepository;

@Service
public class ConsultaClienteService {

    private final ClienteRepository clienteRepository;
    private final UsuarioApiRepository usuarioApiRepository;

    public ConsultaClienteService(ClienteRepository clienteRepository, UsuarioApiRepository usuarioApiRepository) {
        this.clienteRepository = clienteRepository;
        this.usuarioApiRepository = usuarioApiRepository;
    }

    /**
     * Flujo:
     *  1. La validacion de credenciales ya ocurrio en el controlador (AuthService).
     *  2. Se busca el cliente; si no existe se lanza 404 y NO se descuenta ningun token.
     *  3. Solo si la consulta es exitosa se descuenta 1 token.
     *  4. El descuento es atomico: UPDATE ... WHERE tokens_disponibles > 0
     *     evita consumos duplicados en peticiones concurrentes.
     */
    @Transactional
    public ConsultaClienteResponseDTO consultarCliente(Long clienteId, UsuarioApi usuario) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ApiException("Cliente no encontrado", 404));

        int saldoAntes = usuario.getTokensDisponibles();
        int tokensRestantes = descontarTokenAtomico(usuario, saldoAntes);

        return new ConsultaClienteResponseDTO(
                "Consulta realizada correctamente",
                aDto(cliente),
                tokensRestantes);
    }

    private int descontarTokenAtomico(UsuarioApi usuario, int saldoAntes) {
        if (saldoAntes < 1) {
            throw new ApiException("Saldo de tokens agotado", 429);
        }
        int filasActualizadas = usuarioApiRepository.descontarToken(usuario.getId(), saldoAntes);
        if (filasActualizadas == 0) {
            throw new ApiException("Saldo de tokens agotado", 429);
        }
        usuario.setTokensDisponibles(saldoAntes - 1);
        return saldoAntes - 1;
    }

    private ClienteResponseDTO aDto(Cliente cliente) {
        return new ClienteResponseDTO(
                cliente.getId(),
                cliente.getNombres(),
                cliente.getApellidos(),
                cliente.getDni(),
                cliente.getCorreoElectronico(),
                cliente.getTelefono());
    }
}
