package utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.HashMap;
import java.util.Map;

public class TreeVisualization extends JPanel {
    private TreeNode root;
    private Map<TreeNode, Point> nodePositions;
    private double scale = 1.0;
    private Point offset = new Point(0, 0);
    private Point lastMousePos = new Point();
    private TreeNode selectedNode;

    private static final int NODE_RADIUS = 25;
    private static final int LEVEL_HEIGHT = 80;
    private static final int MIN_HORIZONTAL_SPACING = 50;

    public TreeVisualization() {
        setBackground(new Color(248, 249, 250));
        setBorder(BorderFactory.createLoweredBevelBorder());
        setPreferredSize(new Dimension(800, 400));

        nodePositions = new HashMap<>();

        // Mouse listeners para interactividad
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastMousePos = e.getPoint();
                selectedNode = getNodeAt(e.getPoint());
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                selectedNode = null;
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    int dx = e.getX() - lastMousePos.x;
                    int dy = e.getY() - lastMousePos.y;
                    offset.translate(dx, dy);
                    lastMousePos = e.getPoint();
                    repaint();
                }
            }
        });

        addMouseWheelListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                double oldScale = scale;
                scale += e.getWheelRotation() * -0.1;
                scale = Math.max(0.5, Math.min(3.0, scale));

                if (scale != oldScale) {
                    repaint();
                }
            }
        });
    }

    public void setRoot(TreeNode root) {
        this.root = root;
        calculateNodePositions();
        repaint();
    }

    public TreeNode getRoot() {
        return root;
    }

    private void calculateNodePositions() {
        nodePositions.clear();
        if (root != null) {
            int width = getWidth();
            int startX = width / 2;
            calculatePositions(root, startX, 50, width / 4);
        }
    }

    private void calculatePositions(TreeNode node, int x, int y, int horizontalSpacing) {
        if (node == null) return;

        nodePositions.put(node, new Point(x, y));

        if (node.left != null) {
            calculatePositions(node.left, x - horizontalSpacing, y + LEVEL_HEIGHT,
                    Math.max(MIN_HORIZONTAL_SPACING, horizontalSpacing / 2));
        }

        if (node.right != null) {
            calculatePositions(node.right, x + horizontalSpacing, y + LEVEL_HEIGHT,
                    Math.max(MIN_HORIZONTAL_SPACING, horizontalSpacing / 2));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (root == null) {
            g.setColor(Constants.TEXT_COLOR);
            g.setFont(Constants.SUBTITLE_FONT);
            FontMetrics fm = g.getFontMetrics();
            String message = "Construya el árbol para visualizar";
            int x = (getWidth() - fm.stringWidth(message)) / 2;
            int y = getHeight() / 2;
            g.drawString(message, x, y);
            return;
        }

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Aplicar transformaciones
        g2d.translate(offset.x, offset.y);
        g2d.scale(scale, scale);

        // Recalcular posiciones si es necesario
        if (nodePositions.isEmpty()) {
            calculateNodePositions();
        }

        // Dibujar enlaces primero
        drawLinks(g2d);

        // Dibujar nodos encima
        drawNodes(g2d);

        g2d.dispose();

        // Dibujar información de control en la esquina
        drawControlInfo(g);
    }

    private void drawLinks(Graphics2D g2d) {
        if (root == null) return;

        drawLinksRecursive(g2d, root);
    }

    private void drawLinksRecursive(Graphics2D g2d, TreeNode node) {
        Point nodePos = nodePositions.get(node);
        if (nodePos == null) return;

        g2d.setStroke(new BasicStroke(2));

        if (node.left != null) {
            Point leftPos = nodePositions.get(node.left);
            if (leftPos != null) {
                g2d.setColor(new Color(52, 152, 219)); // Azul para izquierda (0)
                g2d.drawLine(nodePos.x, nodePos.y + NODE_RADIUS,
                        leftPos.x, leftPos.y - NODE_RADIUS);

                // Etiqueta "0"
                int midX = (nodePos.x + leftPos.x) / 2 - 10;
                int midY = (nodePos.y + leftPos.y) / 2;
                g2d.setFont(new Font("Arial", Font.BOLD, 12));
                g2d.drawString("0", midX, midY);
            }
            drawLinksRecursive(g2d, node.left);
        }

        if (node.right != null) {
            Point rightPos = nodePositions.get(node.right);
            if (rightPos != null) {
                g2d.setColor(new Color(231, 76, 60)); // Rojo para derecha (1)
                g2d.drawLine(nodePos.x, nodePos.y + NODE_RADIUS,
                        rightPos.x, rightPos.y - NODE_RADIUS);

                // Etiqueta "1"
                int midX = (nodePos.x + rightPos.x) / 2 + 5;
                int midY = (nodePos.y + rightPos.y) / 2;
                g2d.setFont(new Font("Arial", Font.BOLD, 12));
                g2d.drawString("1", midX, midY);
            }
            drawLinksRecursive(g2d, node.right);
        }
    }

    private void drawNodes(Graphics2D g2d) {
        if (root == null) return;

        drawNodesRecursive(g2d, root);
    }

    private void drawNodesRecursive(Graphics2D g2d, TreeNode node) {
        Point pos = nodePositions.get(node);
        if (pos == null) return;

        // Determinar color del nodo
        Color nodeColor = Constants.BACKGROUND_COLOR;
        Color borderColor = Constants.TEXT_COLOR;
        Color textColor = Constants.TEXT_COLOR;

        if (node == selectedNode) {
            nodeColor = Constants.PRIMARY_COLOR;
            textColor = Color.WHITE;
        } else if (node.data != null) {
            if (node.isLeaf) {
                nodeColor = Constants.SUCCESS_COLOR;
                textColor = Color.WHITE;
            } else {
                nodeColor = Constants.INFO_COLOR;
                textColor = Color.WHITE;
            }
        }

        // Dibujar círculo del nodo
        g2d.setColor(nodeColor);
        g2d.fillOval(pos.x - NODE_RADIUS, pos.y - NODE_RADIUS,
                NODE_RADIUS * 2, NODE_RADIUS * 2);

        g2d.setColor(borderColor);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawOval(pos.x - NODE_RADIUS, pos.y - NODE_RADIUS,
                NODE_RADIUS * 2, NODE_RADIUS * 2);

        // Dibujar texto del nodo
        g2d.setColor(textColor);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        FontMetrics fm = g2d.getFontMetrics();

        String nodeText = node.data == null ? "" : node.toString();
        if (nodeText.length() > 3) {
            nodeText = nodeText.substring(0, 3);
        }

        int textX = pos.x - fm.stringWidth(nodeText) / 2;
        int textY = pos.y + fm.getAscent() / 2;
        g2d.drawString(nodeText, textX, textY);

        // Información adicional encima del nodo
        if (node.frequency > 0) {
            g2d.setFont(new Font("Arial", Font.PLAIN, 10));
            String freqText = "f:" + node.frequency;
            int freqX = pos.x - g2d.getFontMetrics().stringWidth(freqText) / 2;
            int freqY = pos.y - NODE_RADIUS - 5;
            g2d.drawString(freqText, freqX, freqY);
        }

        // Continuar con hijos
        if (node.left != null) {
            drawNodesRecursive(g2d, node.left);
        }
        if (node.right != null) {
            drawNodesRecursive(g2d, node.right);
        }
    }

    private void drawControlInfo(Graphics g) {
        g.setColor(new Color(0, 0, 0, 128));
        g.fillRect(5, 5, 200, 60);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 10));
        g.drawString("🖱️ Arrastrar: Mover vista", 10, 20);
        g.drawString("🖱️ Rueda: Zoom (" + String.format("%.1f", scale) + "x)", 10, 35);
        g.drawString("🖱️ Clic: Seleccionar nodo", 10, 50);
    }

    private TreeNode getNodeAt(Point point) {
        Point adjustedPoint = new Point(
                (int) ((point.x - offset.x) / scale),
                (int) ((point.y - offset.y) / scale)
        );

        for (Map.Entry<TreeNode, Point> entry : nodePositions.entrySet()) {
            Point nodePos = entry.getValue();
            double distance = Math.sqrt(Math.pow(adjustedPoint.x - nodePos.x, 2) +
                    Math.pow(adjustedPoint.y - nodePos.y, 2));
            if (distance <= NODE_RADIUS) {
                return entry.getKey();
            }
        }
        return null;
    }

    public void centerView() {
        offset = new Point(0, 0);
        scale = 1.0;
        calculateNodePositions();
        repaint();
    }

    public void highlightNode(TreeNode node) {
        selectedNode = node;
        repaint();
    }
}
