package tienda.promociones.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import tienda.promociones.dto.PromocionDTO;
import tienda.promociones.exception.PromocionNoEncontradoException;
import tienda.promociones.model.Promocion;
import tienda.promociones.repository.PromocionRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PromocionService {

    private static final Logger log = LoggerFactory.getLogger(PromocionService.class);

    private final PromocionRepository promocionRepository;
    private final WebClient.Builder webClientBuilder;

    public PromocionService(PromocionRepository promocionRepository, WebClient.Builder webClientBuilder) {
        this.promocionRepository = promocionRepository;
        this.webClientBuilder = webClientBuilder;
    }

    public List<PromocionDTO> listarTodos() {
        log.info("Listando todos los promocions");
        List<Promocion> entidades = this.promocionRepository.findAll();
        List<PromocionDTO> dtos = new ArrayList<>();
        for (Promocion e : entidades) { dtos.add(convertirADTO(e)); }
        return dtos;
    }

    public PromocionDTO buscarPorId(Long id) {
        log.info("Buscando promocion con id: {}", id);
        Promocion entidad = this.promocionRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Promocion no encontrado con id: {}", id);
                    return new PromocionNoEncontradoException("El registro con el ID " + id + " no fue encontrado.");
                });
        return convertirADTO(entidad);
    }

    public List<PromocionDTO> buscarPorProducto(Long productoId) {
        List<Promocion> entidades = this.promocionRepository.findByProductoId(productoId);
        List<PromocionDTO> dtos = new ArrayList<>();
        for (Promocion e : entidades) { dtos.add(convertirADTO(e)); }
        return dtos;
    }

    public List<PromocionDTO> buscarPorActiva(Boolean activa) {
        List<Promocion> entidades = this.promocionRepository.findByActiva(activa);
        List<PromocionDTO> dtos = new ArrayList<>();
        for (Promocion e : entidades) { dtos.add(convertirADTO(e)); }
        return dtos;
    }

    public PromocionDTO crear(PromocionDTO dto) {
        if (dto.getDescuento() != null && (dto.getDescuento() < 0 || dto.getDescuento() > 100)) {
            throw new IllegalArgumentException("El descuento debe estar entre 0 y 100.");
        }
        verificarProductoExiste(dto.getProductoId());
        Promocion entidad = new Promocion();
        entidad.setNombre(dto.getNombre());
        entidad.setDescripcion(dto.getDescripcion());
        entidad.setDescuento(dto.getDescuento());
        entidad.setProductoId(dto.getProductoId());
        entidad.setFechaInicio(dto.getFechaInicio());
        entidad.setFechaFin(dto.getFechaFin());
        entidad.setActiva(dto.getActiva());
        Promocion guardado = this.promocionRepository.save(entidad);
        return convertirADTO(guardado);
    }

    public PromocionDTO actualizar(Long id, PromocionDTO dtoActualizado) {
        Promocion entidadExistente = this.promocionRepository.findById(id)
                .orElseThrow(() -> new PromocionNoEncontradoException("Imposible actualizar. No se encontró el registro con el ID " + id + "."));
        entidadExistente.setNombre(dtoActualizado.getNombre());
        entidadExistente.setDescripcion(dtoActualizado.getDescripcion());
        entidadExistente.setDescuento(dtoActualizado.getDescuento());
        entidadExistente.setProductoId(dtoActualizado.getProductoId());
        entidadExistente.setFechaInicio(dtoActualizado.getFechaInicio());
        entidadExistente.setFechaFin(dtoActualizado.getFechaFin());
        entidadExistente.setActiva(dtoActualizado.getActiva());
        Promocion actualizado = this.promocionRepository.save(entidadExistente);
        return convertirADTO(actualizado);
    }

    public void eliminar(Long id) {
        if (!this.promocionRepository.existsById(id)) {
            throw new PromocionNoEncontradoException("Imposible eliminar. No existe registro con el ID " + id + ".");
        }
        this.promocionRepository.deleteById(id);
    }

    private void verificarProductoExiste(Long productoId) {
        try {
            this.webClientBuilder.build()
                    .get()
                    .uri("http://productos/api/productos/" + productoId)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("El producto con ID " + productoId + " no existe o no está disponible.");
        }
    }

    private PromocionDTO convertirADTO(Promocion e) {
        return new PromocionDTO(e.getId(), e.getNombre(), e.getDescripcion(), e.getDescuento(), e.getProductoId(), e.getFechaInicio(), e.getFechaFin(), e.getActiva());
    }
}
