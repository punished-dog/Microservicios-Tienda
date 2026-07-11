package tienda.usuarios.service;

import net.datafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tienda.usuarios.dto.UsuarioDTO;
import tienda.usuarios.exception.UsuarioNoEncontradoException;
import tienda.usuarios.model.Usuario;
import tienda.usuarios.repository.UsuarioRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class UsuarioService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<UsuarioDTO> listarTodos() {
        log.info("Listando todos los usuarios");
        List<Usuario> entidades = this.usuarioRepository.findAll();
        List<UsuarioDTO> dtos = new ArrayList<>();
        for (Usuario entidad : entidades) {
            dtos.add(convertirADTO(entidad));
        }
        return dtos;
    }

    public UsuarioDTO buscarPorId(Long id) {
        log.info("Buscando usuario con id: {}", id);
        Usuario entidad = this.usuarioRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Usuario no encontrado con id: {}", id);
                    return new UsuarioNoEncontradoException("El usuario con el ID " + id + " no fue encontrado.");
                });
        return convertirADTO(entidad);
    }

    public UsuarioDTO crearUsuario(UsuarioDTO dto) {
        log.info("Creando nuevo usuario con email: {}", dto.getEmail());
        Usuario entidad = new Usuario();
        entidad.setNombre(dto.getNombre());
        entidad.setApellido(dto.getApellido());
        entidad.setEmail(dto.getEmail());
        entidad.setContrasena(dto.getContrasena());
        entidad.setTelefono(dto.getTelefono());
        entidad.setDireccion(dto.getDireccion());
        entidad.setRol(dto.getRol());
        Usuario entidadGuardada = this.usuarioRepository.save(entidad);
        log.info("Usuario creado con id: {}", entidadGuardada.getId());
        return convertirADTO(entidadGuardada);
    }

    public UsuarioDTO actualizarUsuario(Long id, UsuarioDTO dtoActualizado) {
        log.info("Actualizando usuario con id: {}", id);
        Usuario entidadExistente = this.usuarioRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Usuario no encontrado para actualizar, id: {}", id);
                    return new UsuarioNoEncontradoException("Imposible actualizar. No se encontró el usuario con el ID " + id + ".");
                });
        entidadExistente.setNombre(dtoActualizado.getNombre());
        entidadExistente.setApellido(dtoActualizado.getApellido());
        entidadExistente.setEmail(dtoActualizado.getEmail());
        entidadExistente.setContrasena(dtoActualizado.getContrasena());
        entidadExistente.setTelefono(dtoActualizado.getTelefono());
        entidadExistente.setDireccion(dtoActualizado.getDireccion());
        entidadExistente.setRol(dtoActualizado.getRol());
        Usuario entidadActualizada = this.usuarioRepository.save(entidadExistente);
        log.info("Usuario actualizado correctamente, id: {}", id);
        return convertirADTO(entidadActualizada);
    }

    public void eliminarUsuario(Long id) {
        log.info("Eliminando usuario con id: {}", id);
        if (!this.usuarioRepository.existsById(id)) {
            log.warn("No se encontró usuario para eliminar con id: {}", id);
            throw new UsuarioNoEncontradoException("Imposible eliminar. No existe usuario con el ID " + id + ".");
        }
        this.usuarioRepository.deleteById(id);
        log.info("Usuario eliminado, id: {}", id);
    }

    public List<UsuarioDTO> generarUsuarios(int cantidad) {
        log.info("Generando {} usuarios de prueba", cantidad);
        Faker faker = new Faker();
        List<UsuarioDTO> generados = new ArrayList<>();
        for (int i = 0; i < cantidad; i++) {
            Usuario entidad = new Usuario();
            entidad.setNombre(faker.name().firstName());
            entidad.setApellido(faker.name().lastName());
            entidad.setEmail(faker.internet().emailAddress());
            entidad.setContrasena(faker.internet().password(6, 12));
            entidad.setTelefono(faker.phoneNumber().cellPhone());
            entidad.setDireccion(faker.address().fullAddress());
            entidad.setRol("CLIENTE");
            Usuario guardado = this.usuarioRepository.save(entidad);
            generados.add(convertirADTO(guardado));
        }
        log.info("Se generaron {} usuarios de prueba", cantidad);
        return generados;
    }

    private UsuarioDTO convertirADTO(Usuario entidad) {
        return new UsuarioDTO(entidad.getId(), entidad.getNombre(), entidad.getApellido(),
                entidad.getEmail(), entidad.getContrasena(), entidad.getTelefono(),
                entidad.getDireccion(), entidad.getRol());
    }
}
