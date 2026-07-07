package tienda.envios.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import tienda.envios.model.Envio;
public interface EnvioRepository extends JpaRepository<Envio, Long> {
    java.util.List<Envio> findByPedidoId(Long pedidoId);
    java.util.List<Envio> findByEstado(String estado);
}
