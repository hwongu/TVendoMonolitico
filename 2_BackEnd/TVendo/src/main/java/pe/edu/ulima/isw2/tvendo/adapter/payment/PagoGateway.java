package pe.edu.ulima.isw2.tvendo.adapter.payment;

import java.math.BigDecimal;

/**
 * Interfaz para el procesamiento de pagos a través de diferentes pasarelas de pago.
 * Define un método para procesar pagos, que recibe información del pago y devuelve un resultado.
 * Implementa el patrón adapter para permitir la integración con diferentes proveedores de pago sin acoplar
 * la lógica de negocio a una implementación específica.
 * @author Henry Wong
 */
public interface PagoGateway {
    ResultadoPago procesarPago(String numeroDestino, String titular, BigDecimal monto, String descripcion);
}
