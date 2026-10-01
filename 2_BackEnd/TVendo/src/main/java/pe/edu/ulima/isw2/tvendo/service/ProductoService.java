package pe.edu.ulima.isw2.tvendo.service;

import pe.edu.ulima.isw2.tvendo.dao.ProductoDAO;
import pe.edu.ulima.isw2.tvendo.entity.Producto;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para la gestión de productos.
 * Proporciona métodos para buscar, listar y validar productos, así como para actualizar su stock.
 * Implementa la lógica de negocio relacionada con los productos.
 * @author Henry Wong
 */
public class ProductoService {

    private final ProductoDAO productoDAO;

    public ProductoService(ProductoDAO productoDAO) {
        this.productoDAO = productoDAO;
    }

    public Optional<Producto> buscarPorCodigo(String codigo) {
        return productoDAO.buscarPorCodigo(codigo);
    }

    public List<Producto> listarProductos() {
        return productoDAO.listar();
    }

    public Producto validarExistencia(String codigoProducto) {
        return buscarPorCodigo(codigoProducto)
                .orElseThrow(() -> new IllegalArgumentException("Producto no existe: " + codigoProducto));
    }

    public void validarStock(Producto producto, Integer cantidad) {
        if (cantidad == null || cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        }
        if (!Boolean.TRUE.equals(producto.getActivo())) {
            throw new IllegalStateException("El producto está inactivo: " + producto.getCodigo());
        }
        if (producto.getStock() < cantidad) {
            throw new IllegalStateException("Stock insuficiente para el producto: " + producto.getCodigo());
        }
    }

    public void actualizarStock(Long productoId, Integer nuevoStock) {
        if (nuevoStock < 0) {
            throw new IllegalStateException("El stock no puede ser negativo");
        }
        productoDAO.actualizarStock(productoId, nuevoStock);
    }
}
