package UI;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import Modelo.*;

public class MainWindow extends JFrame {

    private JPanel contentPanel;
    private DashboardPanel dashboardPanel;
    private Empresa empresa;
    private JButton activeButton = null;

    // Saved nav-button references so dashboard can trigger setActive
    private JButton btnDash;
    private JButton btnIngresos;
    private JButton btnGastos;
    private JButton btnEmpleados;
    private JButton btnNomina;
    private JButton btnReportes;

    private static final Color BG_SIDEBAR    = new Color(22, 22, 28);
    private static final Color BG_NAV_HOVER  = new Color(36, 36, 50);
    private static final Color BG_NAV_ACTIVE = new Color(35, 60, 110);
    private static final Color TEXT_NAV      = new Color(130, 130, 145);
    private static final Color TEXT_NAV_ACT  = new Color(150, 195, 255);
    private static final Color TEXT_SECTION  = new Color(70, 70, 92);
    private static final Color PRIMARY       = new Color(100, 150, 255);
    private static final Color BORDER_SIDE   = new Color(40, 40, 55);
    private static final Color BG_CONTENT    = new Color(28, 28, 32);

    public MainWindow() {
        setTitle("Finanz");
        setSize(1300, 760);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        setBackground(BG_CONTENT);

        cargarDatos();

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_CONTENT);

        root.add(buildSidebar(), BorderLayout.WEST);

        contentPanel = new JPanel(new CardLayout());
        contentPanel.setBackground(BG_CONTENT);

        dashboardPanel = new DashboardPanel(empresa);
        contentPanel.add(dashboardPanel,              "Dashboard");
        contentPanel.add(new IngresosPanel(empresa),  "Ingresos");
        contentPanel.add(new GastosPanel(empresa),    "Gastos");
        contentPanel.add(new EmpleadosPanel(empresa), "Empleados");
        contentPanel.add(new NominaPanel(empresa),    "Nomina");
        contentPanel.add(new ReportesPanel(empresa),  "Reportes");

        // Wire dashboard nav links
        dashboardPanel.setNavHandlers(
            () -> { setActive(btnReportes);  switchPanel("Reportes");  },
            () -> { setActive(btnEmpleados); switchPanel("Empleados"); }
        );

        root.add(contentPanel, BorderLayout.CENTER);
        add(root);
    }

    private void cargarDatos() {
        try {
            empresa = new Empresa("Tech PYME S.A.S", "900-123-456-7");
            GestorArchivos.cargarEmpresa(empresa);
        } catch (IOException e) {
            empresa = new Empresa("Tech PYME S.A.S", "900-123-456-7");
        }
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(BG_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(190, getHeight()));
        sidebar.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 0, 1, BORDER_SIDE),
                new EmptyBorder(24, 0, 24, 0)));

        // ── Logo ──────────────────────────────────────────────────────────────
        JPanel logoPanel = new JPanel(new BorderLayout(10, 0));
        logoPanel.setBackground(BG_SIDEBAR);
        logoPanel.setBorder(new EmptyBorder(0, 18, 24, 18));
        logoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));

        JLabel logoIcon = new JLabel("◈", SwingConstants.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(35, 55, 95));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        logoIcon.setPreferredSize(new Dimension(38, 38));
        logoIcon.setMinimumSize(new Dimension(38, 38));
        logoIcon.setMaximumSize(new Dimension(38, 38));
        logoIcon.setFont(new Font("Segoe UI", Font.BOLD, 16));
        logoIcon.setForeground(new Color(150, 195, 255));
        logoIcon.setOpaque(false);

        JPanel logoText = new JPanel(new BorderLayout());
        logoText.setBackground(BG_SIDEBAR);
        JLabel logoName = new JLabel("Finanz");
        logoName.setFont(new Font("Segoe UI", Font.BOLD, 17));
        logoName.setForeground(new Color(225, 225, 230));
        JLabel logoSub = new JLabel("Gestión financiera");
        logoSub.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        logoSub.setForeground(TEXT_SECTION);
        logoText.add(logoName, BorderLayout.NORTH);
        logoText.add(logoSub, BorderLayout.SOUTH);

        logoPanel.add(logoIcon, BorderLayout.WEST);
        logoPanel.add(logoText, BorderLayout.CENTER);
        sidebar.add(logoPanel);

        sidebar.add(makeSeparator());
        sidebar.add(Box.createVerticalStrut(14));

        // ── PRINCIPAL ─────────────────────────────────────────────────────────
        sidebar.add(makeSectionLabel("PRINCIPAL"));
        btnDash     = makeNavBtn("◆", "Dashboard", "Dashboard");
        btnIngresos = makeNavBtn("↑", "Ingresos",  "Ingresos");
        btnGastos   = makeNavBtn("↓", "Gastos",    "Gastos");
        sidebar.add(btnDash);
        sidebar.add(btnIngresos);
        sidebar.add(btnGastos);

        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(makeSeparator());
        sidebar.add(Box.createVerticalStrut(8));

        // ── EMPRESA ───────────────────────────────────────────────────────────
        sidebar.add(makeSectionLabel("EMPRESA"));
        btnEmpleados = makeNavBtn("◉", "Empleados", "Empleados");
        btnNomina    = makeNavBtn("≡", "Nómina",    "Nomina");
        sidebar.add(btnEmpleados);
        sidebar.add(btnNomina);

        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(makeSeparator());
        sidebar.add(Box.createVerticalStrut(8));

        // ── ANÁLISIS ──────────────────────────────────────────────────────────
        sidebar.add(makeSectionLabel("ANÁLISIS"));
        btnReportes = makeNavBtn("◈", "Reportes", "Reportes");
        sidebar.add(btnReportes);

        sidebar.add(Box.createVerticalGlue());

        JLabel ver = new JLabel("v1.0");
        ver.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        ver.setForeground(TEXT_SECTION);
        ver.setBorder(new EmptyBorder(0, 18, 0, 0));
        sidebar.add(ver);

        setActive(btnDash);
        return sidebar;
    }

    private JButton makeNavBtn(String icon, String label, String panelKey) {
        JButton btn = new JButton(icon + "  " + label);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(TEXT_NAV);
        btn.setBackground(BG_SIDEBAR);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(true);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btn.setBorder(new EmptyBorder(0, 18, 0, 18));

        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                if (btn != activeButton) btn.setBackground(BG_NAV_HOVER);
            }
            @Override public void mouseExited(MouseEvent e) {
                if (btn != activeButton) btn.setBackground(BG_SIDEBAR);
            }
        });

        btn.addActionListener(e -> {
            setActive(btn);
            switchPanel(panelKey);
        });

        return btn;
    }

    void setActive(JButton btn) {
        if (activeButton != null) {
            activeButton.setBackground(BG_SIDEBAR);
            activeButton.setForeground(TEXT_NAV);
            activeButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        }
        activeButton = btn;
        btn.setBackground(BG_NAV_ACTIVE);
        btn.setForeground(TEXT_NAV_ACT);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
    }

    private void switchPanel(String name) {
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, name);
        if (name.equals("Dashboard")) dashboardPanel.refrescar(empresa);
    }

    private JLabel makeSectionLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(TEXT_SECTION);
        lbl.setBorder(new EmptyBorder(4, 18, 6, 18));
        lbl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        return lbl;
    }

    private JSeparator makeSeparator() {
        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER_SIDE);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return sep;
    }
}
