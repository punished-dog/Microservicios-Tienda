package tienda.envios;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tienda.envios.dto.EnvioDTO;
import tienda.envios.exception.EnvioNoEncontradoException;
import tienda.envios.model.Envio;
import tienda.envios.repository.EnvioRepository;
import tienda.envios.service.EnvioService;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class EnvioServiceTest {
    @Mock private EnvioRepository envioRepository;
    @Mock private WebClient.Builder webClientBuilder;
    @Mock private WebClient webClient;
    @Mock private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock private WebClient.ResponseSpec responseSpec;
    private EnvioService envioService;
    private Envio envio;
    private EnvioDTO dto;
    @BeforeEach
    void setUp() {
        when(webClientBuilder.build()).thenReturn(webClient);
        envioService = new EnvioService(envioRepository, webClientBuilder);
        envio = new Envio(1L, 1L, 1L, "Av. Principal 123", "EN_CAMINO", "Chilexpress", "2026-07-01");
        dto = new EnvioDTO(null, 1L, 1L, "Av. Principal 123", "EN_CAMINO", "Chilexpress", "2026-07-01");
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
        when(envioRepository.findAll()).thenReturn(Arrays.asList(envio));
        List<EnvioDTO> resultado = envioService.listarTodos();
        assertEquals(1, resultado.size());
        assertEquals("EN_CAMINO", resultado.get(0).getEstado());
    }
    @Test
    void buscarPorId_conIdExistente_debeRetornarDTO() {
        when(envioRepository.findById(1L)).thenReturn(Optional.of(envio));
        EnvioDTO resultado = envioService.buscarPorId(1L);
        assertNotNull(resultado);
        assertEquals("EN_CAMINO", resultado.getEstado());
    }
    @Test
    void buscarPorId_conIdInexistente_debeLanzarExcepcion() {
        when(envioRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EnvioNoEncontradoException.class, () -> envioService.buscarPorId(99L));
    }
    @Test
    void crearEnvio_sinDireccion_debeLanzarExcepcion() {
        EnvioDTO dtoInvalido = new EnvioDTO(null, 1L, 1L, null, "EN_CAMINO", "Chilexpress", "2026-07-01");
        assertThrows(IllegalArgumentException.class, () -> envioService.crear(dtoInvalido));
    }
    @Test
    void crear_valido_debeVerificarYGuardar() {
        mockWebClient();
        when(envioRepository.save(any(Envio.class))).thenReturn(envio);
        EnvioDTO resultado = envioService.crear(dto);
        assertNotNull(resultado);
        assertEquals("EN_CAMINO", resultado.getEstado());
        verify(envioRepository, times(1)).save(any(Envio.class));
    }
    @Test
    void actualizar_conIdExistente_debeActualizar() {
        when(envioRepository.findById(1L)).thenReturn(Optional.of(envio));
        when(envioRepository.save(any(Envio.class))).thenReturn(envio);
        EnvioDTO resultado = envioService.actualizar(1L, dto);
        assertNotNull(resultado);
        verify(envioRepository).save(any(Envio.class));
    }
    @Test
    void actualizar_conIdInexistente_debeLanzarExcepcion() {
        when(envioRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EnvioNoEncontradoException.class, () -> envioService.actualizar(99L, dto));
    }
    @Test
    void eliminar_conIdExistente_debeEliminar() {
        when(envioRepository.existsById(1L)).thenReturn(true);
        doNothing().when(envioRepository).deleteById(1L);
        assertDoesNotThrow(() -> envioService.eliminar(1L));
        verify(envioRepository).deleteById(1L);
    }
    @Test
    void eliminar_conIdInexistente_debeLanzarExcepcion() {
        when(envioRepository.existsById(99L)).thenReturn(false);
        assertThrows(EnvioNoEncontradoException.class, () -> envioService.eliminar(99L));
    }
}
