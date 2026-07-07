package tienda.inventario.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import tienda.inventario.dto.InventarioDTO;
import tienda.inventario.service.InventarioService;
import java.util.List;
@RestController
@RequestMapping("/api/inventario")
@Tag(name = "Inventario", description = "Gestión de inventario de la tienda")
public class InventarioController {
    private final InventarioService inventarioService;
    public InventarioController(InventarioService inventarioService) { this.inventarioService = inventarioService; }
    @GetMapping
    @Operation(summary = "Listar todos los registros")
    @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    public List<InventarioDTO> obtenerTodos() { return this.inventarioService.listarTodos(); }
    @GetMapping("/{id}")
    @Operation(summary = "Obtener registro por ID")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Registro encontrado"), @ApiResponse(responseCode = "404", description = "Registro no encontrado") })
    public ResponseEntity<InventarioDTO> obtenerPorId(@PathVariable Long id) { return ResponseEntity.ok(this.inventarioService.buscarPorId(id)); }
    @PostMapping
    @Operation(summary = "Crear un nuevo registro")
    @ApiResponse(responseCode = "201", description = "Registro creado exitosamente")
    public ResponseEntity<InventarioDTO> crear(@Valid @RequestBody InventarioDTO dto) { return ResponseEntity.status(HttpStatus.CREATED).body(this.inventarioService.crear(dto)); }
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar registro existente")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Registro actualizado"), @ApiResponse(responseCode = "404", description = "Registro no encontrado") })
    public ResponseEntity<InventarioDTO> actualizar(@PathVariable Long id, @Valid @RequestBody InventarioDTO dto) { return ResponseEntity.ok(this.inventarioService.actualizar(id, dto)); }
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar registro")
    @ApiResponses({ @ApiResponse(responseCode = "204", description = "Registro eliminado"), @ApiResponse(responseCode = "404", description = "Registro no encontrado") })
    public ResponseEntity<Void> eliminar(@PathVariable Long id) { this.inventarioService.eliminar(id); return ResponseEntity.noContent().build(); }
    @GetMapping("/producto/{productoId}")
    @Operation(summary = "Obtener inventario por ID de producto")
    public ResponseEntity<InventarioDTO> obtenerPorProducto(@PathVariable Long productoId) {
        return ResponseEntity.ok(this.inventarioService.buscarPorProductoId(productoId));
    }
}
