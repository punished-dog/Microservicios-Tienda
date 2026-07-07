package tienda.resenas.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import tienda.resenas.dto.ResenaDTO;
import tienda.resenas.exception.ResenaNoEncontradoException;
import tienda.resenas.model.Resena;
import tienda.resenas.repository.ResenaRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ResenaService {

    private static final Logger log = LoggerFactory.getLogger(ResenaService.class);

    private final ResenaRepository resenaRepository;
    private final WebClient.Builder webClientBuilder;

    public ResenaService(ResenaRepository resenaRepository, WebClient.Builder webClientBuilder) {
        this.resenaRepository = resenaRepository;
        this.webClientBuilder = webClientBuilder;
    }

    public List<ResenaDTO> listarTodos() {
        log.info("Listando todos los resenas");
        List<Resena> entidades = this.resenaRepository.findAll();
        List<ResenaDTO> dtos = new ArrayList<>();
        for (Resena e : entidades) { dtos.add(convertirADTO(e)); }
        return dtos;
    }

    public ResenaDTO buscarPorId(Long id) {
        log.info("Buscando resena con id: {}", id);
        Resena entidad = this.resenaRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Resena no encontrado con id: {}", id);
                    return new ResenaNoEncontradoException("El registro con el ID " + id + " no fue encontrado.");
                });
        return convertirADTO(entidad);
    }

    public List<ResenaDTO> buscarPorProducto(Long productoId) {
        List<Resena> entidades = this.resenaRepository.findByProductoId(productoId);
        List<ResenaDTO> dtos = new ArrayList<>();
        for (Resena e : entidades) { dtos.add(convertirADTO(e)); }
        return dtos;
    }

    public List<ResenaDTO> buscarPorUsuario(Long usuarioId) {
        List<Resena> entidades = this.resenaRepository.findByUsuarioId(usuarioId);
        List<ResenaDTO> dtos = new ArrayList<>();
        for (Resena e : entidades) { dtos.add(convertirADTO(e)); }
        return dtos;
    }

    public ResenaDTO crear(ResenaDTO dto) {
        if (dto.getCalificacion() != null && (dto.getCalificacion() < 1 || dto.getCalificacion() > 5)) {
            throw new IllegalArgumentException("La calificación debe estar entre 1 y 5.");
        }
        verificarUsuarioExiste(dto.getUsuarioId());
        verificarProductoExiste(dto.getProductoId());
        Resena entidad = new Resena();
        entidad.setUsuarioId(dto.getUsuarioId());
        entidad.setProductoId(dto.getProductoId());
        entidad.setCalificacion(dto.getCalificacion());
        entidad.setComentario(dto.getComentario());
        Resena guardado = this.resenaRepository.save(entidad);
        return convertirADTO(guardado);
    }

    public ResenaDTO actualizar(Long id, ResenaDTO dtoActualizado) {
        Resena entidadExistente = this.resenaRepository.findById(id)
                .orElseThrow(() -> new ResenaNoEncontradoException("Imposible actualizar. No se encontró el registro con el ID " + id + "."));
        entidadExistente.setUsuarioId(dtoActualizado.getUsuarioId());
        entidadExistente.setProductoId(dtoActualizado.getProductoId());
        entidadExistente.setCalificacion(dtoActualizado.getCalificacion());
        entidadExistente.setComentario(dtoActualizado.getComentario());
        Resena actualizado = this.resenaRepository.save(entidadExistente);
        return convertirADTO(actualizado);
    }

    public void eliminar(Long id) {
        if (!this.resenaRepository.existsById(id)) {
            throw new ResenaNoEncontradoException("Imposible eliminar. No existe registro con el ID " + id + ".");
        }
        this.resenaRepository.deleteById(id);
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

    private ResenaDTO convertirADTO(Resena e) {
        return new ResenaDTO(e.getId(), e.getUsuarioId(), e.getProductoId(), e.getCalificacion(), e.getComentario());
    }
}
