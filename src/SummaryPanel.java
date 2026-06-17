import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;

/**
 * SummaryPanel.java  — Fancy redesign
 *
 * Full-bleed dark header banner, then a 2×4 grid of raised metric cards.
 * Each card has: coloured icon badge, muted label, large value, and a thin
 * coloured progress bar (for percentage metrics).
 */
public class SummaryPanel extends JPanel {

    private static final DecimalFormat DF4 = new DecimalFormat("0.0000");
    private static final DecimalFormat DF2 = new DecimalFormat("0.00");

    // ── Palette (matches InputWindow) ─────────────────────────────────────────
    private static final Color NAVY    = new Color(15,  23,  42);
    private static final Color BG      = new Color(241, 245, 249);
    private static final Color WHITE   = Color.WHITE;
    private static final Color BORDER  = new Color(226, 232, 240);
    private static final Color MUTED   = new Color(100, 116, 139);

    // Card accent colours
    private static final Color C_BLUE   = new Color(37,  99,  235);
    private static final Color C_GREEN  = new Color(22,  163,  74);
    private static final Color C_PURPLE = new Color(124,  58, 237);
    private static final Color C_AMBER  = new Color(217, 119,   6);
    private static final Color C_SLATE  = new Color(71,  85, 105);
    private static final Color C_ROSE   = new Color(225,  29,  72);

    public SummaryPanel(SimulationResult r) {
        setLayout(new BorderLayout(0, 0));
        setBackground(BG);

        add(buildBanner(r), BorderLayout.NORTH);
        add(buildGrid(r),   BorderLayout.CENTER);
        add(buildFooter(),  BorderLayout.SOUTH);
    }

    // ── Header banner ─────────────────────────────────────────────────────────

    private JPanel buildBanner(SimulationResult r) {
        JPanel banner = new JPanel(new BorderLayout(16, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(NAVY);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // subtle dot-grid texture
                g2.setColor(new Color(255,255,255,12));
                for (int x = 8; x < getWidth(); x += 20)
                    for (int y = 8; y < getHeight(); y += 20)
                        g2.fillOval(x, y, 3, 3);
            }
        };
        banner.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JLabel icon = new JLabel("📊");
        icon.setFont(new Font("SansSerif", Font.PLAIN, 32));

        JPanel text = new JPanel(new GridLayout(2, 1, 0, 2));
        text.setOpaque(false);
        JLabel heading = new JLabel("Queue Statistics Summary");
        heading.setFont(new Font("SansSerif", Font.BOLD, 18));
        heading.setForeground(Color.WHITE);

        JLabel sub = new JLabel(String.format(
                "%d customers simulated  ·  simulation ended at %.2f min",
                r.getNumCustomers(), r.getSimulationEndTime()));
        sub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        sub.setForeground(new Color(148, 163, 184));

        text.add(heading);
        text.add(sub);
        banner.add(icon, BorderLayout.WEST);
        banner.add(text, BorderLayout.CENTER);

        // pill badge: utilisation
        JPanel pill = new JPanel(new BorderLayout());
        pill.setOpaque(false);
        JLabel pillLbl = new JLabel(String.format("%.1f%% utilised", r.getServerUtilPct()),
                SwingConstants.CENTER);
        pillLbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        pillLbl.setForeground(new Color(15, 23, 42));
        pillLbl.setBackground(new Color(99, 179, 237));
        pillLbl.setOpaque(true);
        pillLbl.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        pill.add(pillLbl, BorderLayout.CENTER);
        banner.add(pill, BorderLayout.EAST);

        return banner;
    }

    // ── Cards grid ────────────────────────────────────────────────────────────

    private JPanel buildGrid(SimulationResult r) {
        JPanel wrap = new JPanel(new GridLayout(2, 4, 12, 12));
        wrap.setBackground(BG);
        wrap.setBorder(BorderFactory.createEmptyBorder(18, 20, 12, 20));

        addCard(wrap, "⏳", "Avg wait — all customers",
                DF4.format(r.getAvgWaitAll()) + " min", -1, C_BLUE);

        addCard(wrap, "🕐", "Avg wait — who waited",
                DF4.format(r.getAvgWaitWhoWaited()) + " min", -1, C_BLUE);

        addCard(wrap, "⚙", "Avg service time",
                DF4.format(r.getAvgServiceTime()) + " min", -1, C_GREEN);

        addCard(wrap, "🏁", "Avg time in system",
                DF4.format(r.getAvgTimeInSystem()) + " min", -1, C_GREEN);

        addCard(wrap, "📈", "Server utilisation",
                DF2.format(r.getServerUtilPct()) + " %",
                r.getServerUtilPct(), C_PURPLE);

        addCard(wrap, "💤", "Server idle time",
                DF2.format(r.getServerIdlePct()) + " %",
                r.getServerIdlePct(), C_SLATE);

        addCard(wrap, "⚠", "Customers who waited",
                DF2.format(r.getPctCustomersWaited()) + " %",
                r.getPctCustomersWaited(), C_AMBER);

        addCard(wrap, "🕔", "Total simulation time",
                DF2.format(r.getSimulationEndTime()) + " min", -1, C_ROSE);

        return wrap;
    }

    /**
     * @param pct  0–100 for a progress bar, or -1 to omit it
     */
    private static void addCard(JPanel parent, String emoji,
                                String label, String value,
                                double pct, Color accent) {
        JPanel card = new JPanel(new BorderLayout(0, 6));
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));

        // top row: emoji badge + label
        JPanel topRow = new JPanel(new BorderLayout(8, 0));
        topRow.setBackground(WHITE);

        JLabel badgeLbl = new JLabel(emoji);
        badgeLbl.setFont(new Font("SansSerif", Font.PLAIN, 18));
        badgeLbl.setOpaque(true);
        badgeLbl.setBackground(blend(accent, WHITE, 0.12f));
        badgeLbl.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

        JLabel lbl = new JLabel("<html><span style='color:#64748B;font-size:10px'>"
                + label + "</span></html>");
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 11));

        topRow.add(badgeLbl, BorderLayout.WEST);
        topRow.add(lbl,      BorderLayout.CENTER);

        // value
        JLabel val = new JLabel(value);
        val.setFont(new Font("SansSerif", Font.BOLD, 20));
        val.setForeground(new Color(15, 23, 42));

        card.add(topRow, BorderLayout.NORTH);
        card.add(val,    BorderLayout.CENTER);

        // optional progress bar
        if (pct >= 0) {
            JProgressBar bar = new JProgressBar(0, 100);
            bar.setValue((int) Math.round(pct));
            bar.setStringPainted(false);
            bar.setForeground(accent);
            bar.setBackground(blend(accent, WHITE, 0.15f));
            bar.setBorderPainted(false);
            bar.setPreferredSize(new Dimension(0, 5));
            card.add(bar, BorderLayout.SOUTH);
        }

        // hover lift
        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                card.setBackground(blend(accent, WHITE, 0.04f));
                card.repaint();
            }
            @Override public void mouseExited(MouseEvent e) {
                card.setBackground(WHITE);
                card.repaint();
            }
        });

        parent.add(card);
    }

    // ── Footer ────────────────────────────────────────────────────────────────

    private static JPanel buildFooter() {
        JPanel f = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        f.setBackground(new Color(241, 245, 249));
        f.setBorder(BorderFactory.createEmptyBorder(4, 20, 10, 20));
        JLabel note = new JLabel(
                "Single-server queue  ·  E[X] = (a+b)/2  ·  First-come-first-served  ·  Uniform(a,b)");
        note.setFont(new Font("SansSerif", Font.ITALIC, 11));
        note.setForeground(new Color(148, 163, 184));
        f.add(note);
        return f;
    }

    // ── Colour helper ─────────────────────────────────────────────────────────

    /** Blend colour `c` into white at `ratio` opacity (0=white, 1=c). */
    private static Color blend(Color c, Color base, float ratio) {
        int r = (int)(base.getRed()   + (c.getRed()   - base.getRed())   * ratio);
        int g = (int)(base.getGreen() + (c.getGreen() - base.getGreen()) * ratio);
        int b = (int)(base.getBlue()  + (c.getBlue()  - base.getBlue())  * ratio);
        return new Color(Math.max(0,Math.min(255,r)),
                Math.max(0,Math.min(255,g)),
                Math.max(0,Math.min(255,b)));
    }
}