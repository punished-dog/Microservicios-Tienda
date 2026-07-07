package tienda.carrito.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import tienda.carrito.dto.ItemCarritoDTO;
import tienda.carrito.exception.ItemCarritoNoEncontradoException;
import tienda.carrito.model.ItemCarrito;
import tienda.carrito.repository.ItemCarritoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ItemCarritoService {

    private static final Logger log = LoggerFactory.getLogger(ItemCarritoService.class);

    private final ItemCarritoRepository itemCarritoRepository;
    private final WebClient.Builder webClientBuilder;

    public ItemCarritoService(ItemCarritoRepository itemCarritoRepository, WebClient.Builder webClientBuilder) {
        this.itemCarritoRepository = itemCarritoRepository;
        this.webClientBuilder = webClientBuilder;
    }

    public List<ItemCarritoDTO> listarTodos() {
        log.info("Listando todos los itemCarritos");
        List<ItemCarrito> entidades = this.itemCarritoRepository.findAll();
        List<ItemCarritoDTO> dtos = new ArrayList<>();
        for (ItemCarrito e : entidades) { dtos.add(convertirADTO(e)); }
        return dtos;
    }

    public ItemCarritoDTO buscarPorId(Long id) {
        log.info("Buscando itemCarrito con id: {}", id);
        ItemCarrito entidad = this.itemCarritoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("ItemCarrito no encontrado con id: {}", id);
                    return new ItemCarritoNoEncontradoException("El registro con el ID " + id + " no fue encontrado.");
                });
        return convertirADTO(entidad);
    }

    public List<ItemCarritoDTO> buscarPorUsuario(Long usuarioId) {
        List<ItemCarrito> entidades = this.itemCarritoRepository.findByUsuarioId(usuarioId);
        List<ItemCarritoDTO> dtos = new ArrayList<>();
        for (ItemCarrito e : entidades) { dtos.add(convertirADTO(e)); }
        return dtos;
    }

    public ItemCarritoDTO crear(ItemCarritoDTO dto) {
        if (dto.getCantidad() != null && dto.getCantidad() <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
        }
        Map producto = obtenerProducto(dto.getProductoId());
        Double precioReal = producto.get("precio") != null ? ((Number) producto.get("precio")).doubleValue() : dto.getPrecioUnitario();
        ItemCarrito entidad = new ItemCarrito();
        entidad.setUsuarioId(dto.getUsuarioId());
        entidad.setProductoId(dto.getProductoId());
        entidad.setCantidad(dto.getCantidad());
        entidad.setPrecioUnitario(precioReal);
        ItemCarrito guardado = this.itemCarritoRepository.save(entidad);
        return convertirADTO(guardado);
    }

    public ItemCarritoDTO actualizar(Long id, ItemCarritoDTO dtoActualizado) {
        ItemCarrito entidadExistente = this.itemCarritoRepository.findById(id)
                .orElseThrow(() -> new ItemCarritoNoEncontradoException("Imposible actualizar. No se encontró el registro con el ID " + id + "."));
        entidadExistente.setUsuarioId(dtoActualizado.getUsuarioId());
        entidadExistente.setProductoId(dtoActualizado.getProductoId());
        entidadExistente.setCantidad(dtoActualizado.getCantidad());
        entidadExistente.setPrecioUnitario(dtoActualizado.getPrecioUnitario());
        ItemCarrito actualizado = this.itemCarritoRepository.save(entidadExistente);
        return convertirADTO(actualizado);
    }

    public void eliminar(Long id) {
        if (!this.itemCarritoRepository.existsById(id)) {
            throw new ItemCarritoNoEncontradoException("Imposible eliminar. No existe registro con el ID " + id + ".");
        }
        this.itemCarritoRepository.deleteById(id);
    }

    public Map obtenerProducto(Long productoId) {
        try {
            return this.webClientBuilder.build()
                    .get()
                    .uri("http://productos/api/productos/" + productoId)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("El producto con ID " + productoId + " no existe o no está disponible.");
        }
    }

    private ItemCarritoDTO convertirADTO(ItemCarrito e) {
        return new ItemCarritoDTO(e.getId(), e.getUsuarioId(), e.getProductoId(), e.getCantidad(), e.getPrecioUnitario());
    }
}
