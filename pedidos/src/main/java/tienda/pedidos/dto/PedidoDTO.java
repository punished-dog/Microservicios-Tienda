package tienda.pedidos.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class PedidoDTO {
    private Long id;
    @NotNull(message = "El usuarioId es obligatorio")
    private Long usuarioId;
    @NotNull(message = "El productoId es obligatorio")
    private Long productoId;
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;
    @NotNull(message = "El total es obligatorio")
    @PositiveOrZero(message = "El total no puede ser negativo")
    private Double total;
    @NotBlank(message = "El estado es obligatorio")
    private String estado;

    public PedidoDTO() {}
    public PedidoDTO(Long id, Long usuarioId, Long productoId, Integer cantidad, Double total, String estado) {
        this.id = id; this.usuarioId = usuarioId; this.productoId = productoId;
        this.cantidad = cantidad; this.total = total; this.estado = estado;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
