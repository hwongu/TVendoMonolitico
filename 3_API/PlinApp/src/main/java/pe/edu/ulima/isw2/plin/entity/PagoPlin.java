package pe.edu.ulima.isw2.plin.entity;

import java.time.LocalDateTime;

/**
 * Clase que representa un pago realizado mediante Plin.
 * Contiene información relevante sobre el pago, como el código de operación, número de destino,
 * @author Henry Wong
 */
public class PagoPlin {

    private final Long id;
    private final String codigoOperacion;
    private final String numeroDestino;
    private final String titular;
    private final Integer montoCentimos;
    private final String moneda;
    private final String estado;
    private final String detalle;
    private final LocalDateTime fechaCreacion;

    public PagoPlin(
            Long id,
            String codigoOperacion,
            String numeroDestino,
            String titular,
            Integer montoCentimos,
            String moneda,
            String estado,
            String detalle,
            LocalDateTime fechaCreacion
    ) {
        this.id = id;
        this.codigoOperacion = codigoOperacion;
        this.numeroDestino = numeroDestino;
        this.titular = titular;
        this.montoCentimos = montoCentimos;
        this.moneda = moneda;
        this.estado = estado;
        this.detalle = detalle;
        this.fechaCreacion = fechaCreacion;
    }

    public Long getId() {
        return id;
    }

    public String getCodigoOperacion() {
        return codigoOperacion;
    }

    public String getNumeroDestino() {
        return numeroDestino;
    }

    public String getTitular() {
        return titular;
    }

    public Integer getMontoCentimos() {
        return montoCentimos;
    }

    public String getMoneda() {
        return moneda;
    }

    public String getEstado() {
        return estado;
    }

    public String getDetalle() {
        return detalle;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
}
