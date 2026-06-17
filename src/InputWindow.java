import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

/**
 * InputWindow.java  — Fancy redesign
 *
 * Two-panel layout:
 *   LEFT  — dark navy brand sidebar with title, subtitle and a mini legend
 *   RIGHT — white form panel with grouped, card-style sections
 */
public class InputWindow extends JFrame {

    // ── Palette ──────────────────────────────────────────────────────────────
    static final Color NAVY        = new Color(15,  23,  42);
    static final Color NAVY_MID    = new Color(30,  41,  59);
    static final Color ACCENT      = new Color(99, 179, 237);   // sky-blue
    static final Color ACCENT_DARK = new Color(56, 132, 196);
    static final Color WHITE       = Color.WHITE;
    static final Color BG          = new Color(248, 250, 252);
    static final Color BORDER_CLR  = new Color(226, 232, 240);
    static final Color TEXT_MAIN   = new Color(15,  23,  42);
    static final Color TEXT_MUTED  = new Color(100, 116, 139);
    static final Color SEC_BLUE    = new Color(37,  99, 235);
    static final Color SEC_BLUE_BG = new Color(239, 246, 255);

    // ── Controls ──────────────────────────────────────────────────────────────
    private final JSpinner  spnCustomers;
    private final JSpinner  spnIATlow, spnIAThigh;
    private final JSpinner  spnSVClow, spnSVChigh;
    private final JSpinner  spnSeed;
    private final JCheckBox chkFixedSeed;
    private final JButton   btnRun, btnReset;

    public InputWindow() {
        super("Bank Queue Simulation");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        // ── Spinners ──
        spnCustomers = new JSpinner(new SpinnerNumberModel(100, 10, 500, 10));
        spnIATlow    = new JSpinner(new SpinnerNumberModel(1.0, 0.1, 100.0, 0.5));
        spnIAThigh   = new JSpinner(new SpinnerNumberModel(8.0, 0.1, 100.0, 0.5));
        spnSVClow    = new JSpinner(new SpinnerNumberModel(1.0, 0.1, 100.0, 0.5));
        spnSVChigh   = new JSpinner(new SpinnerNumberModel(6.0, 0.1, 100.0, 0.5));
        spnSeed      = new JSpinner(new SpinnerNumberModel(42,  0,   9999,  1));

        styleSpinner(spnCustomers);
        styleSpinner(spnIATlow);  styleSpinner(spnIAThigh);
        styleSpinner(spnSVClow);  styleSpinner(spnSVChigh);
        styleSpinner(spnSeed);

        chkFixedSeed = new JCheckBox("Lock seed for reproducible results", true);
        chkFixedSeed.setFont(new Font("SansSerif", Font.PLAIN, 12));
        chkFixedSeed.setForeground(TEXT_MUTED);
        chkFixedSeed.setBackground(WHITE);
        chkFixedSeed.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        chkFixedSeed.addActionListener(e -> spnSeed.setEnabled(chkFixedSeed.isSelected()));

        btnRun   = makeRunButton();
        btnReset = makeResetButton();
        btnRun  .addActionListener(e -> onRun());
        btnReset.addActionListener(e -> resetDefaults());

        // ── Layout ──
        JPanel root = new JPanel(new BorderLayout());
        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildFormPanel(), BorderLayout.CENTER);

        add(root);
        pack();
        setLocationRelativeTo(null);
    }

    // ── Sidebar ───────────────────────────────────────────────────────────────

    private JPanel buildSidebar() {
        JPanel panel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(NAVY);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // subtle diagonal accent stripe
                g2.setColor(new Color(255, 255, 255, 8));
                for (int y = -getWidth(); y < getHeight(); y += 28) {
                    g2.fillRect(0, y, getWidth(), 14);
                }
            }
        };
        panel.setPreferredSize(new Dimension(210, 0));
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(36, 24, 28, 24));

        // top: icon + title
        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));

        JLabel icon = new JLabel("🏦");
        icon.setFont(new Font("SansSerif", Font.PLAIN, 38));
        icon.setAlignmentX(Component.LEFT_ALIGNMENT);
        icon.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        JLabel title = new JLabel("<html>Bank Queue<br>Simulation</html>");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(WHITE);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(255, 255, 255, 40));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        JLabel sub = new JLabel("<html><span style='line-height:1.6'>Uniform inter-arrival<br>& service time model<br>— single server FCFS</span></html>");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 11));
        sub.setForeground(new Color(148, 163, 184));
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        sub.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        top.add(icon);
        top.add(title);
        top.add(Box.createVerticalStrut(14));
        top.add(sep);
        top.add(sub);

        // bottom: course tag
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        bottom.setOpaque(false);
        JLabel course = new JLabel("ICS 4106  ·  Strathmore");
        course.setFont(new Font("SansSerif", Font.PLAIN, 10));
        course.setForeground(new Color(100, 116, 139));
        bottom.add(course);

        panel.add(top,    BorderLayout.NORTH);
        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }

    // ── Form panel ────────────────────────────────────────────────────────────

    private JPanel buildFormPanel() {
        JPanel outer = new JPanel(new BorderLayout(0, 0));
        outer.setBackground(BG);
        outer.setBorder(BorderFactory.createEmptyBorder(28, 28, 20, 28));

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(BG);

        form.add(buildCard("👥  General",
                new String[]{"Number of customers"},
                new JComponent[]{spnCustomers}));
        form.add(Box.createVerticalStrut(14));

        form.add(buildCard("⏱  Inter-arrival time  —  U(a, b)  [minutes]",
                new String[]{"Lower bound  a", "Upper bound  b"},
                new JComponent[]{spnIATlow, spnIAThigh}));
        form.add(Box.createVerticalStrut(14));

        form.add(buildCard("⚙  Service time  —  U(a, b)  [minutes]",
                new String[]{"Lower bound  a", "Upper bound  b"},
                new JComponent[]{spnSVClow, spnSVChigh}));
        form.add(Box.createVerticalStrut(14));

        JPanel rngCard = buildCard("🎲  Random Number Generator",
                new String[]{"Seed value"},
                new JComponent[]{spnSeed});
        // inject checkbox above the seed row
        JPanel inner = (JPanel) rngCard.getComponent(1);
        inner.add(chkFixedSeed, 0);
        inner.add(Box.createVerticalStrut(6), 1);
        form.add(rngCard);

        outer.add(form, BorderLayout.CENTER);
        outer.add(buildButtonBar(), BorderLayout.SOUTH);
        return outer;
    }

    /** White rounded card with a heading and label→field rows. */
    private JPanel buildCard(String heading, String[] labels, JComponent[] fields) {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR, 1, true),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        JLabel head = new JLabel(heading);
        head.setFont(new Font("SansSerif", Font.BOLD, 12));
        head.setForeground(SEC_BLUE);
        card.add(head, BorderLayout.NORTH);

        JPanel rows = new JPanel(new GridBagLayout());
        rows.setBackground(WHITE);
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(4, 0, 4, 12);

        for (int i = 0; i < labels.length; i++) {
            gc.gridy = i;
            gc.gridx = 0; gc.weightx = 0;
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
            lbl.setForeground(TEXT_MAIN);
            rows.add(lbl, gc);

            gc.gridx = 1; gc.weightx = 1;
            fields[i].setPreferredSize(new Dimension(130, 30));
            rows.add(fields[i], gc);
        }
        card.add(rows, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildButtonBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bar.setBackground(BG);
        bar.setBorder(BorderFactory.createEmptyBorder(14, 0, 0, 0));
        bar.add(btnReset);
        bar.add(btnRun);
        return bar;
    }

    // ── Button factories ──────────────────────────────────────────────────────

    private JButton makeRunButton() {
        JButton btn = new JButton("▶   Run Simulation") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isPressed()
                        ? ACCENT_DARK
                        : getModel().isRollover() ? new Color(72, 149, 215) : ACCENT_DARK;
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setForeground(WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(11, 24, 11, 24));
        return btn;
    }

    private JButton makeResetButton() {
        JButton btn = new JButton("↺  Reset");
        btn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btn.setForeground(TEXT_MUTED);
        btn.setBackground(WHITE);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR, 1, true),
                BorderFactory.createEmptyBorder(10, 18, 10, 18)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(BG); }
            @Override public void mouseExited (MouseEvent e) { btn.setBackground(WHITE); }
        });
        return btn;
    }

    // ── Spinner styling ───────────────────────────────────────────────────────

    private static void styleSpinner(JSpinner s) {
        s.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                BorderFactory.createEmptyBorder(2, 6, 2, 4)
        ));
        JComponent editor = s.getEditor();
        if (editor instanceof JSpinner.DefaultEditor de) {
            de.getTextField().setFont(new Font("SansSerif", Font.PLAIN, 13));
            de.getTextField().setForeground(new Color(15, 23, 42));
        }
    }

    // ── Actions ───────────────────────────────────────────────────────────────

    private void onRun() {
        double iatL = (double) spnIATlow.getValue();
        double iatH = (double) spnIAThigh.getValue();
        double svcL = (double) spnSVClow.getValue();
        double svcH = (double) spnSVChigh.getValue();

        if (iatL >= iatH) {
            JOptionPane.showMessageDialog(this,
                    "IAT lower bound must be strictly less than upper bound.",
                    "Input Error", JOptionPane.ERROR_MESSAGE); return;
        }
        if (svcL >= svcH) {
            JOptionPane.showMessageDialog(this,
                    "Service time lower bound must be strictly less than upper bound.",
                    "Input Error", JOptionPane.ERROR_MESSAGE); return;
        }
        int  n    = (int) spnCustomers.getValue();
        long seed = chkFixedSeed.isSelected() ? (int) spnSeed.getValue() : -1L;
        SimulationResult result = SimulationEngine.run(n, iatL, iatH, svcL, svcH, seed);
        new OutputWindow(result, iatL, iatH, svcL, svcH).setVisible(true);
    }

    private void resetDefaults() {
        spnCustomers.setValue(100);
        spnIATlow   .setValue(1.0);  spnIAThigh.setValue(8.0);
        spnSVClow   .setValue(1.0);  spnSVChigh.setValue(6.0);
        spnSeed     .setValue(42);
        chkFixedSeed.setSelected(true);
        spnSeed     .setEnabled(true);
    }
}