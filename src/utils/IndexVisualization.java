package utils;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class IndexVisualization extends JPanel {
    private IndexStructure indexStructure;
    private int highlightLevel = -1;
    private int highlightPosition = -1;
    private List<IndexStructure.SearchStep> searchSteps;
    private int currentStep = -1;

    private static final int ENTRY_WIDTH = 120;
    private static final int ENTRY_HEIGHT = 30;
    private static final int LEVEL_SPACING = 60;
    private static final int ENTRY_SPACING = 10;

    public IndexVisualization() {
        setBackground(new Color(248, 249, 250));
        setBorder(BorderFactory.createLoweredBevelBorder());
        setPreferredSize(new Dimension(800, 400));
    }

    public void setIndexStructure(IndexStructure structure) {
        this.indexStructure = structure;
        repaint();
    }

    public void highlightEntry(int level, int position) {
        this.highlightLevel = level;
        this.highlightPosition = position;
        repaint();
    }

    public void showSearchSteps(List<IndexStructure.SearchStep> steps) {
        this.searchSteps = steps;
        this.currentStep = -1;
        repaint();
    }

    public void setCurrentStep(int step) {
        this.currentStep = step;
        repaint();
    }

    public void clearHighlight() {
        this.highlightLevel = -1;
        this.highlightPosition = -1;
        this.searchSteps = null;
        this.currentStep = -1;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (indexStructure == null) {
            g.setColor(Constants.TEXT_COLOR);
            g.setFont(Constants.SUBTITLE_FONT);
            FontMetrics fm = g.getFontMetrics();
            String message = "Inicialice la estructura de índices para visualizar";
            int x = (getWidth() - fm.stringWidth(message)) / 2;
            int y = getHeight() / 2;
            g.drawString(message, x, y);
            return;
        }

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Información general
        drawHeader(g2d);

        // Estructura de índices
        if (indexStructure.isMultilevel()) {
            drawMultilevelIndex(g2d);
        } else {
            drawSingleLevelIndex(g2d);
        }

        g2d.dispose();
    }

    private void drawHeader(Graphics2D g2d) {
        g2d.setColor(Constants.TEXT_COLOR);
        g2d.setFont(Constants.BODY_FONT);

        String info = String.format("Tipo: %s | Entradas: %d | Niveles: %d",
                indexStructure.getIndexType(),
                indexStructure.getEntryCount(),
                indexStructure.getLevelCount());

        g2d.drawString(info, 10, 20);
    }

    private void drawSingleLevelIndex(Graphics2D g2d) {
        List<IndexStructure.IndexEntry> entries = indexStructure.getEntries();
        int startY = 50;

        drawLevelLabel(g2d, 0, startY - 20);
        drawIndexLevel(g2d, entries, 0, startY);
    }

    private void drawMultilevelIndex(Graphics2D g2d) {
        List<List<IndexStructure.IndexEntry>> levels = indexStructure.getLevels();
        int startY = 50;

        // Dibujar desde el nivel más alto hacia abajo
        for (int level = levels.size() - 1; level >= 0; level--) {
            int y = startY + (levels.size() - 1 - level) * LEVEL_SPACING;

            drawLevelLabel(g2d, level, y - 20);
            drawIndexLevel(g2d, levels.get(level), level, y);

            // Dibujar conexiones al siguiente nivel
            if (level > 0) {
                drawLevelConnections(g2d, level, y, y + LEVEL_SPACING);
            }
        }
    }

    private void drawLevelLabel(Graphics2D g2d, int level, int y) {
        g2d.setColor(Constants.TEXT_COLOR);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        String label = level == 0 ? "Nivel Hoja" : "Nivel " + level;
        g2d.drawString(label, 10, y);
    }

    private void drawIndexLevel(Graphics2D g2d, List<IndexStructure.IndexEntry> entries, int level, int y) {
        int startX = 100;

        for (int i = 0; i < entries.size(); i++) {
            int x = startX + i * (ENTRY_WIDTH + ENTRY_SPACING);
            drawIndexEntry(g2d, entries.get(i), level, i, x, y);
        }
    }

    private void drawIndexEntry(Graphics2D g2d, IndexStructure.IndexEntry entry, int level, int position, int x, int y) {
        // Determinar color de la entrada
        Color entryColor = Constants.BACKGROUND_COLOR;
        Color borderColor = Constants.TEXT_COLOR;
        Color textColor = Constants.TEXT_COLOR;

        if (level == highlightLevel && position == highlightPosition) {
            entryColor = Constants.WARNING_COLOR;
            textColor = Color.BLACK;
        } else if (searchSteps != null && currentStep >= 0 && currentStep < searchSteps.size()) {
            IndexStructure.SearchStep step = searchSteps.get(currentStep);
            if (step.level == level && step.position == position) {
                entryColor = Constants.INFO_COLOR;
                textColor = Color.WHITE;
            }
        }

        // Dibujar entrada
        g2d.setColor(entryColor);
        g2d.fillRect(x, y, ENTRY_WIDTH, ENTRY_HEIGHT);
        g2d.setColor(borderColor);
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRect(x, y, ENTRY_WIDTH, ENTRY_HEIGHT);

        // Texto de la entrada
        g2d.setColor(textColor);
        g2d.setFont(Constants.MONO_FONT);
        FontMetrics fm = g2d.getFontMetrics();

        String keyText = entry.key.toString();
        if (keyText.length() > 8) keyText = keyText.substring(0, 8) + "...";

        int textX = x + (ENTRY_WIDTH - fm.stringWidth(keyText)) / 2;
        int textY = y + (ENTRY_HEIGHT + fm.getAscent()) / 2 - 5;
        g2d.drawString(keyText, textX, textY);

        // Información adicional
        String pointerText = "";
        if (entry.recordPointers.isEmpty()) {
            pointerText = "B" + entry.blockPointer;
        } else {
            pointerText = entry.recordPointers.size() + " rec";
        }

        g2d.setFont(new Font("Arial", Font.PLAIN, 9));
        fm = g2d.getFontMetrics();
        int pointerX = x + (ENTRY_WIDTH - fm.stringWidth(pointerText)) / 2;
        int pointerY = y + ENTRY_HEIGHT - 5;
        g2d.drawString(pointerText, pointerX, pointerY);
    }

    private void drawLevelConnections(Graphics2D g2d, int fromLevel, int fromY, int toY) {
        // Dibujar líneas de conexión entre niveles
        g2d.setColor(new Color(150, 150, 150));
        g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{3}, 0));

        int startX = 100;
        int connectionSpacing = indexStructure.getMaxEntriesPerLevel();

        List<List<IndexStructure.IndexEntry>> levels = indexStructure.getLevels();
        if (fromLevel < levels.size()) {
            List<IndexStructure.IndexEntry> currentLevel = levels.get(fromLevel);

            for (int i = 0; i < currentLevel.size(); i += connectionSpacing) {
                int x = startX + i * (ENTRY_WIDTH + ENTRY_SPACING) + ENTRY_WIDTH / 2;
                int parentX = startX + (i / connectionSpacing) * (ENTRY_WIDTH + ENTRY_SPACING) + ENTRY_WIDTH / 2;

                g2d.drawLine(x, fromY + ENTRY_HEIGHT, parentX, toY);
            }
        }
    }
}
