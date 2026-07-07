package tienda.pagos.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import tienda.pagos.dto.PagoDTO;
import tienda.pagos.service.PagoService;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api/pagos")
@Tag(name = "Pagos", description = "Gestión de pagos de la tienda")
public class PagoController {
    private final PagoService pagoService;
    public PagoController(PagoService pagoService) { this.pagoService = pagoService; }
    @GetMapping
    @Operation(summary = "Listar todos los pagos")
    @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    public List<PagoDTO> obtenerTodos() { return this.pagoService.listarTodos(); }
    @GetMapping("/{id}")
    @Operation(summary = "Obtener pago por ID")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Pago encontrado"), @ApiResponse(responseCode = "404", description = "Pago no encontrado") })
    public ResponseEntity<PagoDTO> obtenerPorId(@PathVariable Long id) { return ResponseEntity.ok(this.pagoService.buscarPorId(id)); }
    @GetMapping("/pedido/{pedidoId}")
    @Operation(summary = "Obtener pagos por pedido")
    public List<PagoDTO> obtenerPorPedido(@PathVariable Long pedidoId) { return this.pagoService.buscarPorPedido(pedidoId); }
    @GetMapping("/estado/{estado}")
    @Operation(summary = "Obtener pagos por estado")
    public List<PagoDTO> obtenerPorEstado(@PathVariable String estado) { return this.pagoService.buscarPorEstado(estado); }
    @GetMapping("/{id}/pedido")
    @Operation(summary = "Obtener datos del pedido asociado al pago (comunicación REST)")
    @ApiResponse(responseCode = "200", description = "Datos del pedido obtenidos desde microservicio pedidos")
    public ResponseEntity<Map> obtenerPedidoDelPago(@PathVariable Long id) {
        return ResponseEntity.ok(this.pagoService.obtenerPedidoDelPago(id));
    }
    @PostMapping
    @Operation(summary = "Crear un nuevo pago")
    @ApiResponse(responseCode = "201", description = "Pago creado exitosamente")
    public ResponseEntity<PagoDTO> crear(@Valid @RequestBody PagoDTO dto) { return ResponseEntity.status(HttpStatus.CREATED).body(this.pagoService.crear(dto)); }
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar pago existente")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Pago actualizado"), @ApiResponse(responseCode = "404", description = "Pago no encontrado") })
    public ResponseEntity<PagoDTO> actualizar(@PathVariable Long id, @Valid @RequestBody PagoDTO dto) { return ResponseEntity.ok(this.pagoService.actualizar(id, dto)); }
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar pago")
    @ApiResponses({ @ApiResponse(responseCode = "204", description = "Pago eliminado"), @ApiResponse(responseCode = "404", description = "Pago no encontrado") })
    public ResponseEntity<Void> eliminar(@PathVariable Long id) { this.pagoService.eliminar(id); return ResponseEntity.noContent().build(); }
}
