package utils;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class BlockVisualization extends JPanel {
    private BlockStructure blockStructure;
    private int highlightBlock = -1;
    private int highlightPosition = -1;
    private List<BlockStructure.SearchStep> searchSteps;
    private int currentStep = -1;

    private static final int BLOCK_WIDTH = 120;
    private static final int BLOCK_HEIGHT = 150;
    private static final int ELEMENT_HEIGHT = 20;
    private static final int BLOCK_SPACING = 10;

    public BlockVisualization() {
        setBackground(new Color(248, 249, 250));
        setBorder(BorderFactory.createLoweredBevelBorder());
        setPreferredSize(new Dimension(800, 200));
    }

    public void setBlockStructure(BlockStructure blockStructure) {
        this.blockStructure = blockStructure;
        repaint();
    }

    public void highlightPosition(int blockIndex, int position) {
        this.highlightBlock = blockIndex;
        this.highlightPosition = position;
        repaint();
    }

    public void showSearchSteps(List<BlockStructure.SearchStep> steps) {
        this.searchSteps = steps;
        this.currentStep = -1;
        repaint();
    }

    public void setCurrentStep(int step) {
        this.currentStep = step;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (blockStructure == null || !blockStructure.isInitialized()) {
            g.setColor(Constants.TEXT_COLOR);
            g.setFont(Constants.SUBTITLE_FONT);
            FontMetrics fm = g.getFontMetrics();
            String message = "Inicialice la estructura por bloques para visualizar";
            int x = (getWidth() - fm.stringWidth(message)) / 2;
            int y = getHeight() / 2;
            g.drawString(message, x, y);
            return;
        }

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int startX = 10;
        int startY = 20;

        // Dibujar información general
        g2d.setColor(Constants.TEXT_COLOR);
        g2d.setFont(Constants.BODY_FONT);
        g2d.drawString(String.format("Bloques: %d | Elementos por bloque: %d | Total: %d",
                blockStructure.getNumBlocks(), blockStructure.getElementsPerBlock(),
                blockStructure.getTotalElements()), startX, startY);

        startY += 30;

        // Dibujar bloques
        Object[][] blocks = blockStructure.getBlocks();
        int currentX = startX;

        for (int blockIndex = 0; blockIndex < blocks.length; blockIndex++) {
            drawBlock(g2d, blocks[blockIndex], blockIndex, currentX, startY);
            currentX += BLOCK_WIDTH + BLOCK_SPACING;
        }

        g2d.dispose();
    }

    private void drawBlock(Graphics2D g2d, Object[] block, int blockIndex, int x, int y) {
        // Determinar color del bloque
        Color blockColor = Constants.BACKGROUND_COLOR;
        Color borderColor = Constants.TEXT_COLOR;

        if (blockIndex == highlightBlock) {
            borderColor = Constants.PRIMARY_COLOR;
        }

        // Verificar si el bloque está siendo accedido en el paso actual
        if (searchSteps != null && currentStep >= 0 && currentStep < searchSteps.size()) {
            BlockStructure.SearchStep step = searchSteps.get(currentStep);
            if (step.blockIndex == blockIndex && step.type == BlockStructure.SearchStep.Type.BLOCK_ACCESS) {
                blockColor = Constants.INFO_COLOR;
                borderColor = Constants.INFO_COLOR;
            }
        }

        // Dibujar contenedor del bloque
        g2d.setColor(blockColor);
        g2d.fillRect(x, y, BLOCK_WIDTH, BLOCK_HEIGHT);
        g2d.setColor(borderColor);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(x, y, BLOCK_WIDTH, BLOCK_HEIGHT);

        // Encabezado del bloque
        g2d.setColor(Constants.TEXT_COLOR);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        FontMetrics fm = g2d.getFontMetrics();
        String header = "Bloque " + (blockIndex + 1);
        int headerX = x + (BLOCK_WIDTH - fm.stringWidth(header)) / 2;
        g2d.drawString(header, headerX, y + 15);

        // Línea separadora
        g2d.drawLine(x + 5, y + 20, x + BLOCK_WIDTH - 5, y + 20);

        // Dibujar elementos
        int elementY = y + 25;
        for (int i = 0; i < block.length; i++) {
            drawBlockElement(g2d, block[i], blockIndex, i, x + 5, elementY, BLOCK_WIDTH - 10);
            elementY += ELEMENT_HEIGHT + 2;
        }
    }

    private void drawBlockElement(Graphics2D g2d, Object element, int blockIndex, int position,
                                  int x, int y, int width) {

        Color elementColor = Constants.BACKGROUND_COLOR;
        Color textColor = Constants.TEXT_COLOR;
        Color borderColor = new Color(200, 200, 200);

        if (element != null) {
            elementColor = Constants.SUCCESS_COLOR;
            textColor = Color.WHITE;
            borderColor = Constants.SUCCESS_COLOR;
        }

        // Highlighting
        if (blockIndex == highlightBlock && position == highlightPosition) {
            elementColor = Constants.WARNING_COLOR;
            textColor = Color.BLACK;
            borderColor = Constants.WARNING_COLOR;
        }

        // Verificar si el elemento está siendo accedido en el paso actual
        if (searchSteps != null && currentStep >= 0 && currentStep < searchSteps.size()) {
            BlockStructure.SearchStep step = searchSteps.get(currentStep);
            if (step.blockIndex == blockIndex && step.position == position &&
                    step.type == BlockStructure.SearchStep.Type.ELEMENT_ACCESS) {
                elementColor = Constants.INFO_COLOR;
                textColor = Color.WHITE;
                borderColor = Constants.INFO_COLOR;
            }
        }

        // Dibujar elemento
        g2d.setColor(elementColor);
        g2d.fillRect(x, y, width, ELEMENT_HEIGHT);
        g2d.setColor(borderColor);
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRect(x, y, width, ELEMENT_HEIGHT);

        // Texto del elemento
        g2d.setColor(textColor);
        g2d.setFont(Constants.MONO_FONT);
        FontMetrics fm = g2d.getFontMetrics();

        String text = element == null ? "---" : element.toString();
        int textX = x + (width - fm.stringWidth(text)) / 2;
        int textY = y + (ELEMENT_HEIGHT + fm.getAscent()) / 2;
        g2d.drawString(text, textX, textY);

        // Número de posición
        g2d.setFont(new Font("Arial", Font.PLAIN, 9));
        g2d.setColor(Constants.TEXT_COLOR);
        String posText = String.valueOf(position + 1);
        g2d.drawString(posText, x - 12, y + 12);
    }

    public void clearHighlight() {
        highlightBlock = -1;
        highlightPosition = -1;
        searchSteps = null;
        currentStep = -1;
        repaint();
    }
}
