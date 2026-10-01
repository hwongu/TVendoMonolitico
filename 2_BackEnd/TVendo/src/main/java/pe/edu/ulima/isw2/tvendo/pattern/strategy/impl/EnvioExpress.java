package pe.edu.ulima.isw2.tvendo.pattern.strategy.impl;

import pe.edu.ulima.isw2.tvendo.pattern.strategy.EstrategiaEnvio;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Implementación de la estrategia de envío express.
 * Calcula el costo del envío express basado en un costo base y un porcentaje del subtotal.
 * @author Henry Wong
 */
public class EnvioExpress implements EstrategiaEnvio {

    private static final BigDecimal COSTO_BASE = new BigDecimal("18.00");
    private static final BigDecimal PORCENTAJE = new BigDecimal("0.02");

    @Override
    public BigDecimal calcularCosto(BigDecimal subtotal) {
        BigDecimal costo = COSTO_BASE.add(subtotal.multiply(PORCENTAJE));
        return costo.setScale(2, RoundingMode.HALF_UP);
    }
}
