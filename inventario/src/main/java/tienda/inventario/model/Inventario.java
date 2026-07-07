package tienda.inventario.model;
import jakarta.persistence.*;
@Entity
@Table(name = "inventarios")
public class Inventario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long productoId;
    private Integer cantidad;
    private Integer stockMinimo;
    private String ubicacion;
    public Inventario() {}
    public Inventario(Long id, Long productoId, Integer cantidad, Integer stockMinimo, String ubicacion) {
        this.id = id;
        this.productoId = productoId;
        this.cantidad = cantidad;
        this.stockMinimo = stockMinimo;
        this.ubicacion = ubicacion;
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
