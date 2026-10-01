package pe.edu.ulima.isw2.tvendo.service;

import pe.edu.ulima.isw2.tvendo.adapter.payment.PagoGateway;
import pe.edu.ulima.isw2.tvendo.adapter.payment.ResultadoPago;
import pe.edu.ulima.isw2.tvendo.dao.PagoDAO;
import pe.edu.ulima.isw2.tvendo.entity.Pago;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Servicio para procesar pagos y gestionar la persistencia de los mismos.
 * Este servicio utiliza un DAO para acceder a la base de datos y un mapa de gateways de pago para procesar pagos externos.
 * @author Henry Wong
 */
public class PagoService {

    private final PagoDAO pagoDAO;
    private final Map<String, PagoGateway> pagosPorTipo;

    public PagoService(PagoDAO pagoDAO, Map<String, PagoGateway> pagosPorTipo) {
        this.pagoDAO = pagoDAO;
        this.pagosPorTipo = pagosPorTipo;
    }

    public ResultadoPago procesarPagoExterno(String tipo, String numeroDestino, String titular, BigDecimal monto, String descripcion) {
        String tipoNormalizado = normalizarTipo(tipo);
        PagoGateway pagoGateway = pagosPorTipo.get(tipoNormalizado);
        if (pagoGateway == null) {
            throw new IllegalArgumentException("Tipo de pago no soportado: " + tipo);
        }
        return pagoGateway.procesarPago(numeroDestino, titular, monto, descripcion);
    }

    public Pago construirPago(Long ventaId, String tipo, BigDecimal monto, String descripcion, ResultadoPago resultado) {
        return new Pago(
                null,
                ventaId,
                normalizarTipo(tipo),
                monto,
                resultado.estado(),
                resultado.codigoOperacion(),
                LocalDateTime.now(),
                descripcion
        );
    }

    public Pago procesarPago(Long ventaId, String tipo, String numeroDestino, String titular, BigDecimal monto, String descripcion) {
        ResultadoPago resultado = procesarPagoExterno(tipo, numeroDestino, titular, monto, descripcion);
        Pago pago = construirPago(ventaId, tipo, monto, descripcion, resultado);
        return pagoDAO.guardar(pago);
    }

    private String normalizarTipo(String tipo) {
        String tipoNormalizado = tipo == null ? "" : tipo.toUpperCase(Locale.ROOT);
        if (tipoNormalizado.isBlank()) {
            throw new IllegalArgumentException("Tipo de pago no soportado: " + tipo);
        }
        return tipoNormalizado;
    }

    public List<Pago> listarPorVentaId(Long ventaId) {
        return pagoDAO.listarPorVentaId(ventaId);
    }
}
