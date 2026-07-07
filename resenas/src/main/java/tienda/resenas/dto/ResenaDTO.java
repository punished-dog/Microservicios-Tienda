package tienda.resenas.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class ResenaDTO {
    private Long id;
    @NotNull(message = "El usuarioId es obligatorio")
    private Long usuarioId;
    @NotNull(message = "El productoId es obligatorio")
    private Long productoId;
    @NotNull(message = "La calificación es obligatoria")
    @Min(value = 1, message = "La calificación mínima es 1")
    @Max(value = 5, message = "La calificación máxima es 5")
    private Integer calificacion;
    private String comentario;

    public ResenaDTO() {}
    public ResenaDTO(Long id, Long usuarioId, Long productoId, Integer calificacion, String comentario) {
        this.id = id; this.usuarioId = usuarioId; this.productoId = productoId;
        this.calificacion = calificacion; this.comentario = comentario;
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
