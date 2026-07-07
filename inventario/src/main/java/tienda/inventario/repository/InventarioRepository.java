package tienda.inventario.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import tienda.inventario.model.Inventario;
public interface InventarioRepository extends JpaRepository<Inventario, Long> {
    java.util.Optional<Inventario> findByProductoId(Long productoId);
}
