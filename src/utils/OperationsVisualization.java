package utils;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;

public class OperationsVisualization extends JPanel {
    private GraphOperations.Graph graph1;
    private GraphOperations.Graph graph2;
    private GraphOperations.Graph resultGraph;
    private String currentOperation;

    private Map<String, Point2D> positions1;
    private Map<String, Point2D> positions2;
    private Map<String, Point2D> positionsResult;

    private static final int VERTEX_RADIUS = 20;
    private static final Color GRAPH1_COLOR = new Color(100, 150, 255);
    private static final Color GRAPH2_COLOR = new Color(255, 150, 100);
    private static final Color RESULT_COLOR = new Color(150, 255, 150);

    public OperationsVisualization() {
        setBackground(new Color(248, 249, 250));
        setBorder(BorderFactory.createLoweredBevelBorder());
        setPreferredSize(new Dimension(900, 600));

        positions1 = new HashMap<>();
        positions2 = new HashMap<>();
        positionsResult = new HashMap<>();
    }

    public void setGraphs(GraphOperations.Graph g1, GraphOperations.Graph g2,
                          GraphOperations.Graph result, String operation) {
        this.graph1 = g1;
        this.graph2 = g2;
        this.resultGraph = result;
        this.currentOperation = operation;

        calculatePositions();
        repaint();
    }

    private void calculatePositions() {
        if (graph1 != null) calculateGraphPositions(graph1, positions1, 150, 150);
        if (graph2 != null) calculateGraphPositions(graph2, positions2, 450, 150);
        if (resultGraph != null) calculateGraphPositions(resultGraph, positionsResult, 300, 400);
    }

    private void calculateGraphPositions(GraphOperations.Graph graph, Map<String, Point2D> positions,
                                         int centerX, int centerY) {
        positions.clear();
        List<String> vertices = new ArrayList<>(graph.getVertices());
        int n = vertices.size();

        if (n == 0) return;

        if (n == 1) {
            positions.put(vertices.get(0), new Point2D.Double(centerX, centerY));
            return;
        }

        double radius = 80;
        for (int i = 0; i < n; i++) {
            double angle = 2 * Math.PI * i / n;
            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);
            positions.put(vertices.get(i), new Point2D.Double(x, y));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Títulos
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.setColor(Constants.TEXT_COLOR);

        if (graph1 != null) {
            g2d.drawString("Grafo G₁", 120, 100);
            drawGraph(g2d, graph1, positions1, GRAPH1_COLOR);
        }

        if (graph2 != null) {
            g2d.drawString("Grafo G₂", 420, 100);
            drawGraph(g2d, graph2, positions2, GRAPH2_COLOR);
        }

        if (resultGraph != null) {
            g2d.drawString("Resultado: " + (currentOperation != null ? currentOperation : ""), 220, 350);
            drawGraph(g2d, resultGraph, positionsResult, RESULT_COLOR);
        }

        g2d.dispose();
    }

    private void drawGraph(Graphics2D g2d, GraphOperations.Graph graph,
                           Map<String, Point2D> positions, Color color) {
        // Dibujar aristas
        g2d.setColor(color.darker());
        g2d.setStroke(new BasicStroke(2));

        for (GraphOperations.Edge edge : graph.getEdges()) {
            Point2D fromPos = positions.get(edge.from);
            Point2D toPos = positions.get(edge.to);

            if (fromPos != null && toPos != null) {
                g2d.drawLine((int) fromPos.getX(), (int) fromPos.getY(),
                        (int) toPos.getX(), (int) toPos.getY());
            }
        }

        // Dibujar vértices
        for (String vertex : graph.getVertices()) {
            Point2D pos = positions.get(vertex);
            if (pos != null) {
                int x = (int) pos.getX();
                int y = (int) pos.getY();

                // Círculo del vértice
                g2d.setColor(color);
                g2d.fillOval(x - VERTEX_RADIUS, y - VERTEX_RADIUS,
                        VERTEX_RADIUS * 2, VERTEX_RADIUS * 2);

                g2d.setColor(color.darker());
                g2d.drawOval(x - VERTEX_RADIUS, y - VERTEX_RADIUS,
                        VERTEX_RADIUS * 2, VERTEX_RADIUS * 2);

                // Etiqueta del vértice
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Arial", Font.BOLD, 10));
                FontMetrics fm = g2d.getFontMetrics();

                String label = vertex.length() > 6 ? vertex.substring(0, 6) + "..." : vertex;
                int textX = x - fm.stringWidth(label) / 2;
                int textY = y + fm.getAscent() / 2;
                g2d.drawString(label, textX, textY);
            }
        }
    }
}
