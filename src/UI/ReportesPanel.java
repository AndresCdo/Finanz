package UI;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import Modelo.*;

public class ReportesPanel extends JPanel {

    private Empresa empresa;
    private DefaultTableModel tableModel;
    private JTable tabla;
    private JLabel footerLabel;

    private static final Color BG_MAIN      = new Color(28, 28, 32);
    private static final Color BG_CARD      = new Color(38, 38, 44);
    private static final Color BG_CARD2     = new Color(44, 44, 52);
    private static final Color BG_ROW_ALT   = new Color(44, 44, 52);
    private static final Color BORDER_COLOR = new Color(60, 60, 70);
    private static final Color TEXT_PRIMARY = new Color(225, 225, 230);
    private static final Color TEXT_MUTED   = new Color(130, 130, 145);
    private static final Color COLOR_GREEN  = new Color(76, 175, 80);
    private static final Color COLOR_RED    = new Color(229, 83, 75);
    private static final Color ACCENT       = new Color(100, 150, 255);

    public ReportesPanel() { this(null); }

    public ReportesPanel(Empresa empresa) {
        this.empresa = empresa;
        setLayout(new BorderLayout());
        setBackground(BG_MAIN);
        setBorder(new EmptyBorder(25, 25, 25, 25));
        build();
    }

    // ── Build ───────────────────────────────────────────────────────────────────

    private void build() {
        removeAll();

        JPanel north = new JPanel();
        north.setLayout(new BoxLayout(north, BoxLayout.Y_AXIS));
        north.setBackground(BG_MAIN);
        north.add(buildHeader());
        north.add(Box.createVerticalStrut(18));
        north.add(buildBalanceCards());
        north.add(Box.createVerticalStrut(14));
        north.add(buildAlertBar());
        north.add(Box.createVerticalStrut(14));
        north.add(buildFilterPanel());
        north.add(Box.createVerticalStrut(14));
        add(north, BorderLayout.NORTH);

        String[] cols = {"ID", "Tipo", "Descripción", "Categoría", "Fecha", "Monto"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tabla = new JTable(tableModel);
        styleTable();
        loadTable(empresa != null ? empresa.getTransacciones()
                : java.util.Collections.emptyList());

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBackground(BG_CARD);
        scroll.getViewport().setBackground(BG_CARD);
        scroll.setBorder(new LineBorder(BORDER_COLOR, 1, true));
        add(scroll, BorderLayout.CENTER);

        footerLabel = new JLabel();
        updateFooter(empresa != null ? empresa.getTransacciones()
                : java.util.Collections.emptyList());
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(BG_CARD);
        footer.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(12, 15, 12, 15)));
        footerLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        footerLabel.setForeground(TEXT_PRIMARY);
        footer.add(footerLabel, BorderLayout.WEST);
        add(footer, BorderLayout.SOUTH);

        revalidate();
        repaint();
    }

    // ── Header ──────────────────────────────────────────────────────────────────

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_MAIN);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));

        JPanel titles = new JPanel(new BorderLayout());
        titles.setBackground(BG_MAIN);
        JLabel title = new JLabel("◈  Reportes");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(TEXT_PRIMARY);
        JLabel subtitle = new JLabel("Análisis y reportes financieros");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(TEXT_MUTED);
        titles.add(title, BorderLayout.NORTH);
        titles.add(subtitle, BorderLayout.SOUTH);

        header.add(titles, BorderLayout.WEST);
        return header;
    }

    // ── Balance cards ────────────────────────────────────────────────────────────

    private JPanel buildBalanceCards() {
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
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        row.add(metricCard("↑", "Total ingresos", fmt(ingresos), COLOR_GREEN));
        row.add(metricCard("↓", "Total gastos",   fmt(gastos),   COLOR_RED));
        row.add(metricCard("≈", "Balance neto",   fmt(balance),  balance >= 0 ? COLOR_GREEN : COLOR_RED));
        row.add(metricCard("◉", "Costo nómina",   fmt(nomina),   TEXT_MUTED));
        return row;
    }

    private JPanel metricCard(String icon, String title, String value, Color dotColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(BG_CARD);
        card.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(14, 18, 14, 18)));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        top.setBackground(BG_CARD);
        JLabel dot = new JLabel(icon);
        dot.setForeground(dotColor);
        dot.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(TEXT_MUTED);
        top.add(dot);
        top.add(lbl);

        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.BOLD, 18));
        val.setForeground(TEXT_PRIMARY);
        val.setBorder(new EmptyBorder(4, 0, 0, 0));

        card.add(top, BorderLayout.NORTH);
        card.add(val, BorderLayout.CENTER);
        return card;
    }

    // ── Alert bar ────────────────────────────────────────────────────────────────

    private JPanel buildAlertBar() {
        double balance = empresa != null ? empresa.calcularBalance() : 0;
        boolean superavit = balance >= 0;

        Color bg     = superavit ? new Color(76, 175, 80, 18) : new Color(229, 83, 75, 18);
        Color border = superavit ? new Color(76, 175, 80, 70) : new Color(229, 83, 75, 70);
        Color badge  = superavit ? COLOR_GREEN : COLOR_RED;
        String texto = superavit
                ? "La empresa está en superávit. Los ingresos superan los gastos en " + fmt(balance) + "."
                : "La empresa está en déficit. Los gastos superan los ingresos en " + fmt(Math.abs(balance)) + ".";

        JPanel alert = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        alert.setBackground(bg);
        alert.setBorder(new CompoundBorder(
                new LineBorder(border, 1, true),
                new EmptyBorder(12, 14, 12, 14)));
        alert.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));

        JLabel badgeLbl = new JLabel(superavit ? "  Superávit  " : "  Déficit  ");
        badgeLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        badgeLbl.setForeground(Color.WHITE);
        badgeLbl.setBackground(badge);
        badgeLbl.setOpaque(true);
        badgeLbl.setBorder(new EmptyBorder(3, 8, 3, 8));

        JLabel msg = new JLabel(texto);
        msg.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        msg.setForeground(new Color(190, 210, 190));

        alert.add(badgeLbl);
        alert.add(msg);
        return alert;
    }

    // ── Filter panel ─────────────────────────────────────────────────────────────

    private JPanel buildFilterPanel() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(BG_CARD2);
        card.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(14, 16, 14, 16)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 6, 4, 6);
        g.anchor = GridBagConstraints.WEST;
        g.fill   = GridBagConstraints.HORIZONTAL;

        g.gridy = 0;
        g.gridx = 0; g.weightx = 0;
        card.add(filterLabel("Desde:"), g);

        JTextField tfDesde = filterField(12);
        tfDesde.setText(LocalDate.now().withDayOfMonth(1).toString());
        g.gridx = 1; g.weightx = 0.3;
        card.add(tfDesde, g);

        g.gridx = 2; g.weightx = 0;
        card.add(filterLabel("Hasta:"), g);

        JTextField tfHasta = filterField(12);
        tfHasta.setText(LocalDate.now().toString());
        g.gridx = 3; g.weightx = 0.3;
        card.add(tfHasta, g);

        JButton btnFecha = filterButton("Filtrar por fechas", ACCENT);
        g.gridx = 4; g.weightx = 0;
        card.add(btnFecha, g);

        g.gridy = 1;
        g.gridx = 0; g.weightx = 0;
        card.add(filterLabel("Categoría:"), g);

        JTextField tfCat = filterField(20);
        g.gridx = 1; g.weightx = 0.6; g.gridwidth = 3;
        card.add(tfCat, g);
        g.gridwidth = 1;

        JButton btnCat   = filterButton("Filtrar por categoría", ACCENT);
        JButton btnReset = filterButton("Ver todos", new Color(70, 70, 85));
        g.gridx = 4; g.weightx = 0;
        card.add(btnCat, g);
        g.gridx = 5;
        card.add(btnReset, g);

        btnFecha.addActionListener(e -> {
            try {
                LocalDate desde = LocalDate.parse(tfDesde.getText().trim());
                LocalDate hasta = LocalDate.parse(tfHasta.getText().trim());
                List<Transaccion> result = empresa != null
                        ? empresa.filtrarTransacciones(desde, hasta)
                        : java.util.Collections.emptyList();
                loadTable(result);
                updateFooter(result);
            } catch (DateTimeParseException ex) {
                showErr("Fecha inválida. Usa el formato yyyy-MM-dd.");
            } catch (IllegalArgumentException ex) {
                showErr(ex.getMessage());
            }
        });

        btnCat.addActionListener(e -> {
            String cat = tfCat.getText().trim();
            if (cat.isEmpty()) { showErr("Escribe una categoría para filtrar."); return; }
            try {
                List<Transaccion> result = empresa != null
                        ? empresa.filtrarTransacciones(cat)
                        : java.util.Collections.emptyList();
                loadTable(result);
                updateFooter(result);
            } catch (IllegalArgumentException ex) {
                showErr(ex.getMessage());
            }
        });

        btnReset.addActionListener(e -> {
            List<Transaccion> all = empresa != null
                    ? empresa.getTransacciones() : java.util.Collections.emptyList();
            loadTable(all);
            updateFooter(all);
        });

        return card;
    }

    // ── Table ────────────────────────────────────────────────────────────────────

    private void styleTable() {
        tabla.setBackground(BG_CARD);
        tabla.setForeground(TEXT_PRIMARY);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.setRowHeight(45);
        tabla.setShowGrid(false);
        tabla.setIntercellSpacing(new Dimension(0, 0));
        tabla.setSelectionBackground(new Color(80, 80, 100));
        tabla.setSelectionForeground(Color.WHITE);
        tabla.setFillsViewportHeight(true);

        JTableHeader th = tabla.getTableHeader();
        th.setBackground(new Color(35, 35, 45));
        th.setForeground(TEXT_MUTED);
        th.setFont(new Font("Segoe UI", Font.BOLD, 12));
        th.setReorderingAllowed(false);

        tabla.getColumnModel().getColumn(0).setPreferredWidth(40);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(90);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(230);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(130);
        tabla.getColumnModel().getColumn(4).setPreferredWidth(110);
        tabla.getColumnModel().getColumn(5).setPreferredWidth(130);

        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(
                        t, value, isSelected, hasFocus, row, col);
                JLabel label = (JLabel) c;
                label.setBackground(isSelected ? new Color(80, 80, 100)
                        : (row % 2 == 0 ? BG_CARD : BG_ROW_ALT));
                label.setForeground(TEXT_PRIMARY);
                label.setBorder(new EmptyBorder(0, 10, 0, 10));
                label.setFont(new Font("Segoe UI", Font.PLAIN, 13));

                if (col == 1 && value != null) {
                    boolean esIngreso = value.toString().equals("Ingreso");
                    label.setForeground(esIngreso ? COLOR_GREEN : COLOR_RED);
                    label.setFont(new Font("Segoe UI", Font.BOLD, 12));
                }
                if (col == 5 && value != null) {
                    Object tipo = t.getValueAt(row, 1);
                    boolean esIngreso = tipo != null && tipo.toString().equals("Ingreso");
                    label.setForeground(esIngreso ? COLOR_GREEN : COLOR_RED);
                    label.setHorizontalAlignment(SwingConstants.RIGHT);
                }
                return c;
            }
        });
    }

    private void loadTable(List<Transaccion> lista) {
        tableModel.setRowCount(0);
        for (Transaccion t : lista) {
            String tipo = (t instanceof Ingreso) ? "Ingreso" : "Gasto";
            tableModel.addRow(new Object[]{
                    t.getId(), tipo, t.getDescripcion(),
                    t.getCategoria(), t.getFecha().toString(), fmt(t.getMonto())
            });
        }
    }

    private void updateFooter(List<Transaccion> lista) {
        if (footerLabel == null) return;
        long ingresosCnt = lista.stream().filter(t -> t instanceof Ingreso).count();
        long  gastosCnt  = lista.stream().filter(t -> t instanceof Gasto).count();
        double sumIng    = lista.stream().filter(t -> t instanceof Ingreso).mapToDouble(Transaccion::getMonto).sum();
        double sumGas    = lista.stream().filter(t -> t instanceof Gasto).mapToDouble(Transaccion::getMonto).sum();
        footerLabel.setText(String.format(
                "Resultados: %d   |   Ingresos: %d (%s)   |   Gastos: %d (%s)   |   Neto: %s",
                lista.size(), ingresosCnt, fmt(sumIng), gastosCnt, fmt(sumGas), fmt(sumIng - sumGas)));
    }

    // ── Helpers ──────────────────────────────────────────────────────────────────

    private JLabel filterLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(TEXT_MUTED);
        return lbl;
    }

    private JTextField filterField(int cols) {
        JTextField tf = new JTextField(cols);
        tf.setBackground(new Color(55, 55, 65));
        tf.setForeground(TEXT_PRIMARY);
        tf.setCaretColor(TEXT_PRIMARY);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true), new EmptyBorder(6, 8, 6, 8)));
        return tf;
    }

    private JButton filterButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(7, 14, 7, 14));
        return btn;
    }

    private String fmt(double v) { return String.format("$%,.0f", v); }

    private void showErr(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void setEmpresa(Empresa e) { this.empresa = e; build(); }
}
