package pe.edu.ulima.isw2.tvendo.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Clase que representa un pago realizado en el sistema de ventas.
 * Contiene información sobre el pago, incluyendo su identificador, el identificador de la venta asociada,
 * el tipo de pago, el monto, el estado del pago, un código externo para referencia, la fecha del pago y una observación adicional.
 * @author Henry Wong
 */
public class Pago {

    private Long id;
    private Long ventaId;
    private String tipo;
    private BigDecimal monto;
    private String estado;
    private String codigoExterno;
    private LocalDateTime fecha;
    private String observacion;

    public Pago() {
    }

    public Pago(Long id, Long ventaId, String tipo, BigDecimal monto, String estado, String codigoExterno, LocalDateTime fecha, String observacion) {
        this.id = id;
        this.ventaId = ventaId;
        this.tipo = tipo;
        this.monto = monto;
        this.estado = estado;
        this.codigoExterno = codigoExterno;
        this.fecha = fecha;
        this.observacion = observacion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVentaId() {
        return ventaId;
    }

    public void setVentaId(Long ventaId) {
        this.ventaId = ventaId;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCodigoExterno() {
        return codigoExterno;
    }

    public void setCodigoExterno(String codigoExterno) {
        this.codigoExterno = codigoExterno;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
}
