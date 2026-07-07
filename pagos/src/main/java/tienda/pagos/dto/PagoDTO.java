package tienda.pagos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class PagoDTO {
    private Long id;
    @NotNull(message = "El pedidoId es obligatorio")
    private Long pedidoId;
    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser mayor a cero")
    private Double monto;
    @NotBlank(message = "El método de pago es obligatorio")
    private String metodoPago;
    @NotBlank(message = "El estado es obligatorio")
    private String estado;

    public PagoDTO() {}
    public PagoDTO(Long id, Long pedidoId, Double monto, String metodoPago, String estado) {
        this.id = id; this.pedidoId = pedidoId; this.monto = monto;
        this.metodoPago = metodoPago; this.estado = estado;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPedidoId() { return pedidoId; }
    public void setPedidoId(Long pedidoId) { this.pedidoId = pedidoId; }
    public Double getMonto() { return monto; }
    public void setMonto(Double monto) { this.monto = monto; }
    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
