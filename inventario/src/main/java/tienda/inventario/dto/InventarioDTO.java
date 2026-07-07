package tienda.inventario.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class InventarioDTO {
    private Long id;
    @NotNull(message = "El productoId es obligatorio")
    private Long productoId;
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 0, message = "La cantidad no puede ser negativa")
    private Integer cantidad;
    @NotNull(message = "El stock mínimo es obligatorio")
    @Min(value = 0, message = "El stock mínimo no puede ser negativo")
    private Integer stockMinimo;
    @NotBlank(message = "La ubicación es obligatoria")
    private String ubicacion;

    public InventarioDTO() {}
    public InventarioDTO(Long id, Long productoId, Integer cantidad, Integer stockMinimo, String ubicacion) {
        this.id = id; this.productoId = productoId; this.cantidad = cantidad;
        this.stockMinimo = stockMinimo; this.ubicacion = ubicacion;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public Integer getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(Integer stockMinimo) { this.stockMinimo = stockMinimo; }
    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
}
