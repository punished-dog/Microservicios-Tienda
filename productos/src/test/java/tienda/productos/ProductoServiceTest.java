package tienda.productos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tienda.productos.dto.ProductoDTO;
import tienda.productos.exception.ProductoNoEncontradoException;
import tienda.productos.model.Producto;
import tienda.productos.repository.ProductoRepository;
import tienda.productos.service.ProductoService;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {
    @Mock
    private ProductoRepository productoRepository;
    @InjectMocks
    private ProductoService productoService;
    private Producto producto;
    private ProductoDTO dto;
    @BeforeEach
    void setUp() {
        producto = new Producto(1L, "Laptop Gamer", "Laptop gaming", 899990.0, "Electronica");
        dto = new ProductoDTO(null, "Laptop Gamer", "Laptop gaming", 899990.0, "Electronica");
    }
    @Test
    void listarTodos_debeRetornarLista() {
        when(productoRepository.findAll()).thenReturn(Arrays.asList(producto));
        List<ProductoDTO> resultado = productoService.listarTodos();
        assertEquals(1, resultado.size());
        assertEquals("Laptop Gamer", resultado.get(0).getNombre());
    }
    @Test
    void buscarPorId_conIdExistente_debeRetornarProducto() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        ProductoDTO resultado = productoService.buscarPorId(1L);
        assertNotNull(resultado);
        assertEquals("Electronica", resultado.getCategoria());
    }
    @Test
    void buscarPorId_conIdInexistente_debeLanzarExcepcion() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ProductoNoEncontradoException.class, () -> productoService.buscarPorId(99L));
    }
    @Test
    void crearProducto_conPrecioNegativo_debeLanzarExcepcion() {
        ProductoDTO dtoPrecioNegativo = new ProductoDTO(null, "Test", "Test", -100.0, "Test");
        assertThrows(IllegalArgumentException.class, () -> productoService.crearProducto(dtoPrecioNegativo));
    }
    @Test
    void crearProducto_valido_debeGuardar() {
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);
        ProductoDTO resultado = productoService.crearProducto(dto);
        assertNotNull(resultado);
        assertEquals(899990.0, resultado.getPrecio());
        verify(productoRepository, times(1)).save(any(Producto.class));
    }
    @Test
    void actualizarProducto_conIdExistente_debeActualizar() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);
        ProductoDTO resultado = productoService.actualizarProducto(1L, dto);
        assertNotNull(resultado);
        verify(productoRepository).save(any(Producto.class));
    }
    @Test
    void actualizarProducto_conIdInexistente_debeLanzarExcepcion() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ProductoNoEncontradoException.class, () -> productoService.actualizarProducto(99L, dto));
    }
    @Test
    void eliminarProducto_conIdExistente_debeEliminar() {
        when(productoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productoRepository).deleteById(1L);
        assertDoesNotThrow(() -> productoService.eliminarProducto(1L));
        verify(productoRepository).deleteById(1L);
    }
    @Test
    void eliminarProducto_conIdInexistente_debeLanzarExcepcion() {
        when(productoRepository.existsById(99L)).thenReturn(false);
        assertThrows(ProductoNoEncontradoException.class, () -> productoService.eliminarProducto(99L));
    }
}
