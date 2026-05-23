package UI;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import Modelo.*;

public class DashboardPanel extends JPanel {

    private Empresa empresa;
    private Runnable onVerMovimientos;
    private Runnable onGestionar;

    private static final Color BG_MAIN      = new Color(28, 28, 32);
    private static final Color BG_CARD      = new Color(38, 38, 44);
    private static final Color BORDER_COLOR = new Color(60, 60, 70);
    private static final Color TEXT_PRIMARY = new Color(225, 225, 230);
    private static final Color TEXT_MUTED   = new Color(130, 130, 145);
    private static final Color TEXT_SUBTLE  = new Color(85, 85, 100);
    private static final Color COLOR_GREEN  = new Color(76, 175, 80);
    private static final Color COLOR_RED    = new Color(229, 83, 75);
    private static final Color ACCENT       = new Color(100, 150, 255);
    private static final Color AMBER        = new Color(255, 183, 77);

    public DashboardPanel() { this(null); }

    public DashboardPanel(Empresa empresa) {
        this.empresa = empresa;
        setLayout(new BorderLayout());
        setBackground(BG_MAIN);
        build();
    }

    public void setNavHandlers(Runnable verMovimientos, Runnable gestionar) {
        this.onVerMovimientos = verMovimientos;
        this.onGestionar = gestionar;
    }

    public void refrescar(Empresa emp) {
        this.empresa = emp;
        removeAll();
        build();
        revalidate();
        repaint();
    }

    private void build() {
        setBorder(new EmptyBorder(28, 28, 28, 28));

        // ── Header ──────────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_MAIN);
        header.setBorder(new EmptyBorder(0, 0, 22, 0));

        JLabel titleLabel = new JLabel("◆  Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_PRIMARY);

        String mes = LocalDate.now().getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "CO"));
        mes = Character.toUpperCase(mes.charAt(0)) + mes.substring(1);
        JLabel datePill = makePill(mes + " " + LocalDate.now().getYear());

        header.add(titleLabel, BorderLayout.WEST);
        header.add(datePill, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // ── Scrollable content ───────────────────────────────────────────────
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(BG_MAIN);

        content.add(buildMetricsRow());
        content.add(Box.createVerticalStrut(16));
        content.add(buildAlertBar());
        content.add(Box.createVerticalStrut(16));

        JPanel bottom = new JPanel(new GridLayout(1, 2, 18, 0));
        bottom.setBackground(BG_MAIN);
        bottom.setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));
        bottom.add(buildMovementsCard());
        bottom.add(buildEmployeesCard());
        content.add(bottom);
        content.add(Box.createVerticalGlue());

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.setBackground(BG_MAIN);
        scroll.getViewport().setBackground(BG_MAIN);
        scroll.getVerticalScrollBar().setUnitIncrement(12);
        add(scroll, BorderLayout.CENTER);
    }

    // ── Metric cards ─────────────────────────────────────────────────────────

    private JPanel buildMetricsRow() {
        double ingresos = 0, gastos = 0, nomina = 0;
        if (empresa != null) {
            for (Transaccion t : empresa.getTransacciones()) {
                if (t instanceof Ingreso) ingresos += t.getMonto();
                else                      gastos   += t.getMonto();
            }
            for (Empleado e : empresa.getEmpleados()) nomina += e.calcularSalario();
        }
        double balance = ingresos - gastos;

        JPanel row = new JPanel(new GridLayout(1, 4, 14, 0));
        row.setBackground(BG_MAIN);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 108));
        row.add(metricCard("↑", "Ingresos totales", fmt(ingresos), COLOR_GREEN));
        row.add(metricCard("↓", "Gastos totales",   fmt(gastos),   COLOR_RED));
        row.add(metricCard("◉", "Costo nómina",     fmt(nomina),   TEXT_MUTED));
        row.add(metricCard("≈", "Balance",          fmt(balance),  balance >= 0 ? COLOR_GREEN : COLOR_RED));
        return row;
    }

    private JPanel metricCard(String icon, String title, String value, Color accent) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(BG_CARD);
        card.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(16, 18, 16, 18)));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        top.setBackground(BG_CARD);
        JLabel dot = new JLabel(icon);
        dot.setForeground(accent);
        dot.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(TEXT_MUTED);
        top.add(dot);
        top.add(lbl);

        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.BOLD, 20));
        val.setForeground(accent);
        val.setBorder(new EmptyBorder(6, 0, 0, 0));

        card.add(top, BorderLayout.NORTH);
        card.add(val, BorderLayout.CENTER);
        return card;
    }

    // ── Alert bar ────────────────────────────────────────────────────────────

    private JPanel buildAlertBar() {
        double balance   = empresa != null ? empresa.calcularBalance() : 0;
        boolean superavit = balance >= 0;

        Color bg     = superavit ? new Color(76, 175, 80, 25)  : new Color(229, 83, 75, 25);
        Color border = superavit ? new Color(76, 175, 80, 80)  : new Color(229, 83, 75, 80);
        Color badge  = superavit ? COLOR_GREEN : COLOR_RED;
        String texto = superavit
                ? "La empresa está en superávit. Ingresos superan gastos en " + fmt(balance) + "."
                : "La empresa está en déficit. Gastos superan ingresos en " + fmt(Math.abs(balance)) + ".";

        JPanel alert = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        alert.setBackground(bg);
        alert.setBorder(new CompoundBorder(
                new LineBorder(border, 1, true),
                new EmptyBorder(12, 14, 12, 14)));
        alert.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JLabel badgeLbl = new JLabel(superavit ? "  Superávit  " : "  Déficit  ");
        badgeLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badgeLbl.setForeground(Color.WHITE);
        badgeLbl.setBackground(badge);
        badgeLbl.setOpaque(true);
        badgeLbl.setBorder(new EmptyBorder(3, 8, 3, 8));

        JLabel msg = new JLabel(texto);
        msg.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        msg.setForeground(TEXT_PRIMARY);

        alert.add(badgeLbl);
        alert.add(msg);
        return alert;
    }

    // ── Last movements ───────────────────────────────────────────────────────

    private JPanel buildMovementsCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(BG_CARD);
        card.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(18, 18, 18, 18)));

        JPanel hdr = new JPanel(new BorderLayout());
        hdr.setBackground(BG_CARD);
        hdr.setBorder(new EmptyBorder(0, 0, 14, 0));
        JLabel t = new JLabel("↕  Últimos movimientos");
        t.setFont(new Font("Segoe UI", Font.BOLD, 14));
        t.setForeground(TEXT_PRIMARY);

        JLabel link = new JLabel("Ver todos →");
        link.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        link.setForeground(ACCENT);
        link.setCursor(new Cursor(Cursor.HAND_CURSOR));
        link.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (onVerMovimientos != null) onVerMovimientos.run();
            }
            @Override public void mouseEntered(MouseEvent e) { link.setForeground(Color.WHITE); }
            @Override public void mouseExited(MouseEvent e)  { link.setForeground(ACCENT); }
        });

        hdr.add(t, BorderLayout.WEST);
        hdr.add(link, BorderLayout.EAST);
        card.add(hdr, BorderLayout.NORTH);

        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setBackground(BG_CARD);

        if (empresa != null && !empresa.getTransacciones().isEmpty()) {
            List<Transaccion> all = empresa.getTransacciones();
            int start = Math.max(0, all.size() - 4);
            for (int i = all.size() - 1; i >= start; i--) {
                Transaccion tr = all.get(i);
                boolean esIngreso = tr instanceof Ingreso;
                Color iconBg = esIngreso ? new Color(25, 55, 25) : new Color(55, 25, 25);
                Color iconFg = esIngreso ? COLOR_GREEN            : COLOR_RED;
                String amount = (esIngreso ? "+" : "−") + fmt(tr.getMonto());
                String info   = tr.getFecha().getDayOfMonth() + " "
                        + tr.getFecha().getMonth().getDisplayName(TextStyle.SHORT, new Locale("es"))
                        + " · " + tr.getCategoria();
                list.add(buildMovementRow(tr.getDescripcion(), info, amount, iconBg, iconFg));
                if (i > start) list.add(makeRowSep());
            }
        } else {
            list.add(emptyLabel("Sin movimientos registrados"));
        }
        card.add(list, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildMovementRow(String title, String info, String amount,
                                    Color iconBg, Color iconFg) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setBackground(BG_CARD);
        row.setBorder(new EmptyBorder(9, 0, 9, 0));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));

        JLabel icon = new JLabel(iconFg.equals(COLOR_GREEN) ? "+" : "−", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI", Font.BOLD, 14));
        icon.setForeground(iconFg);
        icon.setBackground(iconBg);
        icon.setOpaque(true);
        icon.setPreferredSize(new Dimension(32, 32));
        icon.setBorder(new LineBorder(iconBg, 1, true));

        JPanel info2 = new JPanel(new BorderLayout());
        info2.setBackground(BG_CARD);
        JLabel tl = new JLabel(title);
        tl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tl.setForeground(TEXT_PRIMARY);
        JLabel il = new JLabel(info);
        il.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        il.setForeground(TEXT_MUTED);
        info2.add(tl, BorderLayout.NORTH);
        info2.add(il, BorderLayout.SOUTH);

        JLabel amt = new JLabel(amount);
        amt.setFont(new Font("Segoe UI", Font.BOLD, 13));
        amt.setForeground(iconFg);

        row.add(icon, BorderLayout.WEST);
        row.add(info2, BorderLayout.CENTER);
        row.add(amt, BorderLayout.EAST);
        return row;
    }

    // ── Active employees ─────────────────────────────────────────────────────

    private JPanel buildEmployeesCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(BG_CARD);
        card.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(18, 18, 18, 18)));

        JPanel hdr = new JPanel(new BorderLayout());
        hdr.setBackground(BG_CARD);
        hdr.setBorder(new EmptyBorder(0, 0, 14, 0));
        JLabel t = new JLabel("◉  Empleados activos");
        t.setFont(new Font("Segoe UI", Font.BOLD, 14));
        t.setForeground(TEXT_PRIMARY);

        JLabel link = new JLabel("Gestionar →");
        link.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        link.setForeground(ACCENT);
        link.setCursor(new Cursor(Cursor.HAND_CURSOR));
        link.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (onGestionar != null) onGestionar.run();
            }
            @Override public void mouseEntered(MouseEvent e) { link.setForeground(Color.WHITE); }
            @Override public void mouseExited(MouseEvent e)  { link.setForeground(ACCENT); }
        });

        hdr.add(t, BorderLayout.WEST);
        hdr.add(link, BorderLayout.EAST);
        card.add(hdr, BorderLayout.NORTH);

        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setBackground(BG_CARD);

        if (empresa != null && !empresa.getEmpleados().isEmpty()) {
            List<Empleado> emps = empresa.getEmpleados();
            int limit = Math.min(4, emps.size());
            for (int i = 0; i < limit; i++) {
                Empleado e = emps.get(i);
                boolean esFijo = e instanceof EmpleadoFijo;
                list.add(buildEmployeeRow(e.getNombre(), e.getCargo(),
                        esFijo ? "Fijo" : "Por horas", fmt(e.calcularSalario()), esFijo));
                if (i < limit - 1) list.add(makeRowSep());
            }
        } else {
            list.add(emptyLabel("Sin empleados registrados"));
        }
        card.add(list, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildEmployeeRow(String name, String role,
                                    String type, String salary, boolean fijo) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setBackground(BG_CARD);
        row.setBorder(new EmptyBorder(9, 0, 9, 0));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));

        // Avatar circle
        String initials = getInitials(name);
        JLabel avatar = new JLabel(initials, SwingConstants.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(35, 55, 95));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        avatar.setFont(new Font("Segoe UI", Font.BOLD, 10));
        avatar.setForeground(ACCENT);
        avatar.setOpaque(false);
        avatar.setPreferredSize(new Dimension(32, 32));

        // Name + type pill
        JPanel center = new JPanel(new BorderLayout(6, 0));
        center.setBackground(BG_CARD);

        JPanel nameRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        nameRow.setBackground(BG_CARD);
        JLabel nameLbl = new JLabel(name);
        nameLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        nameLbl.setForeground(TEXT_PRIMARY);

        JLabel badge = makePill(type);
        badge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        badge.setBackground(fijo ? new Color(25, 45, 85) : new Color(65, 50, 15));
        badge.setForeground(fijo ? ACCENT : AMBER);
        badge.setBorder(new EmptyBorder(2, 7, 2, 7));

        nameRow.add(nameLbl);
        nameRow.add(badge);

        JLabel roleLbl = new JLabel(role);
        roleLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        roleLbl.setForeground(TEXT_MUTED);

        center.add(nameRow, BorderLayout.NORTH);
        center.add(roleLbl, BorderLayout.SOUTH);

        JLabel sal = new JLabel(salary);
        sal.setFont(new Font("Segoe UI", Font.BOLD, 13));
        sal.setForeground(TEXT_PRIMARY);

        row.add(avatar, BorderLayout.WEST);
        row.add(center, BorderLayout.CENTER);
        row.add(sal, BorderLayout.EAST);
        return row;
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private JLabel makePill(String text) {
        JLabel lbl = new JLabel(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lbl.setOpaque(false);
        lbl.setBackground(new Color(40, 55, 90));
        lbl.setForeground(ACCENT);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(5, 11, 5, 11)));
        return lbl;
    }

    private JSeparator makeRowSep() {
        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER_COLOR);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return sep;
    }

    private JLabel emptyLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(TEXT_MUTED);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        return lbl;
    }

    private String getInitials(String name) {
        String[] parts = name.trim().split(" ");
        if (parts.length >= 2) return ("" + parts[0].charAt(0) + parts[1].charAt(0)).toUpperCase();
        return name.substring(0, Math.min(2, name.length())).toUpperCase();
    }

    private String fmt(double v) { return String.format("$%,.0f", v); }

    public void setEmpresa(Empresa e) { this.empresa = e; }
}
