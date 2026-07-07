package tienda.carrito.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import tienda.carrito.model.ItemCarrito;
public interface ItemCarritoRepository extends JpaRepository<ItemCarrito, Long> {
    java.util.List<ItemCarrito> findByUsuarioId(Long usuarioId);
}
