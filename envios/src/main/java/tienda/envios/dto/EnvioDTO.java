package tienda.envios.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class EnvioDTO {
    private Long id;
    @NotNull(message = "El pedidoId es obligatorio")
    private Long pedidoId;
    @NotNull(message = "El usuarioId es obligatorio")
    private Long usuarioId;
    @NotBlank(message = "La dirección de destino es obligatoria")
    private String direccionDestino;
    @NotBlank(message = "El estado es obligatorio")
    private String estado;
    private String transportista;
    private String fechaEstimada;

    public EnvioDTO() {}
    public EnvioDTO(Long id, Long pedidoId, Long usuarioId, String direccionDestino, String estado, String transportista, String fechaEstimada) {
        this.id = id; this.pedidoId = pedidoId; this.usuarioId = usuarioId;
        this.direccionDestino = direccionDestino; this.estado = estado;
        this.transportista = transportista; this.fechaEstimada = fechaEstimada;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPedidoId() { return pedidoId; }
    public void setPedidoId(Long pedidoId) { this.pedidoId = pedidoId; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public String getDireccionDestino() { return direccionDestino; }
    public void setDireccionDestino(String direccionDestino) { this.direccionDestino = direccionDestino; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getTransportista() { return transportista; }
    public void setTransportista(String transportista) { this.transportista = transportista; }
    public String getFechaEstimada() { return fechaEstimada; }
    public void setFechaEstimada(String fechaEstimada) { this.fechaEstimada = fechaEstimada; }
}
