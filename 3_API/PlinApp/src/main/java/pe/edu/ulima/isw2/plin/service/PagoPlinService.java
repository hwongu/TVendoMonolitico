package pe.edu.ulima.isw2.plin.service;

import pe.edu.ulima.isw2.plin.dao.PagoPlinDAO;
import pe.edu.ulima.isw2.plin.dto.request.CrearPagoPlinRequestDTO;
import pe.edu.ulima.isw2.plin.dto.response.PagoPlinResponseDTO;
import pe.edu.ulima.isw2.plin.entity.PagoPlin;
import pe.edu.ulima.isw2.plin.exception.PagoPlinNoEncontradoException;
import pe.edu.ulima.isw2.plin.mapper.PagoPlinMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Servicio para la gestión de pagos mediante Plin.
 * Proporciona métodos para procesar, buscar y listar pagos de Plin.
 * Implementa la lógica de negocio relacionada con los pagos de Plin, incluyendo validaciones y generación
 * de códigos de operación.
 * Al usar DAO está desacoplado de la capa de persistencia, permitiendo cambiar la implementación del DAO
 * sin afectar la lógica de negocio.
 * @author Henry Wong
 */
public class PagoPlinService {

    private final PagoPlinDAO pagoPlinDAO;
    private final PagoPlinMapper pagoPlinMapper;

    public PagoPlinService(PagoPlinDAO pagoPlinDAO, PagoPlinMapper pagoPlinMapper) {
        if (pagoPlinDAO == null) {
            throw new IllegalArgumentException("El DAO es obligatorio");
        }
        if (pagoPlinMapper == null) {
            throw new IllegalArgumentException("El mapper es obligatorio");
        }
        this.pagoPlinDAO = pagoPlinDAO;
        this.pagoPlinMapper = pagoPlinMapper;
    }

    public PagoPlinResponseDTO procesar(CrearPagoPlinRequestDTO request) {
        validarRequest(request);

        String codigoOperacion = generarCodigoOperacion();

        PagoPlin pago = new PagoPlin(
                null,
                codigoOperacion,
                request.numeroDestino(),
                request.titular(),
                request.montoCentimos(),
                "PEN",
                "APROBADO",
                request.detalle(),
                LocalDateTime.now()
        );

        PagoPlin guardado = pagoPlinDAO.guardar(pago);
        return pagoPlinMapper.toResponseDTO(guardado);
    }

    public PagoPlinResponseDTO buscar(String codigoOperacion) {
        if (codigoOperacion == null || codigoOperacion.isBlank()) {
            throw new IllegalArgumentException("El código de operación es obligatorio");
        }

        PagoPlin pago = pagoPlinDAO.buscarPorCodigoOperacion(codigoOperacion)
                .orElseThrow(() -> new PagoPlinNoEncontradoException(codigoOperacion));

        return pagoPlinMapper.toResponseDTO(pago);
    }

    public List<PagoPlinResponseDTO> listar() {
        return pagoPlinDAO.listar().stream()
                .map(pagoPlinMapper::toResponseDTO)
                .toList();
    }

    private void validarRequest(CrearPagoPlinRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("El request es obligatorio");
        }

        validarNumeroDestino(request.numeroDestino());
        validarTitular(request.titular());
        validarMontoCentimos(request.montoCentimos());
    }

    private void validarNumeroDestino(String numeroDestino) {
        if (numeroDestino == null || numeroDestino.isBlank()) {
            throw new IllegalArgumentException("El numeroDestino es obligatorio");
        }
        if (!numeroDestino.matches("\\d{9}")) {
            throw new IllegalArgumentException("El numeroDestino debe contener exactamente 9 dígitos");
        }
    }

    private void validarTitular(String titular) {
        if (titular == null || titular.isBlank()) {
            throw new IllegalArgumentException("El titular es obligatorio");
        }
    }

    private void validarMontoCentimos(Integer montoCentimos) {
        if (montoCentimos == null) {
            throw new IllegalArgumentException("El montoCentimos es obligatorio");
        }
        if (montoCentimos <= 0) {
            throw new IllegalArgumentException("El monto en céntimos debe ser mayor a cero");
        }
    }

    private String generarCodigoOperacion() {
        String token = UUID.randomUUID().toString()
                .replace("-", "")
                .substring(0, 8)
                .toUpperCase();
        return "PLIN-" + token;
    }
}
