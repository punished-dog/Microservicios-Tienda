package tienda.carrito.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ItemCarritoDTO {
    private Long id;
    @NotNull(message = "El usuarioId es obligatorio")
    private Long usuarioId;
    @NotNull(message = "El productoId es obligatorio")
    private Long productoId;
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;
    @NotNull(message = "El precio unitario es obligatorio")
    @Positive(message = "El precio unitario debe ser mayor a cero")
    private Double precioUnitario;

    public ItemCarritoDTO() {}
    public ItemCarritoDTO(Long id, Long usuarioId, Long productoId, Integer cantidad, Double precioUnitario) {
        this.id = id; this.usuarioId = usuarioId; this.productoId = productoId;
        this.cantidad = cantidad; this.precioUnitario = precioUnitario;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public Double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(Double precioUnitario) { this.precioUnitario = precioUnitario; }
}
