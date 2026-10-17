package pe.techservices.dto;

public class ErrorResponseDTO {

    private String mensaje;
    private int estado;

    public ErrorResponseDTO() {
    }

    public ErrorResponseDTO(String mensaje, int estado) {
        this.mensaje = mensaje;
        this.estado = estado;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }
}
