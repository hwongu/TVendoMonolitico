package pe.edu.ulima.isw2.tvendo.pattern.strategy;

import java.math.BigDecimal;

/**
 * Interfaz que define la estrategia de envío para calcular el costo de envío basado en el subtotal de la venta.
 * Implementa el patrón Strategy, permitiendo diferentes estrategias de cálculo de costos de envío.
 * @author Henry Wong
 */
public interface EstrategiaEnvio {
    BigDecimal calcularCosto(BigDecimal subtotal);
}
