package tienda.resenas.model;
import jakarta.persistence.*;
@Entity
@Table(name = "resenas")
public class Resena {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long usuarioId;
    private Long productoId;
    private Integer calificacion;
    private String comentario;
    public Resena() {}
    public Resena(Long id, Long usuarioId, Long productoId, Integer calificacion, String comentario) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.productoId = productoId;
        this.calificacion = calificacion;
        this.comentario = comentario;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }
    public Integer getCalificacion() { return calificacion; }
    public void setCalificacion(Integer calificacion) { this.calificacion = calificacion; }
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
}
