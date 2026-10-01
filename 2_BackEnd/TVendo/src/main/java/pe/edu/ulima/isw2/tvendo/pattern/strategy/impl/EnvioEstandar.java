package pe.edu.ulima.isw2.tvendo.pattern.strategy.impl;

import pe.edu.ulima.isw2.tvendo.pattern.strategy.EstrategiaEnvio;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Implementación de la estrategia de envío estándar.
 * Esta clase calcula el costo de envío basado en un umbral de subtotal.
 * Si el subtotal es mayor o igual al umbral, el costo de envío es cero.
 * De lo contrario, se aplica un costo fijo.
 * @author Henry Wong
 */
public class EnvioEstandar implements EstrategiaEnvio {

    private static final BigDecimal UMBRAL = new BigDecimal("100.00");
    private static final BigDecimal COSTO_FIJO = new BigDecimal("8.00");

    @Override
    public BigDecimal calcularCosto(BigDecimal subtotal) {
        BigDecimal costo = subtotal.compareTo(UMBRAL) >= 0 ? BigDecimal.ZERO : COSTO_FIJO;
        return costo.setScale(2, RoundingMode.HALF_UP);
    }
}
