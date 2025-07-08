package utils;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;

public class GraphVisualization extends JPanel {
    private GraphStructure graph;
    private Map<String, Point2D> vertexPositions;
    private Set<String> highlightedVertices;
    private Set<GraphStructure.Edge> highlightedEdges;
    private List<Object> animationSteps;
    private int currentAnimationStep;

    private static final int VERTEX_RADIUS = 25;
    private static final int PANEL_MARGIN = 50;

    public GraphVisualization() {
        setBackground(new Color(248, 249, 250));
        setBorder(BorderFactory.createLoweredBevelBorder());
        setPreferredSize(new Dimension(800, 600));

        vertexPositions = new HashMap<>();
        highlightedVertices = new HashSet<>();
        highlightedEdges = new HashSet<>();
        animationSteps = new ArrayList<>();
        currentAnimationStep = -1;

        // Mouse listeners para interactividad
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                String clickedVertex = getVertexAt(e.getPoint());
                if (clickedVertex != null) {
                    toggleVertexHighlight(clickedVertex);
                }
            }
        });
    }

    public void setGraph(GraphStructure graph) {
        this.graph = graph;
        calculateVertexPositions();
        repaint();
    }

    public void highlightVertex(String vertex) {
        highlightedVertices.clear();
        highlightedVertices.add(vertex);
        repaint();
    }

    public void highlightVertices(Collection<String> vertices) {
        highlightedVertices.clear();
        highlightedVertices.addAll(vertices);
        repaint();
    }

    public void highlightEdge(GraphStructure.Edge edge) {
        highlightedEdges.clear();
        highlightedEdges.add(edge);
        repaint();
    }

    public void highlightEdges(Collection<GraphStructure.Edge> edges) {
        highlightedEdges.clear();
        highlightedEdges.addAll(edges);
        repaint();
    }

    public void clearHighlights() {
        highlightedVertices.clear();
        highlightedEdges.clear();
        repaint();
    }

    private void toggleVertexHighlight(String vertex) {
        if (highlightedVertices.contains(vertex)) {
            highlightedVertices.remove(vertex);
        } else {
            highlightedVertices.add(vertex);
        }
        repaint();
    }

    private void calculateVertexPositions() {
        if (graph == null || graph.getVertices().isEmpty()) return;

        vertexPositions.clear();
        List<String> vertices = graph.getVertices();
        int n = vertices.size();

        if (n == 1) {
            vertexPositions.put(vertices.get(0),
                    new Point2D.Double(getWidth() / 2.0, getHeight() / 2.0));
            return;
        }

        // Disposición circular
        double centerX = getWidth() / 2.0;
        double centerY = getHeight() / 2.0;
        double radius = Math.min(getWidth(), getHeight()) / 2.0 - PANEL_MARGIN - VERTEX_RADIUS;

        for (int i = 0; i < n; i++) {
            double angle = 2 * Math.PI * i / n;
            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);
            vertexPositions.put(vertices.get(i), new Point2D.Double(x, y));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (graph == null) {
            g.setColor(Constants.TEXT_COLOR);
            g.setFont(Constants.SUBTITLE_FONT);
            FontMetrics fm = g.getFontMetrics();
            String message = "Construya un grafo para visualizar";
            int x = (getWidth() - fm.stringWidth(message)) / 2;
            int y = getHeight() / 2;
            g.drawString(message, x, y);
            return;
        }

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Recalcular posiciones si es necesario
        if (vertexPositions.isEmpty()) {
            calculateVertexPositions();
        }

        // Dibujar aristas primero
        drawEdges(g2d);

        // Dibujar vértices encima
        drawVertices(g2d);

        // Información del grafo
        drawGraphInfo(g2d);

        g2d.dispose();
    }

    private void drawEdges(Graphics2D g2d) {
        for (GraphStructure.Edge edge : graph.getEdges()) {
            Point2D fromPos = vertexPositions.get(edge.from);
            Point2D toPos = vertexPositions.get(edge.to);

            if (fromPos == null || toPos == null) continue;

            // Color de la arista
            Color edgeColor = highlightedEdges.contains(edge) ?
                    Constants.WARNING_COLOR : Constants.TEXT_COLOR;
            g2d.setColor(edgeColor);
            g2d.setStroke(new BasicStroke(2));

            // Dibujar línea
            g2d.drawLine((int) fromPos.getX(), (int) fromPos.getY(),
                    (int) toPos.getX(), (int) toPos.getY());

            // Dibujar flecha si es dirigido
            if (graph.isDirected()) {
                drawArrow(g2d, fromPos, toPos);
            }

            // Dibujar peso si es ponderado
            if (graph.isWeighted() && edge.weight != 1.0) {
                drawWeight(g2d, fromPos, toPos, edge.weight);
            }
        }
    }

    private void drawArrow(Graphics2D g2d, Point2D from, Point2D to) {
        double angle = Math.atan2(to.getY() - from.getY(), to.getX() - from.getX());

        // Calcular punto en el borde del vértice destino
        double endX = to.getX() - VERTEX_RADIUS * Math.cos(angle);
        double endY = to.getY() - VERTEX_RADIUS * Math.sin(angle);

        // Dibujar cabeza de flecha
        double arrowLength = 15;
        double arrowAngle = Math.PI / 6;

        double x1 = endX - arrowLength * Math.cos(angle - arrowAngle);
        double y1 = endY - arrowLength * Math.sin(angle - arrowAngle);

        double x2 = endX - arrowLength * Math.cos(angle + arrowAngle);
        double y2 = endY - arrowLength * Math.sin(angle + arrowAngle);

        g2d.drawLine((int) endX, (int) endY, (int) x1, (int) y1);
        g2d.drawLine((int) endX, (int) endY, (int) x2, (int) y2);
    }

    private void drawWeight(Graphics2D g2d, Point2D from, Point2D to, double weight) {
        double midX = (from.getX() + to.getX()) / 2;
        double midY = (from.getY() + to.getY()) / 2;

        g2d.setColor(Constants.ERROR_COLOR);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));

        String weightText = String.format("%.1f", weight);
        FontMetrics fm = g2d.getFontMetrics();

        // Fondo para el peso
        int textWidth = fm.stringWidth(weightText);
        int textHeight = fm.getHeight();

        g2d.setColor(Color.WHITE);
        g2d.fillOval((int) midX - textWidth / 2 - 3, (int) midY - textHeight / 2 - 3,
                textWidth + 6, textHeight + 6);

        g2d.setColor(Constants.ERROR_COLOR);
        g2d.drawOval((int) midX - textWidth / 2 - 3, (int) midY - textHeight / 2 - 3,
                textWidth + 6, textHeight + 6);

        g2d.drawString(weightText, (int) midX - textWidth / 2,
                (int) midY + fm.getAscent() / 2);
    }

    private void drawVertices(Graphics2D g2d) {
        for (String vertex : graph.getVertices()) {
            Point2D pos = vertexPositions.get(vertex);
            if (pos == null) continue;

            int x = (int) pos.getX();
            int y = (int) pos.getY();

            // Color del vértice
            Color vertexColor = highlightedVertices.contains(vertex) ?
                    Constants.WARNING_COLOR : Constants.PRIMARY_COLOR;
            Color textColor = Color.WHITE;

            // Dibujar círculo del vértice
            g2d.setColor(vertexColor);
            g2d.fillOval(x - VERTEX_RADIUS, y - VERTEX_RADIUS,
                    VERTEX_RADIUS * 2, VERTEX_RADIUS * 2);

            g2d.setColor(Constants.TEXT_COLOR);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawOval(x - VERTEX_RADIUS, y - VERTEX_RADIUS,
                    VERTEX_RADIUS * 2, VERTEX_RADIUS * 2);

            // Dibujar etiqueta del vértice
            g2d.setColor(textColor);
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            FontMetrics fm = g2d.getFontMetrics();

            int textX = x - fm.stringWidth(vertex) / 2;
            int textY = y + fm.getAscent() / 2;
            g2d.drawString(vertex, textX, textY);
        }
    }

    private void drawGraphInfo(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 128));
        g2d.fillRect(5, 5, 250, 80);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));

        String type = graph.isDirected() ? "Dirigido" : "No dirigido";
        String weighted = graph.isWeighted() ? "Ponderado" : "No ponderado";

        g2d.drawString("Tipo: " + type + ", " + weighted, 10, 25);
        g2d.drawString("Vértices: " + graph.getVertexCount(), 10, 45);
        g2d.drawString("Aristas: " + graph.getEdgeCount(), 10, 65);
    }

    private String getVertexAt(Point point) {
        for (Map.Entry<String, Point2D> entry : vertexPositions.entrySet()) {
            Point2D pos = entry.getValue();
            double distance = Math.sqrt(Math.pow(point.x - pos.getX(), 2) +
                    Math.pow(point.y - pos.getY(), 2));
            if (distance <= VERTEX_RADIUS) {
                return entry.getKey();
            }
        }
        return null;
    }
}
