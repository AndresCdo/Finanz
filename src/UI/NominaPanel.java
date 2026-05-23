package UI;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.io.*;
import java.util.List;
import Modelo.*;

public class NominaPanel extends JPanel {

    private Empresa empresa;
    private DefaultTableModel tableModel;
    private JTable tabla;

    private static final Color BG_MAIN      = new Color(28, 28, 32);
    private static final Color BG_CARD      = new Color(38, 38, 44);
    private static final Color BG_TOTAL_ROW = new Color(44, 44, 52);
    private static final Color BORDER_COLOR = new Color(60, 60, 70);
    private static final Color TEXT_PRIMARY = new Color(225, 225, 230);
    private static final Color TEXT_MUTED   = new Color(130, 130, 145);
    private static final Color COLOR_GREEN  = new Color(76, 175, 80);
    private static final Color COLOR_RED    = new Color(229, 83, 75);
    private static final Color ACCENT       = new Color(100, 150, 255);
    private static final Color AMBER        = new Color(255, 183, 77);

    public NominaPanel(Empresa empresa) {
        this.empresa = empresa;
        setLayout(new BorderLayout());
        setBackground(BG_MAIN);
        setBorder(new EmptyBorder(25, 25, 25, 25));
        build();
    }

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

        String[] cols = {"ID", "Empleado", "Cargo", "Tipo", "Cálculo", "Salario"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tabla = new JTable(tableModel);
        styleTable();
        loadTableData();

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBackground(BG_CARD);
        scroll.getViewport().setBackground(BG_CARD);
        scroll.setBorder(new LineBorder(BORDER_COLOR, 1, true));
        add(scroll, BorderLayout.CENTER);

        add(buildFooter(), BorderLayout.SOUTH);

        revalidate();
        repaint();
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_MAIN);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));

        JPanel titles = new JPanel(new BorderLayout());
        titles.setBackground(BG_MAIN);
        JLabel title = new JLabel("≡  Nómina");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(TEXT_PRIMARY);
        JLabel subtitle = new JLabel("Cálculo y administración de salarios");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(TEXT_MUTED);
        titles.add(title, BorderLayout.NORTH);
        titles.add(subtitle, BorderLayout.SOUTH);

        JButton btnExport = createOutlinedButton("↓ Exportar nómina");
        btnExport.addActionListener(e -> exportarNomina());

        header.add(titles, BorderLayout.WEST);
        header.add(btnExport, BorderLayout.EAST);
        return header;
    }

    private JPanel buildMetricsRow() {
        List<Empleado> empleados = empresa != null ? empresa.getEmpleados()
                : java.util.Collections.emptyList();
        double costoTotal = empleados.stream().mapToDouble(Empleado::calcularSalario).sum();
        double horasTotales = empleados.stream()
                .filter(e -> e instanceof EmpleadoPorHoras)
                .mapToDouble(e -> ((EmpleadoPorHoras) e).getHorasTrabajadas()).sum();

        JPanel row = new JPanel(new GridLayout(1, 3, 14, 0));
        row.setBackground(BG_MAIN);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        row.add(metricCard("◉", "Empleados en nómina",  String.valueOf(empleados.size()), ACCENT));
        row.add(metricCard("○", "Horas registradas",     String.format("%.0f hrs", horasTotales), ACCENT));
        row.add(metricCard("↓", "Costo total nómina",    fmt(costoTotal), COLOR_RED));
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
        val.setBorder(new EmptyBorder(5, 0, 0, 0));

        card.add(top, BorderLayout.NORTH);
        card.add(val, BorderLayout.CENTER);
        return card;
    }

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

        tabla.getColumnModel().getColumn(1).setPreferredWidth(190);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(140);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(110);
        tabla.getColumnModel().getColumn(4).setPreferredWidth(210);
        tabla.getColumnModel().getColumn(5).setPreferredWidth(130);

        tabla.getColumnModel().getColumn(3).setCellRenderer(new TipoPillRenderer());

        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                JLabel label = (JLabel) c;
                boolean isTotal = row == t.getRowCount() - 1;
                label.setBackground(isTotal ? BG_TOTAL_ROW
                        : (isSelected ? new Color(55, 75, 130) : BG_CARD));
                label.setForeground(isTotal && col == 5 ? COLOR_RED : TEXT_PRIMARY);
                label.setFont(isTotal ? new Font("Segoe UI", Font.BOLD, 13)
                        : new Font("Segoe UI", Font.PLAIN, 13));
                label.setBorder(new EmptyBorder(0, 10, 0, 10));
                if (col == 5 && !isTotal) {
                    label.setForeground(COLOR_GREEN);
                    label.setHorizontalAlignment(SwingConstants.RIGHT);
                }
                if (isTotal && col == 5) label.setHorizontalAlignment(SwingConstants.RIGHT);
                return c;
            }
        });
    }

    private void loadTableData() {
        tableModel.setRowCount(0);
        if (empresa == null) return;
        double costoTotal = 0;
        for (Empleado e : empresa.getEmpleados()) {
            String tipo;
            String calculo;
            if (e instanceof EmpleadoFijo ef) {
                tipo    = "Fijo";
                calculo = "Salario base: " + fmt(ef.getSalarioBase());
            } else {
                EmpleadoPorHoras ep = (EmpleadoPorHoras) e;
                tipo    = "Por horas";
                calculo = String.format("%.0f hrs × %s/hr",
                        ep.getHorasTrabajadas(), fmt(ep.getTarifaHora()));
            }
            double salario = e.calcularSalario();
            costoTotal += salario;
            tableModel.addRow(new Object[]{
                    e.getId(), e.getNombre(), e.getCargo(), tipo, calculo, fmt(salario)
            });
        }
        tableModel.addRow(new Object[]{"", "TOTAL", "", "", "", fmt(costoTotal)});
    }

    private JPanel buildFooter() {
        double total = empresa != null
                ? empresa.getEmpleados().stream().mapToDouble(Empleado::calcularSalario).sum() : 0;
        int count = empresa != null ? empresa.getEmpleados().size() : 0;

        JLabel stats = new JLabel("Empleados: " + count + "   |   Costo total: " + fmt(total));
        stats.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        stats.setForeground(TEXT_MUTED);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(BG_CARD);
        footer.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(11, 15, 11, 15)));
        footer.add(stats, BorderLayout.WEST);
        return footer;
    }

    private void exportarNomina() {
        if (empresa == null || empresa.getEmpleados().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No hay empleados para exportar.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar reporte de nómina");
        chooser.setSelectedFile(new File("nomina.txt"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        File archivo = chooser.getSelectedFile();
        if (!archivo.getName().endsWith(".txt"))
            archivo = new File(archivo.getAbsolutePath() + ".txt");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo))) {
            bw.write(empresa.generarReporteNomina());
            JOptionPane.showMessageDialog(this,
                    "Nómina exportada en:\n" + archivo.getAbsolutePath(),
                    "Exportación exitosa", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al exportar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String fmt(double v) { return String.format("$%,.0f", v); }

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

    public void setEmpresa(Empresa e) { this.empresa = e; build(); }

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
}
