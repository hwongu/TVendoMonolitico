package pe.edu.ulima.isw2.tvendodesktop.view;

import pe.edu.ulima.isw2.tvendodesktop.controller.VentaUIController;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.concurrent.ExecutionException;

/**
 * PanelComprobante es un componente de la interfaz de usuario que permite al usuario ingresar un código de venta,
 * mostrar el comprobante correspondiente y anular la venta si es necesario.
 * Utiliza un controlador (VentaUIController) para interactuar con la lógica de negocio.
 * @author Henry Wong
 */
public class PanelComprobante extends JPanel {

    private final VentaUIController controller;
    private final JTextField txtCodigoVenta;
    private final JTextArea txtAreaComprobante;
    private final JButton btnMostrarComprobante;
    private final JButton btnAnularVenta;

    private Runnable postAnulacionListener;

    public PanelComprobante(VentaUIController controller) {
        this.controller = controller;
        setLayout(new BorderLayout(8, 8));

        JPanel cabecera = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        cabecera.add(new JLabel("Código de venta:"));

        txtCodigoVenta = new JTextField(16);
        cabecera.add(txtCodigoVenta);

        btnMostrarComprobante = new JButton("Mostrar comprobante");
        btnMostrarComprobante.addActionListener(event -> mostrarComprobanteAsync());
        cabecera.add(btnMostrarComprobante);

        btnAnularVenta = new JButton("Anular venta");
        btnAnularVenta.addActionListener(event -> anularVentaAsync());
        cabecera.add(btnAnularVenta);

        add(cabecera, BorderLayout.NORTH);

        txtAreaComprobante = new JTextArea();
        txtAreaComprobante.setEditable(false);
        add(new JScrollPane(txtAreaComprobante), BorderLayout.CENTER);
    }

    public void setPostAnulacionListener(Runnable postAnulacionListener) {
        this.postAnulacionListener = postAnulacionListener;
    }

    private void mostrarComprobanteAsync() {
        String codigoVenta = txtCodigoVenta.getText().trim();
        if (codigoVenta.isEmpty()) {
            mostrarError("Debe ingresar el código de venta.");
            return;
        }

        habilitarBotones(false);
        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() {
                return controller.obtenerComprobante(codigoVenta);
            }

            @Override
            protected void done() {
                habilitarBotones(true);
                try {
                    txtAreaComprobante.setText(get());
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

    private void anularVentaAsync() {
        String codigoVenta = txtCodigoVenta.getText().trim();
        if (codigoVenta.isEmpty()) {
            mostrarError("Debe ingresar el código de venta.");
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro de anular la venta " + codigoVenta + "?",
                "Confirmar anulación",
                JOptionPane.YES_NO_OPTION
        );
        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        habilitarBotones(false);
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                controller.anularVenta(codigoVenta);
                return null;
            }

            @Override
            protected void done() {
                habilitarBotones(true);
                try {
                    get();
                    JOptionPane.showMessageDialog(PanelComprobante.this, "Venta anulada correctamente");
                    mostrarComprobanteAsync();
                    if (postAnulacionListener != null) {
                        postAnulacionListener.run();
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

    private void habilitarBotones(boolean enabled) {
        btnMostrarComprobante.setEnabled(enabled);
        btnAnularVenta.setEnabled(enabled);
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
}
