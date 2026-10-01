package pe.edu.ulima.isw2.tvendo.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Clase que representa una venta en el sistema.
 * Contiene información sobre la venta, incluyendo su identificador, código, fecha, subtotal,
 * costo de envío, total, estado y observaciones adicionales.
 * @author Henry Wong
 */
public class Venta {

    private Long id;
    private String codigo;
    private LocalDateTime fecha;
    private BigDecimal subtotal;
    private BigDecimal costoEnvio;
    private BigDecimal total;
    private String estado;
    private String observacion;

    public Venta() {
    }

    public Venta(Long id, String codigo, LocalDateTime fecha, BigDecimal subtotal, BigDecimal costoEnvio, BigDecimal total, String estado, String observacion) {
        this.id = id;
        this.codigo = codigo;
        this.fecha = fecha;
        this.subtotal = subtotal;
        this.costoEnvio = costoEnvio;
        this.total = total;
        this.estado = estado;
        this.observacion = observacion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getCostoEnvio() {
        return costoEnvio;
    }

    public void setCostoEnvio(BigDecimal costoEnvio) {
        this.costoEnvio = costoEnvio;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
}
