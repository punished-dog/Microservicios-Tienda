package tienda.resenas.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import tienda.resenas.model.Resena;
public interface ResenaRepository extends JpaRepository<Resena, Long> {
    java.util.List<Resena> findByProductoId(Long productoId);
    java.util.List<Resena> findByUsuarioId(Long usuarioId);
}
