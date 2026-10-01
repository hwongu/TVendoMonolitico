package pe.edu.ulima.isw2.tvendo.pattern.strategy.impl;

import pe.edu.ulima.isw2.tvendo.pattern.strategy.EstrategiaEnvio;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Implementación de la estrategia de envío programado.
 * Calcula el costo del envío sumando un costo base y un porcentaje del subtotal.
 * Esta clase implementa la interfaz EstrategiaEnvio, permitiendo calcular el costo de envío
 * de manera flexible según la estrategia seleccionada.
 * @author Henry Wong
 */
public class EnvioProgramado implements EstrategiaEnvio {

    private static final BigDecimal COSTO_BASE = new BigDecimal("12.00");
    private static final BigDecimal PORCENTAJE = new BigDecimal("0.01");

    @Override
    public BigDecimal calcularCosto(BigDecimal subtotal) {
        BigDecimal costo = COSTO_BASE.add(subtotal.multiply(PORCENTAJE));
        return costo.setScale(2, RoundingMode.HALF_UP);
    }
}
