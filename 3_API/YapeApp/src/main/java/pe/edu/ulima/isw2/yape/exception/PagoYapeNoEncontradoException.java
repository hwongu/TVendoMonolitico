package pe.edu.ulima.isw2.yape.exception;

/**
 * Excepción personalizada que se lanza cuando no se encuentra un pago realizado mediante Yape
 * con un código de operación específico.
 * Esta excepción extiende de RuntimeException, lo que permite que sea una excepción no verificada.
 * @author Henry Wong
 */
public class PagoYapeNoEncontradoException extends RuntimeException {

    public PagoYapeNoEncontradoException(String codigoOperacion) {
        super("No existe una operación Yape con código: " + codigoOperacion);
    }
}
