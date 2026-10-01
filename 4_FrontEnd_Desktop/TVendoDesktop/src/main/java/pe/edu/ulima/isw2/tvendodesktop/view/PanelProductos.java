package pe.edu.ulima.isw2.tvendodesktop.view;

import pe.edu.ulima.isw2.tvendo.entity.Producto;
import pe.edu.ulima.isw2.tvendodesktop.controller.VentaUIController;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * PanelProductos es un panel de la interfaz de usuario que muestra una lista de productos en una tabla.
 * Permite actualizar la lista de productos desde el controlador de la aplicación.
 * Utiliza SwingWorker para realizar la actualización de manera asíncrona y evitar bloquear la interfaz de usuario.
 * @author Henry Wong
 */
public class PanelProductos extends JPanel {

    private final VentaUIController controller;
    private final DefaultTableModel tableModel;
    private final JButton btnActualizar;

    public PanelProductos(VentaUIController controller) {
        this.controller = controller;
        setLayout(new BorderLayout(8, 8));

        this.tableModel = new DefaultTableModel(
                new Object[]{"Código", "Nombre", "Descripción", "Precio", "Stock", "Activo"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnActualizar = new JButton("Actualizar productos");
        btnActualizar.addActionListener(event -> actualizarProductosAsync());
        acciones.add(btnActualizar);
        add(acciones, BorderLayout.SOUTH);
    }

    public void actualizarProductosAsync() {
        btnActualizar.setEnabled(false);
        SwingWorker<List<Producto>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Producto> doInBackground() {
                return controller.listarProductos();
            }

            @Override
            protected void done() {
                btnActualizar.setEnabled(true);
                try {
                    cargarTabla(get());
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

    private void cargarTabla(List<Producto> productos) {
        tableModel.setRowCount(0);
        for (Producto producto : productos) {
            tableModel.addRow(new Object[]{
                    producto.getCodigo(),
                    producto.getNombre(),
                    producto.getDescripcion(),
                    producto.getPrecio(),
                    producto.getStock(),
                    producto.getActivo()
            });
        }
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private String obtenerMensajeError(ExecutionException ex) {
        Throwable causa = ex.getCause();
        if (causa != null && causa.getMessage() != null && !causa.getMessage().isBlank()) {
            return causa.getMessage();
        }
        return "No fue posible actualizar la lista de productos.";
    }
}
