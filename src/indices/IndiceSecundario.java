package indices;

import utils.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;

public class IndiceSecundario extends AlgorithmWindow {
    private JTextField keyField, valueField, recordPointerField;
    private JButton insertButton, searchButton, deleteButton;
    private IndexVisualization indexVisualization;
    private IndexStructure indexStructure;

    public IndiceSecundario(JFrame parent) {
        super(parent, "Índice Secundario");
        logToTerminal("Sistema de Índice Secundario iniciado", "info");
        logToTerminal("Características: Claves duplicadas permitidas, múltiples punteros", "info");
        logToTerminal("Estructura: Clave -> Lista de punteros a registros", "info");
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

        JButton showDuplicatesButton = createStyledButton("Mostrar Duplicados", Constants.WARNING_COLOR);
        showDuplicatesButton.addActionListener(this::showDuplicateKeys);
        controlPanel.add(showDuplicatesButton);

        visualizationPanel.add(controlPanel, BorderLayout.SOUTH);
    }

    @Override
    protected JPanel createConfigurationPanel() {
        JPanel configPanel = new JPanel(new GridBagLayout());
        configPanel.setBackground(Constants.BACKGROUND_COLOR);
        configPanel.setBorder(BorderFactory.createTitledBorder("Configuración del Índice Secundario"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Inicialización automática
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel infoLabel = new JLabel("El índice secundario se inicializa automáticamente");
        infoLabel.setFont(Constants.SUBTITLE_FONT);
        infoLabel.setForeground(Constants.INFO_COLOR);
        configPanel.add(infoLabel, gbc);

        // Información sobre características
        gbc.gridy = 1;
        JTextArea characteristicsArea = new JTextArea(4, 50);
        characteristicsArea.setBackground(new Color(255, 248, 240));
        characteristicsArea.setFont(Constants.BODY_FONT);
        characteristicsArea.setEditable(false);
        characteristicsArea.setText(
                "• Permite claves duplicadas (no única)\n" +
                        "• Múltiples registros por clave\n" +
                        "• Estructura ordenada por clave secundaria\n" +
                        "• Útil para consultas por atributos no únicos"
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
        controlsPanel.add(new JLabel("Clave secundaria:"), gbc);
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
        controlsPanel.add(new JLabel("Registro:"), gbc);
        gbc.gridx = 5;
        recordPointerField = new JTextField(5);
        recordPointerField.setPreferredSize(new Dimension(80, 25));
        controlsPanel.add(recordPointerField, gbc);

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
            indexStructure = new IndexStructure("SECUNDARIO", 10);
            indexVisualization.setIndexStructure(indexStructure);
            logToTerminal("Índice secundario inicializado automáticamente", "success");
        }
    }

    private void insertEntry(ActionEvent e) {
        try {
            initializeIfNeeded();

            String key = keyField.getText().trim();
            String value = valueField.getText().trim();
            String recordPointerStr = recordPointerField.getText().trim();

            if (key.isEmpty()) {
                throw new Exception("Ingrese una clave secundaria válida");
            }

            if (value.isEmpty()) {
                value = key; // Usar clave como valor por defecto
            }

            int recordPointer = 1;
            if (!recordPointerStr.isEmpty()) {
                recordPointer = Integer.parseInt(recordPointerStr);
            }

            logToTerminal(String.format("Insertando entrada: Clave='%s', Valor='%s', Registro=%d",
                    key, value, recordPointer), "info");

            indexStructure.insert(key, value, recordPointer);

            logToTerminal(String.format("Entrada insertada exitosamente"), "success");

            // Mostrar información sobre duplicados
            IndexStructure.IndexEntry existingEntry = findExistingEntry(key);
            if (existingEntry != null && existingEntry.recordPointers.size() > 1) {
                logToTerminal(String.format("Clave '%s' ahora tiene %d registros asociados",
                        key, existingEntry.recordPointers.size()), "info");
            }

            logToTerminal(String.format("Total de claves únicas en índice: %d", indexStructure.getEntryCount()), "info");

            updateIndexVisualization();
            clearFields();

        } catch (NumberFormatException ex) {
            logToTerminal("Error: El puntero de registro debe ser un número", "error");
        } catch (Exception ex) {
            logToTerminal("Error al insertar: " + ex.getMessage(), "error");
        }
    }

    private IndexStructure.IndexEntry findExistingEntry(String key) {
        for (IndexStructure.IndexEntry entry : indexStructure.getEntries()) {
            if (entry.key.equals(key)) {
                return entry;
            }
        }
        return null;
    }

    private void searchEntry(ActionEvent e) {
        try {
            initializeIfNeeded();

            String key = keyField.getText().trim();

            if (key.isEmpty()) {
                throw new Exception("Ingrese una clave para buscar");
            }

            logToTerminal(String.format("Buscando clave secundaria: '%s'", key), "info");
            logToTerminal("=== PROCESO DE BÚSQUEDA ===", "info");

            IndexStructure.SearchResult result = indexStructure.search(key);

            // Mostrar pasos de búsqueda
            logSearchSteps(result.steps);

            // Animar búsqueda
            animateSearch(result.steps);

            if (result.found) {
                logToTerminal(String.format("¡Clave encontrada! Valor: '%s'", result.entry.value), "success");

                if (result.entry.recordPointers.size() == 1) {
                    logToTerminal(String.format("Registro asociado: %d", result.entry.recordPointers.get(0)), "info");
                } else {
                    logToTerminal(String.format("Registros asociados (%d): %s",
                            result.entry.recordPointers.size(),
                            result.entry.recordPointers.toString()), "info");
                }

                indexVisualization.highlightEntry(result.level, result.position);
            } else {
                logToTerminal("Clave no encontrada en el índice secundario", "error");
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

            logToTerminal(String.format("Eliminando clave secundaria: '%s'", key), "info");

            boolean removed = indexStructure.remove(key);

            if (removed) {
                logToTerminal("Entrada eliminada exitosamente (todos los registros asociados)", "success");
                logToTerminal(String.format("Total de claves restantes: %d", indexStructure.getEntryCount()), "info");
                updateIndexVisualization();
                clearFields();
            } else {
                logToTerminal("Clave no encontrada para eliminar", "error");
            }

        } catch (Exception ex) {
            logToTerminal("Error al eliminar: " + ex.getMessage(), "error");
        }
    }

    private void showDuplicateKeys(ActionEvent e) {
        if (indexStructure == null) {
            logToTerminal("No hay índice para analizar", "warning");
            return;
        }

        logToTerminal("=== ANÁLISIS DE CLAVES DUPLICADAS ===", "info");

        int totalDuplicates = 0;
        for (IndexStructure.IndexEntry entry : indexStructure.getEntries()) {
            if (entry.recordPointers.size() > 1) {
                logToTerminal(String.format("Clave '%s': %d registros → %s",
                        entry.key, entry.recordPointers.size(), entry.recordPointers.toString()), "info");
                totalDuplicates++;
            }
        }

        if (totalDuplicates == 0) {
            logToTerminal("No se encontraron claves con registros múltiples", "info");
        } else {
            logToTerminal(String.format("Total de claves con múltiples registros: %d", totalDuplicates), "success");
        }

        logToTerminal(String.format("Total de claves únicas: %d", indexStructure.getEntryCount()), "info");
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
        recordPointerField.setText("");
    }

    @Override
    protected void exportToCSV() {
        if (indexStructure == null || indexStructure.getEntryCount() == 0) {
            JOptionPane.showMessageDialog(this, "No hay datos para exportar",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Índice Secundario como CSV");

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(fileChooser.getSelectedFile()))) {
                writer.println("#CONFIGURACION");
                writer.println("Algoritmo,Indice Secundario");
                writer.println("Tipo_Indice," + indexStructure.getIndexType());
                writer.println("Total_Claves," + indexStructure.getEntryCount());

                writer.println("#ENTRADAS_INDICE");
                writer.println("Clave,Valor,Registros");
                for (IndexStructure.IndexEntry entry : indexStructure.getEntries()) {
                    String registros = entry.recordPointers.toString().replaceAll("[\\[\\]\\s]", "");
                    writer.println(String.format("%s,%s,\"%s\"",
                            entry.key, entry.value, registros));
                }

                writer.println("#CONSOLA");
                writer.println(terminalArea.getText().replace("\n", "\\n"));

                logToTerminal("Índice secundario exportado exitosamente", "success");

            } catch (IOException ex) {
                logToTerminal("Error al exportar: " + ex.getMessage(), "error");
            }
        }
    }

    @Override
    protected void importFromCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Importar Índice Secundario desde CSV");

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

                    if (section.equals("#ENTRADAS_INDICE") && !line.equals("Clave,Valor,Registros")) {
                        String[] parts = line.split(",");
                        if (parts.length >= 3) {
                            try {
                                String key = parts[0];
                                String value = parts[1];
                                String registros = parts[2].replaceAll("\"", "");

                                // Procesar múltiples registros
                                String[] recordArray = registros.split(",");
                                for (String recordStr : recordArray) {
                                    if (!recordStr.trim().isEmpty()) {
                                        indexStructure.insert(key, value, Integer.parseInt(recordStr.trim()));
                                    }
                                }
                            } catch (Exception ex) {
                                logToTerminal("Error importando entrada: " + line, "warning");
                            }
                        }
                    }
                }

                updateIndexVisualization();
                logToTerminal("Índice secundario importado exitosamente", "success");

            } catch (IOException ex) {
                logToTerminal("Error al importar: " + ex.getMessage(), "error");
            }
        }
    }

    @Override
    protected void resetAll() {
        int option = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de que desea resetear el índice secundario?",
                "Confirmar Reset",
                JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            if (indexStructure != null) {
                indexStructure.reset();
            }

            clearFields();
            terminalArea.setText("");

            updateIndexVisualization();
            logToTerminal("Índice secundario reseteado", "warning");
        }
    }
}
