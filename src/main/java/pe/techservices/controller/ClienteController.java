package pe.techservices.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.techservices.dto.ConsultaClienteResponseDTO;
import pe.techservices.model.UsuarioApi;
import pe.techservices.security.ApiKeyValidator;
import pe.techservices.service.ConsultaClienteService;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ApiKeyValidator apiKeyValidator;
    private final ConsultaClienteService consultaClienteService;

    public ClienteController(ApiKeyValidator apiKeyValidator, ConsultaClienteService consultaClienteService) {
        this.apiKeyValidator = apiKeyValidator;
        this.consultaClienteService = consultaClienteService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConsultaClienteResponseDTO> consultarCliente(
            @PathVariable Long id,
            @RequestHeader(value = "x-api-key", required = false) String apiKey,
            @RequestHeader(value = "Authorization", required = false) String authorization) {

        ApiKeyValidator.UsuarioApiCredentials credenciales =
                ApiKeyValidator.extraerCredenciales(apiKey, authorization);

        UsuarioApi usuario = apiKeyValidator.validar(credenciales);

        return ResponseEntity.ok(consultaClienteService.consultarCliente(id, usuario));
    }
}
