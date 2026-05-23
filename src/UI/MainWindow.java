package UI;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import Modelo.*;

/**
 * Ventana principal de la aplicación.
 * Contiene el sidebar de navegación y el panel de contenido.
 * Carga datos reales desde los CSV.
 */
public class MainWindow extends JFrame {

    private JPanel sidebarPanel;
    private JPanel contentPanel;
    private JButton toggleSidebarBtn;
    private boolean sidebarVisible = true;
    private DashboardPanel dashboardPanel;
    private Empresa empresa;
    private static final int SIDEBAR_WIDTH = 250;
    private static final int SIDEBAR_COLLAPSED = 60;

    public MainWindow() {
        setTitle("Finanz - Gestión Financiera");
        setSize(1400, 800);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);

        // Cargar datos desde CSV
        cargarDatos();

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(30, 30, 35));

        // Sidebar
        sidebarPanel = createSidebar();
        mainPanel.add(sidebarPanel, BorderLayout.WEST);

        // Contenido
        contentPanel = new JPanel(new CardLayout());
        contentPanel.setBackground(new Color(40, 40, 45));

        dashboardPanel = new DashboardPanel(empresa);
        contentPanel.add(dashboardPanel, "Dashboard");
        contentPanel.add(new IngresosPanel(), "Ingresos");
        contentPanel.add(new GastosPanel(), "Gastos");
        contentPanel.add(new EmpleadosPanel(), "Empleados");
        contentPanel.add(new NominaPanel(), "Nomina");
        contentPanel.add(new ReportesPanel(), "Reportes");

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    /**
     * Carga datos desde los archivos CSV.
     */
    private void cargarDatos() {
        try {
            empresa = new Empresa("Tech PYME S.A.S", "900-123-456-7");
            GestorArchivos.cargarEmpresa(empresa);
            System.out.println("[MainWindow] Datos cargados correctamente.");
            System.out.println("  - Empleados: " + empresa.getEmpleados().size());
            System.out.println("  - Transacciones: " + empresa.getTransacciones().size());
        } catch (IOException e) {
            System.err.println("[MainWindow] Error al cargar datos: " + e.getMessage());
            // Crear empresa vacía como fallback
            empresa = new Empresa("Tech PYME S.A.S", "900-123-456-7");
        }
    }

    /**
     * Crea el panel del sidebar con navegación.
     */
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(SIDEBAR_WIDTH, getHeight()));
        sidebar.setBackground(new Color(25, 25, 30));
        sidebar.setBorder(new EmptyBorder(20, 15, 20, 15));

        // Header con logo
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(25, 25, 30));
        headerPanel.setMaximumSize(new Dimension(SIDEBAR_WIDTH, 50));

        JLabel logoLabel = new JLabel("◻ Finanz");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        logoLabel.setForeground(new Color(200, 200, 200));

        toggleSidebarBtn = new JButton("☰");
        toggleSidebarBtn.setFont(new Font("Arial", Font.PLAIN, 16));
        toggleSidebarBtn.setPreferredSize(new Dimension(35, 35));
        toggleSidebarBtn.setOpaque(false);
        toggleSidebarBtn.setContentAreaFilled(false);
        toggleSidebarBtn.setBorderPainted(false);
        toggleSidebarBtn.setForeground(new Color(150, 150, 150));
        toggleSidebarBtn.addActionListener(e -> toggleSidebar());

        headerPanel.add(logoLabel, BorderLayout.WEST);
        headerPanel.add(toggleSidebarBtn, BorderLayout.EAST);
        sidebar.add(headerPanel);
        sidebar.add(Box.createVerticalStrut(20));

        // Sección PRINCIPAL
        sidebar.add(createSectionLabel("PRINCIPAL"));
        sidebar.add(createNavButton("Dashboard", "[D]", () -> switchPanel("Dashboard")));
        sidebar.add(createNavButton("Ingresos", "[+]", () -> switchPanel("Ingresos")));
        sidebar.add(createNavButton("Gastos", "[-]", () -> switchPanel("Gastos")));

        sidebar.add(Box.createVerticalStrut(20));

        // Sección EMPRESA
        sidebar.add(createSectionLabel("EMPRESA"));
        sidebar.add(createNavButton("Empleados", "[E]", () -> switchPanel("Empleados")));
        sidebar.add(createNavButton("Nómina", "[N]", () -> switchPanel("Nomina")));

        sidebar.add(Box.createVerticalStrut(20));

        // Sección ANÁLISIS
        sidebar.add(createSectionLabel("ANÁLISIS"));
        sidebar.add(createNavButton("Reportes", "[R]", () -> switchPanel("Reportes")));

        sidebar.add(Box.createVerticalGlue());

        // Footer
        JLabel versionLabel = new JLabel("v1.0");
        versionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        versionLabel.setForeground(new Color(100, 100, 100));
        sidebar.add(versionLabel);

        return sidebar;
    }

    /**
     * Crea una etiqueta de sección.
     */
    private JLabel createSectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        label.setForeground(new Color(120, 120, 130));
        label.setMaximumSize(new Dimension(Short.MAX_VALUE, 20));
        return label;
    }

    /**
     * Crea un botón de navegación.
     */
    private JButton createNavButton(String text, String icon, Runnable action) {
        JButton btn = new JButton(icon + " " + text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(new Color(180, 180, 190));
        btn.setBackground(new Color(40, 40, 50));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(true);
        btn.setMaximumSize(new Dimension(Short.MAX_VALUE, 40));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(60, 60, 75));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(new Color(40, 40, 50));
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                action.run();
            }
        });

        btn.addActionListener(e -> action.run());
        return btn;
    }

    /**
     * Cambia al panel especificado.
     */
    private void switchPanel(String panelName) {
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, panelName);
    }

    /**
     * Alterna la visibilidad del sidebar.
     */
    private void toggleSidebar() {
        sidebarVisible = !sidebarVisible;
        Dimension newSize = sidebarVisible ?
                new Dimension(SIDEBAR_WIDTH, sidebarPanel.getHeight()) :
                new Dimension(SIDEBAR_COLLAPSED, sidebarPanel.getHeight());
        sidebarPanel.setPreferredSize(newSize);
        sidebarPanel.revalidate();
        repaint();
    }
}
