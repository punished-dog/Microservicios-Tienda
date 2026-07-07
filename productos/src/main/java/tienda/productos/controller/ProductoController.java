package tienda.productos.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import tienda.productos.dto.ProductoDTO;
import tienda.productos.service.ProductoService;
import java.util.List;
@RestController
@RequestMapping("/api/productos")
@Tag(name = "Productos", description = "Gestión de productos de la tienda")
public class ProductoController {
    private final ProductoService productoService;
    public ProductoController(ProductoService productoService) { this.productoService = productoService; }
    @GetMapping
    @Operation(summary = "Listar todos los productos")
    @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    public List<ProductoDTO> obtenerTodos() { return this.productoService.listarTodos(); }
    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto por ID")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Producto encontrado"), @ApiResponse(responseCode = "404", description = "Producto no encontrado") })
    public ResponseEntity<ProductoDTO> obtenerPorId(@PathVariable Long id) { return ResponseEntity.ok(this.productoService.buscarPorId(id)); }
    @GetMapping("/categoria/{categoria}")
    @Operation(summary = "Buscar productos por categoría")
    public List<ProductoDTO> obtenerPorCategoria(@PathVariable String categoria) { return this.productoService.buscarPorCategoria(categoria); }
    @PostMapping
    @Operation(summary = "Crear un nuevo producto")
    @ApiResponse(responseCode = "201", description = "Producto creado exitosamente")
    public ResponseEntity<ProductoDTO> crear(@Valid @RequestBody ProductoDTO dto) { return ResponseEntity.status(HttpStatus.CREATED).body(this.productoService.crearProducto(dto)); }
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar producto existente")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Producto actualizado"), @ApiResponse(responseCode = "404", description = "Producto no encontrado") })
    public ResponseEntity<ProductoDTO> actualizar(@PathVariable Long id, @Valid @RequestBody ProductoDTO dto) { return ResponseEntity.ok(this.productoService.actualizarProducto(id, dto)); }
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar producto")
    @ApiResponses({ @ApiResponse(responseCode = "204", description = "Producto eliminado"), @ApiResponse(responseCode = "404", description = "Producto no encontrado") })
    public ResponseEntity<Void> eliminar(@PathVariable Long id) { this.productoService.eliminarProducto(id); return ResponseEntity.noContent().build(); }
}
