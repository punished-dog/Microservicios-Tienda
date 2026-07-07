package tienda.promociones;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tienda.promociones.dto.PromocionDTO;
import tienda.promociones.exception.PromocionNoEncontradoException;
import tienda.promociones.model.Promocion;
import tienda.promociones.repository.PromocionRepository;
import tienda.promociones.service.PromocionService;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class PromocionServiceTest {
    @Mock private PromocionRepository promocionRepository;
    @Mock private WebClient.Builder webClientBuilder;
    @Mock private WebClient webClient;
    @Mock private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock private WebClient.ResponseSpec responseSpec;
    private PromocionService promocionService;
    private Promocion promocion;
    private PromocionDTO dto;
    @BeforeEach
    void setUp() {
        when(webClientBuilder.build()).thenReturn(webClient);
        promocionService = new PromocionService(promocionRepository, webClientBuilder);
        promocion = new Promocion(1L, "Black Friday", "Descuento", 20.0, 1L, "2026-07-01", "2026-07-05", true);
        dto = new PromocionDTO(null, "Black Friday", "Descuento", 20.0, 1L, "2026-07-01", "2026-07-05", true);
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
        when(promocionRepository.findAll()).thenReturn(Arrays.asList(promocion));
        List<PromocionDTO> resultado = promocionService.listarTodos();
        assertEquals(1, resultado.size());
        assertEquals(20.0, resultado.get(0).getDescuento());
    }
    @Test
    void buscarPorId_conIdExistente_debeRetornarDTO() {
        when(promocionRepository.findById(1L)).thenReturn(Optional.of(promocion));
        PromocionDTO resultado = promocionService.buscarPorId(1L);
        assertNotNull(resultado);
        assertEquals(20.0, resultado.getDescuento());
    }
    @Test
    void buscarPorId_conIdInexistente_debeLanzarExcepcion() {
        when(promocionRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(PromocionNoEncontradoException.class, () -> promocionService.buscarPorId(99L));
    }
    @Test
    void crearPromocion_conDescuentoSuperior100_debeLanzarExcepcion() {
        PromocionDTO dtoInvalido = new PromocionDTO(null, "Test", "Test", 150.0, 1L, "2026-07-01", "2026-07-05", true);
        assertThrows(IllegalArgumentException.class, () -> promocionService.crear(dtoInvalido));
    }
    @Test
    void crear_valido_debeVerificarYGuardar() {
        mockWebClient();
        when(promocionRepository.save(any(Promocion.class))).thenReturn(promocion);
        PromocionDTO resultado = promocionService.crear(dto);
        assertNotNull(resultado);
        assertEquals(20.0, resultado.getDescuento());
        verify(promocionRepository, times(1)).save(any(Promocion.class));
    }
    @Test
    void actualizar_conIdExistente_debeActualizar() {
        when(promocionRepository.findById(1L)).thenReturn(Optional.of(promocion));
        when(promocionRepository.save(any(Promocion.class))).thenReturn(promocion);
        PromocionDTO resultado = promocionService.actualizar(1L, dto);
        assertNotNull(resultado);
        verify(promocionRepository).save(any(Promocion.class));
    }
    @Test
    void actualizar_conIdInexistente_debeLanzarExcepcion() {
        when(promocionRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(PromocionNoEncontradoException.class, () -> promocionService.actualizar(99L, dto));
    }
    @Test
    void eliminar_conIdExistente_debeEliminar() {
        when(promocionRepository.existsById(1L)).thenReturn(true);
        doNothing().when(promocionRepository).deleteById(1L);
        assertDoesNotThrow(() -> promocionService.eliminar(1L));
        verify(promocionRepository).deleteById(1L);
    }
    @Test
    void eliminar_conIdInexistente_debeLanzarExcepcion() {
        when(promocionRepository.existsById(99L)).thenReturn(false);
        assertThrows(PromocionNoEncontradoException.class, () -> promocionService.eliminar(99L));
    }
}
