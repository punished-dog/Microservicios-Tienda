package tienda.inventario.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import tienda.inventario.dto.InventarioDTO;
import tienda.inventario.exception.InventarioNoEncontradoException;
import tienda.inventario.model.Inventario;
import tienda.inventario.repository.InventarioRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class InventarioService {

    private static final Logger log = LoggerFactory.getLogger(InventarioService.class);

    private final InventarioRepository inventarioRepository;
    private final WebClient.Builder webClientBuilder;

    public InventarioService(InventarioRepository inventarioRepository, WebClient.Builder webClientBuilder) {
        this.inventarioRepository = inventarioRepository;
        this.webClientBuilder = webClientBuilder;
    }

    public List<InventarioDTO> listarTodos() {
        log.info("Listando todos los inventarios");
        List<Inventario> entidades = this.inventarioRepository.findAll();
        List<InventarioDTO> dtos = new ArrayList<>();
        for (Inventario e : entidades) { dtos.add(convertirADTO(e)); }
        return dtos;
    }

    public InventarioDTO buscarPorId(Long id) {
        log.info("Buscando inventario con id: {}", id);
        Inventario entidad = this.inventarioRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Inventario no encontrado con id: {}", id);
                    return new InventarioNoEncontradoException("El registro con el ID " + id + " no fue encontrado.");
                });
        return convertirADTO(entidad);
    }

    public InventarioDTO buscarPorProductoId(Long productoId) {
        Inventario entidad = this.inventarioRepository.findByProductoId(productoId)
                .orElseThrow(() -> new InventarioNoEncontradoException("No se encontró inventario para el producto con ID " + productoId + "."));
        return convertirADTO(entidad);
    }

    public InventarioDTO crear(InventarioDTO dto) {
        if (dto.getStockMinimo() != null && dto.getStockMinimo() < 0) {
            throw new IllegalArgumentException("El stock mínimo no puede ser negativo.");
        }
        verificarProductoExiste(dto.getProductoId());
        Inventario entidad = new Inventario();
        entidad.setProductoId(dto.getProductoId());
        entidad.setCantidad(dto.getCantidad());
        entidad.setStockMinimo(dto.getStockMinimo());
        entidad.setUbicacion(dto.getUbicacion());
        Inventario guardado = this.inventarioRepository.save(entidad);
        return convertirADTO(guardado);
    }

    public InventarioDTO actualizar(Long id, InventarioDTO dtoActualizado) {
        Inventario entidadExistente = this.inventarioRepository.findById(id)
                .orElseThrow(() -> new InventarioNoEncontradoException("Imposible actualizar. No se encontró el registro con el ID " + id + "."));
        entidadExistente.setProductoId(dtoActualizado.getProductoId());
        entidadExistente.setCantidad(dtoActualizado.getCantidad());
        entidadExistente.setStockMinimo(dtoActualizado.getStockMinimo());
        entidadExistente.setUbicacion(dtoActualizado.getUbicacion());
        Inventario actualizado = this.inventarioRepository.save(entidadExistente);
        return convertirADTO(actualizado);
    }

    public void eliminar(Long id) {
        if (!this.inventarioRepository.existsById(id)) {
            throw new InventarioNoEncontradoException("Imposible eliminar. No existe registro con el ID " + id + ".");
        }
        this.inventarioRepository.deleteById(id);
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

    private InventarioDTO convertirADTO(Inventario e) {
        return new InventarioDTO(e.getId(), e.getProductoId(), e.getCantidad(), e.getStockMinimo(), e.getUbicacion());
    }
}
