package tienda.soporte.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import tienda.soporte.dto.TicketDTO;
import tienda.soporte.service.TicketService;
import java.util.List;
@RestController
@RequestMapping("/api/soporte")
@Tag(name = "Soporte", description = "Gestión de tickets de soporte")
public class TicketController {
    private final TicketService ticketService;
    public TicketController(TicketService ticketService) { this.ticketService = ticketService; }
    @GetMapping
    @Operation(summary = "Listar todos los registros")
    @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    public List<TicketDTO> obtenerTodos() { return this.ticketService.listarTodos(); }
    @GetMapping("/{id}")
    @Operation(summary = "Obtener registro por ID")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Registro encontrado"), @ApiResponse(responseCode = "404", description = "Registro no encontrado") })
    public ResponseEntity<TicketDTO> obtenerPorId(@PathVariable Long id) { return ResponseEntity.ok(this.ticketService.buscarPorId(id)); }
    @PostMapping
    @Operation(summary = "Crear un nuevo registro")
    @ApiResponse(responseCode = "201", description = "Registro creado exitosamente")
    public ResponseEntity<TicketDTO> crear(@Valid @RequestBody TicketDTO dto) { return ResponseEntity.status(HttpStatus.CREATED).body(this.ticketService.crear(dto)); }
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar registro existente")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Registro actualizado"), @ApiResponse(responseCode = "404", description = "Registro no encontrado") })
    public ResponseEntity<TicketDTO> actualizar(@PathVariable Long id, @Valid @RequestBody TicketDTO dto) { return ResponseEntity.ok(this.ticketService.actualizar(id, dto)); }
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar registro")
    @ApiResponses({ @ApiResponse(responseCode = "204", description = "Registro eliminado"), @ApiResponse(responseCode = "404", description = "Registro no encontrado") })
    public ResponseEntity<Void> eliminar(@PathVariable Long id) { this.ticketService.eliminar(id); return ResponseEntity.noContent().build(); }
    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Obtener tickets por usuario")
    public List<TicketDTO> obtenerPorUsuario(@PathVariable Long usuarioId) { return this.ticketService.buscarPorUsuario(usuarioId); }
    @GetMapping("/estado/{estado}")
    @Operation(summary = "Obtener tickets por estado")
    public List<TicketDTO> obtenerPorEstado(@PathVariable String estado) { return this.ticketService.buscarPorEstado(estado); }
    @GetMapping("/prioridad/{prioridad}")
    @Operation(summary = "Obtener tickets por prioridad")
    public List<TicketDTO> obtenerPorPrioridad(@PathVariable String prioridad) { return this.ticketService.buscarPorPrioridad(prioridad); }
}
