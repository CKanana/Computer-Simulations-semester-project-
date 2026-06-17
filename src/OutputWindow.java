import javax.swing.*;
import java.awt.*;

/**
 * OutputWindow.java  — Fancy redesign
 *
 * Hosts the two result tabs. Tab bar uses the navy palette to match InputWindow.
 */
public class OutputWindow extends JFrame {

    public OutputWindow(SimulationResult result,
                        double iatL, double iatH,
                        double svcL, double svcH) {

        super(String.format(
                "Results — %d customers  |  IAT ~ U(%.1f, %.1f)  |  SVC ~ U(%.1f, %.1f)",
                result.getNumCustomers(), iatL, iatH, svcL, svcH));

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
        tabs.setFont(new Font("SansSerif", Font.BOLD, 13));
        tabs.setBackground(new Color(15, 23, 42));
        tabs.setForeground(Color.WHITE);

        tabs.addTab("  📊  Queue Statistics  ", new SummaryPanel(result));
        tabs.addTab("  📋  Simulation Table  ", new SimulationTablePanel(result));

        add(tabs);
        setPreferredSize(new Dimension(1120, 700));
        pack();
        setLocationRelativeTo(null);
    }
}