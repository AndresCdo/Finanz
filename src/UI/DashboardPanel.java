package UI;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import Modelo.*;

/**
 * Panel del Dashboard - Vista principal con métricas y resumen financiero.
 * Conectado a datos reales de la empresa.
 */
public class DashboardPanel extends JPanel {

    private Empresa empresa;

    public DashboardPanel() {
        this(null);
    }

    public DashboardPanel(Empresa empresa) {
        this.empresa = empresa;

        setLayout(new BorderLayout());
        setBackground(new Color(40, 40, 45));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(40, 40, 45));
        headerPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 60));

        JLabel titleLabel = new JLabel("Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(220, 220, 230));

        JLabel dateLabel = new JLabel("📅 " + LocalDate.now().getMonth());
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateLabel.setForeground(new Color(150, 150, 160));

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(dateLabel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Contenido principal
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(40, 40, 45));
        contentPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        // Sección de métricas
        contentPanel.add(createMetricsSection());
        contentPanel.add(Box.createVerticalStrut(20));

        // Alerta de status
        contentPanel.add(createStatusAlert());
        contentPanel.add(Box.createVerticalStrut(20));

        // Sección de movimientos y empleados
        JPanel bottomSection = new JPanel(new GridLayout(1, 2, 20, 0));
        bottomSection.setBackground(new Color(40, 40, 45));
        bottomSection.setMaximumSize(new Dimension(Short.MAX_VALUE, 300));

        bottomSection.add(createMovementsPanel());
        bottomSection.add(createEmployeesPanel());

        contentPanel.add(bottomSection);
        contentPanel.add(Box.createVerticalGlue());

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBackground(new Color(40, 40, 45));
        scrollPane.getViewport().setBackground(new Color(40, 40, 45));
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Crea la sección de métricas (4 cards) con datos reales.
     */
    private JPanel createMetricsSection() {
        JPanel metricsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        metricsPanel.setBackground(new Color(40, 40, 45));
        metricsPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 140));

        if (empresa != null) {
            double totalIngresos = 0;
            double totalGastos = 0;
            double costoNomina = 0;

            // Calcular totales
            for (Transaccion t : empresa.getTransacciones()) {
                if (t instanceof Ingreso) {
                    totalIngresos += t.getMonto();
                } else if (t instanceof Gasto) {
                    totalGastos += t.getMonto();
                }
            }

            for (Empleado e : empresa.getEmpleados()) {
                costoNomina += e.calcularSalario();
            }

            double balance = totalIngresos - totalGastos;

            metricsPanel.add(createMetricCard("Ingresos totales", formatearMoneda(totalIngresos), "$", new Color(76, 175, 80)));
            metricsPanel.add(createMetricCard("Gastos totales", formatearMoneda(totalGastos), "$", new Color(244, 67, 54)));
            metricsPanel.add(createMetricCard("Costo nómina", formatearMoneda(costoNomina), "$", new Color(158, 158, 158)));
            metricsPanel.add(createMetricCard("Balance", formatearMoneda(balance), "✓", balance >= 0 ? new Color(76, 175, 80) : new Color(244, 67, 54)));
        } else {
            metricsPanel.add(createMetricCard("Ingresos totales", "$0", "$", new Color(76, 175, 80)));
            metricsPanel.add(createMetricCard("Gastos totales", "$0", "$", new Color(244, 67, 54)));
            metricsPanel.add(createMetricCard("Costo nómina", "$0", "$", new Color(158, 158, 158)));
            metricsPanel.add(createMetricCard("Balance", "$0", "✓", new Color(76, 175, 80)));
        }

        return metricsPanel;
    }

    /**
     * Crea una card de métrica individual.
     */
    private JPanel createMetricCard(String title, String value, String icon, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(55, 55, 65));
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(70, 70, 80), 1, true),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(55, 55, 65));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        iconLabel.setForeground(accentColor);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLabel.setForeground(new Color(150, 150, 160));

        topPanel.add(iconLabel, BorderLayout.WEST);
        topPanel.add(titleLabel, BorderLayout.CENTER);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        valueLabel.setForeground(new Color(220, 220, 230));

        card.add(topPanel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    /**
     * Crea la alerta de estado financiero.
     */
    private JPanel createStatusAlert() {
        JPanel alertPanel = new JPanel(new BorderLayout(10, 0));

        double balance = 0;
        if (empresa != null) {
            balance = empresa.calcularBalance();
        }

        boolean superavit = balance >= 0;
        Color bgColor = superavit ? new Color(76, 175, 80, 20) : new Color(244, 67, 54, 20);
        Color borderColor = superavit ? new Color(76, 175, 80, 80) : new Color(244, 67, 54, 80);
        Color textColor = superavit ? new Color(76, 175, 80) : new Color(244, 67, 54);
        String estado = superavit ? "Superávit" : "Déficit";

        alertPanel.setBackground(bgColor);
        alertPanel.setBorder(new CompoundBorder(
                new LineBorder(borderColor, 1, true),
                new EmptyBorder(15, 15, 15, 15)
        ));
        alertPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 70));

        JLabel statusIcon = new JLabel("✓ " + estado);
        statusIcon.setFont(new Font("Segoe UI", Font.BOLD, 13));
        statusIcon.setOpaque(true);
        statusIcon.setBackground(textColor);
        statusIcon.setForeground(Color.WHITE);
        statusIcon.setBorder(new EmptyBorder(5, 10, 5, 10));
        statusIcon.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

        String mensaje = superavit ?
                "La empresa está en superávit este período. Los ingresos superan los gastos." :
                "La empresa está en déficit. Los gastos superan los ingresos.";

        JLabel messageLabel = new JLabel(mensaje);
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        messageLabel.setForeground(new Color(180, 200, 180));

        alertPanel.add(statusIcon, BorderLayout.WEST);
        alertPanel.add(messageLabel, BorderLayout.CENTER);

        return alertPanel;
    }

    /**
     * Crea el panel de últimos movimientos con datos reales.
     */
    private JPanel createMovementsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(55, 55, 65));
        panel.setBorder(new CompoundBorder(
                new LineBorder(new Color(70, 70, 80), 1, true),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel("Últimos movimientos");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(new Color(220, 220, 230));

        JLabel linkLabel = new JLabel("Ver todos →");
        linkLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        linkLabel.setForeground(new Color(100, 150, 255));
        linkLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(55, 55, 65));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(linkLabel, BorderLayout.EAST);

        panel.add(headerPanel, BorderLayout.NORTH);

        JPanel movementsListPanel = new JPanel();
        movementsListPanel.setLayout(new BoxLayout(movementsListPanel, BoxLayout.Y_AXIS));
        movementsListPanel.setBackground(new Color(55, 55, 65));
        movementsListPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        if (empresa != null && !empresa.getTransacciones().isEmpty()) {
            // Mostrar últimas 4 transacciones
            List<Transaccion> transacciones = empresa.getTransacciones();
            int inicio = Math.max(0, transacciones.size() - 4);

            for (int i = transacciones.size() - 1; i >= inicio; i--) {
                Transaccion t = transacciones.get(i);
                String icon = t instanceof Ingreso ? "📈" : "📉";
                Color color = t instanceof Ingreso ? new Color(76, 175, 80) : new Color(244, 67, 54);
                movementsListPanel.add(createMovementItem(icon, t.getDescripcion(),
                        t.getFecha() + " · " + t.getCategoria(),
                        (t instanceof Ingreso ? "+" : "-") + formatearMoneda(t.getMonto()), color));
                if (i > inicio) {
                    movementsListPanel.add(Box.createVerticalStrut(10));
                }
            }
        }

        panel.add(movementsListPanel, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Crea un item de movimiento.
     */
    private JPanel createMovementItem(String icon, String title, String info, String amount, Color amountColor) {
        JPanel item = new JPanel(new BorderLayout(10, 0));
        item.setBackground(new Color(55, 55, 65));
        item.setMaximumSize(new Dimension(Short.MAX_VALUE, 40));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        iconLabel.setPreferredSize(new Dimension(35, 35));

        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(new Color(55, 55, 65));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLabel.setForeground(new Color(220, 220, 230));

        JLabel infoLabel = new JLabel(info);
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        infoLabel.setForeground(new Color(120, 120, 130));

        infoPanel.add(titleLabel, BorderLayout.NORTH);
        infoPanel.add(infoLabel, BorderLayout.SOUTH);

        JLabel amountLabel = new JLabel(amount);
        amountLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        amountLabel.setForeground(amountColor);

        item.add(iconLabel, BorderLayout.WEST);
        item.add(infoPanel, BorderLayout.CENTER);
        item.add(amountLabel, BorderLayout.EAST);

        return item;
    }

    /**
     * Crea el panel de empleados activos con datos reales.
     */
    private JPanel createEmployeesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(55, 55, 65));
        panel.setBorder(new CompoundBorder(
                new LineBorder(new Color(70, 70, 80), 1, true),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel("Empleados activos");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(new Color(220, 220, 230));

        JLabel linkLabel = new JLabel("Gestionar →");
        linkLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        linkLabel.setForeground(new Color(100, 150, 255));
        linkLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(55, 55, 65));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(linkLabel, BorderLayout.EAST);

        panel.add(headerPanel, BorderLayout.NORTH);

        JPanel employeesListPanel = new JPanel();
        employeesListPanel.setLayout(new BoxLayout(employeesListPanel, BoxLayout.Y_AXIS));
        employeesListPanel.setBackground(new Color(55, 55, 65));
        employeesListPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        if (empresa != null && !empresa.getEmpleados().isEmpty()) {
            List<Empleado> empleados = empresa.getEmpleados();
            int limite = Math.min(4, empleados.size());

            for (int i = 0; i < limite; i++) {
                Empleado e = empleados.get(i);
                String initials = e.getNombre().replaceAll("[a-z ]", "");
                String type = e instanceof EmpleadoFijo ? "Fijo" : "Por horas";
                String salary = formatearMoneda(e.calcularSalario());

                employeesListPanel.add(createEmployeeItem(initials, e.getNombre(), e.getCargo(), type, salary));
                if (i < limite - 1) {
                    employeesListPanel.add(Box.createVerticalStrut(10));
                }
            }
        }

        panel.add(employeesListPanel, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Crea un item de empleado.
     */
    private JPanel createEmployeeItem(String initials, String name, String role, String type, String salary) {
        JPanel item = new JPanel(new BorderLayout(10, 0));
        item.setBackground(new Color(55, 55, 65));
        item.setMaximumSize(new Dimension(Short.MAX_VALUE, 45));

        JLabel avatarLabel = new JLabel(initials);
        avatarLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        avatarLabel.setForeground(Color.WHITE);
        avatarLabel.setBackground(new Color(150, 150, 160));
        avatarLabel.setOpaque(true);
        avatarLabel.setHorizontalAlignment(SwingConstants.CENTER);
        avatarLabel.setPreferredSize(new Dimension(40, 40));
        avatarLabel.setBorder(new EmptyBorder(0, 0, 0, 0));

        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(new Color(55, 55, 65));

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        nameLabel.setForeground(new Color(220, 220, 230));

        JPanel detailsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        detailsPanel.setBackground(new Color(55, 55, 65));

        JLabel roleLabel = new JLabel(role);
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        roleLabel.setForeground(new Color(120, 120, 130));

        JLabel typeLabel = new JLabel(type);
        typeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        typeLabel.setForeground(Color.WHITE);
        typeLabel.setBackground(new Color(255, 152, 0));
        typeLabel.setOpaque(true);
        typeLabel.setBorder(new EmptyBorder(2, 6, 2, 6));

        detailsPanel.add(roleLabel);
        detailsPanel.add(typeLabel);

        infoPanel.add(nameLabel, BorderLayout.NORTH);
        infoPanel.add(detailsPanel, BorderLayout.SOUTH);

        JLabel salaryLabel = new JLabel(salary);
        salaryLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        salaryLabel.setForeground(new Color(200, 200, 200));

        item.add(avatarLabel, BorderLayout.WEST);
        item.add(infoPanel, BorderLayout.CENTER);
        item.add(salaryLabel, BorderLayout.EAST);

        return item;
    }

    /**
     * Formatea un número como moneda.
     */
    private String formatearMoneda(double valor) {
        return String.format("$%,.0f", valor);
    }

    /**
     * Establece la empresa para cargar datos.
     */
    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
        revalidate();
        repaint();
    }
}
