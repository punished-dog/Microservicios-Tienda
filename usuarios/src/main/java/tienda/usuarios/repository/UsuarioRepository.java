package tienda.usuarios.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tienda.usuarios.model.Usuario;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
}
