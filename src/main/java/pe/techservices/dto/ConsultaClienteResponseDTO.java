package pe.techservices.dto;

public class ConsultaClienteResponseDTO {

    private String mensaje;
    private ClienteResponseDTO cliente;
    private Integer tokensRestantes;

    public ConsultaClienteResponseDTO() {
    }

    public ConsultaClienteResponseDTO(String mensaje, ClienteResponseDTO cliente, Integer tokensRestantes) {
        this.mensaje = mensaje;
        this.cliente = cliente;
        this.tokensRestantes = tokensRestantes;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public ClienteResponseDTO getCliente() {
        return cliente;
    }

    public void setCliente(ClienteResponseDTO cliente) {
        this.cliente = cliente;
    }

    public Integer getTokensRestantes() {
        return tokensRestantes;
    }

    public void setTokensRestantes(Integer tokensRestantes) {
        this.tokensRestantes = tokensRestantes;
    }
}
