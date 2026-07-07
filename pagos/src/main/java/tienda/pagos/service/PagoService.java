package tienda.pagos.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import tienda.pagos.dto.PagoDTO;
import tienda.pagos.exception.PagoNoEncontradoException;
import tienda.pagos.model.Pago;
import tienda.pagos.repository.PagoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PagoService {

    private static final Logger log = LoggerFactory.getLogger(PagoService.class);

    private final PagoRepository pagoRepository;
    private final WebClient.Builder webClientBuilder;

    public PagoService(PagoRepository pagoRepository, WebClient.Builder webClientBuilder) {
        this.pagoRepository = pagoRepository;
        this.webClientBuilder = webClientBuilder;
    }

    public List<PagoDTO> listarTodos() {
        log.info("Listando todos los pagos");
        List<Pago> entidades = this.pagoRepository.findAll();
        List<PagoDTO> dtos = new ArrayList<>();
        for (Pago e : entidades) { dtos.add(convertirADTO(e)); }
        return dtos;
    }

    public PagoDTO buscarPorId(Long id) {
        log.info("Buscando pago con id: {}", id);
        Pago entidad = this.pagoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Pago no encontrado con id: {}", id);
                    return new PagoNoEncontradoException("El registro con el ID " + id + " no fue encontrado.");
                });
        return convertirADTO(entidad);
    }

    public List<PagoDTO> buscarPorPedido(Long pedidoId) {
        List<Pago> entidades = this.pagoRepository.findByPedidoId(pedidoId);
        List<PagoDTO> dtos = new ArrayList<>();
        for (Pago e : entidades) { dtos.add(convertirADTO(e)); }
        return dtos;
    }

    public List<PagoDTO> buscarPorEstado(String estado) {
        List<Pago> entidades = this.pagoRepository.findByEstado(estado);
        List<PagoDTO> dtos = new ArrayList<>();
        for (Pago e : entidades) { dtos.add(convertirADTO(e)); }
        return dtos;
    }

    public PagoDTO crear(PagoDTO dto) {
        if (dto.getMonto() != null && dto.getMonto() <= 0) {
            throw new IllegalArgumentException("El monto del pago debe ser mayor a cero.");
        }
        verificarPedidoExiste(dto.getPedidoId());
        Pago entidad = new Pago();
        entidad.setPedidoId(dto.getPedidoId());
        entidad.setMonto(dto.getMonto());
        entidad.setMetodoPago(dto.getMetodoPago());
        entidad.setEstado(dto.getEstado());
        Pago guardado = this.pagoRepository.save(entidad);
        return convertirADTO(guardado);
    }

    public PagoDTO actualizar(Long id, PagoDTO dtoActualizado) {
        Pago entidadExistente = this.pagoRepository.findById(id)
                .orElseThrow(() -> new PagoNoEncontradoException("Imposible actualizar. No se encontró el registro con el ID " + id + "."));
        entidadExistente.setPedidoId(dtoActualizado.getPedidoId());
        entidadExistente.setMonto(dtoActualizado.getMonto());
        entidadExistente.setMetodoPago(dtoActualizado.getMetodoPago());
        entidadExistente.setEstado(dtoActualizado.getEstado());
        Pago actualizado = this.pagoRepository.save(entidadExistente);
        return convertirADTO(actualizado);
    }

    public void eliminar(Long id) {
        if (!this.pagoRepository.existsById(id)) {
            throw new PagoNoEncontradoException("Imposible eliminar. No existe registro con el ID " + id + ".");
        }
        this.pagoRepository.deleteById(id);
    }

    public Map obtenerPedidoDelPago(Long id) {
        Pago pago = this.pagoRepository.findById(id)
                .orElseThrow(() -> new PagoNoEncontradoException("El pago con el ID " + id + " no fue encontrado."));
        try {
            return this.webClientBuilder.build()
                    .get()
                    .uri("http://pedidos/api/pedidos/" + pago.getPedidoId())
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("No se pudo obtener el pedido del pago: " + e.getMessage());
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

    private PagoDTO convertirADTO(Pago e) {
        return new PagoDTO(e.getId(), e.getPedidoId(), e.getMonto(), e.getMetodoPago(), e.getEstado());
    }
}
