package tienda.soporte.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import tienda.soporte.dto.TicketDTO;
import tienda.soporte.exception.TicketNoEncontradoException;
import tienda.soporte.model.Ticket;
import tienda.soporte.repository.TicketRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class TicketService {

    private static final Logger log = LoggerFactory.getLogger(TicketService.class);

    private final TicketRepository ticketRepository;
    private final WebClient.Builder webClientBuilder;

    public TicketService(TicketRepository ticketRepository, WebClient.Builder webClientBuilder) {
        this.ticketRepository = ticketRepository;
        this.webClientBuilder = webClientBuilder;
    }

    public List<TicketDTO> listarTodos() {
        log.info("Listando todos los tickets");
        List<Ticket> entidades = this.ticketRepository.findAll();
        List<TicketDTO> dtos = new ArrayList<>();
        for (Ticket e : entidades) { dtos.add(convertirADTO(e)); }
        return dtos;
    }

    public TicketDTO buscarPorId(Long id) {
        log.info("Buscando ticket con id: {}", id);
        Ticket entidad = this.ticketRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Ticket no encontrado con id: {}", id);
                    return new TicketNoEncontradoException("El registro con el ID " + id + " no fue encontrado.");
                });
        return convertirADTO(entidad);
    }

    public List<TicketDTO> buscarPorUsuario(Long usuarioId) {
        List<Ticket> entidades = this.ticketRepository.findByUsuarioId(usuarioId);
        List<TicketDTO> dtos = new ArrayList<>();
        for (Ticket e : entidades) { dtos.add(convertirADTO(e)); }
        return dtos;
    }

    public List<TicketDTO> buscarPorEstado(String estado) {
        List<Ticket> entidades = this.ticketRepository.findByEstado(estado);
        List<TicketDTO> dtos = new ArrayList<>();
        for (Ticket e : entidades) { dtos.add(convertirADTO(e)); }
        return dtos;
    }

    public List<TicketDTO> buscarPorPrioridad(String prioridad) {
        List<Ticket> entidades = this.ticketRepository.findByPrioridad(prioridad);
        List<TicketDTO> dtos = new ArrayList<>();
        for (Ticket e : entidades) { dtos.add(convertirADTO(e)); }
        return dtos;
    }

    public TicketDTO crear(TicketDTO dto) {
        if (dto.getAsunto() == null || dto.getAsunto().isBlank()) {
            throw new IllegalArgumentException("El asunto del ticket es obligatorio.");
        }
        verificarUsuarioExiste(dto.getUsuarioId());
        Ticket entidad = new Ticket();
        entidad.setUsuarioId(dto.getUsuarioId());
        entidad.setAsunto(dto.getAsunto());
        entidad.setDescripcion(dto.getDescripcion());
        entidad.setEstado(dto.getEstado());
        entidad.setPrioridad(dto.getPrioridad());
        Ticket guardado = this.ticketRepository.save(entidad);
        return convertirADTO(guardado);
    }

    public TicketDTO actualizar(Long id, TicketDTO dtoActualizado) {
        Ticket entidadExistente = this.ticketRepository.findById(id)
                .orElseThrow(() -> new TicketNoEncontradoException("Imposible actualizar. No se encontró el registro con el ID " + id + "."));
        entidadExistente.setUsuarioId(dtoActualizado.getUsuarioId());
        entidadExistente.setAsunto(dtoActualizado.getAsunto());
        entidadExistente.setDescripcion(dtoActualizado.getDescripcion());
        entidadExistente.setEstado(dtoActualizado.getEstado());
        entidadExistente.setPrioridad(dtoActualizado.getPrioridad());
        Ticket actualizado = this.ticketRepository.save(entidadExistente);
        return convertirADTO(actualizado);
    }

    public void eliminar(Long id) {
        if (!this.ticketRepository.existsById(id)) {
            throw new TicketNoEncontradoException("Imposible eliminar. No existe registro con el ID " + id + ".");
        }
        this.ticketRepository.deleteById(id);
    }

    private void verificarUsuarioExiste(Long usuarioId) {
        try {
            this.webClientBuilder.build()
                    .get()
                    .uri("http://usuarios/api/usuarios/" + usuarioId)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("El usuario con ID " + usuarioId + " no existe o no está disponible.");
        }
    }

    private TicketDTO convertirADTO(Ticket e) {
        return new TicketDTO(e.getId(), e.getUsuarioId(), e.getAsunto(), e.getDescripcion(), e.getEstado(), e.getPrioridad());
    }
}
