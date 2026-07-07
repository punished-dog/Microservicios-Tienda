package tienda.carrito.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import tienda.carrito.dto.ItemCarritoDTO;
import tienda.carrito.service.ItemCarritoService;
import java.util.List;
@RestController
@RequestMapping("/api/carrito")
@Tag(name = "Carrito", description = "Gestión del carrito de compras")
public class ItemCarritoController {
    private final ItemCarritoService itemCarritoService;
    public ItemCarritoController(ItemCarritoService itemCarritoService) { this.itemCarritoService = itemCarritoService; }
    @GetMapping
    @Operation(summary = "Listar todos los registros")
    @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    public List<ItemCarritoDTO> obtenerTodos() { return this.itemCarritoService.listarTodos(); }
    @GetMapping("/{id}")
    @Operation(summary = "Obtener registro por ID")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Registro encontrado"), @ApiResponse(responseCode = "404", description = "Registro no encontrado") })
    public ResponseEntity<ItemCarritoDTO> obtenerPorId(@PathVariable Long id) { return ResponseEntity.ok(this.itemCarritoService.buscarPorId(id)); }
    @PostMapping
    @Operation(summary = "Crear un nuevo registro")
    @ApiResponse(responseCode = "201", description = "Registro creado exitosamente")
    public ResponseEntity<ItemCarritoDTO> crear(@Valid @RequestBody ItemCarritoDTO dto) { return ResponseEntity.status(HttpStatus.CREATED).body(this.itemCarritoService.crear(dto)); }
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar registro existente")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Registro actualizado"), @ApiResponse(responseCode = "404", description = "Registro no encontrado") })
    public ResponseEntity<ItemCarritoDTO> actualizar(@PathVariable Long id, @Valid @RequestBody ItemCarritoDTO dto) { return ResponseEntity.ok(this.itemCarritoService.actualizar(id, dto)); }
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar registro")
    @ApiResponses({ @ApiResponse(responseCode = "204", description = "Registro eliminado"), @ApiResponse(responseCode = "404", description = "Registro no encontrado") })
    public ResponseEntity<Void> eliminar(@PathVariable Long id) { this.itemCarritoService.eliminar(id); return ResponseEntity.noContent().build(); }
    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Obtener items del carrito por usuario")
    public List<ItemCarritoDTO> obtenerPorUsuario(@PathVariable Long usuarioId) { return this.itemCarritoService.buscarPorUsuario(usuarioId); }
}
