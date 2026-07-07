package tienda.pedidos.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import tienda.pedidos.dto.PedidoDTO;
import tienda.pedidos.service.PedidoService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pedidos")
@Tag(name = "Pedidos", description = "Gestión de pedidos de la tienda")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) { this.pedidoService = pedidoService; }

    @GetMapping
    @Operation(summary = "Listar todos los pedidos")
    @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    public List<PedidoDTO> obtenerTodos() { return this.pedidoService.listarTodos(); }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener pedido por ID")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Pedido encontrado"), @ApiResponse(responseCode = "404", description = "Pedido no encontrado") })
    public ResponseEntity<PedidoDTO> obtenerPorId(@PathVariable Long id) { return ResponseEntity.ok(this.pedidoService.buscarPorId(id)); }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Obtener pedidos por usuario")
    public List<PedidoDTO> obtenerPorUsuario(@PathVariable Long usuarioId) { return this.pedidoService.buscarPorUsuario(usuarioId); }

    @GetMapping("/estado/{estado}")
    @Operation(summary = "Obtener pedidos por estado")
    public List<PedidoDTO> obtenerPorEstado(@PathVariable String estado) { return this.pedidoService.buscarPorEstado(estado); }

    @GetMapping("/{id}/usuario")
    @Operation(summary = "Obtener datos del usuario de un pedido (comunicación REST)")
    @ApiResponse(responseCode = "200", description = "Datos del usuario obtenidos desde microservicio usuarios")
    public ResponseEntity<Map> obtenerUsuarioDePedido(@PathVariable Long id) {
        return ResponseEntity.ok(this.pedidoService.obtenerUsuarioDelPedido(id));
    }

    @GetMapping("/{id}/producto")
    @Operation(summary = "Obtener datos del producto de un pedido (comunicación REST)")
    @ApiResponse(responseCode = "200", description = "Datos del producto obtenidos desde microservicio productos")
    public ResponseEntity<Map> obtenerProductoDePedido(@PathVariable Long id) {
        return ResponseEntity.ok(this.pedidoService.obtenerProductoDelPedido(id));
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo pedido")
    @ApiResponse(responseCode = "201", description = "Pedido creado exitosamente")
    public ResponseEntity<PedidoDTO> crear(@Valid @RequestBody PedidoDTO dto) { return ResponseEntity.status(HttpStatus.CREATED).body(this.pedidoService.crear(dto)); }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar pedido existente")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Pedido actualizado"), @ApiResponse(responseCode = "404", description = "Pedido no encontrado") })
    public ResponseEntity<PedidoDTO> actualizar(@PathVariable Long id, @Valid @RequestBody PedidoDTO dto) { return ResponseEntity.ok(this.pedidoService.actualizar(id, dto)); }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar pedido")
    @ApiResponses({ @ApiResponse(responseCode = "204", description = "Pedido eliminado"), @ApiResponse(responseCode = "404", description = "Pedido no encontrado") })
    public ResponseEntity<Void> eliminar(@PathVariable Long id) { this.pedidoService.eliminar(id); return ResponseEntity.noContent().build(); }
}
