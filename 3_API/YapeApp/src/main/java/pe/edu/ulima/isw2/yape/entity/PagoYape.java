package pe.edu.ulima.isw2.yape.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Clase que representa un pago realizado mediante Yape.
 * Contiene información sobre el pago, incluyendo el código de operación, el teléfono del cliente,
 * el nombre del cliente, el monto, la moneda, el estado del pago, una descripción y la fecha de creación.
 * Cuando se usa DAO esta clase sirve como entidad que se mapea a la tabla correspondiente en la base de datos.
 * @author Henry Wong
 */
public class PagoYape {

    private final Long id;
    private final String codigoOperacion;
    private final String telefono;
    private final String nombreCliente;
    private final BigDecimal monto;
    private final String moneda;
    private final String estado;
    private final String descripcion;
    private final LocalDateTime fechaCreacion;

    public PagoYape(
            Long id,
            String codigoOperacion,
            String telefono,
            String nombreCliente,
            BigDecimal monto,
            String moneda,
            String estado,
            String descripcion,
            LocalDateTime fechaCreacion
    ) {
        this.id = id;
        this.codigoOperacion = codigoOperacion;
        this.telefono = telefono;
        this.nombreCliente = nombreCliente;
        this.monto = monto;
        this.moneda = moneda;
        this.estado = estado;
        this.descripcion = descripcion;
        this.fechaCreacion = fechaCreacion;
    }

    public Long getId() {
        return id;
    }

    public String getCodigoOperacion() {
        return codigoOperacion;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public String getMoneda() {
        return moneda;
    }

    public String getEstado() {
        return estado;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
}
