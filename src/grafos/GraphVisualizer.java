package grafos;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * Clase que se encarga de la visualización gráfica de los grafos.
 * Permite pan, zoom y diferentes tipos de layout.
 */
public class GraphVisualizer extends JPanel {

    private Graph graph;
    private boolean isGridLayout;
    private Map<String, Point> nodePositions;
    private double scale = 1.0;
    private Point panOffset = new Point(0, 0);
    private Point lastMousePosition;
    private boolean isDragging = false;

    // Configuración visual armonizada con la aplicación principal
    private static final int NODE_RADIUS = 18;
    private static final Color NODE_BORDER_COLOR = new Color(33, 128, 141); // Color principal de la aplicación
    private static final Color EDGE_COLOR = new Color(100, 116, 139); // Color secundario
    private static final Color TEXT_COLOR = new Color(51, 51, 51); // Color de texto principal
    private static final Font NODE_FONT = new Font("Arial", Font.BOLD, 11);
    private static final Stroke EDGE_STROKE = new BasicStroke(2.5f);
    private static final Stroke NODE_STROKE = new BasicStroke(2.0f);

    public GraphVisualizer() {
        setBackground(new Color(248, 250, 252)); // Fondo armonizado
        setupMouseListeners();
        nodePositions = new HashMap<>();
    }

    /**
     * Establece el grafo a visualizar.
     * @param graph Grafo a visualizar
     * @param isGridLayout true para layout de grilla, false para layout circular
     */
    public void setGraph(Graph graph, boolean isGridLayout) {
        this.graph = graph;
        this.isGridLayout = isGridLayout;
        calculateLayout();
        repaint();
    }

    /**
     * Reinicia la vista (zoom y pan).
     */
    public void resetView() {
        scale = 1.0;
        panOffset = new Point(0, 0);
        calculateLayout();
        repaint();
    }

    /**
     * Calcula las posiciones de los nodos según el tipo de layout.
     */
    private void calculateLayout() {
        if (graph == null || graph.getNodes().isEmpty()) {
            return;
        }

        nodePositions.clear();

        int width = getWidth();
        int height = getHeight();

        if (width == 0 || height == 0) {
            width = 800;
            height = 400;
        }

        List<String> nodes = graph.getNodes();
        int nodeCount = nodes.size();

        if (isGridLayout && nodeCount > 1) {
            calculateGridLayout(nodes, width, height);
        } else {
            calculateCircularLayout(nodes, width, height);
        }
    }

    /**
     * Calcula el layout en grilla para productos cartesianos.
     */
    private void calculateGridLayout(List<String> nodes, int width, int height) {
        // Intentar detectar las dimensiones de la grilla
        Set<String> firstComponents = new HashSet<>();
        Set<String> secondComponents = new HashSet<>();

        for (String node : nodes) {
            if (node.length() >= 2) {
                firstComponents.add(node.substring(0, 1));
                secondComponents.add(node.substring(1));
            }
        }

        List<String> firstList = new ArrayList<>(firstComponents);
        List<String> secondList = new ArrayList<>(secondComponents);

        Collections.sort(firstList);
        Collections.sort(secondList);

        int cols = firstList.size();
        int rows = secondList.size();

        if (cols == 0 || rows == 0) {
            calculateCircularLayout(nodes, width, height);
            return;
        }

        int margin = 80;
        int usableWidth = width - 2 * margin;
        int usableHeight = height - 2 * margin;

        double xSpacing = cols > 1 ? (double) usableWidth / (cols - 1) : 0;
        double ySpacing = rows > 1 ? (double) usableHeight / (rows - 1) : 0;

        for (String node : nodes) {
            if (node.length() >= 2) {
                String first = node.substring(0, 1);
                String second = node.substring(1);

                int col = firstList.indexOf(first);
                int row = secondList.indexOf(second);

                if (col >= 0 && row >= 0) {
                    int x = margin + (cols > 1 ? (int) (col * xSpacing) : usableWidth / 2);
                    int y = margin + (rows > 1 ? (int) (row * ySpacing) : usableHeight / 2);

                    nodePositions.put(node, new Point(x, y));
                }
            }
        }
    }

    /**
     * Calcula el layout circular para operaciones de conjuntos.
     */
    private void calculateCircularLayout(List<String> nodes, int width, int height) {
        int centerX = width / 2;
        int centerY = height / 2;

        int margin = 80;
        int radiusX = Math.max(50, (width - 2 * margin) / 2);
        int radiusY = Math.max(50, (height - 2 * margin) / 2);

        if (nodes.size() == 1) {
            nodePositions.put(nodes.get(0), new Point(centerX, centerY));
            return;
        }

        for (int i = 0; i < nodes.size(); i++) {
            double angle = 2 * Math.PI * i / nodes.size() - Math.PI / 2;
            int x = centerX + (int) (radiusX * Math.cos(angle));
            int y = centerY + (int) (radiusY * Math.sin(angle));

            nodePositions.put(nodes.get(i), new Point(x, y));
        }
    }

    /**
     * Configura los listeners del mouse para pan y zoom.
     */
    private void setupMouseListeners() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastMousePosition = e.getPoint();
                isDragging = true;
                setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isDragging = false;
                setCursor(Cursor.getDefaultCursor());
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (isDragging && lastMousePosition != null) {
                    int dx = e.getX() - lastMousePosition.x;
                    int dy = e.getY() - lastMousePosition.y;

                    panOffset.x += dx;
                    panOffset.y += dy;

                    lastMousePosition = e.getPoint();
                    repaint();
                }
            }
        });

        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                double zoomFactor = 1.1;
                double oldScale = scale;

                if (e.getWheelRotation() < 0) {
                    scale *= zoomFactor;
                } else {
                    scale /= zoomFactor;
                }

                scale = Math.max(0.1, Math.min(5.0, scale));

                // Ajustar el pan para hacer zoom hacia la posición del mouse
                Point mousePos = e.getPoint();
                double scaleDiff = scale - oldScale;

                panOffset.x -= (int) ((mousePos.x - panOffset.x) * scaleDiff / oldScale);
                panOffset.y -= (int) ((mousePos.y - panOffset.y) * scaleDiff / oldScale);

                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (graph == null || graph.getNodes().isEmpty()) {
            // Mostrar mensaje cuando no hay grafo
            g.setColor(new Color(100, 116, 139));
            g.setFont(new Font("Arial", Font.PLAIN, 14));
            FontMetrics fm = g.getFontMetrics();
            String message = "Seleccione una operación para ver el grafo resultante";
            int x = (getWidth() - fm.stringWidth(message)) / 2;
            int y = getHeight() / 2;
            g.drawString(message, x, y);
            return;
        }

        // Recalcular layout si es necesario
        if (nodePositions.isEmpty()) {
            calculateLayout();
        }

        Graphics2D g2d = (Graphics2D) g.create();

        // Configurar antialiasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Aplicar transformaciones
        g2d.translate(panOffset.x, panOffset.y);
        g2d.scale(scale, scale);

        // Dibujar aristas
        drawEdges(g2d);

        // Dibujar nodos
        drawNodes(g2d);

        g2d.dispose();
    }

    /**
     * Dibuja las aristas del grafo.
     */
    private void drawEdges(Graphics2D g2d) {
        if (graph.getEdges().isEmpty()) {
            return;
        }

        g2d.setColor(EDGE_COLOR);
        g2d.setStroke(EDGE_STROKE);

        for (String[] edge : graph.getEdges()) {
            Point p1 = nodePositions.get(edge[0]);
            Point p2 = nodePositions.get(edge[1]);

            if (p1 != null && p2 != null) {
                g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
            }
        }
    }

    /**
     * Dibuja los nodos del grafo con efectos visuales mejorados.
     */
    private void drawNodes(Graphics2D g2d) {
        g2d.setFont(NODE_FONT);
        FontMetrics fm = g2d.getFontMetrics();

        for (String node : graph.getNodes()) {
            Point pos = nodePositions.get(node);
            if (pos == null) continue;

            // Crear gradiente para el nodo
            RadialGradientPaint gradient = new RadialGradientPaint(
                    pos.x, pos.y - NODE_RADIUS/3, NODE_RADIUS * 1.2f,
                    new float[]{0f, 1f},
                    new Color[]{Color.WHITE, new Color(245, 245, 245)}
            );

            // Sombra del nodo
            g2d.setColor(new Color(0, 0, 0, 30));
            g2d.fillOval(pos.x - NODE_RADIUS + 2, pos.y - NODE_RADIUS + 2,
                    2 * NODE_RADIUS, 2 * NODE_RADIUS);

            // Círculo del nodo con gradiente
            g2d.setPaint(gradient);
            g2d.fillOval(pos.x - NODE_RADIUS, pos.y - NODE_RADIUS,
                    2 * NODE_RADIUS, 2 * NODE_RADIUS);

            // Borde del nodo
            g2d.setColor(NODE_BORDER_COLOR);
            g2d.setStroke(NODE_STROKE);
            g2d.drawOval(pos.x - NODE_RADIUS, pos.y - NODE_RADIUS,
                    2 * NODE_RADIUS, 2 * NODE_RADIUS);

            // Dibujar etiqueta del nodo con sombra
            g2d.setColor(TEXT_COLOR);
            String label = isGridLayout ? formatGridLabel(node) : node.toUpperCase();

            int textWidth = fm.stringWidth(label);
            int textHeight = fm.getHeight();
            int textX = pos.x - textWidth / 2;
            int textY = pos.y + textHeight / 4;

            // Sombra del texto
            g2d.setColor(new Color(0, 0, 0, 50));
            g2d.drawString(label, textX + 1, textY + 1);

            // Texto principal
            g2d.setColor(TEXT_COLOR);
            g2d.drawString(label, textX, textY);
        }
    }

    /**
     * Formatea la etiqueta para el layout de grilla.
     */
    private String formatGridLabel(String node) {
        if (node.length() >= 2) {
            return "(" + node.charAt(0) + "," + node.substring(1) + ")";
        }
        return node;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(800, 400);
    }
}
