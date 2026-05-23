package UI;

import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.*;

public class FinanzApp {

    public static void main(String[] args) {
        FlatDarkLaf.setup();
        UIManager.put("Button.arc", 8);
        UIManager.put("Component.arc", 8);
        UIManager.put("TextComponent.arc", 8);

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new MainWindow();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
