package tienda.inventario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tienda.inventario.dto.InventarioDTO;
import tienda.inventario.exception.InventarioNoEncontradoException;
import tienda.inventario.model.Inventario;
import tienda.inventario.repository.InventarioRepository;
import tienda.inventario.service.InventarioService;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class InventarioServiceTest {
    @Mock private InventarioRepository inventarioRepository;
    @Mock private WebClient.Builder webClientBuilder;
    @Mock private WebClient webClient;
    @Mock private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock private WebClient.ResponseSpec responseSpec;
    private InventarioService inventarioService;
    private Inventario inventario;
    private InventarioDTO dto;
    @BeforeEach
    void setUp() {
        when(webClientBuilder.build()).thenReturn(webClient);
        inventarioService = new InventarioService(inventarioRepository, webClientBuilder);
        inventario = new Inventario(1L, 1L, 50, 5, "Bodega A");
        dto = new InventarioDTO(null, 1L, 50, 5, "Bodega A");
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
        when(inventarioRepository.findAll()).thenReturn(Arrays.asList(inventario));
        List<InventarioDTO> resultado = inventarioService.listarTodos();
        assertEquals(1, resultado.size());
        assertEquals("Bodega A", resultado.get(0).getUbicacion());
    }
    @Test
    void buscarPorId_conIdExistente_debeRetornarDTO() {
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(inventario));
        InventarioDTO resultado = inventarioService.buscarPorId(1L);
        assertNotNull(resultado);
        assertEquals("Bodega A", resultado.getUbicacion());
    }
    @Test
    void buscarPorId_conIdInexistente_debeLanzarExcepcion() {
        when(inventarioRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(InventarioNoEncontradoException.class, () -> inventarioService.buscarPorId(99L));
    }
    @Test
    void crearInventario_conStockMinimoNegativo_debeLanzarExcepcion() {
        InventarioDTO dtoInvalido = new InventarioDTO(null, 1L, 50, -1, "X");
        assertThrows(IllegalArgumentException.class, () -> inventarioService.crear(dtoInvalido));
    }
    @Test
    void crear_valido_debeVerificarYGuardar() {
        mockWebClient();
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(inventario);
        InventarioDTO resultado = inventarioService.crear(dto);
        assertNotNull(resultado);
        assertEquals("Bodega A", resultado.getUbicacion());
        verify(inventarioRepository, times(1)).save(any(Inventario.class));
    }
    @Test
    void actualizar_conIdExistente_debeActualizar() {
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(inventario));
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(inventario);
        InventarioDTO resultado = inventarioService.actualizar(1L, dto);
        assertNotNull(resultado);
        verify(inventarioRepository).save(any(Inventario.class));
    }
    @Test
    void actualizar_conIdInexistente_debeLanzarExcepcion() {
        when(inventarioRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(InventarioNoEncontradoException.class, () -> inventarioService.actualizar(99L, dto));
    }
    @Test
    void eliminar_conIdExistente_debeEliminar() {
        when(inventarioRepository.existsById(1L)).thenReturn(true);
        doNothing().when(inventarioRepository).deleteById(1L);
        assertDoesNotThrow(() -> inventarioService.eliminar(1L));
        verify(inventarioRepository).deleteById(1L);
    }
    @Test
    void eliminar_conIdInexistente_debeLanzarExcepcion() {
        when(inventarioRepository.existsById(99L)).thenReturn(false);
        assertThrows(InventarioNoEncontradoException.class, () -> inventarioService.eliminar(99L));
    }
}
