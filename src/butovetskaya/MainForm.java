package butovetskaya;

import javax.swing.*;
import java.awt.*;
//import java.beans.Expression;
//import java.beans.Expression;
import java.util.ArrayList;
import java.util.List;
import net.objecthunter.exp4j.*;
public class MainForm extends JFrame {
    private JTextField textFieldFunc;
    private JSpinner spinnerValueA;
    private JSpinner spinnerValueX;
    private JSpinner spinnerValueY;
    private JButton buttonAddPoint;
    private JComboBox comboBoxMethod;
    private JButton buttonDrawFunc;
    private JPanel panelDraw;
    private JPanel panelMain;
    private JPanel panelFunc;
    private JPanel panelCurve;
    private JButton buttonClear;
    private JPanel panel;
    private JSpinner spinnerScale;

    private int centerX;
    private int centerY;
    private int step;
    private List<Point> pointsFunc = new ArrayList<>();
    private List<Point> pointsCurve = new ArrayList<>();
    private List<Point> changedPoints = new ArrayList<>();

    DrawFunc drawFunc;
    DrawCurve drawCurve;

    public MainForm() {
        this.setContentPane(panel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setLocationRelativeTo(null);
        this.pack();
        this.setSize(new Dimension(1200, 700));
        this.spinnerScale.setValue(1);
        DrawPanel drawPanel = new DrawPanel();

        textFieldFunc.setText("x^2");

        buttonClear.addActionListener( k -> {
            pointsFunc.clear();
            pointsCurve.clear();
        });

        buttonDrawFunc.addActionListener(k -> {
            pointsFunc.clear();
            for (double x = -centerX; x <= centerX; x += 0.001) {
                if (x == 0) x += 0.0000001;
                if (findParameter()) {
                    Expression e = new ExpressionBuilder(textFieldFunc.getText())
                            .variables("x", "a")
                            .build()
                            .setVariable("x", x)
                            .setVariable("a", Integer.parseInt(String.valueOf(spinnerValueA.getValue())));
                    double y = e.evaluate();
                    if (y >= -centerY && y <= centerY) pointsFunc.add(new Point(x, y));
                } else {
                    Expression e = new ExpressionBuilder(textFieldFunc.getText())
                            .variables("x")
                            .build()
                            .setVariable("x", x);
                    double y = e.evaluate();
                    if (y >= -centerY && y <= centerY) pointsFunc.add(new Point(x, y));
                }
            }
        });

        buttonAddPoint.addActionListener(e -> {
            pointsFunc.clear();
            centerX = panelDraw.getWidth() / 2;
            centerY = panelDraw.getHeight() / 2;
            step = 10 * Integer.parseInt(String.valueOf(spinnerScale.getValue()));
            pointsCurve.add((new Point(Float.parseFloat(String.valueOf(spinnerValueX.getValue())),
                    Math.round(Float.parseFloat(String.valueOf(spinnerValueY.getValue()))))));
        });
    }

    private boolean findParameter() {
        String str = textFieldFunc.getText();
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == 'a') return true;
        }
        return false;
    }

    private void createUIComponents() {
        panelDraw = new DrawPanel();
        panelDraw.setMinimumSize(new Dimension(700, 650));
    }

    public class DrawPanel extends JPanel {

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;

            centerX = panelDraw.getWidth() / 2;
            centerY = panelDraw.getHeight() / 2;
            step = 10 * Integer.parseInt(String.valueOf(spinnerScale.getValue()));

            drawLines(g2, centerX, centerY, step);
            g2.setStroke(new BasicStroke(2));
            drawAxes(g2, centerX, centerY);
            g2.setStroke(new BasicStroke(1));

            if (pointsFunc.size() > 0) {
                drawFunc = new DrawFunc(g2, centerX, centerY, step, pointsFunc);
                drawFunc.paintFunc();
            } else if (pointsCurve.size() > 0) {
                drawCurve = new DrawCurve(g2, centerX, centerY, step, pointsCurve, changedPoints, String.valueOf(comboBoxMethod.getSelectedItem()));
                drawCurve.paintCurve();
            }
            g2.setColor(Color.black);
            repaint();

        }
        private void drawAxes(Graphics2D g2, int x, int y) {
            g2.setColor(new Color(0, 0, 0));
            g2.drawLine(x, 0, x, y * 2);
            g2.drawLine(0, y, x * 2, y);
        }

        private void drawLines(Graphics2D g2, int centerX, int centerY, int one_step) {
            g2.setColor(new Color(200, 200, 200));
            int step = centerX;
            while (step <= centerX * 2) {
                g2.drawLine(step, 0, step, centerY * 2);
                step += one_step;
            }
            step = centerX;
            while (step >= 0) {
                g2.drawLine(step, 0, step, centerY * 2);
                step -= one_step;
            }
            step = centerY;
            while (step >= 0) {
                g2.drawLine(0, step, centerX * 2, step);
                step -= one_step;
            }
            step = centerY;
            while (step <= centerY * 2) {
                g2.drawLine(0, step, centerX * 2, step);
                step += one_step;
            }
        }
    }
}
