package indices;

import utils.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;

public class MultinivelSecundario extends AlgorithmWindow {
    private JTextField keyField, valueField, recordPointerField, maxEntriesField;
    private JButton initButton, insertButton, searchButton, deleteButton;
    private IndexVisualization indexVisualization;
    private IndexStructure indexStructure;

    public MultinivelSecundario(JFrame parent) {
        super(parent, "Multinivel Secundario");
        logToTerminal("Sistema de Índice Multinivel Secundario iniciado", "info");
        logToTerminal("Características: Múltiples niveles, claves duplicadas, múltiples registros", "info");
        logToTerminal("Optimizado para consultas complejas sobre atributos no únicos", "info");
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

        JButton showLevelsButton = createStyledButton("Mostrar Niveles", Constants.WARNING_COLOR);
        showLevelsButton.addActionListener(this::showLevelInfo);
        controlPanel.add(showLevelsButton);

        JButton showDuplicatesButton = createStyledButton("Análisis Duplicados", Constants.ERROR_COLOR);
        showDuplicatesButton.addActionListener(this::showDuplicateAnalysis);
        controlPanel.add(showDuplicatesButton);

        visualizationPanel.add(controlPanel, BorderLayout.SOUTH);
    }

    @Override
    protected JPanel createConfigurationPanel() {
        JPanel configPanel = new JPanel(new GridBagLayout());
        configPanel.setBackground(Constants.BACKGROUND_COLOR);
        configPanel.setBorder(BorderFactory.createTitledBorder("Configuración del Índice Multinivel Secundario"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Configuración de entradas por nivel
        gbc.gridx = 0; gbc.gridy = 0;
        configPanel.add(new JLabel("Máx. entradas por nivel:"), gbc);
        gbc.gridx = 1;
        maxEntriesField = new JTextField(10);
        maxEntriesField.setText("3"); // Valor por defecto menor para secundario
        maxEntriesField.setPreferredSize(Constants.INPUT_SIZE);
        configPanel.add(maxEntriesField, gbc);

        // Botón inicializar
        gbc.gridx = 2;
        initButton = createStyledButton("Inicializar", Constants.PRIMARY_COLOR);
        initButton.addActionListener(this::initializeStructure);
        configPanel.add(initButton, gbc);

        // Información sobre características
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 3;
        JTextArea characteristicsArea = new JTextArea(5, 50);
        characteristicsArea.setBackground(new Color(255, 245, 240));
        characteristicsArea.setFont(Constants.BODY_FONT);
        characteristicsArea.setEditable(false);
        characteristicsArea.setText(
                "• Estructura jerárquica multinivel para atributos secundarios\n" +
                        "• Permite múltiples registros por clave (claves duplicadas)\n" +
                        "• Nivel hoja contiene listas de punteros a registros\n" +
                        "• Niveles superiores organizan rangos de claves secundarias\n" +
                        "• Eficiente para consultas sobre atributos no únicos"
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
        insertButton.setEnabled(false);
        insertButton.addActionListener(this::insertEntry);
        controlsPanel.add(insertButton, gbc);

        gbc.gridx = 1;
        searchButton = createStyledButton("Buscar", Constants.INFO_COLOR);
        searchButton.setEnabled(false);
        searchButton.addActionListener(this::searchEntry);
        controlsPanel.add(searchButton, gbc);

        gbc.gridx = 2;
        deleteButton = createStyledButton("Eliminar", Constants.ERROR_COLOR);
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(this::deleteEntry);
        controlsPanel.add(deleteButton, gbc);

        return controlsPanel;
    }

    private void initializeStructure(ActionEvent e) {
        try {
            int maxEntries = Integer.parseInt(maxEntriesField.getText().trim());

            if (maxEntries < 2) {
                throw new Exception("El máximo de entradas por nivel debe ser al menos 2");
            }

            indexStructure = new IndexStructure("MULTINIVEL_SECUNDARIO", maxEntries);
            indexVisualization.setIndexStructure(indexStructure);

            enableControls(true);

            logToTerminal("Índice multinivel secundario inicializado", "success");
            logToTerminal(String.format("Máximo de entradas por nivel: %d", maxEntries), "info");
            logToTerminal("Estructura jerárquica lista para claves secundarias", "info");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Ingrese un número válido para las entradas por nivel",
                    "Error de configuración",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Error de configuración", JOptionPane.ERROR_MESSAGE);
            logToTerminal("Error: " + ex.getMessage(), "error");
        }
    }

    private void insertEntry(ActionEvent e) {
        try {
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

            int oldLevels = indexStructure.getLevelCount();
            indexStructure.insert(key, value, recordPointer);
            int newLevels = indexStructure.getLevelCount();

            if (newLevels > oldLevels) {
                logToTerminal(String.format("¡Nuevo nivel creado! Niveles: %d → %d", oldLevels, newLevels), "warning");
            }

            // Información sobre duplicados
            IndexStructure.IndexEntry existingEntry = findExistingEntry(key);
            if (existingEntry != null && existingEntry.recordPointers.size() > 1) {
                logToTerminal(String.format("Clave secundaria '%s' ahora tiene %d registros asociados",
                        key, existingEntry.recordPointers.size()), "info");
            }

            logToTerminal(String.format("Entrada insertada exitosamente"), "success");
            logToTerminal(String.format("Estructura actual: %d niveles, %d claves únicas",
                    indexStructure.getLevelCount(), indexStructure.getEntryCount()), "info");

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
            String key = keyField.getText().trim();

            if (key.isEmpty()) {
                throw new Exception("Ingrese una clave para buscar");
            }

            logToTerminal(String.format("Buscando clave secundaria multinivel: '%s'", key), "info");
            logToTerminal("=== PROCESO DE BÚSQUEDA MULTINIVEL SECUNDARIO ===", "info");

            IndexStructure.SearchResult result = indexStructure.search(key);

            // Mostrar pasos de búsqueda
            logMultilevelSearchSteps(result.steps);

            // Animar búsqueda
            animateSearch(result.steps);

            if (result.found) {
                logToTerminal(String.format("¡Clave encontrada en nivel %d! Valor: '%s'",
                        result.level, result.entry.value), "success");

                if (result.entry.recordPointers.size() == 1) {
                    logToTerminal(String.format("Registro asociado: %d", result.entry.recordPointers.get(0)), "info");
                } else {
                    logToTerminal(String.format("Registros asociados (%d): %s",
                            result.entry.recordPointers.size(),
                            result.entry.recordPointers.toString()), "info");
                }

                indexVisualization.highlightEntry(result.level, result.position);
            } else {
                logToTerminal("Clave no encontrada en el índice multinivel secundario", "error");
            }

            logToTerminal(String.format("Total de accesos a niveles: %d",
                    countLevelAccesses(result.steps)), "info");

        } catch (Exception ex) {
            logToTerminal("Error en búsqueda: " + ex.getMessage(), "error");
        }
    }

    private void deleteEntry(ActionEvent e) {
        try {
            String key = keyField.getText().trim();

            if (key.isEmpty()) {
                throw new Exception("Ingrese una clave para eliminar");
            }

            logToTerminal(String.format("Eliminando clave secundaria multinivel: '%s'", key), "info");

            int oldLevels = indexStructure.getLevelCount();
            boolean removed = indexStructure.remove(key);
            int newLevels = indexStructure.getLevelCount();

            if (removed) {
                logToTerminal("Entrada eliminada exitosamente (todos los registros asociados)", "success");

                if (newLevels < oldLevels) {
                    logToTerminal(String.format("Niveles reducidos: %d → %d", oldLevels, newLevels), "info");
                }

                logToTerminal(String.format("Estructura actual: %d niveles, %d claves restantes",
                        indexStructure.getLevelCount(), indexStructure.getEntryCount()), "info");
                updateIndexVisualization();
                clearFields();
            } else {
                logToTerminal("Clave no encontrada para eliminar", "error");
            }

        } catch (Exception ex) {
            logToTerminal("Error al eliminar: " + ex.getMessage(), "error");
        }
    }

    private void showLevelInfo(ActionEvent e) {
        if (indexStructure == null) {
            logToTerminal("No hay índice para analizar", "warning");
            return;
        }

        logToTerminal("=== INFORMACIÓN DE NIVELES MULTINIVEL SECUNDARIO ===", "info");
        logToTerminal(String.format("Total de niveles: %d", indexStructure.getLevelCount()), "info");

        int totalRecords = 0;

        for (int level = indexStructure.getLevelCount() - 1; level >= 0; level--) {
            java.util.List<IndexStructure.IndexEntry> levelEntries = indexStructure.getLevels().get(level);
            String levelType = level == 0 ? "HOJA" : "INTERNO";

            logToTerminal(String.format("Nivel %d (%s): %d claves únicas",
                    level, levelType, levelEntries.size()), "info");

            if (level == 0) { // Solo contar registros en nivel hoja
                for (IndexStructure.IndexEntry entry : levelEntries) {
                    totalRecords += entry.recordPointers.size();
                }
            }

            if (!levelEntries.isEmpty()) {
                Object firstKey = levelEntries.get(0).key;
                Object lastKey = levelEntries.get(levelEntries.size() - 1).key;
                logToTerminal(String.format("  Rango de claves: %s ... %s", firstKey, lastKey), "info");
            }
        }

        logToTerminal(String.format("Total de registros referenciados: %d", totalRecords), "success");
        logToTerminal(String.format("Máximo configurado por nivel: %d entradas",
                indexStructure.getMaxEntriesPerLevel()), "info");
    }

    private void showDuplicateAnalysis(ActionEvent e) {
        if (indexStructure == null) {
            logToTerminal("No hay índice para analizar", "warning");
            return;
        }

        logToTerminal("=== ANÁLISIS DE CLAVES DUPLICADAS MULTINIVEL ===", "info");

        int totalDuplicates = 0;
        int totalRecords = 0;
        int maxRecordsPerKey = 0;
        String keyWithMostRecords = "";

        for (IndexStructure.IndexEntry entry : indexStructure.getEntries()) {
            int recordCount = entry.recordPointers.size();
            totalRecords += recordCount;

            if (recordCount > 1) {
                logToTerminal(String.format("Clave '%s': %d registros → %s",
                        entry.key, recordCount, entry.recordPointers.toString()), "info");
                totalDuplicates++;
            }

            if (recordCount > maxRecordsPerKey) {
                maxRecordsPerKey = recordCount;
                keyWithMostRecords = entry.key.toString();
            }
        }

        if (totalDuplicates == 0) {
            logToTerminal("No se encontraron claves con registros múltiples", "info");
        } else {
            logToTerminal(String.format("Claves con múltiples registros: %d", totalDuplicates), "success");
        }

        logToTerminal(String.format("Total de claves únicas: %d", indexStructure.getEntryCount()), "info");
        logToTerminal(String.format("Total de registros: %d", totalRecords), "info");
        logToTerminal(String.format("Clave con más registros: '%s' (%d registros)",
                keyWithMostRecords, maxRecordsPerKey), "info");

        double avgRecordsPerKey = (double) totalRecords / indexStructure.getEntryCount();
        logToTerminal(String.format("Promedio de registros por clave: %.2f", avgRecordsPerKey), "info");
    }

    private void logMultilevelSearchSteps(java.util.List<IndexStructure.SearchStep> steps) {
        int currentLevel = -1;

        for (int i = 0; i < steps.size(); i++) {
            IndexStructure.SearchStep step = steps.get(i);

            if (step.level != currentLevel) {
                currentLevel = step.level;
                String levelType = currentLevel == 0 ? "HOJA" : "INTERNO";
                logToTerminal(String.format("--- NIVEL %d (%s) ---", currentLevel, levelType), "info");
            }

            logToTerminal(String.format("  %s", step.action), "info");
        }
    }

    private int countLevelAccesses(java.util.List<IndexStructure.SearchStep> steps) {
        java.util.Set<Integer> accessedLevels = new java.util.HashSet<>();
        for (IndexStructure.SearchStep step : steps) {
            accessedLevels.add(step.level);
        }
        return accessedLevels.size();
    }

    private void animateSearch(java.util.List<IndexStructure.SearchStep> steps) {
        indexVisualization.showSearchSteps(steps);

        Timer timer = new Timer(1000, null);
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

    private void enableControls(boolean enabled) {
        insertButton.setEnabled(enabled);
        searchButton.setEnabled(enabled);
        deleteButton.setEnabled(enabled);
    }

    @Override
    protected void exportToCSV() {
        if (indexStructure == null || indexStructure.getEntryCount() == 0) {
            JOptionPane.showMessageDialog(this, "No hay datos para exportar",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Índice Multinivel Secundario como CSV");

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(fileChooser.getSelectedFile()))) {
                writer.println("#CONFIGURACION");
                writer.println("Algoritmo,Multinivel Secundario");
                writer.println("Tipo_Indice," + indexStructure.getIndexType());
                writer.println("Max_Entradas_Nivel," + indexStructure.getMaxEntriesPerLevel());
                writer.println("Total_Niveles," + indexStructure.getLevelCount());
                writer.println("Total_Claves," + indexStructure.getEntryCount());

                writer.println("#ESTRUCTURA_NIVELES");
                for (int level = 0; level < indexStructure.getLevelCount(); level++) {
                    java.util.List<IndexStructure.IndexEntry> levelEntries = indexStructure.getLevels().get(level);
                    for (IndexStructure.IndexEntry entry : levelEntries) {
                        String registros = entry.recordPointers.toString().replaceAll("[\\[\\]\\s]", "");
                        writer.println(String.format("NIVEL_%d,%s,%s,\"%s\"",
                                level, entry.key, entry.value, registros));
                    }
                }

                writer.println("#CONSOLA");
                writer.println(terminalArea.getText().replace("\n", "\\n"));

                logToTerminal("Índice multinivel secundario exportado exitosamente", "success");

            } catch (IOException ex) {
                logToTerminal("Error al exportar: " + ex.getMessage(), "error");
            }
        }
    }

    @Override
    protected void importFromCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Importar Índice Multinivel Secundario desde CSV");

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (BufferedReader reader = new BufferedReader(new FileReader(fileChooser.getSelectedFile()))) {
                String line;
                String section = "";

                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("#")) {
                        section = line;
                        continue;
                    }

                    if (section.equals("#CONFIGURACION")) {
                        String[] parts = line.split(",");
                        if (parts[0].equals("Max_Entradas_Nivel")) {
                            maxEntriesField.setText(parts[1]);
                        }
                    }
                }

                logToTerminal("Configuración importada. Presione Inicializar y luego reconstruya manualmente", "success");

            } catch (IOException ex) {
                logToTerminal("Error al importar: " + ex.getMessage(), "error");
            }
        }
    }

    @Override
    protected void resetAll() {
        int option = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de que desea resetear el índice multinivel secundario?",
                "Confirmar Reset",
                JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            if (indexStructure != null) {
                indexStructure.reset();
            }

            maxEntriesField.setText("3");
            clearFields();
            enableControls(false);
            terminalArea.setText("");

            updateIndexVisualization();
            logToTerminal("Índice multinivel secundario reseteado", "warning");
        }
    }
}
