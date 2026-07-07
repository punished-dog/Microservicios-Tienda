package tienda.promociones.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import tienda.promociones.dto.PromocionDTO;
import tienda.promociones.service.PromocionService;
import java.util.List;
@RestController
@RequestMapping("/api/promociones")
@Tag(name = "Promociones", description = "Gestión de promociones de la tienda")
public class PromocionController {
    private final PromocionService promocionService;
    public PromocionController(PromocionService promocionService) { this.promocionService = promocionService; }
    @GetMapping
    @Operation(summary = "Listar todos los registros")
    @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    public List<PromocionDTO> obtenerTodos() { return this.promocionService.listarTodos(); }
    @GetMapping("/{id}")
    @Operation(summary = "Obtener registro por ID")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Registro encontrado"), @ApiResponse(responseCode = "404", description = "Registro no encontrado") })
    public ResponseEntity<PromocionDTO> obtenerPorId(@PathVariable Long id) { return ResponseEntity.ok(this.promocionService.buscarPorId(id)); }
    @PostMapping
    @Operation(summary = "Crear un nuevo registro")
    @ApiResponse(responseCode = "201", description = "Registro creado exitosamente")
    public ResponseEntity<PromocionDTO> crear(@Valid @RequestBody PromocionDTO dto) { return ResponseEntity.status(HttpStatus.CREATED).body(this.promocionService.crear(dto)); }
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar registro existente")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Registro actualizado"), @ApiResponse(responseCode = "404", description = "Registro no encontrado") })
    public ResponseEntity<PromocionDTO> actualizar(@PathVariable Long id, @Valid @RequestBody PromocionDTO dto) { return ResponseEntity.ok(this.promocionService.actualizar(id, dto)); }
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar registro")
    @ApiResponses({ @ApiResponse(responseCode = "204", description = "Registro eliminado"), @ApiResponse(responseCode = "404", description = "Registro no encontrado") })
    public ResponseEntity<Void> eliminar(@PathVariable Long id) { this.promocionService.eliminar(id); return ResponseEntity.noContent().build(); }
    @GetMapping("/producto/{productoId}")
    @Operation(summary = "Obtener promociones por producto")
    public List<PromocionDTO> obtenerPorProducto(@PathVariable Long productoId) { return this.promocionService.buscarPorProducto(productoId); }
    @GetMapping("/activa/{activa}")
    @Operation(summary = "Obtener promociones por estado activo")
    public List<PromocionDTO> obtenerPorActiva(@PathVariable Boolean activa) { return this.promocionService.buscarPorActiva(activa); }
}
