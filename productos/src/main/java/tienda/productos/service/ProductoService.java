package tienda.productos.service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tienda.productos.dto.ProductoDTO;
import tienda.productos.exception.ProductoNoEncontradoException;
import tienda.productos.model.Producto;
import tienda.productos.repository.ProductoRepository;
import java.util.ArrayList;
import java.util.List;
@Service
public class ProductoService {

    private static final Logger log = LoggerFactory.getLogger(ProductoService.class);
    private final ProductoRepository productoRepository;
    public ProductoService(ProductoRepository productoRepository) { this.productoRepository = productoRepository; }
    public List<ProductoDTO> listarTodos() {
        log.info("Listando todos los productos");
        List<Producto> entidades = this.productoRepository.findAll();
        List<ProductoDTO> dtos = new ArrayList<>();
        for (Producto e : entidades) { dtos.add(convertirADTO(e)); }
        return dtos;
    }
    public ProductoDTO buscarPorId(Long id) {
        log.info("Buscando producto con id: {}", id);
        Producto entidad = this.productoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Producto no encontrado con id: {}", id);
                    return new ProductoNoEncontradoException("El producto con el ID " + id + " no fue encontrado.");
                });
        return convertirADTO(entidad);
    }
    public List<ProductoDTO> buscarPorCategoria(String categoria) {
        List<Producto> entidades = this.productoRepository.findByCategoria(categoria);
        List<ProductoDTO> dtos = new ArrayList<>();
        for (Producto e : entidades) { dtos.add(convertirADTO(e)); }
        return dtos;
    }
    public ProductoDTO crearProducto(ProductoDTO dto) {
        if (dto.getPrecio() != null && dto.getPrecio() < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo.");
        }
        Producto entidad = new Producto();
        entidad.setNombre(dto.getNombre()); entidad.setDescripcion(dto.getDescripcion());
        entidad.setPrecio(dto.getPrecio()); entidad.setCategoria(dto.getCategoria());
        Producto guardado = this.productoRepository.save(entidad);
        return convertirADTO(guardado);
    }
    public ProductoDTO actualizarProducto(Long id, ProductoDTO dtoActualizado) {
        Producto entidadExistente = this.productoRepository.findById(id)
                .orElseThrow(() -> new ProductoNoEncontradoException("Imposible actualizar. No se encontró el producto con el ID " + id + "."));
        if (dtoActualizado.getPrecio() != null && dtoActualizado.getPrecio() < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo.");
        }
        entidadExistente.setNombre(dtoActualizado.getNombre()); entidadExistente.setDescripcion(dtoActualizado.getDescripcion());
        entidadExistente.setPrecio(dtoActualizado.getPrecio()); entidadExistente.setCategoria(dtoActualizado.getCategoria());
        Producto actualizado = this.productoRepository.save(entidadExistente);
        return convertirADTO(actualizado);
    }
    public void eliminarProducto(Long id) {
        if (!this.productoRepository.existsById(id)) {
            throw new ProductoNoEncontradoException("Imposible eliminar. No existe producto con el ID " + id + ".");
        }
        this.productoRepository.deleteById(id);
    }
    private ProductoDTO convertirADTO(Producto e) {
        return new ProductoDTO(e.getId(), e.getNombre(), e.getDescripcion(), e.getPrecio(), e.getCategoria());
    }
}
