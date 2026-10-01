package pe.edu.ulima.isw2.tvendoapi.mapper;

import pe.edu.ulima.isw2.tvendo.entity.Producto;
import pe.edu.ulima.isw2.tvendoapi.dto.response.ProductoResponseDTO;

import java.util.List;

/**
 * Mapper para convertir entidades Producto a DTOs de respuesta.
 * Proporciona métodos para mapear un solo producto o una lista de productos.
 * @author Henry Wong
 */
public class ProductoMapper {

    public ProductoResponseDTO toResponse(Producto producto) {
        return new ProductoResponseDTO(
                producto.getId(),
                producto.getCodigo(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecio(),
                producto.getStock(),
                producto.getActivo()
        );
    }

    public List<ProductoResponseDTO> toResponseList(List<Producto> productos) {
        return productos.stream().map(this::toResponse).toList();
    }
}
