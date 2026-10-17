package pe.techservices.security;

import java.util.Optional;
import org.springframework.stereotype.Component;
import pe.techservices.exception.ApiException;
import pe.techservices.model.UsuarioApi;
import pe.techservices.repository.UsuarioApiRepository;

@Component
public class ApiKeyValidator {

    private final UsuarioApiRepository usuarioApiRepository;

    public ApiKeyValidator(UsuarioApiRepository usuarioApiRepository) {
        this.usuarioApiRepository = usuarioApiRepository;
    }

    /**
     * Valida la API Key y el token Bearer contra la tabla usuarios_api.
     * Reglas de seguridad:
     *  - Ambos encabezados son obligatorios.
     *  - Deben pertenecer al MISMO usuario autorizado.
     *  - El usuario debe estar activo.
     *  - El usuario debe tener al menos un token disponible.
     */
    public UsuarioApi validar(UsuarioApiCredentials credentials) {
        if (credentials == null || sinApiKey(credentials) || sinBearerToken(credentials)) {
            throw new ApiException("Credenciales incompletas: se requieren x-api-key y Authorization Bearer", 401);
        }

        Optional<UsuarioApi> usuarioOpt = usuarioApiRepository
                .findByApiKeyAndTokenAutenticacion(credentials.apiKey(), credentials.bearerToken());

        if (usuarioOpt.isEmpty()) {
            throw new ApiException("API Key o token invalidos", 401);
        }

        UsuarioApi usuario = usuarioOpt.get();

        if (!"activo".equalsIgnoreCase(usuario.getEstado())) {
            throw new ApiException("Usuario inactivo, no autorizado para consultar", 403);
        }

        if (usuario.getTokensDisponibles() == null || usuario.getTokensDisponibles() < 1) {
            throw new ApiException("Saldo de tokens agotado", 429);
        }

        return usuario;
    }

    /**
     * Extrae el token del encabezado "Authorization: Bearer <token>".
     * Devuelve null si el encabezado falta o no tiene el formato correcto.
     */
    public static UsuarioApiCredentials extraerCredenciales(String apiKeyHeader, String authorizationHeader) {
        boolean authorizationValido = authorizationHeader != null
                && authorizationHeader.startsWith("Bearer ")
                && authorizationHeader.substring(7).trim().length() > 0;

        if (!authorizationValido || apiKeyHeader == null || apiKeyHeader.trim().isEmpty()) {
            return null;
        }

        String token = authorizationHeader.substring(7).trim();
        return new UsuarioApiCredentials(apiKeyHeader.trim(), token);
    }

    private boolean sinApiKey(UsuarioApiCredentials credentials) {
        return credentials.apiKey() == null || credentials.apiKey().trim().isEmpty();
    }

    private boolean sinBearerToken(UsuarioApiCredentials credentials) {
        return credentials.bearerToken() == null || credentials.bearerToken().trim().isEmpty();
    }

    public record UsuarioApiCredentials(String apiKey, String bearerToken) {
    }
}
