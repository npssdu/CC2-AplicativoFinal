package utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class AlgorithmWindow extends JDialog {
    protected JPanel mainPanel;
    protected JTextArea terminalArea;
    protected JScrollPane terminalScroll;
    protected DataStructure structure;
    protected JPanel visualizationPanel;

    public AlgorithmWindow(JFrame parent, String title) {
        super(parent, title, true);
        initializeWindow();
        createComponents();
        setupLayout();
        setupWindow();
    }

    private void initializeWindow() {
        setBackground(Constants.BACKGROUND_COLOR);
        setLayout(new BorderLayout());
    }

    private void createComponents() {
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Constants.BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Terminal
        terminalArea = new JTextArea(8, 80);
        terminalArea.setBackground(new Color(44, 62, 80));
        terminalArea.setForeground(new Color(236, 240, 241));
        terminalArea.setFont(Constants.MONO_FONT);
        terminalArea.setEditable(false);
        terminalArea.setLineWrap(true);
        terminalArea.setWrapStyleWord(true);

        terminalScroll = new JScrollPane(terminalArea);
        terminalScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        terminalScroll.setBorder(BorderFactory.createTitledBorder("Terminal de Resultados"));

        // Panel de visualización
        visualizationPanel = new JPanel();
        visualizationPanel.setBackground(new Color(248, 249, 250));
        visualizationPanel.setBorder(BorderFactory.createTitledBorder("Visualización"));
        visualizationPanel.setPreferredSize(new Dimension(600, 200));
    }

    private void setupLayout() {
        // Panel superior con configuración y controles
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Constants.BACKGROUND_COLOR);

        topPanel.add(createConfigurationPanel(), BorderLayout.NORTH);
        topPanel.add(createControlsPanel(), BorderLayout.CENTER);

        // Panel central con visualización
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Constants.BACKGROUND_COLOR);
        centerPanel.add(visualizationPanel, BorderLayout.CENTER);

        // Panel inferior con terminal y botones
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Constants.BACKGROUND_COLOR);
        bottomPanel.add(terminalScroll, BorderLayout.CENTER);
        bottomPanel.add(createButtonPanel(), BorderLayout.SOUTH);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void setupWindow() {
        setSize(800, 700);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Constants.BACKGROUND_COLOR);

        JButton exportButton = createStyledButton("Exportar CSV", Constants.INFO_COLOR);
        JButton importButton = createStyledButton("Importar CSV", Constants.INFO_COLOR);
        JButton resetButton = createStyledButton("Resetear", Constants.WARNING_COLOR);
        JButton closeButton = createStyledButton("Cerrar", new Color(108, 117, 125));

        exportButton.addActionListener(e -> exportToCSV());
        importButton.addActionListener(e -> importFromCSV());
        resetButton.addActionListener(e -> resetAll());
        closeButton.addActionListener(e -> dispose());

        buttonPanel.add(exportButton);
        buttonPanel.add(importButton);
        buttonPanel.add(resetButton);
        buttonPanel.add(closeButton);

        return buttonPanel;
    }

    protected JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setPreferredSize(Constants.BUTTON_SIZE);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Efecto hover
        Color originalColor = backgroundColor;
        Color hoverColor = backgroundColor.darker();

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(originalColor);
            }
        });

        return button;
    }

    protected void logToTerminal(String message, String type) {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String icon = getIconForType(type);
        String formattedMessage = String.format("[%s] %s %s\n", timestamp, icon, message);

        SwingUtilities.invokeLater(() -> {
            terminalArea.append(formattedMessage);
            terminalArea.setCaretPosition(terminalArea.getDocument().getLength());
        });
    }

    private String getIconForType(String type) {
        switch (type.toLowerCase()) {
            case "success": return "✓";
            case "error": return "✗";
            case "warning": return "⚠";
            case "info": return "ℹ";
            default: return "•";
        }
    }

    protected void updateVisualization() {
        if (structure == null || !structure.isInitialized()) {
            showEmptyVisualization();
            return;
        }

        visualizationPanel.removeAll();
        visualizationPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

        Object[] data = structure.getData();
        for (int i = 0; i < data.length; i++) {
            JPanel itemPanel = createVisualizationItem(i + 1, data[i]);
            visualizationPanel.add(itemPanel);
        }

        visualizationPanel.revalidate();
        visualizationPanel.repaint();
    }

    private JPanel createVisualizationItem(int index, Object value) {
        JPanel item = new JPanel(new BorderLayout());
        item.setPreferredSize(new Dimension(60, 40));
        item.setBorder(BorderFactory.createLineBorder(Constants.TEXT_COLOR, 1));

        if (value == null) {
            item.setBackground(Constants.BACKGROUND_COLOR);
        } else {
            item.setBackground(Constants.SUCCESS_COLOR);
        }

        JLabel indexLabel = new JLabel(String.valueOf(index), SwingConstants.CENTER);
        indexLabel.setFont(new Font("Arial", Font.BOLD, 10));
        indexLabel.setForeground(Constants.TEXT_COLOR);

        JLabel valueLabel = new JLabel(value == null ? "---" : value.toString(), SwingConstants.CENTER);
        valueLabel.setFont(Constants.BODY_FONT);
        valueLabel.setForeground(value == null ? Constants.TEXT_COLOR : Color.WHITE);

        item.add(indexLabel, BorderLayout.NORTH);
        item.add(valueLabel, BorderLayout.CENTER);

        return item;
    }

    private void showEmptyVisualization() {
        visualizationPanel.removeAll();
        visualizationPanel.setLayout(new BorderLayout());

        JLabel emptyLabel = new JLabel("Inicialice la estructura para comenzar", SwingConstants.CENTER);
        emptyLabel.setFont(Constants.SUBTITLE_FONT);
        emptyLabel.setForeground(Constants.TEXT_COLOR);

        visualizationPanel.add(emptyLabel, BorderLayout.CENTER);
        visualizationPanel.revalidate();
        visualizationPanel.repaint();
    }

    // Métodos abstractos que deben implementar las clases hijas
    protected abstract JPanel createConfigurationPanel();
    protected abstract JPanel createControlsPanel();
    protected abstract void exportToCSV();
    protected abstract void importFromCSV();
    protected abstract void resetAll();
}
