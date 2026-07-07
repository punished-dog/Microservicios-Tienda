package tienda.usuarios;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tienda.usuarios.dto.UsuarioDTO;
import tienda.usuarios.exception.UsuarioNoEncontradoException;
import tienda.usuarios.model.Usuario;
import tienda.usuarios.repository.UsuarioRepository;
import tienda.usuarios.service.UsuarioService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;
    private UsuarioDTO dto;

    @BeforeEach
    void setUp() {
        usuario = new Usuario(1L, "Juan", "Pérez", "juan@email.com", "123456", "912345678", "Av. Principal 123", "CLIENTE");
        dto = new UsuarioDTO(null, "Juan", "Pérez", "juan@email.com", "123456", "912345678", "Av. Principal 123", "CLIENTE");
    }

    @Test
    void listarTodos_debeRetornarListaDeUsuarios() {
        when(usuarioRepository.findAll()).thenReturn(Arrays.asList(usuario));

        List<UsuarioDTO> resultado = usuarioService.listarTodos();

        assertEquals(1, resultado.size());
        assertEquals("Juan", resultado.get(0).getNombre());
        assertEquals("Pérez", resultado.get(0).getApellido());
    }

    @Test
    void buscarPorId_conIdExistente_debeRetornarUsuario() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        UsuarioDTO resultado = usuarioService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals("juan@email.com", resultado.getEmail());
    }

    @Test
    void buscarPorId_conIdInexistente_debeLanzarExcepcion() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UsuarioNoEncontradoException.class, () -> usuarioService.buscarPorId(99L));
    }

    @Test
    void crearUsuario_debeGuardarYRetornarDTO() {
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        UsuarioDTO resultado = usuarioService.crearUsuario(dto);

        assertNotNull(resultado);
        assertEquals("Juan", resultado.getNombre());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void actualizarUsuario_conIdExistente_debeActualizar() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        UsuarioDTO resultado = usuarioService.actualizarUsuario(1L, dto);

        assertNotNull(resultado);
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void actualizarUsuario_conIdInexistente_debeLanzarExcepcion() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UsuarioNoEncontradoException.class, () -> usuarioService.actualizarUsuario(99L, dto));
    }

    @Test
    void eliminarUsuario_conIdExistente_debeEliminar() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        doNothing().when(usuarioRepository).deleteById(1L);

        assertDoesNotThrow(() -> usuarioService.eliminarUsuario(1L));
        verify(usuarioRepository).deleteById(1L);
    }

    @Test
    void eliminarUsuario_conIdInexistente_debeLanzarExcepcion() {
        when(usuarioRepository.existsById(99L)).thenReturn(false);

        assertThrows(UsuarioNoEncontradoException.class, () -> usuarioService.eliminarUsuario(99L));
    }
}
