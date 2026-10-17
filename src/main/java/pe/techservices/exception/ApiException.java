package pe.techservices.exception;

public class ApiException extends RuntimeException {

    private final int estado;

    public ApiException(String mensaje, int estado) {
        super(mensaje);
        this.estado = estado;
    }

    public int getEstado() {
        return estado;
    }
}
