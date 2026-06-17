import javax.swing.*;

/**
 * Main.java
 *
 * Application entry point.
 * Launches the InputWindow on the Swing Event Dispatch Thread.
 *
 * To run:
 *   javac *.java
 *   java Main
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}

            new InputWindow().setVisible(true);
        });
    }
}