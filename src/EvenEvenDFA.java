import javax.swing.*;
import java.awt.*;

public class EvenEvenDFA extends JFrame {

    int[][] delta = { {1, 2}, {0, 3}, {3, 0}, {2, 1} };

    int currentState = 0;
    int step = 0;
    String inputStr = "";
    Timer timer;

    JTextField inputField;
    JTextArea logArea;
    DFAPanel dfaPanel;
    JButton autoBtn, resetBtn;
    JLabel resultLabel;

    public EvenEvenDFA() {
        setTitle("EVEN-EVEN DFA Simulator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(300, 300);
        setLayout(new BorderLayout(5, 5));

        // Top
        JPanel top = new JPanel();
        top.add(new JLabel("Input (a/b only):"));
        inputField = new JTextField(15);
        top.add(inputField);
        JButton loadBtn = new JButton("Load");
        loadBtn.addActionListener(e -> loadInput());
        top.add(loadBtn);
        add(top, BorderLayout.NORTH);

        // Center
        dfaPanel = new DFAPanel();
        dfaPanel.setPreferredSize(new Dimension(500, 270));
        add(dfaPanel, BorderLayout.CENTER);

        // Bottom
        JPanel bottom = new JPanel(new BorderLayout());
        JPanel btns = new JPanel();
        autoBtn = new JButton("▶ Simulate");
        resetBtn = new JButton("Reset");
        autoBtn.setEnabled(false);
        autoBtn.addActionListener(e -> startAuto());
        resetBtn.addActionListener(e -> reset());
        btns.add(autoBtn);
        btns.add(resetBtn);

        resultLabel = new JLabel("  Enter a string and click Load.", SwingConstants.CENTER);
        resultLabel.setFont(resultLabel.getFont().deriveFont(Font.BOLD, 13f));
        bottom.add(btns, BorderLayout.NORTH);
        bottom.add(resultLabel, BorderLayout.CENTER);

        logArea = new JTextArea(6, 40);
        logArea.setEditable(false);
        bottom.add(new JScrollPane(logArea), BorderLayout.SOUTH);
        add(bottom, BorderLayout.SOUTH);

        // 800 ms for One step
        timer = new Timer(800, e -> autoStep());

        pack();
        setLocationRelativeTo(null);
        setVisible(true); // frame visible
    }

    void loadInput() {
        String s = inputField.getText().trim().toLowerCase();
        if (!s.matches("[ab]*") || s.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a non-empty string with only 'a' and 'b'!");
            return;
        }
        inputStr = s;
        currentState = 0;
        step = 0;
        logArea.setText("");
        resultLabel.setText("Loaded: \"" + inputStr + "\"  —  Click ▶ Simulate");
        resultLabel.setForeground(Color.BLACK);
        autoBtn.setEnabled(true);
        dfaPanel.repaint();
    }

    void startAuto() {
        autoBtn.setEnabled(false);
        resultLabel.setText("Simulating...");
        resultLabel.setForeground(Color.BLACK);
        timer.start();
    }

    void autoStep() {
        if (step < inputStr.length()) {
            char c = inputStr.charAt(step);
            int sym = (c == 'a') ? 0 : 1;
            int prev = currentState;
            currentState = delta[currentState][sym];
            logArea.append("Step " + (step + 1) + ": Read '" + c + "'  ->  q" + prev + "  ->  q" + currentState + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
            step++;
            dfaPanel.repaint();
        } else {
            timer.stop();
            showResult();
        }
    }

    void showResult() {
        if (currentState == 0) {
            resultLabel.setText("ACCEPTED  (even number of a's and b's)");
            resultLabel.setForeground(new Color(0, 140, 0));
        } else {
            resultLabel.setText("REJECTED  (odd number of a's or b's)");
            resultLabel.setForeground(Color.RED);
        }
    }

    void reset() {
        timer.stop();
        inputField.setText("");
        inputStr = "";
        currentState = 0;
        step = 0;
        logArea.setText("");
        resultLabel.setText("  Enter a string and click Load.");
        resultLabel.setForeground(Color.BLACK);
        autoBtn.setEnabled(false);
        dfaPanel.repaint();
    }

    // DFA diagram panel
    class DFAPanel extends JPanel {
        int[] cx = {120, 300, 120, 300};
        int[] cy = {110, 110, 210, 210};
        int r = 35;

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setStroke(new BasicStroke(2));

            // Arrows
            drawArrow(g2, 0, 1, "a", -14);
            drawArrow(g2, 1, 0, "a",  14);
            drawArrow(g2, 0, 2, "b", -14);
            drawArrow(g2, 2, 0, "b",  14);
            drawArrow(g2, 1, 3, "b", -14);
            drawArrow(g2, 3, 1, "b",  14);
            drawArrow(g2, 2, 3, "a", -14);
            drawArrow(g2, 3, 2, "a",  14);

            // States
            String[] subLabels = {"even a, even b", "odd a, even b", "even a, odd b", "odd a, odd b"};
            for (int i = 0; i < 4; i++) {
                g2.setColor(i == currentState ? new Color(255, 215, 0) : new Color(200, 225, 255));
                g2.fillOval(cx[i] - r, cy[i] - r, 2 * r, 2 * r);
                g2.setColor(Color.BLACK);
                g2.drawOval(cx[i] - r, cy[i] - r, 2 * r, 2 * r);
                if (i == 0) g2.drawOval(cx[i]-r+5, cy[i]-r+5, 2*r-10, 2*r-10); // accept double circle
                g2.setFont(new Font("Arial", Font.BOLD, 13));
                g2.drawString("q" + i, cx[i] - 9, cy[i] + 5);
                g2.setFont(new Font("Arial", Font.PLAIN, 9));
                g2.setColor(Color.GRAY);
                g2.drawString(subLabels[i], cx[i] - 28, cy[i] + r + 14);
            }

            // Start arrow
            g2.setColor(Color.BLACK);
            g2.drawLine(55, cy[0], cx[0] - r, cy[0]);
            g2.fillPolygon(new int[]{cx[0]-r, cx[0]-r-8, cx[0]-r-8}, new int[]{cy[0], cy[0]-5, cy[0]+5}, 3);
            g2.setFont(new Font("Arial", Font.PLAIN, 10));
            g2.drawString("start", 57, cy[0] - 4);
        }

        void drawArrow(Graphics2D g2, int from, int to, String label, int offset) {
            int x1 = cx[from], y1 = cy[from], x2 = cx[to], y2 = cy[to];
            double dx = x2 - x1, dy = y2 - y1;
            double len = Math.sqrt(dx*dx + dy*dy);
            double nx = -dy/len * offset, ny = dx/len * offset;

            int cpx = (x1+x2)/2 + (int)nx;
            int cpy = (y1+y2)/2 + (int)ny;

            double a1 = Math.atan2(cpy-y1, cpx-x1);
            double a2 = Math.atan2(cpy-y2, cpx-x2);
            int sx = (int)(x1 + r*Math.cos(a1)), sy = (int)(y1 + r*Math.sin(a1));
            int ex = (int)(x2 + r*Math.cos(a2)), ey = (int)(y2 + r*Math.sin(a2));

            g2.setColor(new Color(60, 60, 60));
            g2.drawLine(sx, sy, cpx, cpy);
            g2.drawLine(cpx, cpy, ex, ey);

            double aAngle = Math.atan2(ey-cpy, ex-cpx);
            g2.fillPolygon(
                new int[]{ex, (int)(ex-10*Math.cos(aAngle-0.4)), (int)(ex-10*Math.cos(aAngle+0.4))},
                new int[]{ey, (int)(ey-10*Math.sin(aAngle-0.4)), (int)(ey-10*Math.sin(aAngle+0.4))}, 3);

            g2.setFont(new Font("Arial", Font.BOLD, 11));
            g2.setColor(new Color(0, 0, 180));
            g2.drawString(label, cpx+3, cpy-3);
        }
    }

    public static void main(String[] args) {
    	new EvenEvenDFA();
    }
}