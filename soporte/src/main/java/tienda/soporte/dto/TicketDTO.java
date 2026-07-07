package tienda.soporte.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TicketDTO {
    private Long id;
    @NotNull(message = "El usuarioId es obligatorio")
    private Long usuarioId;
    @NotBlank(message = "El asunto es obligatorio")
    private String asunto;
    private String descripcion;
    @NotBlank(message = "El estado es obligatorio")
    private String estado;
    @NotBlank(message = "La prioridad es obligatoria")
    private String prioridad;

    public TicketDTO() {}
    public TicketDTO(Long id, Long usuarioId, String asunto, String descripcion, String estado, String prioridad) {
        this.id = id; this.usuarioId = usuarioId; this.asunto = asunto;
        this.descripcion = descripcion; this.estado = estado; this.prioridad = prioridad;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public String getAsunto() { return asunto; }
    public void setAsunto(String asunto) { this.asunto = asunto; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getPrioridad() { return prioridad; }
    public void setPrioridad(String prioridad) { this.prioridad = prioridad; }
}
