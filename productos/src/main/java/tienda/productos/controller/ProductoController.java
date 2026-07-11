package tienda.productos.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import tienda.productos.dto.ProductoDTO;
import tienda.productos.service.ProductoService;
import java.util.List;
import java.util.stream.Collectors;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
@RestController
@RequestMapping("/api/productos")
@Tag(name = "Productos", description = "Gestión de productos de la tienda")
public class ProductoController {
    private final ProductoService productoService;
    public ProductoController(ProductoService productoService) { this.productoService = productoService; }
    @GetMapping
    @Operation(summary = "Listar todos los productos")
    @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    public CollectionModel<EntityModel<ProductoDTO>> obtenerTodos() {
        List<EntityModel<ProductoDTO>> productos = this.productoService.listarTodos().stream()
                .map(this::aModelo).collect(Collectors.toList());
        return CollectionModel.of(productos, linkTo(methodOn(ProductoController.class).obtenerTodos()).withSelfRel());
    }
    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto por ID")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Producto encontrado"), @ApiResponse(responseCode = "404", description = "Producto no encontrado") })
    public EntityModel<ProductoDTO> obtenerPorId(@PathVariable Long id) { return aModelo(this.productoService.buscarPorId(id)); }
    @GetMapping("/categoria/{categoria}")
    @Operation(summary = "Buscar productos por categoría")
    public List<ProductoDTO> obtenerPorCategoria(@PathVariable String categoria) { return this.productoService.buscarPorCategoria(categoria); }
    @PostMapping
    @Operation(summary = "Crear un nuevo producto")
    @ApiResponse(responseCode = "201", description = "Producto creado exitosamente")
    public ResponseEntity<EntityModel<ProductoDTO>> crear(@Valid @RequestBody ProductoDTO dto) { return ResponseEntity.status(HttpStatus.CREATED).body(aModelo(this.productoService.crearProducto(dto))); }
    @PostMapping("/generar/{cantidad}")
    @Operation(summary = "Generar productos de prueba con datos aleatorios")
    @ApiResponse(responseCode = "201", description = "Productos generados")
    public ResponseEntity<List<ProductoDTO>> generar(@PathVariable int cantidad) { return ResponseEntity.status(HttpStatus.CREATED).body(this.productoService.generarProductos(cantidad)); }
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar producto existente")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Producto actualizado"), @ApiResponse(responseCode = "404", description = "Producto no encontrado") })
    public ResponseEntity<EntityModel<ProductoDTO>> actualizar(@PathVariable Long id, @Valid @RequestBody ProductoDTO dto) { return ResponseEntity.ok(aModelo(this.productoService.actualizarProducto(id, dto))); }
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar producto")
    @ApiResponses({ @ApiResponse(responseCode = "204", description = "Producto eliminado"), @ApiResponse(responseCode = "404", description = "Producto no encontrado") })
    public ResponseEntity<Void> eliminar(@PathVariable Long id) { this.productoService.eliminarProducto(id); return ResponseEntity.noContent().build(); }
    private EntityModel<ProductoDTO> aModelo(ProductoDTO dto) {
        return EntityModel.of(dto,
                linkTo(methodOn(ProductoController.class).obtenerPorId(dto.getId())).withSelfRel(),
                linkTo(methodOn(ProductoController.class).obtenerTodos()).withRel("productos"));
    }
}
