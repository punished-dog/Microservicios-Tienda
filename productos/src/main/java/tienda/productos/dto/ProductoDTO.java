package tienda.productos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class ProductoDTO {
    private Long id;
    @NotBlank(message = "El nombre del producto es obligatorio")
    private String nombre;
    private String descripcion;
    @NotNull(message = "El precio es obligatorio")
    @PositiveOrZero(message = "El precio no puede ser negativo")
    private Double precio;
    @NotBlank(message = "La categoría es obligatoria")
    private String categoria;

    public ProductoDTO() {}
    public ProductoDTO(Long id, String nombre, String descripcion, Double precio, String categoria) {
        this.id = id; this.nombre = nombre; this.descripcion = descripcion;
        this.precio = precio; this.categoria = categoria;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
}
