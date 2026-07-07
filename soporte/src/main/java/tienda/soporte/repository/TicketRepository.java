package tienda.soporte.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import tienda.soporte.model.Ticket;
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    java.util.List<Ticket> findByUsuarioId(Long usuarioId);
    java.util.List<Ticket> findByEstado(String estado);
    java.util.List<Ticket> findByPrioridad(String prioridad);
}
