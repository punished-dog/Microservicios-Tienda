package tienda.pagos.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import tienda.pagos.model.Pago;
public interface PagoRepository extends JpaRepository<Pago, Long> {
    java.util.List<Pago> findByPedidoId(Long pedidoId);
    java.util.List<Pago> findByEstado(String estado);
}
