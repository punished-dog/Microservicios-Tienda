package tienda.resenas.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import tienda.resenas.dto.ResenaDTO;
import tienda.resenas.service.ResenaService;
import java.util.List;
@RestController
@RequestMapping("/api/resenas")
@Tag(name = "Resenas", description = "Gestión de reseñas de productos")
public class ResenaController {
    private final ResenaService resenaService;
    public ResenaController(ResenaService resenaService) { this.resenaService = resenaService; }
    @GetMapping
    @Operation(summary = "Listar todos los registros")
    @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    public List<ResenaDTO> obtenerTodos() { return this.resenaService.listarTodos(); }
    @GetMapping("/{id}")
    @Operation(summary = "Obtener registro por ID")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Registro encontrado"), @ApiResponse(responseCode = "404", description = "Registro no encontrado") })
    public ResponseEntity<ResenaDTO> obtenerPorId(@PathVariable Long id) { return ResponseEntity.ok(this.resenaService.buscarPorId(id)); }
    @PostMapping
    @Operation(summary = "Crear un nuevo registro")
    @ApiResponse(responseCode = "201", description = "Registro creado exitosamente")
    public ResponseEntity<ResenaDTO> crear(@Valid @RequestBody ResenaDTO dto) { return ResponseEntity.status(HttpStatus.CREATED).body(this.resenaService.crear(dto)); }
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar registro existente")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Registro actualizado"), @ApiResponse(responseCode = "404", description = "Registro no encontrado") })
    public ResponseEntity<ResenaDTO> actualizar(@PathVariable Long id, @Valid @RequestBody ResenaDTO dto) { return ResponseEntity.ok(this.resenaService.actualizar(id, dto)); }
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar registro")
    @ApiResponses({ @ApiResponse(responseCode = "204", description = "Registro eliminado"), @ApiResponse(responseCode = "404", description = "Registro no encontrado") })
    public ResponseEntity<Void> eliminar(@PathVariable Long id) { this.resenaService.eliminar(id); return ResponseEntity.noContent().build(); }
    @GetMapping("/producto/{productoId}")
    @Operation(summary = "Obtener reseñas por producto")
    public List<ResenaDTO> obtenerPorProducto(@PathVariable Long productoId) { return this.resenaService.buscarPorProducto(productoId); }
    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Obtener reseñas por usuario")
    public List<ResenaDTO> obtenerPorUsuario(@PathVariable Long usuarioId) { return this.resenaService.buscarPorUsuario(usuarioId); }
}
