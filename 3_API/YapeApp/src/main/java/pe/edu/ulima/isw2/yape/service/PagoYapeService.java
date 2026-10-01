package pe.edu.ulima.isw2.yape.service;

import pe.edu.ulima.isw2.yape.dao.PagoYapeDAO;
import pe.edu.ulima.isw2.yape.dto.request.CrearPagoYapeRequestDTO;
import pe.edu.ulima.isw2.yape.dto.response.PagoYapeResponseDTO;
import pe.edu.ulima.isw2.yape.entity.PagoYape;
import pe.edu.ulima.isw2.yape.exception.PagoYapeNoEncontradoException;
import pe.edu.ulima.isw2.yape.mapper.PagoYapeMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Servicio para la gestión de pagos mediante Yape.
 * Proporciona métodos para procesar, buscar y listar pagos de Yape.
 * Implementa la lógica de negocio relacionada con los pagos de Yape, incluyendo validaciones y generación
 * de códigos de operación.
 * Al usar DAO está desacoplado de la capa de persistencia, permitiendo cambiar la implementación del DAO
 * sin afectar la lógica de negocio.
 * @author Henry Wong
 */
public class PagoYapeService {

    private final PagoYapeDAO pagoYapeDAO;
    private final PagoYapeMapper pagoYapeMapper;

    public PagoYapeService(PagoYapeDAO pagoYapeDAO, PagoYapeMapper pagoYapeMapper) {
        this.pagoYapeDAO = Objects.requireNonNull(pagoYapeDAO, "PagoYapeDAO es obligatorio");
        this.pagoYapeMapper = Objects.requireNonNull(pagoYapeMapper, "PagoYapeMapper es obligatorio");
    }

    public PagoYapeResponseDTO procesar(CrearPagoYapeRequestDTO request) {
        validarRequest(request);

        String codigoOperacion = generarCodigoOperacion();
        PagoYape nuevoPago = new PagoYape(
                null,
                codigoOperacion,
                request.telefono().trim(),
                request.nombreCliente().trim(),
                request.monto(),
                "PEN",
                "APROBADO",
                request.descripcion(),
                LocalDateTime.now()
        );

        PagoYape pagoGuardado = pagoYapeDAO.guardar(nuevoPago);
        return pagoYapeMapper.toResponseDTO(pagoGuardado);
    }

    public PagoYapeResponseDTO buscar(String codigoOperacion) {
        if (codigoOperacion == null || codigoOperacion.isBlank()) {
            throw new IllegalArgumentException("El código de operación es obligatorio");
        }

        PagoYape pago = pagoYapeDAO.buscarPorCodigoOperacion(codigoOperacion.trim())
                .orElseThrow(() -> new PagoYapeNoEncontradoException(codigoOperacion));

        return pagoYapeMapper.toResponseDTO(pago);
    }

    public List<PagoYapeResponseDTO> listar() {
        return pagoYapeDAO.listar()
                .stream()
                .map(pagoYapeMapper::toResponseDTO)
                .toList();
    }

    private void validarRequest(CrearPagoYapeRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("El request no puede ser null");
        }
        validarTelefono(request.telefono());
        validarNombreCliente(request.nombreCliente());
        validarMonto(request.monto());
    }

    private void validarTelefono(String telefono) {
        if (telefono == null || telefono.isBlank()) {
            throw new IllegalArgumentException("El teléfono es obligatorio");
        }
        if (!telefono.trim().matches("\\d{9}")) {
            throw new IllegalArgumentException("El teléfono debe contener exactamente 9 dígitos");
        }
    }

    private void validarNombreCliente(String nombreCliente) {
        if (nombreCliente == null || nombreCliente.isBlank()) {
            throw new IllegalArgumentException("El nombre del cliente es obligatorio");
        }
    }

    private void validarMonto(BigDecimal monto) {
        if (monto == null) {
            throw new IllegalArgumentException("El monto es obligatorio");
        }
        if (monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a cero");
        }
    }

    private String generarCodigoOperacion() {
        String sufijo = UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 8)
                .toUpperCase();
        return "YAPE-" + sufijo;
    }
}
