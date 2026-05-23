package UI;

import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.*;

/**
 * Clase principal de la aplicación Finanz.
 * Inicializa la interfaz gráfica con FlatLaf y lanza la ventana principal.
 */
public class FinanzApp {

    public static void main(String[] args) {
        // Configurar el Look & Feel de FlatLaf
        FlatDarkLaf.setup();
        UIManager.put("Button.arc", 6);
        UIManager.put("Component.arc", 6);
        UIManager.put("TextComponent.arc", 4);

        // Crear y mostrar la ventana principal en el EDT
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new MainWindow();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
