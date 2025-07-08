package indices;

import utils.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;

public class IndicePrimario extends AlgorithmWindow {
    private JTextField keyField, valueField, blockPointerField;
    private JButton insertButton, searchButton, deleteButton;
    private IndexVisualization indexVisualization;
    private IndexStructure indexStructure;

    public IndicePrimario(JFrame parent) {
        super(parent, "Índice Primario");
        logToTerminal("Sistema de Índice Primario iniciado", "info");
        logToTerminal("Características: Clave única, ordenado, acceso directo", "info");
        logToTerminal("Estructura: Clave -> Puntero a bloque", "info");
        initializeIndexVisualization();
    }

    private void initializeIndexVisualization() {
        indexVisualization = new IndexVisualization();

        visualizationPanel.removeAll();
        visualizationPanel.setLayout(new BorderLayout());
        visualizationPanel.add(indexVisualization, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.setBackground(Constants.BACKGROUND_COLOR);

        JButton clearHighlightButton = createStyledButton("Limpiar Resaltado", Constants.WARNING_COLOR);
        clearHighlightButton.addActionListener(e -> indexVisualization.clearHighlight());
        controlPanel.add(clearHighlightButton);

        JButton animateSearchButton = createStyledButton("Animar Búsqueda", Constants.INFO_COLOR);
        animateSearchButton.addActionListener(this::animateLastSearch);
        controlPanel.add(animateSearchButton);

        visualizationPanel.add(controlPanel, BorderLayout.SOUTH);
    }

    @Override
    protected JPanel createConfigurationPanel() {
        JPanel configPanel = new JPanel(new GridBagLayout());
        configPanel.setBackground(Constants.BACKGROUND_COLOR);
        configPanel.setBorder(BorderFactory.createTitledBorder("Configuración del Índice Primario"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Inicialización automática
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel infoLabel = new JLabel("El índice primario se inicializa automáticamente");
        infoLabel.setFont(Constants.SUBTITLE_FONT);
        infoLabel.setForeground(Constants.INFO_COLOR);
        configPanel.add(infoLabel, gbc);

        // Información sobre características
        gbc.gridy = 1;
        JTextArea characteristicsArea = new JTextArea(3, 50);
        characteristicsArea.setBackground(new Color(240, 248, 255));
        characteristicsArea.setFont(Constants.BODY_FONT);
        characteristicsArea.setEditable(false);
        characteristicsArea.setText(
                "• Cada clave es única (clave primaria)\n" +
                        "• Estructura ordenada por clave\n" +
                        "• Acceso directo O(log n) mediante búsqueda binaria"
        );
        characteristicsArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        configPanel.add(characteristicsArea, gbc);

        return configPanel;
    }

    @Override
    protected JPanel createControlsPanel() {
        JPanel controlsPanel = new JPanel(new GridBagLayout());
        controlsPanel.setBackground(Constants.BACKGROUND_COLOR);
        controlsPanel.setBorder(BorderFactory.createTitledBorder("Controles"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Campos de entrada
        gbc.gridx = 0; gbc.gridy = 0;
        controlsPanel.add(new JLabel("Clave primaria:"), gbc);
        gbc.gridx = 1;
        keyField = new JTextField(10);
        keyField.setPreferredSize(Constants.INPUT_SIZE);
        controlsPanel.add(keyField, gbc);

        gbc.gridx = 2; gbc.gridy = 0;
        controlsPanel.add(new JLabel("Valor:"), gbc);
        gbc.gridx = 3;
        valueField = new JTextField(10);
        valueField.setPreferredSize(Constants.INPUT_SIZE);
        controlsPanel.add(valueField, gbc);

        gbc.gridx = 4; gbc.gridy = 0;
        controlsPanel.add(new JLabel("Bloque:"), gbc);
        gbc.gridx = 5;
        blockPointerField = new JTextField(5);
        blockPointerField.setPreferredSize(new Dimension(80, 25));
        controlsPanel.add(blockPointerField, gbc);

        // Botones
        gbc.gridx = 0; gbc.gridy = 1;
        insertButton = createStyledButton("Insertar", Constants.SUCCESS_COLOR);
        insertButton.addActionListener(this::insertEntry);
        controlsPanel.add(insertButton, gbc);

        gbc.gridx = 1;
        searchButton = createStyledButton("Buscar", Constants.INFO_COLOR);
        searchButton.addActionListener(this::searchEntry);
        controlsPanel.add(searchButton, gbc);

        gbc.gridx = 2;
        deleteButton = createStyledButton("Eliminar", Constants.ERROR_COLOR);
        deleteButton.addActionListener(this::deleteEntry);
        controlsPanel.add(deleteButton, gbc);

        return controlsPanel;
    }

    private void initializeIfNeeded() {
        if (indexStructure == null) {
            indexStructure = new IndexStructure("PRIMARIO", 10);
            indexVisualization.setIndexStructure(indexStructure);
            logToTerminal("Índice primario inicializado automáticamente", "success");
        }
    }

    private void insertEntry(ActionEvent e) {
        try {
            initializeIfNeeded();

            String key = keyField.getText().trim();
            String value = valueField.getText().trim();
            String blockPointerStr = blockPointerField.getText().trim();

            if (key.isEmpty()) {
                throw new Exception("Ingrese una clave primaria válida");
            }

            if (value.isEmpty()) {
                value = key; // Usar clave como valor por defecto
            }

            int blockPointer = 1;
            if (!blockPointerStr.isEmpty()) {
                blockPointer = Integer.parseInt(blockPointerStr);
            }

            logToTerminal(String.format("Insertando entrada: Clave='%s', Valor='%s', Bloque=%d",
                    key, value, blockPointer), "info");

            indexStructure.insert(key, value, blockPointer);

            logToTerminal(String.format("Entrada insertada exitosamente"), "success");
            logToTerminal(String.format("Total de entradas en índice: %d", indexStructure.getEntryCount()), "info");

            updateIndexVisualization();
            clearFields();

        } catch (NumberFormatException ex) {
            logToTerminal("Error: El puntero de bloque debe ser un número", "error");
        } catch (Exception ex) {
            logToTerminal("Error al insertar: " + ex.getMessage(), "error");
        }
    }

    private void searchEntry(ActionEvent e) {
        try {
            initializeIfNeeded();

            String key = keyField.getText().trim();

            if (key.isEmpty()) {
                throw new Exception("Ingrese una clave para buscar");
            }

            logToTerminal(String.format("Buscando clave primaria: '%s'", key), "info");
            logToTerminal("=== PROCESO DE BÚSQUEDA ===", "info");

            IndexStructure.SearchResult result = indexStructure.search(key);

            // Mostrar pasos de búsqueda
            logSearchSteps(result.steps);

            // Animar búsqueda
            animateSearch(result.steps);

            if (result.found) {
                logToTerminal(String.format("¡Clave encontrada! Valor: '%s', Bloque: %d",
                        result.entry.value, result.entry.blockPointer), "success");
                indexVisualization.highlightEntry(result.level, result.position);
            } else {
                logToTerminal("Clave no encontrada en el índice primario", "error");
            }

            logToTerminal(String.format("Comparaciones realizadas: %d", result.steps.size()), "info");

        } catch (Exception ex) {
            logToTerminal("Error en búsqueda: " + ex.getMessage(), "error");
        }
    }

    private void deleteEntry(ActionEvent e) {
        try {
            initializeIfNeeded();

            String key = keyField.getText().trim();

            if (key.isEmpty()) {
                throw new Exception("Ingrese una clave para eliminar");
            }

            logToTerminal(String.format("Eliminando clave primaria: '%s'", key), "info");

            boolean removed = indexStructure.remove(key);

            if (removed) {
                logToTerminal("Entrada eliminada exitosamente", "success");
                logToTerminal(String.format("Total de entradas restantes: %d", indexStructure.getEntryCount()), "info");
                updateIndexVisualization();
                clearFields();
            } else {
                logToTerminal("Clave no encontrada para eliminar", "error");
            }

        } catch (Exception ex) {
            logToTerminal("Error al eliminar: " + ex.getMessage(), "error");
        }
    }

    private void logSearchSteps(java.util.List<IndexStructure.SearchStep> steps) {
        for (int i = 0; i < steps.size(); i++) {
            IndexStructure.SearchStep step = steps.get(i);
            logToTerminal(String.format("Paso %d: %s (Posición %d)",
                    i + 1, step.action, step.position), "info");
        }
    }

    private void animateSearch(java.util.List<IndexStructure.SearchStep> steps) {
        indexVisualization.showSearchSteps(steps);

        Timer timer = new Timer(800, null);
        final int[] currentStep = {0};

        timer.addActionListener(actionEvent -> {
            if (currentStep[0] < steps.size()) {
                indexVisualization.setCurrentStep(currentStep[0]);
                currentStep[0]++;
            } else {
                timer.stop();
                indexVisualization.setCurrentStep(-1);
            }
        });

        timer.start();
    }

    private void animateLastSearch(ActionEvent e) {
        if (indexStructure != null && !keyField.getText().trim().isEmpty()) {
            IndexStructure.SearchResult result = indexStructure.search(keyField.getText().trim());
            animateSearch(result.steps);
        }
    }

    private void updateIndexVisualization() {
        indexVisualization.setIndexStructure(indexStructure);
    }

    private void clearFields() {
        keyField.setText("");
        valueField.setText("");
        blockPointerField.setText("");
    }

    @Override
    protected void exportToCSV() {
        if (indexStructure == null || indexStructure.getEntryCount() == 0) {
            JOptionPane.showMessageDialog(this, "No hay datos para exportar",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Índice Primario como CSV");

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(fileChooser.getSelectedFile()))) {
                writer.println("#CONFIGURACION");
                writer.println("Algoritmo,Indice Primario");
                writer.println("Tipo_Indice," + indexStructure.getIndexType());
                writer.println("Total_Entradas," + indexStructure.getEntryCount());

                writer.println("#ENTRADAS_INDICE");
                writer.println("Clave,Valor,Bloque");
                for (IndexStructure.IndexEntry entry : indexStructure.getEntries()) {
                    writer.println(String.format("%s,%s,%d",
                            entry.key, entry.value, entry.blockPointer));
                }

                writer.println("#CONSOLA");
                writer.println(terminalArea.getText().replace("\n", "\\n"));

                logToTerminal("Índice primario exportado exitosamente", "success");

            } catch (IOException ex) {
                logToTerminal("Error al exportar: " + ex.getMessage(), "error");
            }
        }
    }

    @Override
    protected void importFromCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Importar Índice Primario desde CSV");

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (BufferedReader reader = new BufferedReader(new FileReader(fileChooser.getSelectedFile()))) {
                String line;
                String section = "";

                initializeIfNeeded();

                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("#")) {
                        section = line;
                        continue;
                    }

                    if (section.equals("#ENTRADAS_INDICE") && !line.equals("Clave,Valor,Bloque")) {
                        String[] parts = line.split(",");
                        if (parts.length >= 3) {
                            try {
                                indexStructure.insert(parts[0], parts[1], Integer.parseInt(parts[2]));
                            } catch (Exception ex) {
                                logToTerminal("Error importando entrada: " + line, "warning");
                            }
                        }
                    }
                }

                updateIndexVisualization();
                logToTerminal("Índice primario importado exitosamente", "success");

            } catch (IOException ex) {
                logToTerminal("Error al importar: " + ex.getMessage(), "error");
            }
        }
    }

    @Override
    protected void resetAll() {
        int option = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de que desea resetear el índice primario?",
                "Confirmar Reset",
                JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            if (indexStructure != null) {
                indexStructure.reset();
            }

            clearFields();
            terminalArea.setText("");

            updateIndexVisualization();
            logToTerminal("Índice primario reseteado", "warning");
        }
    }
}
