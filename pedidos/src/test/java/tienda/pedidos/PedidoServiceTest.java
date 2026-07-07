package tienda.pedidos;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tienda.pedidos.dto.PedidoDTO;
import tienda.pedidos.exception.PedidoNoEncontradoException;
import tienda.pedidos.model.Pedido;
import tienda.pedidos.repository.PedidoRepository;
import tienda.pedidos.service.PedidoService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;
    @Mock
    private WebClient.Builder webClientBuilder;
    @Mock
    private WebClient webClient;
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock
    private WebClient.ResponseSpec responseSpec;

    private PedidoService pedidoService;
    private Pedido pedido;
    private PedidoDTO dto;

    @BeforeEach
    void setUp() {
        when(webClientBuilder.build()).thenReturn(webClient);
        pedidoService = new PedidoService(pedidoRepository, webClientBuilder);
        pedido = new Pedido(1L, 1L, 1L, 2, 1799980.0, "PENDIENTE");
        dto = new PedidoDTO(null, 1L, 1L, 2, 1799980.0, "PENDIENTE");
    }

    @SuppressWarnings("unchecked")
    private void mockWebClientVerificacion() {
        when(webClient.get()).thenReturn((WebClient.RequestHeadersUriSpec) requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Map.class)).thenReturn(Mono.just(Map.of("id", 1)));
    }

    @Test
    void listarTodos_debeRetornarLista() {
        when(pedidoRepository.findAll()).thenReturn(Arrays.asList(pedido));
        List<PedidoDTO> resultado = pedidoService.listarTodos();
        assertEquals(1, resultado.size());
        assertEquals("PENDIENTE", resultado.get(0).getEstado());
    }

    @Test
    void buscarPorId_conIdExistente_debeRetornarDTO() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        PedidoDTO resultado = pedidoService.buscarPorId(1L);
        assertNotNull(resultado);
        assertEquals("PENDIENTE", resultado.getEstado());
    }

    @Test
    void buscarPorId_conIdInexistente_debeLanzarExcepcion() {
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(PedidoNoEncontradoException.class, () -> pedidoService.buscarPorId(99L));
    }

    @Test
    void crearPedido_conTotalNegativo_debeLanzarExcepcion() {
        PedidoDTO dtoInvalido = new PedidoDTO(null, 1L, 1L, 2, -100.0, "PENDIENTE");
        assertThrows(IllegalArgumentException.class, () -> pedidoService.crear(dtoInvalido));
    }

    @Test
    void crear_valido_debeVerificarYGuardar() {
        mockWebClientVerificacion();
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);
        PedidoDTO resultado = pedidoService.crear(dto);
        assertNotNull(resultado);
        assertEquals("PENDIENTE", resultado.getEstado());
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
    }

    @Test
    void actualizar_conIdExistente_debeActualizar() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);
        PedidoDTO resultado = pedidoService.actualizar(1L, dto);
        assertNotNull(resultado);
        verify(pedidoRepository).save(any(Pedido.class));
    }

    @Test
    void actualizar_conIdInexistente_debeLanzarExcepcion() {
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(PedidoNoEncontradoException.class, () -> pedidoService.actualizar(99L, dto));
    }

    @Test
    void eliminar_conIdExistente_debeEliminar() {
        when(pedidoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(pedidoRepository).deleteById(1L);
        assertDoesNotThrow(() -> pedidoService.eliminar(1L));
        verify(pedidoRepository).deleteById(1L);
    }

    @Test
    void eliminar_conIdInexistente_debeLanzarExcepcion() {
        when(pedidoRepository.existsById(99L)).thenReturn(false);
        assertThrows(PedidoNoEncontradoException.class, () -> pedidoService.eliminar(99L));
    }
}
