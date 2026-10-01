package pe.edu.ulima.isw2.tvendoapi.app;

import java.util.Locale;

public enum MotorBd {
    MYSQL,
    POSTGRES;

    public static MotorBd from(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("Motor de base de datos no soportado: " + valor);
        }
        try {
            return MotorBd.valueOf(valor.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Motor de base de datos no soportado: " + valor);
        }
    }
}
