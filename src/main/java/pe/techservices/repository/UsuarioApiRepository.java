package pe.techservices.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.techservices.model.UsuarioApi;

@Repository
public interface UsuarioApiRepository extends JpaRepository<UsuarioApi, Long> {

    Optional<UsuarioApi> findByApiKeyAndTokenAutenticacion(String apiKey, String tokenAutenticacion);

    /**
     * Descuento atomico para evitar consumos duplicados en solicitudes concurrentes:
     * la fila solo se actualiza si el saldo es exactamente el que se verifico
     * (control optoimista mediante expectedTokens > 0) y saldo > 0.
     * Devuelve el numero de filas actualizadas (0 si otro proceso desconto antes).
     */
    @Modifying
    @Query(value = "UPDATE usuarios_api SET tokens_disponibles = tokens_disponibles - 1 "
            + "WHERE id = :id AND tokens_disponibles = :expected AND tokens_disponibles > 0",
            nativeQuery = true)
    int descontarToken(@Param("id") Long id, @Param("expected") int expectedTokens);
}
