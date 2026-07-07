package tienda.carrito;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tienda.carrito.dto.ItemCarritoDTO;
import tienda.carrito.exception.ItemCarritoNoEncontradoException;
import tienda.carrito.model.ItemCarrito;
import tienda.carrito.repository.ItemCarritoRepository;
import tienda.carrito.service.ItemCarritoService;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class ItemCarritoServiceTest {
    @Mock private ItemCarritoRepository itemCarritoRepository;
    @Mock private WebClient.Builder webClientBuilder;
    @Mock private WebClient webClient;
    @Mock private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock private WebClient.ResponseSpec responseSpec;
    private ItemCarritoService itemCarritoService;
    private ItemCarrito itemCarrito;
    private ItemCarritoDTO dto;
    @BeforeEach
    void setUp() {
        when(webClientBuilder.build()).thenReturn(webClient);
        itemCarritoService = new ItemCarritoService(itemCarritoRepository, webClientBuilder);
        itemCarrito = new ItemCarrito(1L, 1L, 1L, 2, 899990.0);
        dto = new ItemCarritoDTO(null, 1L, 1L, 2, 899990.0);
    }
    @SuppressWarnings("unchecked")
    private void mockWebClient() {
        when(webClient.get()).thenReturn((WebClient.RequestHeadersUriSpec) requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Map.class)).thenReturn(Mono.just(Map.of("id", 1, "precio", 899990.0)));
    }
    @Test
    void listarTodos_debeRetornarLista() {
        when(itemCarritoRepository.findAll()).thenReturn(Arrays.asList(itemCarrito));
        List<ItemCarritoDTO> resultado = itemCarritoService.listarTodos();
        assertEquals(1, resultado.size());
        assertEquals(899990.0, resultado.get(0).getPrecioUnitario());
    }
    @Test
    void buscarPorId_conIdExistente_debeRetornarDTO() {
        when(itemCarritoRepository.findById(1L)).thenReturn(Optional.of(itemCarrito));
        ItemCarritoDTO resultado = itemCarritoService.buscarPorId(1L);
        assertNotNull(resultado);
        assertEquals(899990.0, resultado.getPrecioUnitario());
    }
    @Test
    void buscarPorId_conIdInexistente_debeLanzarExcepcion() {
        when(itemCarritoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ItemCarritoNoEncontradoException.class, () -> itemCarritoService.buscarPorId(99L));
    }
    @Test
    void crearItemCarrito_conCantidadCero_debeLanzarExcepcion() {
        ItemCarritoDTO dtoInvalido = new ItemCarritoDTO(null, 1L, 1L, 0, 899990.0);
        assertThrows(IllegalArgumentException.class, () -> itemCarritoService.crear(dtoInvalido));
    }
    @Test
    void crear_valido_debeVerificarYGuardar() {
        mockWebClient();
        when(itemCarritoRepository.save(any(ItemCarrito.class))).thenReturn(itemCarrito);
        ItemCarritoDTO resultado = itemCarritoService.crear(dto);
        assertNotNull(resultado);
        assertEquals(899990.0, resultado.getPrecioUnitario());
        verify(itemCarritoRepository, times(1)).save(any(ItemCarrito.class));
    }
    @Test
    void actualizar_conIdExistente_debeActualizar() {
        when(itemCarritoRepository.findById(1L)).thenReturn(Optional.of(itemCarrito));
        when(itemCarritoRepository.save(any(ItemCarrito.class))).thenReturn(itemCarrito);
        ItemCarritoDTO resultado = itemCarritoService.actualizar(1L, dto);
        assertNotNull(resultado);
        verify(itemCarritoRepository).save(any(ItemCarrito.class));
    }
    @Test
    void actualizar_conIdInexistente_debeLanzarExcepcion() {
        when(itemCarritoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ItemCarritoNoEncontradoException.class, () -> itemCarritoService.actualizar(99L, dto));
    }
    @Test
    void eliminar_conIdExistente_debeEliminar() {
        when(itemCarritoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(itemCarritoRepository).deleteById(1L);
        assertDoesNotThrow(() -> itemCarritoService.eliminar(1L));
        verify(itemCarritoRepository).deleteById(1L);
    }
    @Test
    void eliminar_conIdInexistente_debeLanzarExcepcion() {
        when(itemCarritoRepository.existsById(99L)).thenReturn(false);
        assertThrows(ItemCarritoNoEncontradoException.class, () -> itemCarritoService.eliminar(99L));
    }
}
