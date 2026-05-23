package UI;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import Modelo.*;

public class GastosPanel extends JPanel {

    private Empresa empresa;
    private DefaultTableModel tableModel;
    private JTable tabla;

    private static final Color BG_MAIN      = new Color(28, 28, 32);
    private static final Color BG_CARD      = new Color(38, 38, 44);
    private static final Color BG_ROW_ALT   = new Color(44, 44, 52);
    private static final Color BORDER_COLOR = new Color(60, 60, 70);
    private static final Color TEXT_PRIMARY = new Color(225, 225, 230);
    private static final Color TEXT_MUTED   = new Color(130, 130, 145);
    private static final Color COLOR_GREEN  = new Color(76, 175, 80);
    private static final Color COLOR_RED    = new Color(229, 83, 75);
    private static final Color ACCENT       = new Color(100, 150, 255);

    public GastosPanel() { this(null); }

    public GastosPanel(Empresa empresa) {
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
        north.add(buildMetricsRow());
        north.add(Box.createVerticalStrut(18));
        add(north, BorderLayout.NORTH);

        String[] cols = {"ID", "Descripción", "Categoría", "Fecha", "Monto", "Acciones"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return col == 5; }
        };
        tabla = new JTable(tableModel);
        styleTable();
        loadTableData();
        tabla.getColumn("Acciones").setCellRenderer(new DeleteButtonRenderer());
        tabla.getColumn("Acciones").setCellEditor(new DeleteButtonEditor(new JCheckBox(), this));

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBackground(BG_CARD);
        scroll.getViewport().setBackground(BG_CARD);
        scroll.setBorder(new LineBorder(BORDER_COLOR, 1, true));
        add(scroll, BorderLayout.CENTER);

        add(buildFooter(), BorderLayout.SOUTH);

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
        JLabel title = new JLabel("↓  Gastos");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(TEXT_PRIMARY);
        JLabel subtitle = new JLabel("Registro y análisis de gastos");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(TEXT_MUTED);
        titles.add(title, BorderLayout.NORTH);
        titles.add(subtitle, BorderLayout.SOUTH);

        JButton btnAdd = createButton("+ Registrar Gasto", COLOR_RED);
        btnAdd.addActionListener(e -> mostrarFormularioAgregar());

        header.add(titles, BorderLayout.WEST);
        header.add(btnAdd, BorderLayout.EAST);
        return header;
    }

    // ── Metric cards ────────────────────────────────────────────────────────────

    private JPanel buildMetricsRow() {
        List<Transaccion> gastos = getGastos();
        double total  = gastos.stream().mapToDouble(Transaccion::getMonto).sum();
        int    count  = gastos.size();
        double avg    = count > 0 ? total / count : 0;
        String topCat = gastos.stream()
                .collect(Collectors.groupingBy(Transaccion::getCategoria, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("—");

        JPanel row = new JPanel(new GridLayout(1, 4, 14, 0));
        row.setBackground(BG_MAIN);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        row.add(metricCard("↓", "Total gastos",         fmt(total),            COLOR_RED));
        row.add(metricCard("≡", "Nº de registros",      String.valueOf(count), ACCENT));
        row.add(metricCard("≈", "Promedio por gasto",   fmt(avg),              TEXT_MUTED));
        row.add(metricCard("★", "Categoría principal",  topCat,                ACCENT));
        return row;
    }

    private JPanel metricCard(String icon, String title, String value, Color dotColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(BG_CARD);
        card.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(16, 18, 16, 18)));

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
        val.setFont(new Font("Segoe UI", Font.BOLD, 20));
        val.setForeground(TEXT_PRIMARY);
        val.setBorder(new EmptyBorder(6, 0, 0, 0));

        card.add(top, BorderLayout.NORTH);
        card.add(val, BorderLayout.CENTER);
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

        tabla.getColumnModel().getColumn(0).setPreferredWidth(45);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(240);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(130);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(110);
        tabla.getColumnModel().getColumn(4).setPreferredWidth(130);
        tabla.getColumnModel().getColumn(5).setPreferredWidth(100);

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
                if (col == 4) {
                    label.setForeground(COLOR_RED);
                    label.setHorizontalAlignment(SwingConstants.RIGHT);
                }
                return c;
            }
        });
    }

    private void loadTableData() {
        tableModel.setRowCount(0);
        if (empresa == null) return;
        for (Transaccion t : empresa.getTransacciones()) {
            if (t instanceof Gasto) {
                tableModel.addRow(new Object[]{
                        t.getId(), t.getDescripcion(), t.getCategoria(),
                        t.getFecha().toString(), fmt(t.getMonto()), "Eliminar"
                });
            }
        }
    }

    // ── Footer ───────────────────────────────────────────────────────────────────

    private JPanel buildFooter() {
        List<Transaccion> gastos = getGastos();
        double total = gastos.stream().mapToDouble(Transaccion::getMonto).sum();

        JLabel stats = new JLabel("Total registros: " + gastos.size()
                + "   |   Suma total: " + fmt(total));
        stats.setFont(new Font("Segoe UI", Font.BOLD, 13));
        stats.setForeground(TEXT_PRIMARY);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(BG_CARD);
        footer.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(12, 15, 12, 15)));
        footer.add(stats, BorderLayout.WEST);
        return footer;
    }

    // ── Add form ─────────────────────────────────────────────────────────────────

    public void mostrarFormularioAgregar() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Registrar Gasto", true);
        dialog.setSize(460, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        dialog.getContentPane().setBackground(BG_CARD);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG_CARD);
        panel.setBorder(new EmptyBorder(25, 30, 25, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets  = new Insets(4, 0, 4, 0);
        int r = 0;

        JLabel dlgTitle = new JLabel("Nuevo Gasto");
        dlgTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        dlgTitle.setForeground(TEXT_PRIMARY);
        gbc.gridx = 0; gbc.gridy = r++; gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 14, 0);
        panel.add(dlgTitle, gbc);
        gbc.gridwidth = 1;
        gbc.insets = new Insets(4, 0, 2, 0);

        panel.add(mkLabel("Descripción"),        gbcRow(gbc, 0, r++, 2));
        JTextField tfDesc = mkField();
        panel.add(tfDesc,                        gbcRow(gbc, 0, r++, 2));

        panel.add(mkLabel("Categoría"),          gbcRow(gbc, 0, r++, 2));
        JComboBox<String> cbCat = new JComboBox<>(new String[]{
                "Arriendo", "Servicios", "Equipos", "Suministros",
                "Nómina", "Impuestos", "Marketing", "Otros"});
        cbCat.setBackground(new Color(55, 55, 65));
        cbCat.setForeground(TEXT_PRIMARY);
        cbCat.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(cbCat,                         gbcRow(gbc, 0, r++, 2));

        panel.add(mkLabel("Monto ($)"),          gbcRow(gbc, 0, r++, 2));
        JTextField tfMonto = mkField();
        panel.add(tfMonto,                       gbcRow(gbc, 0, r++, 2));

        panel.add(mkLabel("Fecha (yyyy-MM-dd)"), gbcRow(gbc, 0, r++, 2));
        JTextField tfFecha = mkField();
        tfFecha.setText(LocalDate.now().toString());
        panel.add(tfFecha,                       gbcRow(gbc, 0, r++, 2));

        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER_COLOR);
        gbc.insets = new Insets(12, 0, 12, 0);
        panel.add(sep,                           gbcRow(gbc, 0, r++, 2));
        gbc.insets = new Insets(4, 0, 4, 0);

        JButton btnCancel = createButton("Cancelar", new Color(70, 70, 85));
        JButton btnSave   = createButton("Guardar",  COLOR_RED);
        btnCancel.addActionListener(e -> dialog.dispose());
        btnSave.addActionListener(e -> {
            try {
                String desc     = tfDesc.getText().trim();
                String cat      = cbCat.getSelectedItem().toString();
                String raw      = tfMonto.getText().trim().replace(",", "").replace(".", "");
                String fechaStr = tfFecha.getText().trim();

                if (desc.isEmpty()) { showErr(dialog, "La descripción no puede estar vacía."); return; }
                if (raw.isEmpty())  { showErr(dialog, "Ingresa el monto.");                   return; }

                double    monto = Double.parseDouble(raw);
                LocalDate fecha = LocalDate.parse(fechaStr);

                int nextId = (empresa == null || empresa.getTransacciones().isEmpty()) ? 1
                        : empresa.getTransacciones().stream()
                                .mapToInt(Transaccion::getId).max().getAsInt() + 1;

                if (empresa == null) empresa = new Empresa("Empresa", "000");
                empresa.agregarTransaccion(new Gasto(nextId, desc, monto, fecha, cat));
                saveCSV();
                build();
                dialog.dispose();
                JOptionPane.showMessageDialog(GastosPanel.this,
                        "Gasto registrado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);

            } catch (NumberFormatException ex) {
                showErr(dialog, "El monto debe ser un número entero válido (ej: 350000).");
            } catch (DateTimeParseException ex) {
                showErr(dialog, "Fecha inválida. Usa el formato yyyy-MM-dd.");
            } catch (IllegalArgumentException ex) {
                showErr(dialog, ex.getMessage());
            }
        });

        gbc.gridwidth = 1; gbc.weightx = 0.5;
        gbc.gridx = 0; gbc.gridy = r;
        panel.add(btnCancel, gbc);
        gbc.gridx = 1;
        panel.add(btnSave, gbc);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    // ── Delete ───────────────────────────────────────────────────────────────────

    public void eliminarGasto(int fila) {
        if (empresa == null || fila < 0) return;
        String desc = tableModel.getValueAt(fila, 1).toString();
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Eliminar el gasto \"" + desc + "\"?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            int id = Integer.parseInt(tableModel.getValueAt(fila, 0).toString());
            empresa.eliminarTransaccion(id);
            saveCSV();
            build();
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────────

    private List<Transaccion> getGastos() {
        if (empresa == null) return Collections.emptyList();
        return empresa.getTransacciones().stream()
                .filter(t -> t instanceof Gasto)
                .collect(Collectors.toList());
    }

    private void saveCSV() {
        try {
            GestorArchivos.guardarTransacciones(empresa.getTransacciones());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al guardar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String fmt(double v) { return String.format("$%,.0f", v); }

    private void showErr(JDialog d, String msg) {
        JOptionPane.showMessageDialog(d, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private GridBagConstraints gbcRow(GridBagConstraints g, int x, int y, int w) {
        g.gridx = x; g.gridy = y; g.gridwidth = w; g.weightx = 1.0; return g;
    }

    private JTextField mkField() {
        JTextField tf = new JTextField();
        tf.setBackground(new Color(55, 55, 65));
        tf.setForeground(TEXT_PRIMARY);
        tf.setCaretColor(TEXT_PRIMARY);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true), new EmptyBorder(8, 10, 8, 10)));
        tf.setPreferredSize(new Dimension(0, 38));
        return tf;
    }

    private JLabel mkLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(TEXT_MUTED);
        return lbl;
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        return btn;
    }

    public void setEmpresa(Empresa e) { this.empresa = e; build(); }

    // ── Inner classes: table button renderer/editor ───────────────────────────────

    static class DeleteButtonRenderer extends JButton implements TableCellRenderer {
        public DeleteButtonRenderer() {
            setOpaque(true);
            setText("Eliminar");
            setFont(new Font("Segoe UI", Font.BOLD, 11));
            setForeground(Color.WHITE);
            setBackground(new Color(229, 83, 75));
            setBorder(new EmptyBorder(5, 12, 5, 12));
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) { return this; }
    }

    static class DeleteButtonEditor extends DefaultCellEditor {
        private final JButton button;
        private final GastosPanel panel;
        private int currentRow;

        public DeleteButtonEditor(JCheckBox checkBox, GastosPanel panel) {
            super(checkBox);
            this.panel = panel;
            button = new JButton("Eliminar");
            button.setFont(new Font("Segoe UI", Font.BOLD, 11));
            button.setForeground(Color.WHITE);
            button.setBackground(new Color(229, 83, 75));
            button.setBorder(new EmptyBorder(5, 12, 5, 12));
            button.setOpaque(true);
            button.addActionListener(e -> {
                fireEditingStopped();
                panel.eliminarGasto(currentRow);
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            currentRow = row;
            return button;
        }

        @Override
        public Object getCellEditorValue() { return "Eliminar"; }
    }
}
