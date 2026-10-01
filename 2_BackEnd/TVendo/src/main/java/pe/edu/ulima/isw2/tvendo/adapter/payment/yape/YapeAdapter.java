package pe.edu.ulima.isw2.tvendo.adapter.payment.yape;

import pe.edu.ulima.isw2.tvendo.adapter.payment.PagoGateway;
import pe.edu.ulima.isw2.tvendo.adapter.payment.ResultadoPago;

import java.math.BigDecimal;

public class YapeAdapter implements PagoGateway {

    private final YapeClient yapeClient;

    public YapeAdapter(YapeClient yapeClient) {
        this.yapeClient = yapeClient;
    }

    @Override
    public ResultadoPago procesarPago(String numeroDestino, String titular, BigDecimal monto, String descripcion) {
        YapeRequestDTO request = new YapeRequestDTO(
                numeroDestino,
                titular,
                monto,
                descripcion
        );
        YapeResponseDTO response = yapeClient.procesarPago(request);
        return new ResultadoPago("YAPE", response.codigoOperacion(), response.estado());
    }
}
