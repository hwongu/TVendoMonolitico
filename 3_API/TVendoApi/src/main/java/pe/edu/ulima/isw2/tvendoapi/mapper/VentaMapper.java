package pe.edu.ulima.isw2.tvendoapi.mapper;

import pe.edu.ulima.isw2.tvendo.entity.DetalleVenta;
import pe.edu.ulima.isw2.tvendo.entity.Pago;
import pe.edu.ulima.isw2.tvendo.entity.Venta;
import pe.edu.ulima.isw2.tvendoapi.dto.response.DetalleVentaResponseDTO;
import pe.edu.ulima.isw2.tvendoapi.dto.response.PagoResponseDTO;
import pe.edu.ulima.isw2.tvendoapi.dto.response.VentaResponseDTO;

import java.util.List;

/**
 * Mapper para convertir entidades de venta, detalle de venta y pago a sus respectivos DTOs de respuesta.
 * Proporciona métodos para mapear una venta completa con sus detalles y pagos asociados.
 * @author Henry Wong
 */
public class VentaMapper {

    public VentaResponseDTO toResponse(Venta venta, List<DetalleVenta> detalles, List<Pago> pagos) {
        List<DetalleVentaResponseDTO> detallesResponse = detalles.stream().map(this::toDetalleResponse).toList();
        List<PagoResponseDTO> pagosResponse = pagos.stream().map(this::toPagoResponse).toList();

        return new VentaResponseDTO(
                venta.getCodigo(),
                venta.getFecha(),
                venta.getSubtotal(),
                venta.getCostoEnvio(),
                venta.getTotal(),
                venta.getEstado(),
                venta.getObservacion(),
                detallesResponse,
                pagosResponse
        );
    }

    private DetalleVentaResponseDTO toDetalleResponse(DetalleVenta detalleVenta) {
        return new DetalleVentaResponseDTO(
                detalleVenta.getProductoId(),
                detalleVenta.getCantidad(),
                detalleVenta.getPrecioUnitario(),
                detalleVenta.getSubtotal()
        );
    }

    private PagoResponseDTO toPagoResponse(Pago pago) {
        return new PagoResponseDTO(
                pago.getTipo(),
                pago.getMonto(),
                pago.getEstado(),
                pago.getCodigoExterno(),
                pago.getFecha(),
                pago.getObservacion()
        );
    }
}
