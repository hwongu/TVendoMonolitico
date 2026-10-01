package pe.edu.ulima.isw2.tvendodesktop.view;

import pe.edu.ulima.isw2.tvendo.entity.Producto;
import pe.edu.ulima.isw2.tvendodesktop.controller.VentaUIController;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * PanelVenta es un panel de interfaz de usuario que permite registrar ventas de productos.
 * Proporciona campos para ingresar el código de venta, seleccionar productos y cantidades,
 * elegir el tipo de envío y la forma de pago, y registrar la venta.
 * También muestra un detalle de los productos seleccionados en una tabla.
 * @author Henry Wong
 */
public class PanelVenta extends JPanel {

    private final VentaUIController controller;
    private final Map<String, Integer> productosSeleccionados;
    private final Map<String, Producto> catalogoProductos;

    private final JTextField txtCodigoVenta;
    private final JComboBox<ProductoComboItem> cbProductos;
    private final JSpinner spCantidad;
    private final JRadioButton rbEnvioEstandar;
    private final JRadioButton rbEnvioExpress;
    private final JRadioButton rbEnvioProgramado;
    private final ButtonGroup groupEnvio;
    private final JRadioButton rbPagoYape;
    private final JRadioButton rbPagoPlin;
    private final ButtonGroup groupPago;
    private final JTextField txtNumeroDestino;
    private final JTextField txtTitular;
    private final JTextField txtDescripcionPago;
    private final JButton btnRegistrarVenta;
    private final DefaultTableModel detalleModel;
    private final JTable tableDetalle;

    private Runnable postRegistroListener;

    public PanelVenta(VentaUIController controller) {
        this.controller = controller;
        this.productosSeleccionados = new LinkedHashMap<>();
        this.catalogoProductos = new LinkedHashMap<>();
        setLayout(new BorderLayout(8, 8));

        JPanel formulario = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        txtCodigoVenta = new JTextField(25);
        cbProductos = new JComboBox<>();
        spCantidad = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
        rbEnvioEstandar = new JRadioButton("Envío estándar");
        rbEnvioExpress = new JRadioButton("Envío express");
        rbEnvioProgramado = new JRadioButton("Envío programado");
        groupEnvio = new ButtonGroup();
        groupEnvio.add(rbEnvioEstandar);
        groupEnvio.add(rbEnvioExpress);
        groupEnvio.add(rbEnvioProgramado);

        rbPagoYape = new JRadioButton("Yape");
        rbPagoPlin = new JRadioButton("Plin");
        groupPago = new ButtonGroup();
        groupPago.add(rbPagoYape);
        groupPago.add(rbPagoPlin);

        txtNumeroDestino = new JTextField(20);
        txtTitular = new JTextField(25);
        txtDescripcionPago = new JTextField(30);

        JButton btnAgregarProducto = new JButton("Agregar producto");
        JButton btnEliminarProducto = new JButton("Eliminar producto seleccionado");
        btnAgregarProducto.addActionListener(event -> agregarProductoSeleccionado());
        btnEliminarProducto.addActionListener(event -> eliminarProductoSeleccionado());

        int row = 0;
        agregarFilaFormulario(formulario, gbc, row++, "Código de venta:", txtCodigoVenta);

        JPanel filaProducto = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        filaProducto.add(new JLabel("Producto:"));
        filaProducto.add(cbProductos);
        filaProducto.add(new JLabel("Cantidad:"));
        filaProducto.add(spCantidad);
        filaProducto.add(btnAgregarProducto);
        filaProducto.add(btnEliminarProducto);
        agregarFilaFormulario(formulario, gbc, row++, "", filaProducto);

        JPanel panelEnvio = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panelEnvio.add(rbEnvioEstandar);
        panelEnvio.add(rbEnvioExpress);
        panelEnvio.add(rbEnvioProgramado);
        agregarFilaFormulario(formulario, gbc, row++, "Tipo de envío:", panelEnvio);

        JPanel panelPago = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panelPago.add(rbPagoYape);
        panelPago.add(rbPagoPlin);
        agregarFilaFormulario(formulario, gbc, row++, "Forma de pago:", panelPago);

        agregarFilaFormulario(formulario, gbc, row++, "Número destino:", txtNumeroDestino);
        agregarFilaFormulario(formulario, gbc, row++, "Titular:", txtTitular);
        agregarFilaFormulario(formulario, gbc, row++, "Descripción pago:", txtDescripcionPago);

        add(formulario, BorderLayout.NORTH);

        detalleModel = new DefaultTableModel(
                new Object[]{"Código", "Nombre", "Precio", "Cantidad", "Subtotal"},
                0
        ) {
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };
        tableDetalle = new JTable(detalleModel);
        add(new JScrollPane(tableDetalle), BorderLayout.CENTER);

        JPanel pie = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnRegistrarVenta = new JButton("Registrar Venta");
        btnRegistrarVenta.addActionListener(event -> registrarVentaAsync());
        pie.add(btnRegistrarVenta);
        add(pie, BorderLayout.SOUTH);
    }

    public void actualizarProductosAsync() {
        SwingWorker<List<Producto>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Producto> doInBackground() {
                return controller.listarProductos();
            }

            @Override
            protected void done() {
                try {
                    actualizarCatalogo(get());
                    refrescarTablaDetalle();
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    mostrarError("Operación interrumpida.");
                } catch (ExecutionException ex) {
                    mostrarError(obtenerMensajeError(ex));
                }
            }
        };
        worker.execute();
    }

    public void setPostRegistroListener(Runnable postRegistroListener) {
        this.postRegistroListener = postRegistroListener;
    }

    private void actualizarCatalogo(List<Producto> productos) {
        catalogoProductos.clear();
        cbProductos.removeAllItems();
        for (Producto producto : productos) {
            catalogoProductos.put(producto.getCodigo(), producto);
            cbProductos.addItem(new ProductoComboItem(producto.getCodigo(), producto.getNombre(), producto.getStock()));
        }
    }

    private void agregarProductoSeleccionado() {
        ProductoComboItem item = (ProductoComboItem) cbProductos.getSelectedItem();
        if (item == null) {
            mostrarError("Debe seleccionar un producto.");
            return;
        }

        int cantidad = (int) spCantidad.getValue();
        if (cantidad <= 0) {
            mostrarError("La cantidad debe ser mayor a cero.");
            return;
        }

        productosSeleccionados.merge(item.codigo(), cantidad, Integer::sum);
        refrescarTablaDetalle();
        spCantidad.setValue(1);
    }

    private void eliminarProductoSeleccionado() {
        int selectedRow = tableDetalle.getSelectedRow();
        if (selectedRow < 0) {
            mostrarError("Debe seleccionar una fila del detalle.");
            return;
        }

        String codigo = String.valueOf(detalleModel.getValueAt(selectedRow, 0));
        productosSeleccionados.remove(codigo);
        refrescarTablaDetalle();
    }

    private void registrarVentaAsync() {
        String codigoVenta = txtCodigoVenta.getText().trim();
        if (codigoVenta.isEmpty()) {
            mostrarError("Debe ingresar el código de venta.");
            return;
        }
        if (productosSeleccionados.isEmpty()) {
            mostrarError("Debe agregar al menos un producto.");
            return;
        }

        String tipoEnvio = obtenerTipoEnvio();
        if (tipoEnvio == null) {
            mostrarError("Debe seleccionar un tipo de envío.");
            return;
        }

        String tipoPago = obtenerTipoPago();
        if (tipoPago == null) {
            mostrarError("Debe seleccionar una forma de pago.");
            return;
        }

        String numeroDestino = txtNumeroDestino.getText().trim();
        if (numeroDestino.isEmpty()) {
            mostrarError("Debe ingresar el número de destino.");
            return;
        }

        String titular = txtTitular.getText().trim();
        if (titular.isEmpty()) {
            mostrarError("Debe ingresar el titular.");
            return;
        }

        String descripcionPago = txtDescripcionPago.getText().trim();
        Map<String, Integer> productosParaRegistro = new LinkedHashMap<>(productosSeleccionados);

        btnRegistrarVenta.setEnabled(false);
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                controller.registrarVenta(
                        codigoVenta,
                        productosParaRegistro,
                        tipoEnvio,
                        tipoPago,
                        numeroDestino,
                        titular,
                        descripcionPago
                );
                return null;
            }

            @Override
            protected void done() {
                btnRegistrarVenta.setEnabled(true);
                try {
                    get();
                    limpiarFormulario();
                    actualizarProductosAsync();
                    JOptionPane.showMessageDialog(PanelVenta.this, "Venta registrada correctamente");
                    if (postRegistroListener != null) {
                        postRegistroListener.run();
                    }
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    mostrarError("Operación interrumpida.");
                } catch (ExecutionException ex) {
                    mostrarError(obtenerMensajeError(ex));
                }
            }
        };
        worker.execute();
    }

    private void limpiarFormulario() {
        txtCodigoVenta.setText("");
        txtNumeroDestino.setText("");
        txtTitular.setText("");
        txtDescripcionPago.setText("");
        spCantidad.setValue(1);
        groupEnvio.clearSelection();
        groupPago.clearSelection();
        productosSeleccionados.clear();
        refrescarTablaDetalle();
    }

    private void refrescarTablaDetalle() {
        detalleModel.setRowCount(0);
        for (Map.Entry<String, Integer> entry : productosSeleccionados.entrySet()) {
            String codigo = entry.getKey();
            Integer cantidad = entry.getValue();
            Producto producto = catalogoProductos.get(codigo);
            String nombre = producto != null ? producto.getNombre() : "(no disponible)";
            BigDecimal precio = producto != null ? producto.getPrecio() : BigDecimal.ZERO;
            BigDecimal subtotal = precio.multiply(BigDecimal.valueOf(cantidad));
            detalleModel.addRow(new Object[]{codigo, nombre, precio, cantidad, subtotal});
        }
    }

    private void agregarFilaFormulario(JPanel panel, GridBagConstraints gbc, int row, String labelText, java.awt.Component component) {
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.weightx = 0;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(component, gbc);
    }

    private String obtenerTipoEnvio() {
        if (rbEnvioEstandar.isSelected()) {
            return "ESTANDAR";
        }
        if (rbEnvioExpress.isSelected()) {
            return "EXPRESS";
        }
        if (rbEnvioProgramado.isSelected()) {
            return "PROGRAMADO";
        }
        return null;
    }

    private String obtenerTipoPago() {
        if (rbPagoYape.isSelected()) {
            return "YAPE";
        }
        if (rbPagoPlin.isSelected()) {
            return "PLIN";
        }
        return null;
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private String obtenerMensajeError(ExecutionException ex) {
        Throwable causa = ex.getCause();
        if (causa != null && causa.getMessage() != null && !causa.getMessage().isBlank()) {
            return causa.getMessage();
        }
        return "No fue posible completar la operación.";
    }

    private record ProductoComboItem(String codigo, String nombre, Integer stock) {
        @Override
        public String toString() {
            return codigo + " - " + nombre + " (Stock: " + stock + ")";
        }
    }
}
