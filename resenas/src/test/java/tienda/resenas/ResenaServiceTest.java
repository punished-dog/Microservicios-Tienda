package tienda.resenas;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tienda.resenas.dto.ResenaDTO;
import tienda.resenas.exception.ResenaNoEncontradoException;
import tienda.resenas.model.Resena;
import tienda.resenas.repository.ResenaRepository;
import tienda.resenas.service.ResenaService;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class ResenaServiceTest {
    @Mock private ResenaRepository resenaRepository;
    @Mock private WebClient.Builder webClientBuilder;
    @Mock private WebClient webClient;
    @Mock private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock private WebClient.ResponseSpec responseSpec;
    private ResenaService resenaService;
    private Resena resena;
    private ResenaDTO dto;
    @BeforeEach
    void setUp() {
        when(webClientBuilder.build()).thenReturn(webClient);
        resenaService = new ResenaService(resenaRepository, webClientBuilder);
        resena = new Resena(1L, 1L, 1L, 5, "Excelente producto");
        dto = new ResenaDTO(null, 1L, 1L, 5, "Excelente producto");
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
        when(resenaRepository.findAll()).thenReturn(Arrays.asList(resena));
        List<ResenaDTO> resultado = resenaService.listarTodos();
        assertEquals(1, resultado.size());
        assertEquals(5, resultado.get(0).getCalificacion());
    }
    @Test
    void buscarPorId_conIdExistente_debeRetornarDTO() {
        when(resenaRepository.findById(1L)).thenReturn(Optional.of(resena));
        ResenaDTO resultado = resenaService.buscarPorId(1L);
        assertNotNull(resultado);
        assertEquals(5, resultado.getCalificacion());
    }
    @Test
    void buscarPorId_conIdInexistente_debeLanzarExcepcion() {
        when(resenaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResenaNoEncontradoException.class, () -> resenaService.buscarPorId(99L));
    }
    @Test
    void crearResena_conCalificacionFueraDeRango_debeLanzarExcepcion() {
        ResenaDTO dtoInvalido = new ResenaDTO(null, 1L, 1L, 6, "Bueno");
        assertThrows(IllegalArgumentException.class, () -> resenaService.crear(dtoInvalido));
    }
    @Test
    void crear_valido_debeVerificarYGuardar() {
        mockWebClient();
        when(resenaRepository.save(any(Resena.class))).thenReturn(resena);
        ResenaDTO resultado = resenaService.crear(dto);
        assertNotNull(resultado);
        assertEquals(5, resultado.getCalificacion());
        verify(resenaRepository, times(1)).save(any(Resena.class));
    }
    @Test
    void actualizar_conIdExistente_debeActualizar() {
        when(resenaRepository.findById(1L)).thenReturn(Optional.of(resena));
        when(resenaRepository.save(any(Resena.class))).thenReturn(resena);
        ResenaDTO resultado = resenaService.actualizar(1L, dto);
        assertNotNull(resultado);
        verify(resenaRepository).save(any(Resena.class));
    }
    @Test
    void actualizar_conIdInexistente_debeLanzarExcepcion() {
        when(resenaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResenaNoEncontradoException.class, () -> resenaService.actualizar(99L, dto));
    }
    @Test
    void eliminar_conIdExistente_debeEliminar() {
        when(resenaRepository.existsById(1L)).thenReturn(true);
        doNothing().when(resenaRepository).deleteById(1L);
        assertDoesNotThrow(() -> resenaService.eliminar(1L));
        verify(resenaRepository).deleteById(1L);
    }
    @Test
    void eliminar_conIdInexistente_debeLanzarExcepcion() {
        when(resenaRepository.existsById(99L)).thenReturn(false);
        assertThrows(ResenaNoEncontradoException.class, () -> resenaService.eliminar(99L));
    }
}
