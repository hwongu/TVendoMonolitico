package pe.edu.ulima.isw2.tvendo.adapter.payment.plin;

import pe.edu.ulima.isw2.tvendo.adapter.payment.PagoGateway;
import pe.edu.ulima.isw2.tvendo.adapter.payment.ResultadoPago;

import java.math.BigDecimal;

/**
 * Adaptador para el servicio de pago Plin.
 * Implementa la interfaz PagoGateway para permitir la integración con el sistema de pagos Plin.
 * Convierte los datos de pago a un formato compatible con Plin y procesa el pago mediante el cliente PlinClient.
 * @author Henry Wong
 */
public class PlinAdapter implements PagoGateway {

    private final PlinClient plinClient;

    public PlinAdapter(PlinClient plinClient) {
        this.plinClient = plinClient;
    }

    @Override
    public ResultadoPago procesarPago(String numeroDestino, String titular, BigDecimal monto, String descripcion) {
        int montoCentimos = monto.movePointRight(2).intValueExact();
        PlinRequestDTO request = new PlinRequestDTO(
                numeroDestino,
                titular,
                montoCentimos,
                descripcion
        );
        PlinResponseDTO response = plinClient.procesarPago(request);
        return new ResultadoPago("PLIN", response.codigoOperacion(), response.estado());
    }
}
