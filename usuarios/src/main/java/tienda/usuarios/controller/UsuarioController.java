package tienda.usuarios.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import tienda.usuarios.dto.UsuarioDTO;
import tienda.usuarios.service.UsuarioService;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "Gestión de usuarios de la tienda")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    @Operation(summary = "Listar todos los usuarios")
    @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    public CollectionModel<EntityModel<UsuarioDTO>> obtenerTodos() {
        List<EntityModel<UsuarioDTO>> usuarios = this.usuarioService.listarTodos().stream()
                .map(this::aModelo)
                .collect(Collectors.toList());
        return CollectionModel.of(usuarios,
                linkTo(methodOn(UsuarioController.class).obtenerTodos()).withSelfRel());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public EntityModel<UsuarioDTO> obtenerPorId(@PathVariable Long id) {
        UsuarioDTO dto = this.usuarioService.buscarPorId(id);
        return aModelo(dto);
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo usuario")
    @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente")
    public ResponseEntity<EntityModel<UsuarioDTO>> crear(@Valid @RequestBody UsuarioDTO dto) {
        UsuarioDTO creado = this.usuarioService.crearUsuario(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(aModelo(creado));
    }

    @PostMapping("/generar/{cantidad}")
    @Operation(summary = "Generar usuarios de prueba con datos aleatorios")
    @ApiResponse(responseCode = "201", description = "Usuarios generados")
    public ResponseEntity<List<UsuarioDTO>> generar(@PathVariable int cantidad) {
        List<UsuarioDTO> generados = this.usuarioService.generarUsuarios(cantidad);
        return ResponseEntity.status(HttpStatus.CREATED).body(generados);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar usuario existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario actualizado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<EntityModel<UsuarioDTO>> actualizar(@PathVariable Long id, @Valid @RequestBody UsuarioDTO dto) {
        UsuarioDTO actualizado = this.usuarioService.actualizarUsuario(id, dto);
        return ResponseEntity.ok(aModelo(actualizado));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar usuario")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Usuario eliminado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        this.usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    private EntityModel<UsuarioDTO> aModelo(UsuarioDTO dto) {
        return EntityModel.of(dto,
                linkTo(methodOn(UsuarioController.class).obtenerPorId(dto.getId())).withSelfRel(),
                linkTo(methodOn(UsuarioController.class).obtenerTodos()).withRel("usuarios"));
    }
}
