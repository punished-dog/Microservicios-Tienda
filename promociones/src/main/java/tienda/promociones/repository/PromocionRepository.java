package tienda.promociones.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import tienda.promociones.model.Promocion;
public interface PromocionRepository extends JpaRepository<Promocion, Long> {
    java.util.List<Promocion> findByProductoId(Long productoId);
    java.util.List<Promocion> findByActiva(Boolean activa);
}
