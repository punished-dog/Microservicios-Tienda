package tienda.pedidos.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import tienda.pedidos.model.Pedido;
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    java.util.List<Pedido> findByUsuarioId(Long usuarioId);
    java.util.List<Pedido> findByEstado(String estado);
}
