package pe.edu.ulima.isw2.tvendodesktop.view;

import pe.edu.ulima.isw2.tvendodesktop.controller.VentaUIController;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;

/**
 * Clase principal de la interfaz gráfica de usuario (GUI) para el sistema de ventas TVendo.
 * Extiende JFrame y configura la ventana principal con pestañas para las diferentes funcionalidades del sistema.
 * Contiene paneles para la gestión de ventas, productos y comprobantes.
 * @author Henry Wong
 */
public class VentanaPrincipal extends JFrame {

    public VentanaPrincipal(VentaUIController controller) {
        setTitle("TVendo - Sistema de Ventas - Henry Wong");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        PanelVenta panelVenta = new PanelVenta(controller);
        PanelProductos panelProductos = new PanelProductos(controller);
        PanelComprobante panelComprobante = new PanelComprobante(controller);

        panelVenta.setPostRegistroListener(panelProductos::actualizarProductosAsync);
        panelComprobante.setPostAnulacionListener(panelProductos::actualizarProductosAsync);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Venta", panelVenta);
        tabbedPane.addTab("Productos", panelProductos);
        tabbedPane.addTab("Comprobante", panelComprobante);
        add(tabbedPane, BorderLayout.CENTER);

        panelVenta.actualizarProductosAsync();
        panelProductos.actualizarProductosAsync();
    }
}
