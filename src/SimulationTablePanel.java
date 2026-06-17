import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.text.DecimalFormat;

/**
 * SimulationTablePanel.java  — Fancy redesign
 *
 * Clean full-width table with:
 *  - Dark navy header row
 *  - Taller rows with better spacing
 *  - Status column: ✅ served immediately / ⏳ waited
 *  - Amber rows for customers who waited
 *  - Alternating stripe for others
 *  - Stats bar across the bottom
 */
public class SimulationTablePanel extends JPanel {

    private static final DecimalFormat DF4 = new DecimalFormat("0.0000");
    private static final DecimalFormat DF2 = new DecimalFormat("0.00");

    private static final Color NAVY    = new Color(15,  23,  42);
    private static final Color BG      = new Color(248, 250, 252);
    private static final Color WHITE   = Color.WHITE;
    private static final Color STRIPE  = new Color(241, 245, 249);
    private static final Color AMBER   = new Color(255, 251, 235);
    private static final Color AMBER_B = new Color(253, 230, 138);
    private static final Color MUTED   = new Color(100, 116, 139);
    private static final Color BORDER  = new Color(226, 232, 240);

    private static final String[] COLUMNS = {
            "#", "Arrival Time", "IAT", "Service Time",
            "Service Start", "Service End", "Wait Time", "Time in System", "Status"
    };
    private static final int[] WIDTHS = {44, 105, 80, 105, 105, 100, 100, 115, 80};

    public SimulationTablePanel(SimulationResult result) {
        setLayout(new BorderLayout());
        setBackground(BG);

        JTable table = buildTable(result);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(WHITE);

        // column header extra height
        table.getTableHeader().setPreferredSize(
                new Dimension(0, 38));

        add(buildTopBar(result), BorderLayout.NORTH);
        add(scroll,              BorderLayout.CENTER);
        add(buildStatsBar(result), BorderLayout.SOUTH);
    }

    // ── Top info bar ──────────────────────────────────────────────────────────

    private static JPanel buildTopBar(SimulationResult r) {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 8));
        bar.setBackground(new Color(15, 23, 42));

        addPill(bar, "📋 " + r.getNumCustomers() + " customers",
                new Color(99, 179, 237), new Color(12, 68, 124));
        addPill(bar, "⏳ " + r.getNumWhoWaited() + " waited",
                new Color(252, 211, 77), new Color(120, 53, 15));
        addPill(bar, "✅ " + (r.getNumCustomers() - r.getNumWhoWaited()) + " served immediately",
                new Color(110, 231, 183), new Color(6, 78, 59));

        return bar;
    }

    private static void addPill(JPanel parent, String text, Color bg, Color fg) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 11));
        lbl.setForeground(fg);
        lbl.setBackground(bg);
        lbl.setOpaque(true);
        lbl.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        parent.add(lbl);
    }

    // ── Table ─────────────────────────────────────────────────────────────────

    private JTable buildTable(SimulationResult result) {
        int n = result.getNumCustomers();
        Object[][] rows = new Object[n][9];
        for (int i = 0; i < n; i++) {
            rows[i][0] = i + 1;
            rows[i][1] = DF4.format(result.getArrivalTime(i));
            rows[i][2] = (i == 0) ? "—" : DF4.format(result.getIAT(i));
            rows[i][3] = DF4.format(result.getServiceTime(i));
            rows[i][4] = DF4.format(result.getServiceStart(i));
            rows[i][5] = DF4.format(result.getServiceEnd(i));
            rows[i][6] = DF4.format(result.getWaitTime(i));
            rows[i][7] = DF4.format(result.getTimeInSystem(i));
            rows[i][8] = result.customerWaited(i) ? "⏳ Waited" : "✅ Direct";
        }

        DefaultTableModel model = new DefaultTableModel(rows, COLUMNS) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(model);
        table.setFont(new Font("Monospaced", Font.PLAIN, 12));
        table.setRowHeight(28);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setSelectionForeground(NAVY);

        // ── Header renderer ──
        table.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                JLabel lbl = new JLabel(val == null ? "" : val.toString(), SwingConstants.CENTER);
                lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
                lbl.setForeground(Color.WHITE);
                lbl.setBackground(NAVY);
                lbl.setOpaque(true);
                lbl.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
                return lbl;
            }
        });

        // ── Cell renderer ──
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object value, boolean isSelected,
                    boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);

                boolean waited = result.customerWaited(row);
                int align = (col == 0 || col == 8)
                        ? SwingConstants.CENTER : SwingConstants.RIGHT;
                setHorizontalAlignment(align);
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));

                if (!isSelected) {
                    if (waited) {
                        setBackground(AMBER);
                        setForeground(new Color(120, 53, 15));
                    } else {
                        setBackground(row % 2 == 0 ? WHITE : STRIPE);
                        setForeground(NAVY);
                    }
                    if (col == 8) {
                        setForeground(waited
                                ? new Color(180, 83, 9)
                                : new Color(5, 122, 85));
                        setFont(new Font("SansSerif", Font.BOLD, 11));
                    }
                }
                return this;
            }
        });

        for (int i = 0; i < WIDTHS.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(WIDTHS[i]);

        return table;
    }

    // ── Bottom stats bar ──────────────────────────────────────────────────────

    private static JPanel buildStatsBar(SimulationResult r) {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 7));
        bar.setBackground(new Color(241, 245, 249));
        bar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER));

        addStat(bar, "Avg wait",       DF4.format(r.getAvgWaitAll())       + " min");
        addStat(bar, "Avg service",    DF4.format(r.getAvgServiceTime())   + " min");
        addStat(bar, "Avg in system",  DF4.format(r.getAvgTimeInSystem())  + " min");
        addStat(bar, "Utilisation",    DF2.format(r.getServerUtilPct())    + " %");
        addStat(bar, "Sim end",        DF2.format(r.getSimulationEndTime())+ " min");

        // amber legend swatch
        JPanel swatch = new JPanel();
        swatch.setPreferredSize(new Dimension(12, 12));
        swatch.setBackground(AMBER_B);
        swatch.setBorder(BorderFactory.createLineBorder(new Color(200,150,0)));
        JLabel swatchLbl = new JLabel("= waited in queue");
        swatchLbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
        swatchLbl.setForeground(MUTED);
        bar.add(Box.createHorizontalStrut(8));
        bar.add(swatch);
        bar.add(swatchLbl);

        return bar;
    }

    private static void addStat(JPanel bar, String label, String value) {
        JLabel lbl = new JLabel("<html><span style='color:#64748B;font-size:9px'>"
                + label.toUpperCase() + "</span><br>"
                + "<b>" + value + "</b></html>");
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lbl.setForeground(NAVY);
        bar.add(lbl);
    }
}