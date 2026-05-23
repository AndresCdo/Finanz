package UI;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import java.awt.*;
import java.io.IOException;
import Modelo.*;

public class EmpleadosPanel extends JPanel {

    private Empresa empresa;
    private DefaultTableModel tableModel;
    private JTable tabla;
    private JLabel totalLabel;
    private TableRowSorter<DefaultTableModel> sorter;

    private static final Color BG_MAIN      = new Color(28, 28, 32);
    private static final Color BG_CARD      = new Color(38, 38, 44);
    private static final Color BG_CARD2     = new Color(44, 44, 52);
    private static final Color BORDER_COLOR = new Color(60, 60, 70);
    private static final Color TEXT_PRIMARY = new Color(225, 225, 230);
    private static final Color TEXT_MUTED   = new Color(130, 130, 145);
    private static final Color COLOR_GREEN  = new Color(76, 175, 80);
    private static final Color ACCENT       = new Color(100, 150, 255);
    private static final Color AMBER        = new Color(255, 183, 77);
    private static final Color COLOR_RED    = new Color(229, 83, 75);

    public EmpleadosPanel() { this(null); }

    public EmpleadosPanel(Empresa empresa) {
        this.empresa = empresa;
        setLayout(new BorderLayout());
        setBackground(BG_MAIN);
        setBorder(new EmptyBorder(25, 25, 25, 25));
        build();
    }

    private void build() {
        removeAll();

        // ── NORTH: header + search bar ─────────────────────────────────────
        JPanel north = new JPanel();
        north.setLayout(new BoxLayout(north, BoxLayout.Y_AXIS));
        north.setBackground(BG_MAIN);
        north.add(buildHeader());
        north.add(Box.createVerticalStrut(14));
        north.add(buildSearchBar());
        north.add(Box.createVerticalStrut(14));
        add(north, BorderLayout.NORTH);

        // ── CENTER: table ─────────────────────────────────────────────────
        String[] cols = {"ID", "Empleado", "Documento", "Cargo", "Tipo", "Salario", "Acciones"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return col == 6; }
        };

        tabla = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        tabla.setRowSorter(sorter);
        styleTable();
        cargarDatosTabla();
        tabla.getColumn("Acciones").setCellRenderer(new ButtonRenderer());
        tabla.getColumn("Acciones").setCellEditor(new ButtonEditor(new JCheckBox(), this));

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBackground(BG_CARD);
        scroll.getViewport().setBackground(BG_CARD);
        scroll.setBorder(new LineBorder(BORDER_COLOR, 1, true));
        add(scroll, BorderLayout.CENTER);

        // ── SOUTH: footer ─────────────────────────────────────────────────
        add(buildFooter(), BorderLayout.SOUTH);

        revalidate();
        repaint();
    }

    // ── Header ─────────────────────────────────────────────────────────────────

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_MAIN);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));

        JPanel titles = new JPanel(new BorderLayout());
        titles.setBackground(BG_MAIN);
        JLabel title = new JLabel("◉  Empleados");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(TEXT_PRIMARY);
        JLabel subtitle = new JLabel("Gestión de recursos humanos");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(TEXT_MUTED);
        titles.add(title, BorderLayout.NORTH);
        titles.add(subtitle, BorderLayout.SOUTH);

        JButton btnAdd = createPrimaryButton("+ Nuevo empleado");
        btnAdd.addActionListener(e -> mostrarFormularioAgregar());

        header.add(titles, BorderLayout.WEST);
        header.add(btnAdd, BorderLayout.EAST);
        return header;
    }

    // ── Search bar ─────────────────────────────────────────────────────────────

    private JPanel buildSearchBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        bar.setBackground(BG_MAIN);
        bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JTextField tfSearch = new JTextField(22);
        tfSearch.putClientProperty("JTextField.placeholderText", "Buscar por nombre o documento...");
        tfSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tfSearch.setBackground(new Color(55, 55, 65));
        tfSearch.setForeground(TEXT_PRIMARY);
        tfSearch.setCaretColor(TEXT_PRIMARY);
        tfSearch.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true), new EmptyBorder(6, 10, 6, 10)));

        String[] tipos = {"Todos los tipos", "Fijo", "Por horas"};
        JComboBox<String> cbTipo = new JComboBox<>(tipos);
        cbTipo.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        tfSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e)  { applyFilter(tfSearch.getText(), cbTipo.getSelectedIndex()); }
            @Override public void removeUpdate(DocumentEvent e)  { applyFilter(tfSearch.getText(), cbTipo.getSelectedIndex()); }
            @Override public void changedUpdate(DocumentEvent e) {}
        });

        cbTipo.addActionListener(e -> applyFilter(tfSearch.getText(), cbTipo.getSelectedIndex()));

        bar.add(tfSearch);
        bar.add(cbTipo);
        return bar;
    }

    private void applyFilter(String text, int tipoIdx) {
        RowFilter<DefaultTableModel, Object> textFilter = text.isBlank() ? null
                : RowFilter.regexFilter("(?i)" + text, 1, 2);

        RowFilter<DefaultTableModel, Object> tipoFilter = null;
        if (tipoIdx == 1) tipoFilter = RowFilter.regexFilter("Fijo", 4);
        else if (tipoIdx == 2) tipoFilter = RowFilter.regexFilter("Por horas", 4);

        if (textFilter != null && tipoFilter != null) {
            sorter.setRowFilter(RowFilter.andFilter(java.util.Arrays.asList(textFilter, tipoFilter)));
        } else if (textFilter != null) {
            sorter.setRowFilter(textFilter);
        } else {
            sorter.setRowFilter(tipoFilter);
        }
    }

    // ── Table styling ───────────────────────────────────────────────────────────

    private void styleTable() {
        tabla.setBackground(BG_CARD);
        tabla.setForeground(TEXT_PRIMARY);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.setRowHeight(38);
        tabla.setShowGrid(false);
        tabla.setIntercellSpacing(new Dimension(0, 0));
        tabla.setSelectionBackground(new Color(55, 75, 130));
        tabla.setSelectionForeground(TEXT_PRIMARY);
        tabla.setFillsViewportHeight(true);

        JTableHeader th = tabla.getTableHeader();
        th.setBackground(new Color(35, 35, 45));
        th.setForeground(TEXT_MUTED);
        th.setFont(new Font("Segoe UI", Font.BOLD, 11));
        th.setReorderingAllowed(false);
        th.setBorder(new MatteBorder(0, 0, 1, 0, BORDER_COLOR));

        // Hide ID column
        tabla.getColumnModel().getColumn(0).setMinWidth(0);
        tabla.getColumnModel().getColumn(0).setMaxWidth(0);
        tabla.getColumnModel().getColumn(0).setWidth(0);

        tabla.getColumnModel().getColumn(1).setPreferredWidth(200);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(120);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(140);
        tabla.getColumnModel().getColumn(4).setPreferredWidth(110);
        tabla.getColumnModel().getColumn(5).setPreferredWidth(130);
        tabla.getColumnModel().getColumn(6).setPreferredWidth(100);

        tabla.getColumnModel().getColumn(1).setCellRenderer(new AvatarNameRenderer());
        tabla.getColumnModel().getColumn(4).setCellRenderer(new TipoPillRenderer());
        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                JLabel label = (JLabel) c;
                label.setBackground(isSelected ? new Color(55, 75, 130) : BG_CARD);
                label.setForeground(TEXT_PRIMARY);
                label.setBorder(new EmptyBorder(0, 10, 0, 10));
                label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                if (col == 5) {
                    label.setForeground(COLOR_GREEN);
                    label.setHorizontalAlignment(SwingConstants.RIGHT);
                }
                return c;
            }
        });
    }

    public void cargarDatosTabla() {
        tableModel.setRowCount(0);
        if (empresa == null) return;
        for (Empleado e : empresa.getEmpleados()) {
            tableModel.addRow(new Object[]{
                    e.getId(), e.getNombre(), e.getDocumento(), e.getCargo(),
                    (e instanceof EmpleadoFijo) ? "Fijo" : "Por horas",
                    fmt(e.calcularSalario()), "Eliminar"
            });
        }
    }

    // ── Footer ─────────────────────────────────────────────────────────────────

    private JPanel buildFooter() {
        int total = empresa != null ? empresa.getEmpleados().size() : 0;
        double costo = empresa != null
                ? empresa.getEmpleados().stream().mapToDouble(Empleado::calcularSalario).sum() : 0;

        totalLabel = new JLabel("Empleados: " + total + "   |   Costo nómina: " + fmt(costo));
        totalLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        totalLabel.setForeground(TEXT_MUTED);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(BG_CARD);
        footer.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(11, 15, 11, 15)));
        footer.add(totalLabel, BorderLayout.WEST);
        return footer;
    }

    private void actualizarFooter() {
        if (empresa == null || totalLabel == null) return;
        int total = empresa.getEmpleados().size();
        double costo = empresa.getEmpleados().stream().mapToDouble(Empleado::calcularSalario).sum();
        totalLabel.setText("Empleados: " + total + "   |   Costo nómina: " + fmt(costo));
    }

    // ── Add form ───────────────────────────────────────────────────────────────

    public void mostrarFormularioAgregar() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Nuevo empleado", true);
        dialog.setSize(500, 480);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        dialog.getContentPane().setBackground(BG_CARD);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG_CARD);
        panel.setBorder(new EmptyBorder(28, 32, 28, 32));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets  = new Insets(4, 0, 4, 0);
        int r = 0;

        JLabel dlgTitle = new JLabel("Nuevo empleado");
        dlgTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        dlgTitle.setForeground(TEXT_PRIMARY);
        gbc.gridx = 0; gbc.gridy = r++; gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 18, 0);
        panel.add(dlgTitle, gbc);
        gbc.insets = new Insets(4, 0, 2, 0);

        JTextField tfNombre    = formField();
        JTextField tfDocumento = formField();
        gbc.gridwidth = 1; gbc.weightx = 0.5;
        gbc.gridx = 0; gbc.gridy = r;
        panel.add(formLabel("Nombre completo"), gbc);
        gbc.gridx = 1;
        panel.add(formLabel("Número de documento"), gbc);
        r++;
        gbc.gridx = 0; gbc.gridy = r;
        panel.add(tfNombre, gbc);
        gbc.gridx = 1;
        panel.add(tfDocumento, gbc);
        r++;
        gbc.gridwidth = 2; gbc.weightx = 1.0;

        panel.add(formLabel("Cargo"), gbcRow(gbc, 0, r++, 2));
        JTextField tfCargo = formField();
        panel.add(tfCargo, gbcRow(gbc, 0, r++, 2));

        panel.add(formLabel("Tipo de contrato"), gbcRow(gbc, 0, r++, 2));
        JButton btnFijo  = new JButton("Salario fijo");
        JButton btnHoras = new JButton("Por horas");
        styleToggle(btnFijo,  true);
        styleToggle(btnHoras, false);

        JPanel togglePanel = new JPanel(new GridLayout(1, 2, 0, 0));
        togglePanel.setBackground(BG_CARD);
        togglePanel.setBorder(new LineBorder(BORDER_COLOR, 1, true));
        togglePanel.add(btnFijo);
        togglePanel.add(btnHoras);
        gbc.insets = new Insets(4, 0, 10, 0);
        panel.add(togglePanel, gbcRow(gbc, 0, r++, 2));
        gbc.insets = new Insets(4, 0, 2, 0);

        JPanel panelFijo  = buildFijoPanel();
        JPanel panelHoras = buildHorasPanel();
        panelHoras.setVisible(false);
        panel.add(panelFijo,  gbcRow(gbc, 0, r,   2));
        panel.add(panelHoras, gbcRow(gbc, 0, r++, 2));

        btnFijo.addActionListener(e -> {
            styleToggle(btnFijo, true); styleToggle(btnHoras, false);
            panelFijo.setVisible(true); panelHoras.setVisible(false);
            dialog.revalidate();
        });
        btnHoras.addActionListener(e -> {
            styleToggle(btnFijo, false); styleToggle(btnHoras, true);
            panelFijo.setVisible(false); panelHoras.setVisible(true);
            dialog.revalidate();
        });

        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER_COLOR);
        gbc.insets = new Insets(14, 0, 14, 0);
        panel.add(sep, gbcRow(gbc, 0, r++, 2));
        gbc.insets = new Insets(4, 0, 4, 0);

        JButton btnCancel = createOutlinedButton("Cancelar");
        JButton btnSave   = createPrimaryButton("Guardar empleado");
        btnCancel.addActionListener(e -> dialog.dispose());
        btnSave.addActionListener(e -> {
            try {
                String nombre    = tfNombre.getText().trim();
                String documento = tfDocumento.getText().trim();
                String cargo     = tfCargo.getText().trim();
                if (nombre.isEmpty() || documento.isEmpty() || cargo.isEmpty()) {
                    showErr(dialog, "Completa todos los campos obligatorios.");
                    return;
                }
                boolean esFijo = panelFijo.isVisible();
                int nextId = empresa == null || empresa.getEmpleados().isEmpty() ? 1
                        : empresa.getEmpleados().stream().mapToInt(Empleado::getId).max().getAsInt() + 1;

                Empleado nuevo;
                if (esFijo) {
                    JTextField tfSalario = (JTextField) ((JPanel) panelFijo.getComponent(1)).getComponent(0);
                    String raw = tfSalario.getText().trim().replace(",", "").replace(".", "");
                    if (raw.isEmpty()) { showErr(dialog, "Ingresa el salario base."); return; }
                    nuevo = new EmpleadoFijo(nextId, nombre, documento, cargo, Double.parseDouble(raw));
                } else {
                    JPanel ph = (JPanel) panelHoras;
                    JTextField tfTarifa = (JTextField) ((JPanel) ph.getComponent(1)).getComponent(0);
                    JTextField tfHoras  = (JTextField) ((JPanel) ph.getComponent(3)).getComponent(0);
                    String rawT = tfTarifa.getText().trim().replace(",", "").replace(".", "");
                    String rawH = tfHoras.getText().trim().replace(",", "");
                    if (rawT.isEmpty() || rawH.isEmpty()) { showErr(dialog, "Ingresa tarifa y horas."); return; }
                    nuevo = new EmpleadoPorHoras(nextId, nombre, documento, cargo,
                            Double.parseDouble(rawT), Double.parseDouble(rawH));
                }

                if (empresa == null) empresa = new Empresa("Empresa", "000");
                empresa.agregarEmpleado(nuevo);
                guardarCSV();
                cargarDatosTabla();
                actualizarFooter();
                dialog.dispose();
                JOptionPane.showMessageDialog(EmpleadosPanel.this,
                        "Empleado agregado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);

            } catch (NumberFormatException ex) {
                showErr(dialog, "Los valores numéricos no son válidos.");
            } catch (IllegalArgumentException ex) {
                showErr(dialog, ex.getMessage());
            }
        });

        gbc.gridwidth = 1; gbc.weightx = 0.5;
        gbc.gridx = 0; gbc.gridy = r;
        panel.add(btnCancel, gbc);
        gbc.gridx = 1;
        panel.add(btnSave, gbc);

        JScrollPane sp = new JScrollPane(panel);
        sp.setBorder(null);
        dialog.setContentPane(sp);
        dialog.setVisible(true);
    }

    private JPanel buildFijoPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG_CARD2);
        p.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true), new EmptyBorder(12, 14, 12, 14)));
        p.add(formLabel("Salario mensual base ($)"), BorderLayout.NORTH);
        JPanel fp = new JPanel(new BorderLayout());
        fp.setBackground(BG_CARD2);
        fp.add(formField(), BorderLayout.CENTER);
        p.add(fp, BorderLayout.CENTER);
        return p;
    }

    private JPanel buildHorasPanel() {
        JPanel p = new JPanel(new GridLayout(4, 1, 0, 4));
        p.setBackground(BG_CARD2);
        p.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true), new EmptyBorder(12, 14, 12, 14)));
        JPanel tp = new JPanel(new BorderLayout()); tp.setBackground(BG_CARD2);
        tp.add(formField(), BorderLayout.CENTER);
        JPanel hp = new JPanel(new BorderLayout()); hp.setBackground(BG_CARD2);
        hp.add(formField(), BorderLayout.CENTER);
        p.add(formLabel("Tarifa por hora ($)"));
        p.add(tp);
        p.add(formLabel("Horas trabajadas"));
        p.add(hp);
        return p;
    }

    // ── Delete ─────────────────────────────────────────────────────────────────

    public void eliminarEmpleado(int fila) {
        if (empresa == null || fila < 0) return;
        int modelRow = tabla.convertRowIndexToModel(fila);
        String nombre = tableModel.getValueAt(modelRow, 1).toString();
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Eliminar a " + nombre + "?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            int id = Integer.parseInt(tableModel.getValueAt(modelRow, 0).toString());
            empresa.eliminarEmpleado(id);
            guardarCSV();
            cargarDatosTabla();
            actualizarFooter();
        }
    }

    private void guardarCSV() {
        try {
            GestorArchivos.guardarEmpleados(empresa.getEmpleados());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al guardar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private String fmt(double v) { return String.format("$%,.0f", v); }

    private void showErr(JDialog d, String msg) {
        JOptionPane.showMessageDialog(d, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private GridBagConstraints gbcRow(GridBagConstraints g, int x, int y, int w) {
        g.gridx = x; g.gridy = y; g.gridwidth = w; g.weightx = 1.0; return g;
    }

    private JTextField formField() {
        JTextField tf = new JTextField();
        tf.setBackground(new Color(55, 55, 65));
        tf.setForeground(TEXT_PRIMARY);
        tf.setCaretColor(TEXT_PRIMARY);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true), new EmptyBorder(7, 10, 7, 10)));
        tf.setPreferredSize(new Dimension(0, 36));
        return tf;
    }

    private JLabel formLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(TEXT_MUTED);
        return lbl;
    }

    private JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(ACCENT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(9, 18, 9, 18));
        return btn;
    }

    private JButton createOutlinedButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(TEXT_MUTED);
        btn.setBackground(BG_CARD);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true), new EmptyBorder(8, 18, 8, 18)));
        return btn;
    }

    private void styleToggle(JButton btn, boolean selected) {
        if (selected) {
            btn.setBackground(ACCENT);
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        } else {
            btn.setBackground(BG_CARD);
            btn.setForeground(TEXT_MUTED);
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        }
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(8, 0, 8, 0));
    }

    public void setEmpresa(Empresa e) { this.empresa = e; build(); }

    // ── Inner: Avatar+Name renderer ─────────────────────────────────────────────

    class AvatarNameRenderer implements TableCellRenderer {
        private final JPanel container;
        private final JLabel avatarLbl;
        private final JLabel nameLbl;

        AvatarNameRenderer() {
            container = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
            avatarLbl = new JLabel() {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(35, 55, 95));
                    g2.fillOval(0, 0, getWidth(), getHeight());
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            avatarLbl.setOpaque(false);
            avatarLbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
            avatarLbl.setForeground(ACCENT);
            avatarLbl.setHorizontalAlignment(SwingConstants.CENTER);
            avatarLbl.setPreferredSize(new Dimension(26, 26));
            nameLbl = new JLabel();
            nameLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            container.add(avatarLbl);
            container.add(nameLbl);
        }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object value,
                boolean isSelected, boolean hasFocus, int row, int col) {
            String name = value != null ? value.toString() : "";
            String[] parts = name.trim().split(" ");
            String initials = parts.length >= 2
                    ? ("" + parts[0].charAt(0) + parts[1].charAt(0)).toUpperCase()
                    : name.substring(0, Math.min(2, name.length())).toUpperCase();
            avatarLbl.setText(initials);
            nameLbl.setText(name);
            nameLbl.setForeground(TEXT_PRIMARY);
            container.setBackground(isSelected ? new Color(55, 75, 130) : BG_CARD);
            return container;
        }
    }

    // ── Inner: Tipo pill renderer ───────────────────────────────────────────────

    static class TipoPillRenderer implements TableCellRenderer {
        private final JPanel container;
        private final JLabel pill;

        TipoPillRenderer() {
            container = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
            pill = new JLabel() {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getBackground());
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            pill.setOpaque(false);
            pill.setFont(new Font("Segoe UI", Font.BOLD, 11));
            pill.setBorder(new EmptyBorder(2, 8, 2, 8));
            container.add(pill);
        }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object value,
                boolean isSelected, boolean hasFocus, int row, int col) {
            String text = value != null ? value.toString() : "";
            boolean esFijo = text.equals("Fijo");
            pill.setText(text);
            pill.setBackground(esFijo ? new Color(25, 45, 85) : new Color(65, 50, 15));
            pill.setForeground(esFijo ? new Color(100, 150, 255) : new Color(255, 183, 77));
            container.setBackground(isSelected ? new Color(55, 75, 130) : new Color(38, 38, 44));
            return container;
        }
    }

    // ── Inner: Delete button renderer/editor ───────────────────────────────────

    static class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true); setText("Eliminar");
            setFont(new Font("Segoe UI", Font.BOLD, 11));
            setForeground(new Color(255, 120, 110));
            setBackground(new Color(80, 25, 25));
            setBorderPainted(false);
            setBorder(new EmptyBorder(5, 10, 5, 10));
        }
        @Override public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) { return this; }
    }

    static class ButtonEditor extends DefaultCellEditor {
        private final JButton button;
        private final EmpleadosPanel panel;
        private int currentRow;

        public ButtonEditor(JCheckBox cb, EmpleadosPanel panel) {
            super(cb);
            this.panel = panel;
            button = new JButton("Eliminar");
            button.setFont(new Font("Segoe UI", Font.BOLD, 11));
            button.setForeground(new Color(255, 120, 110));
            button.setBackground(new Color(80, 25, 25));
            button.setBorderPainted(false);
            button.setOpaque(true);
            button.setBorder(new EmptyBorder(5, 10, 5, 10));
            button.addActionListener(e -> { fireEditingStopped(); panel.eliminarEmpleado(currentRow); });
        }

        @Override public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) { currentRow = row; return button; }
        @Override public Object getCellEditorValue() { return "Eliminar"; }
    }
}
