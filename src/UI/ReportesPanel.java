package UI;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

/**
 * Panel de Reportes (placeholder).
 */
public class ReportesPanel extends JPanel {
    public ReportesPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(40, 40, 45));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Reportes");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(220, 220, 230));

        JLabel descLabel = new JLabel("Análisis y reportes financieros");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descLabel.setForeground(new Color(150, 150, 160));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(40, 40, 45));
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(descLabel, BorderLayout.SOUTH);

        add(headerPanel, BorderLayout.NORTH);

        JLabel contentLabel = new JLabel("En construcción...");
        contentLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        contentLabel.setForeground(new Color(120, 120, 130));
        contentLabel.setHorizontalAlignment(SwingConstants.CENTER);

        add(contentLabel, BorderLayout.CENTER);
    }
}
