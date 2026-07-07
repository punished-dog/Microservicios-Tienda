package tienda.envios.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import tienda.envios.dto.EnvioDTO;
import tienda.envios.exception.EnvioNoEncontradoException;
import tienda.envios.model.Envio;
import tienda.envios.repository.EnvioRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class EnvioService {

    private static final Logger log = LoggerFactory.getLogger(EnvioService.class);

    private final EnvioRepository envioRepository;
    private final WebClient.Builder webClientBuilder;

    public EnvioService(EnvioRepository envioRepository, WebClient.Builder webClientBuilder) {
        this.envioRepository = envioRepository;
        this.webClientBuilder = webClientBuilder;
    }

    public List<EnvioDTO> listarTodos() {
        log.info("Listando todos los envios");
        List<Envio> entidades = this.envioRepository.findAll();
        List<EnvioDTO> dtos = new ArrayList<>();
        for (Envio e : entidades) { dtos.add(convertirADTO(e)); }
        return dtos;
    }

    public EnvioDTO buscarPorId(Long id) {
        log.info("Buscando envio con id: {}", id);
        Envio entidad = this.envioRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Envio no encontrado con id: {}", id);
                    return new EnvioNoEncontradoException("El registro con el ID " + id + " no fue encontrado.");
                });
        return convertirADTO(entidad);
    }

    public List<EnvioDTO> buscarPorPedido(Long pedidoId) {
        List<Envio> entidades = this.envioRepository.findByPedidoId(pedidoId);
        List<EnvioDTO> dtos = new ArrayList<>();
        for (Envio e : entidades) { dtos.add(convertirADTO(e)); }
        return dtos;
    }

    public List<EnvioDTO> buscarPorEstado(String estado) {
        List<Envio> entidades = this.envioRepository.findByEstado(estado);
        List<EnvioDTO> dtos = new ArrayList<>();
        for (Envio e : entidades) { dtos.add(convertirADTO(e)); }
        return dtos;
    }

    public EnvioDTO crear(EnvioDTO dto) {
        if (dto.getDireccionDestino() == null || dto.getDireccionDestino().isBlank()) {
            throw new IllegalArgumentException("La dirección de destino es obligatoria.");
        }
        verificarPedidoExiste(dto.getPedidoId());
        verificarUsuarioExiste(dto.getUsuarioId());
        Envio entidad = new Envio();
        entidad.setPedidoId(dto.getPedidoId());
        entidad.setUsuarioId(dto.getUsuarioId());
        entidad.setDireccionDestino(dto.getDireccionDestino());
        entidad.setEstado(dto.getEstado());
        entidad.setTransportista(dto.getTransportista());
        entidad.setFechaEstimada(dto.getFechaEstimada());
        Envio guardado = this.envioRepository.save(entidad);
        return convertirADTO(guardado);
    }

    public EnvioDTO actualizar(Long id, EnvioDTO dtoActualizado) {
        Envio entidadExistente = this.envioRepository.findById(id)
                .orElseThrow(() -> new EnvioNoEncontradoException("Imposible actualizar. No se encontró el registro con el ID " + id + "."));
        entidadExistente.setPedidoId(dtoActualizado.getPedidoId());
        entidadExistente.setUsuarioId(dtoActualizado.getUsuarioId());
        entidadExistente.setDireccionDestino(dtoActualizado.getDireccionDestino());
        entidadExistente.setEstado(dtoActualizado.getEstado());
        entidadExistente.setTransportista(dtoActualizado.getTransportista());
        entidadExistente.setFechaEstimada(dtoActualizado.getFechaEstimada());
        Envio actualizado = this.envioRepository.save(entidadExistente);
        return convertirADTO(actualizado);
    }

    public void eliminar(Long id) {
        if (!this.envioRepository.existsById(id)) {
            throw new EnvioNoEncontradoException("Imposible eliminar. No existe registro con el ID " + id + ".");
        }
        this.envioRepository.deleteById(id);
    }

    public Map obtenerPedidoDelEnvio(Long id) {
        Envio envio = this.envioRepository.findById(id)
                .orElseThrow(() -> new EnvioNoEncontradoException("El envío con el ID " + id + " no fue encontrado."));
        try {
            return this.webClientBuilder.build()
                    .get()
                    .uri("http://pedidos/api/pedidos/" + envio.getPedidoId())
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("No se pudo obtener el pedido del envío: " + e.getMessage());
        }
    }

    private void verificarPedidoExiste(Long pedidoId) {
        try {
            this.webClientBuilder.build()
                    .get()
                    .uri("http://pedidos/api/pedidos/" + pedidoId)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("El pedido con ID " + pedidoId + " no existe o no está disponible.");
        }
    }

    private void verificarUsuarioExiste(Long usuarioId) {
        try {
            this.webClientBuilder.build()
                    .get()
                    .uri("http://usuarios/api/usuarios/" + usuarioId)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("El usuario con ID " + usuarioId + " no existe o no está disponible.");
        }
    }

    private EnvioDTO convertirADTO(Envio e) {
        return new EnvioDTO(e.getId(), e.getPedidoId(), e.getUsuarioId(), e.getDireccionDestino(), e.getEstado(), e.getTransportista(), e.getFechaEstimada());
    }
}
