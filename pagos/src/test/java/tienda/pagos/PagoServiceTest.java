package tienda.pagos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tienda.pagos.dto.PagoDTO;
import tienda.pagos.exception.PagoNoEncontradoException;
import tienda.pagos.model.Pago;
import tienda.pagos.repository.PagoRepository;
import tienda.pagos.service.PagoService;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class PagoServiceTest {
    @Mock private PagoRepository pagoRepository;
    @Mock private WebClient.Builder webClientBuilder;
    @Mock private WebClient webClient;
    @Mock private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock private WebClient.ResponseSpec responseSpec;
    private PagoService pagoService;
    private Pago pago;
    private PagoDTO dto;
    @BeforeEach
    void setUp() {
        when(webClientBuilder.build()).thenReturn(webClient);
        pagoService = new PagoService(pagoRepository, webClientBuilder);
        pago = new Pago(1L, 1L, 1799980.0, "TARJETA", "COMPLETADO");
        dto = new PagoDTO(null, 1L, 1799980.0, "TARJETA", "COMPLETADO");
    }
    @SuppressWarnings("unchecked")
    private void mockWebClient() {
        when(webClient.get()).thenReturn((WebClient.RequestHeadersUriSpec) requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Map.class)).thenReturn(Mono.just(Map.of("id", 1)));
    }
    @Test
    void listarTodos_debeRetornarLista() {
        when(pagoRepository.findAll()).thenReturn(Arrays.asList(pago));
        List<PagoDTO> resultado = pagoService.listarTodos();
        assertEquals(1, resultado.size());
        assertEquals("COMPLETADO", resultado.get(0).getEstado());
    }
    @Test
    void buscarPorId_conIdExistente_debeRetornarDTO() {
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pago));
        PagoDTO resultado = pagoService.buscarPorId(1L);
        assertNotNull(resultado);
        assertEquals("COMPLETADO", resultado.getEstado());
    }
    @Test
    void buscarPorId_conIdInexistente_debeLanzarExcepcion() {
        when(pagoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(PagoNoEncontradoException.class, () -> pagoService.buscarPorId(99L));
    }
    @Test
    void crearPago_conMontoInvalido_debeLanzarExcepcion() {
        PagoDTO dtoInvalido = new PagoDTO(null, 1L, 0.0, "TARJETA", "PENDIENTE");
        assertThrows(IllegalArgumentException.class, () -> pagoService.crear(dtoInvalido));
    }
    @Test
    void crear_valido_debeVerificarYGuardar() {
        mockWebClient();
        when(pagoRepository.save(any(Pago.class))).thenReturn(pago);
        PagoDTO resultado = pagoService.crear(dto);
        assertNotNull(resultado);
        assertEquals("COMPLETADO", resultado.getEstado());
        verify(pagoRepository, times(1)).save(any(Pago.class));
    }
    @Test
    void actualizar_conIdExistente_debeActualizar() {
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pago));
        when(pagoRepository.save(any(Pago.class))).thenReturn(pago);
        PagoDTO resultado = pagoService.actualizar(1L, dto);
        assertNotNull(resultado);
        verify(pagoRepository).save(any(Pago.class));
    }
    @Test
    void actualizar_conIdInexistente_debeLanzarExcepcion() {
        when(pagoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(PagoNoEncontradoException.class, () -> pagoService.actualizar(99L, dto));
    }
    @Test
    void eliminar_conIdExistente_debeEliminar() {
        when(pagoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(pagoRepository).deleteById(1L);
        assertDoesNotThrow(() -> pagoService.eliminar(1L));
        verify(pagoRepository).deleteById(1L);
    }
    @Test
    void eliminar_conIdInexistente_debeLanzarExcepcion() {
        when(pagoRepository.existsById(99L)).thenReturn(false);
        assertThrows(PagoNoEncontradoException.class, () -> pagoService.eliminar(99L));
    }
}
