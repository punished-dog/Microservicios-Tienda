package tienda.envios.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import tienda.envios.dto.EnvioDTO;
import tienda.envios.service.EnvioService;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api/envios")
@Tag(name = "Envios", description = "Gestión de envios de la tienda")
public class EnvioController {
    private final EnvioService envioService;
    public EnvioController(EnvioService envioService) { this.envioService = envioService; }
    @GetMapping
    @Operation(summary = "Listar todos los envios")
    @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    public List<EnvioDTO> obtenerTodos() { return this.envioService.listarTodos(); }
    @GetMapping("/{id}")
    @Operation(summary = "Obtener envio por ID")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Envio encontrado"), @ApiResponse(responseCode = "404", description = "Envio no encontrado") })
    public ResponseEntity<EnvioDTO> obtenerPorId(@PathVariable Long id) { return ResponseEntity.ok(this.envioService.buscarPorId(id)); }
    @GetMapping("/pedido/{pedidoId}")
    @Operation(summary = "Obtener envios por pedido")
    public List<EnvioDTO> obtenerPorPedido(@PathVariable Long pedidoId) { return this.envioService.buscarPorPedido(pedidoId); }
    @GetMapping("/estado/{estado}")
    @Operation(summary = "Obtener envios por estado")
    public List<EnvioDTO> obtenerPorEstado(@PathVariable String estado) { return this.envioService.buscarPorEstado(estado); }
    @GetMapping("/{id}/pedido")
    @Operation(summary = "Obtener datos del pedido de un envio (comunicación REST)")
    @ApiResponse(responseCode = "200", description = "Datos del pedido obtenidos desde microservicio pedidos")
    public ResponseEntity<Map> obtenerPedidoDelEnvio(@PathVariable Long id) {
        return ResponseEntity.ok(this.envioService.obtenerPedidoDelEnvio(id));
    }
    @PostMapping
    @Operation(summary = "Crear un nuevo envio")
    @ApiResponse(responseCode = "201", description = "Envio creado exitosamente")
    public ResponseEntity<EnvioDTO> crear(@Valid @RequestBody EnvioDTO dto) { return ResponseEntity.status(HttpStatus.CREATED).body(this.envioService.crear(dto)); }
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar envio existente")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Envio actualizado"), @ApiResponse(responseCode = "404", description = "Envio no encontrado") })
    public ResponseEntity<EnvioDTO> actualizar(@PathVariable Long id, @Valid @RequestBody EnvioDTO dto) { return ResponseEntity.ok(this.envioService.actualizar(id, dto)); }
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar envio")
    @ApiResponses({ @ApiResponse(responseCode = "204", description = "Envio eliminado"), @ApiResponse(responseCode = "404", description = "Envio no encontrado") })
    public ResponseEntity<Void> eliminar(@PathVariable Long id) { this.envioService.eliminar(id); return ResponseEntity.noContent().build(); }
}
