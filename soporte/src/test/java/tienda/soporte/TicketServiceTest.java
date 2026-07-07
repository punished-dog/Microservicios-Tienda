package tienda.soporte;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tienda.soporte.dto.TicketDTO;
import tienda.soporte.exception.TicketNoEncontradoException;
import tienda.soporte.model.Ticket;
import tienda.soporte.repository.TicketRepository;
import tienda.soporte.service.TicketService;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class TicketServiceTest {
    @Mock private TicketRepository ticketRepository;
    @Mock private WebClient.Builder webClientBuilder;
    @Mock private WebClient webClient;
    @Mock private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock private WebClient.ResponseSpec responseSpec;
    private TicketService ticketService;
    private Ticket ticket;
    private TicketDTO dto;
    @BeforeEach
    void setUp() {
        when(webClientBuilder.build()).thenReturn(webClient);
        ticketService = new TicketService(ticketRepository, webClientBuilder);
        ticket = new Ticket(1L, 1L, "Problema con pedido", "No llegó", "ABIERTO", "ALTA");
        dto = new TicketDTO(null, 1L, "Problema con pedido", "No llegó", "ABIERTO", "ALTA");
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
        when(ticketRepository.findAll()).thenReturn(Arrays.asList(ticket));
        List<TicketDTO> resultado = ticketService.listarTodos();
        assertEquals(1, resultado.size());
        assertEquals("ALTA", resultado.get(0).getPrioridad());
    }
    @Test
    void buscarPorId_conIdExistente_debeRetornarDTO() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        TicketDTO resultado = ticketService.buscarPorId(1L);
        assertNotNull(resultado);
        assertEquals("ALTA", resultado.getPrioridad());
    }
    @Test
    void buscarPorId_conIdInexistente_debeLanzarExcepcion() {
        when(ticketRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(TicketNoEncontradoException.class, () -> ticketService.buscarPorId(99L));
    }
    @Test
    void crearTicket_sinAsunto_debeLanzarExcepcion() {
        TicketDTO dtoInvalido = new TicketDTO(null, 1L, null, "Sin asunto", "ABIERTO", "ALTA");
        assertThrows(IllegalArgumentException.class, () -> ticketService.crear(dtoInvalido));
    }
    @Test
    void crear_valido_debeVerificarYGuardar() {
        mockWebClient();
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);
        TicketDTO resultado = ticketService.crear(dto);
        assertNotNull(resultado);
        assertEquals("ALTA", resultado.getPrioridad());
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }
    @Test
    void actualizar_conIdExistente_debeActualizar() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);
        TicketDTO resultado = ticketService.actualizar(1L, dto);
        assertNotNull(resultado);
        verify(ticketRepository).save(any(Ticket.class));
    }
    @Test
    void actualizar_conIdInexistente_debeLanzarExcepcion() {
        when(ticketRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(TicketNoEncontradoException.class, () -> ticketService.actualizar(99L, dto));
    }
    @Test
    void eliminar_conIdExistente_debeEliminar() {
        when(ticketRepository.existsById(1L)).thenReturn(true);
        doNothing().when(ticketRepository).deleteById(1L);
        assertDoesNotThrow(() -> ticketService.eliminar(1L));
        verify(ticketRepository).deleteById(1L);
    }
    @Test
    void eliminar_conIdInexistente_debeLanzarExcepcion() {
        when(ticketRepository.existsById(99L)).thenReturn(false);
        assertThrows(TicketNoEncontradoException.class, () -> ticketService.eliminar(99L));
    }
}
