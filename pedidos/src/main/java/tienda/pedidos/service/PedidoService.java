package tienda.pedidos.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import tienda.pedidos.dto.PedidoDTO;
import tienda.pedidos.exception.PedidoNoEncontradoException;
import tienda.pedidos.model.Pedido;
import tienda.pedidos.repository.PedidoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PedidoService {

    private static final Logger log = LoggerFactory.getLogger(PedidoService.class);

    private final PedidoRepository pedidoRepository;
    private final WebClient.Builder webClientBuilder;

    public PedidoService(PedidoRepository pedidoRepository, WebClient.Builder webClientBuilder) {
        this.pedidoRepository = pedidoRepository;
        this.webClientBuilder = webClientBuilder;
    }

    public List<PedidoDTO> listarTodos() {
        log.info("Listando todos los pedidos");
        List<Pedido> entidades = this.pedidoRepository.findAll();
        List<PedidoDTO> dtos = new ArrayList<>();
        for (Pedido e : entidades) { dtos.add(convertirADTO(e)); }
        return dtos;
    }

    public PedidoDTO buscarPorId(Long id) {
        log.info("Buscando pedido con id: {}", id);
        Pedido entidad = this.pedidoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Pedido no encontrado con id: {}", id);
                    return new PedidoNoEncontradoException("El registro con el ID " + id + " no fue encontrado.");
                });
        return convertirADTO(entidad);
    }

    public List<PedidoDTO> buscarPorUsuario(Long usuarioId) {
        List<Pedido> entidades = this.pedidoRepository.findByUsuarioId(usuarioId);
        List<PedidoDTO> dtos = new ArrayList<>();
        for (Pedido e : entidades) { dtos.add(convertirADTO(e)); }
        return dtos;
    }

    public List<PedidoDTO> buscarPorEstado(String estado) {
        List<Pedido> entidades = this.pedidoRepository.findByEstado(estado);
        List<PedidoDTO> dtos = new ArrayList<>();
        for (Pedido e : entidades) { dtos.add(convertirADTO(e)); }
        return dtos;
    }

    public PedidoDTO crear(PedidoDTO dto) {
        if (dto.getTotal() != null && dto.getTotal() < 0) {
            throw new IllegalArgumentException("El total no puede ser negativo.");
        }
        verificarUsuarioExiste(dto.getUsuarioId());
        verificarProductoExiste(dto.getProductoId());
        Pedido entidad = new Pedido();
        entidad.setUsuarioId(dto.getUsuarioId());
        entidad.setProductoId(dto.getProductoId());
        entidad.setCantidad(dto.getCantidad());
        entidad.setTotal(dto.getTotal());
        entidad.setEstado(dto.getEstado());
        Pedido guardado = this.pedidoRepository.save(entidad);
        return convertirADTO(guardado);
    }

    public PedidoDTO actualizar(Long id, PedidoDTO dtoActualizado) {
        Pedido entidadExistente = this.pedidoRepository.findById(id)
                .orElseThrow(() -> new PedidoNoEncontradoException("Imposible actualizar. No se encontró el registro con el ID " + id + "."));
        entidadExistente.setUsuarioId(dtoActualizado.getUsuarioId());
        entidadExistente.setProductoId(dtoActualizado.getProductoId());
        entidadExistente.setCantidad(dtoActualizado.getCantidad());
        entidadExistente.setTotal(dtoActualizado.getTotal());
        entidadExistente.setEstado(dtoActualizado.getEstado());
        Pedido actualizado = this.pedidoRepository.save(entidadExistente);
        return convertirADTO(actualizado);
    }

    public void eliminar(Long id) {
        if (!this.pedidoRepository.existsById(id)) {
            throw new PedidoNoEncontradoException("Imposible eliminar. No existe registro con el ID " + id + ".");
        }
        this.pedidoRepository.deleteById(id);
    }

    public Map obtenerUsuarioDelPedido(Long id) {
        Pedido pedido = this.pedidoRepository.findById(id)
                .orElseThrow(() -> new PedidoNoEncontradoException("El pedido con el ID " + id + " no fue encontrado."));
        try {
            return this.webClientBuilder.build()
                    .get()
                    .uri("http://usuarios/api/usuarios/" + pedido.getUsuarioId())
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("No se pudo obtener el usuario del pedido: " + e.getMessage());
        }
    }

    public Map obtenerProductoDelPedido(Long id) {
        Pedido pedido = this.pedidoRepository.findById(id)
                .orElseThrow(() -> new PedidoNoEncontradoException("El pedido con el ID " + id + " no fue encontrado."));
        try {
            return this.webClientBuilder.build()
                    .get()
                    .uri("http://productos/api/productos/" + pedido.getProductoId())
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("No se pudo obtener el producto del pedido: " + e.getMessage());
        }
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

    private PedidoDTO convertirADTO(Pedido e) {
        return new PedidoDTO(e.getId(), e.getUsuarioId(), e.getProductoId(), e.getCantidad(), e.getTotal(), e.getEstado());
    }
}
