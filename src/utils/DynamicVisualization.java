package utils;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DynamicVisualization extends JPanel {
    private DynamicStructure dynamicStructure;
    private int highlightBucket = -1;

    private static final int BUCKET_WIDTH = 80;
    private static final int BUCKET_HEIGHT = 100;
    private static final int BUCKET_SPACING = 10;
    private static final int ELEMENT_HEIGHT = 18;

    public DynamicVisualization() {
        setBackground(new Color(248, 249, 250));
        setBorder(BorderFactory.createLoweredBevelBorder());
        setPreferredSize(new Dimension(800, 300));
    }

    public void setDynamicStructure(DynamicStructure structure) {
        this.dynamicStructure = structure;
        repaint();
    }

    public void highlightBucket(int bucketIndex) {
        this.highlightBucket = bucketIndex;
        repaint();
    }

    public void clearHighlight() {
        this.highlightBucket = -1;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (dynamicStructure == null) {
            g.setColor(Constants.TEXT_COLOR);
            g.setFont(Constants.SUBTITLE_FONT);
            FontMetrics fm = g.getFontMetrics();
            String message = "Inicialice la estructura dinámica para visualizar";
            int x = (getWidth() - fm.stringWidth(message)) / 2;
            int y = getHeight() / 2;
            g.drawString(message, x, y);
            return;
        }

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Información general
        drawHeader(g2d);

        // Buckets
        drawBuckets(g2d);

        g2d.dispose();
    }

    private void drawHeader(Graphics2D g2d) {
        g2d.setColor(Constants.TEXT_COLOR);
        g2d.setFont(Constants.BODY_FONT);

        int currentSize = dynamicStructure.getCurrentTableSize();
        int level = dynamicStructure.getCurrentLevel();
        double loadFactor = dynamicStructure.getLoadFactor();

        String info = String.format("Nivel: %d | Tamaño: %d | Elementos: %d | Factor de carga: %.3f",
                level, currentSize, dynamicStructure.getTotalElements(), loadFactor);

        g2d.drawString(info, 10, 20);

        // Barra de factor de carga
        drawLoadFactorBar(g2d, loadFactor);
    }

    private void drawLoadFactorBar(Graphics2D g2d, double loadFactor) {
        int barX = 10;
        int barY = 30;
        int barWidth = 200;
        int barHeight = 15;

        // Fondo
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fillRect(barX, barY, barWidth, barHeight);

        // Nivel actual
        Color barColor = loadFactor > 0.75 ? Constants.ERROR_COLOR :
                loadFactor > 0.5 ? Constants.WARNING_COLOR : Constants.SUCCESS_COLOR;
        g2d.setColor(barColor);
        int fillWidth = (int) (barWidth * Math.min(loadFactor, 1.0));
        g2d.fillRect(barX, barY, fillWidth, barHeight);

        // Borde
        g2d.setColor(Constants.TEXT_COLOR);
        g2d.drawRect(barX, barY, barWidth, barHeight);

        // Línea de umbral (0.75)
        g2d.setColor(Constants.ERROR_COLOR);
        int thresholdX = barX + (int) (barWidth * 0.75);
        g2d.drawLine(thresholdX, barY, thresholdX, barY + barHeight);

        // Etiqueta
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        g2d.setColor(Constants.TEXT_COLOR);
        g2d.drawString("Factor de carga (umbral: 0.75)", barX + barWidth + 10, barY + 12);
    }

    private void drawBuckets(Graphics2D g2d) {
        List<List<Object>> buckets = dynamicStructure.getBuckets();
        int startX = 10;
        int startY = 60;

        int bucketsPerRow = Math.max(1, (getWidth() - 20) / (BUCKET_WIDTH + BUCKET_SPACING));

        for (int i = 0; i < buckets.size(); i++) {
            int row = i / bucketsPerRow;
            int col = i % bucketsPerRow;

            int x = startX + col * (BUCKET_WIDTH + BUCKET_SPACING);
            int y = startY + row * (BUCKET_HEIGHT + BUCKET_SPACING + 20);

            drawBucket(g2d, buckets.get(i), i, x, y);
        }
    }

    private void drawBucket(Graphics2D g2d, List<Object> bucket, int bucketIndex, int x, int y) {
        // Color del bucket
        Color bucketColor = Constants.BACKGROUND_COLOR;
        Color borderColor = Constants.TEXT_COLOR;

        if (bucketIndex == highlightBucket) {
            bucketColor = Constants.INFO_COLOR;
            borderColor = Constants.INFO_COLOR;
        } else if (!bucket.isEmpty()) {
            bucketColor = new Color(230, 247, 255);
        }

        // Dibujar contenedor
        g2d.setColor(bucketColor);
        g2d.fillRect(x, y, BUCKET_WIDTH, BUCKET_HEIGHT);
        g2d.setColor(borderColor);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(x, y, BUCKET_WIDTH, BUCKET_HEIGHT);

        // Encabezado del bucket
        g2d.setColor(Constants.TEXT_COLOR);
        g2d.setFont(new Font("Arial", Font.BOLD, 11));
        FontMetrics fm = g2d.getFontMetrics();
        String header = "B" + bucketIndex;
        int headerX = x + (BUCKET_WIDTH - fm.stringWidth(header)) / 2;
        g2d.drawString(header, headerX, y + 15);

        // Línea separadora
        g2d.drawLine(x + 5, y + 20, x + BUCKET_WIDTH - 5, y + 20);

        // Elementos
        g2d.setFont(Constants.MONO_FONT);
        int elementY = y + 35;

        for (int i = 0; i < bucket.size() && elementY < y + BUCKET_HEIGHT - 5; i++) {
            Object element = bucket.get(i);

            // Fondo del elemento
            g2d.setColor(Constants.SUCCESS_COLOR);
            g2d.fillRect(x + 5, elementY - 12, BUCKET_WIDTH - 10, ELEMENT_HEIGHT - 2);

            // Texto del elemento
            g2d.setColor(Color.WHITE);
            String text = element.toString();
            if (text.length() > 8) text = text.substring(0, 8);

            FontMetrics elemFm = g2d.getFontMetrics();
            int textX = x + (BUCKET_WIDTH - elemFm.stringWidth(text)) / 2;
            g2d.drawString(text, textX, elementY);

            elementY += ELEMENT_HEIGHT;
        }

        // Indicador de overflow
        if (bucket.size() > (BUCKET_HEIGHT - 40) / ELEMENT_HEIGHT) {
            g2d.setColor(Constants.WARNING_COLOR);
            g2d.setFont(new Font("Arial", Font.BOLD, 10));
            g2d.drawString("...", x + BUCKET_WIDTH - 20, y + BUCKET_HEIGHT - 10);
        }

        // Contador de elementos
        g2d.setColor(Constants.TEXT_COLOR);
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        String count = "(" + bucket.size() + ")";
        int countX = x + (BUCKET_WIDTH - g2d.getFontMetrics().stringWidth(count)) / 2;
        g2d.drawString(count, countX, y + BUCKET_HEIGHT + 15);
    }
}
