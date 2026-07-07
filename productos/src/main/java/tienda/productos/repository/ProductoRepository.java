package tienda.productos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tienda.productos.model.Producto;
import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByCategoria(String categoria);
}
