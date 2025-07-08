package dinamicas;

import utils.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;

public class ExpansionTotal extends AlgorithmWindow {
    private JTextField initialSizeField, keyLengthField, searchKeyField;
    private JLabel currentSizeLabel, levelLabel, loadFactorLabel;
    private JButton initButton, insertButton, searchButton, deleteButton, forceExpansionButton;
    private DynamicVisualization dynamicVisualization;
    private DynamicStructure dynamicStructure;
    private JTextArea expansionHistoryArea;

    public ExpansionTotal(JFrame parent) {
        super(parent, "Expansión Total - Hashing Dinámico");
        logToTerminal("Sistema de Expansión Total iniciado", "info");
        logToTerminal("Fórmula: T = 2^i * N (duplicación de cubetas)", "info");
        logToTerminal("Umbral de expansión: factor de carga > 0.75", "info");
        initializeDynamicVisualization();
    }

    private void initializeDynamicVisualization() {
        dynamicVisualization = new DynamicVisualization();

        // Reemplazar el panel de visualización
        visualizationPanel.removeAll();
        visualizationPanel.setLayout(new BorderLayout());

        // Panel principal con visualización
        JPanel mainVisPanel = new JPanel(new BorderLayout());
        mainVisPanel.add(dynamicVisualization, BorderLayout.CENTER);

        // Panel de historial de expansiones
        expansionHistoryArea = new JTextArea(4, 50);
        expansionHistoryArea.setBackground(new Color(255, 255, 240));
        expansionHistoryArea.setFont(Constants.MONO_FONT);
        expansionHistoryArea.setEditable(false);
        expansionHistoryArea.setBorder(BorderFactory.createTitledBorder("Historial de Expansiones"));

        JScrollPane historyScroll = new JScrollPane(expansionHistoryArea);
        mainVisPanel.add(historyScroll, BorderLayout.SOUTH);

        visualizationPanel.add(mainVisPanel, BorderLayout.CENTER);

        // Panel de controles
        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.setBackground(Constants.BACKGROUND_COLOR);

        JButton clearHighlightButton = createStyledButton("Limpiar Resaltado", Constants.WARNING_COLOR);
        clearHighlightButton.addActionListener(e -> dynamicVisualization.clearHighlight());
        controlPanel.add(clearHighlightButton);

        visualizationPanel.add(controlPanel, BorderLayout.NORTH);
    }

    @Override
    protected JPanel createConfigurationPanel() {
        JPanel configPanel = new JPanel(new GridBagLayout());
        configPanel.setBackground(Constants.BACKGROUND_COLOR);
        configPanel.setBorder(BorderFactory.createTitledBorder("Configuración"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Tamaño inicial
        gbc.gridx = 0; gbc.gridy = 0;
        configPanel.add(new JLabel("Tamaño inicial (N):"), gbc);
        gbc.gridx = 1;
        initialSizeField = new JTextField(10);
        initialSizeField.setPreferredSize(Constants.INPUT_SIZE);
        initialSizeField.setText("4"); // Valor por defecto
        initialSizeField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                updateCalculatedValues();
            }
        });
        configPanel.add(initialSizeField, gbc);

        // Longitud de clave
        gbc.gridx = 2; gbc.gridy = 0;
        configPanel.add(new JLabel("Longitud de clave:"), gbc);
        gbc.gridx = 3;
        keyLengthField = new JTextField(10);
        keyLengthField.setPreferredSize(Constants.INPUT_SIZE);
        configPanel.add(keyLengthField, gbc);

        // Información calculada
        gbc.gridx = 0; gbc.gridy = 1;
        configPanel.add(new JLabel("Tamaño actual:"), gbc);
        gbc.gridx = 1;
        currentSizeLabel = new JLabel("2^0 * N = ?");
        currentSizeLabel.setFont(Constants.MONO_FONT);
        configPanel.add(currentSizeLabel, gbc);

        gbc.gridx = 2; gbc.gridy = 1;
        configPanel.add(new JLabel("Nivel:"), gbc);
        gbc.gridx = 3;
        levelLabel = new JLabel("i = 0");
        levelLabel.setFont(Constants.MONO_FONT);
        configPanel.add(levelLabel, gbc);

        // Factor de carga
        gbc.gridx = 0; gbc.gridy = 2;
        configPanel.add(new JLabel("Factor de carga:"), gbc);
        gbc.gridx = 1;
        loadFactorLabel = new JLabel("0.000");
        loadFactorLabel.setFont(Constants.MONO_FONT);
        configPanel.add(loadFactorLabel, gbc);

        // Botón inicializar
        gbc.gridx = 4; gbc.gridy = 0; gbc.gridheight = 3;
        initButton = createStyledButton("Inicializar", Constants.PRIMARY_COLOR);
        initButton.addActionListener(this::initializeStructure);
        configPanel.add(initButton, gbc);

        return configPanel;
    }

    @Override
    protected JPanel createControlsPanel() {
        JPanel controlsPanel = new JPanel(new GridBagLayout());
        controlsPanel.setBackground(Constants.BACKGROUND_COLOR);
        controlsPanel.setBorder(BorderFactory.createTitledBorder("Controles"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Campo de clave
        gbc.gridx = 0; gbc.gridy = 0;
        controlsPanel.add(new JLabel("Clave:"), gbc);
        gbc.gridx = 1;
        searchKeyField = new JTextField(10);
        searchKeyField.setPreferredSize(Constants.INPUT_SIZE);
        controlsPanel.add(searchKeyField, gbc);

        // Botones
        gbc.gridx = 2;
        insertButton = createStyledButton("Insertar", Constants.SUCCESS_COLOR);
        insertButton.setEnabled(false);
        insertButton.addActionListener(this::insertKey);
        controlsPanel.add(insertButton, gbc);

        gbc.gridx = 3;
        searchButton = createStyledButton("Buscar", Constants.INFO_COLOR);
        searchButton.setEnabled(false);
        searchButton.addActionListener(this::searchKey);
        controlsPanel.add(searchButton, gbc);

        gbc.gridx = 4;
        deleteButton = createStyledButton("Eliminar", Constants.ERROR_COLOR);
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(this::deleteKey);
        controlsPanel.add(deleteButton, gbc);

        gbc.gridx = 5;
        forceExpansionButton = createStyledButton("Forzar Expansión", Constants.WARNING_COLOR);
        forceExpansionButton.setEnabled(false);
        forceExpansionButton.addActionListener(this::forceExpansion);
        controlsPanel.add(forceExpansionButton, gbc);

        return controlsPanel;
    }

    private void updateCalculatedValues() {
        try {
            int initialSize = Integer.parseInt(initialSizeField.getText().trim());
            if (initialSize > 0) {
                currentSizeLabel.setText("2^0 * " + initialSize + " = " + initialSize);
                levelLabel.setText("i = 0");
                loadFactorLabel.setText("0.000");
            }
        } catch (NumberFormatException e) {
            currentSizeLabel.setText("2^0 * N = ?");
            levelLabel.setText("i = 0");
            loadFactorLabel.setText("0.000");
        }
    }

    private void initializeStructure(ActionEvent e) {
        try {
            int initialSize = Integer.parseInt(initialSizeField.getText().trim());
            int keyLength = Integer.parseInt(keyLengthField.getText().trim());

            if (initialSize <= 0 || keyLength <= 0) {
                throw new NumberFormatException("Los valores deben ser positivos");
            }

            if (initialSize < 2) {
                throw new Exception("El tamaño inicial debe ser al menos 2");
            }

            dynamicStructure = new DynamicStructure(initialSize, keyLength, "TOTAL");

            enableControls(true);
            updateDisplayValues();
            updateDynamicVisualization();

            logToTerminal(String.format("Estructura dinámica inicializada con expansión total"), "success");
            logToTerminal(String.format("Tamaño inicial: %d cubetas, claves de %d dígitos",
                    initialSize, keyLength), "info");
            logToTerminal("Nivel inicial: i = 0", "info");

            expansionHistoryArea.setText("=== HISTORIAL DE EXPANSIONES ===\n");
            expansionHistoryArea.append("Estructura inicializada\n");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Ingrese valores numéricos válidos",
                    "Error de configuración",
                    JOptionPane.ERROR_MESSAGE);
            logToTerminal("Error en configuración: " + ex.getMessage(), "error");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Error de configuración", JOptionPane.ERROR_MESSAGE);
            logToTerminal("Error: " + ex.getMessage(), "error");
        }
    }

    private void insertKey(ActionEvent e) {
        try {
            String key = searchKeyField.getText().trim();

            if (key.isEmpty()) {
                throw new Exception("Ingrese una clave válida");
            }

            if (key.length() != dynamicStructure.getKeyLength()) {
                throw new Exception(String.format("La clave debe tener %d dígitos",
                        dynamicStructure.getKeyLength()));
            }

            // Calcular bucket antes de insertar
            int bucketIndex = Integer.parseInt(key) % dynamicStructure.getCurrentTableSize();

            logToTerminal(String.format("Insertando clave '%s' en bucket %d", key, bucketIndex), "info");

            // Insertar (esto puede provocar expansión automática)
            int oldLevel = dynamicStructure.getCurrentLevel();
            dynamicStructure.insert(key);
            int newLevel = dynamicStructure.getCurrentLevel();

            // Verificar si hubo expansión
            if (newLevel > oldLevel) {
                logToTerminal(String.format("¡EXPANSIÓN AUTOMÁTICA! Nivel %d → %d", oldLevel, newLevel), "warning");
                updateExpansionHistory();
            }

            logToTerminal(String.format("Clave '%s' insertada exitosamente", key), "success");

            updateDisplayValues();
            updateDynamicVisualization();
            dynamicVisualization.highlightBucket(bucketIndex % dynamicStructure.getCurrentTableSize());
            searchKeyField.setText("");

        } catch (Exception ex) {
            logToTerminal("Error al insertar: " + ex.getMessage(), "error");
        }
    }

    private void searchKey(ActionEvent e) {
        try {
            String key = searchKeyField.getText().trim();

            if (key.isEmpty()) {
                throw new Exception("Ingrese una clave válida");
            }

            int bucketIndex = dynamicStructure.search(key);

            if (bucketIndex != -1) {
                logToTerminal(String.format("Clave '%s' encontrada en bucket %d", key, bucketIndex), "success");
                dynamicVisualization.highlightBucket(bucketIndex);
            } else {
                logToTerminal(String.format("Clave '%s' no encontrada", key), "error");
            }

        } catch (Exception ex) {
            logToTerminal("Error en búsqueda: " + ex.getMessage(), "error");
        }
    }

    private void deleteKey(ActionEvent e) {
        try {
            String key = searchKeyField.getText().trim();

            if (key.isEmpty()) {
                throw new Exception("Ingrese una clave válida");
            }

            boolean removed = dynamicStructure.remove(key);

            if (removed) {
                logToTerminal(String.format("Clave '%s' eliminada", key), "success");
                updateDisplayValues();
                updateDynamicVisualization();
                searchKeyField.setText("");
            } else {
                logToTerminal(String.format("Clave '%s' no encontrada", key), "error");
            }

        } catch (Exception ex) {
            logToTerminal("Error al eliminar: " + ex.getMessage(), "error");
        }
    }

    private void forceExpansion(ActionEvent e) {
        try {
            int oldLevel = dynamicStructure.getCurrentLevel();
            int oldSize = dynamicStructure.getCurrentTableSize();

            // Simular expansión forzada
            DynamicStructure.ExpansionEvent event = new DynamicStructure.ExpansionEvent(
                    oldLevel + 1, oldSize, (int)(oldSize * 2), "Expansión forzada por usuario"
            );

            // Crear nueva estructura con nivel incrementado
            DynamicStructure newStructure = new DynamicStructure(
                    dynamicStructure.getInitialSize(),
                    dynamicStructure.getKeyLength(),
                    "TOTAL"
            );

            // Incrementar nivel manualmente
            while (newStructure.getCurrentLevel() < oldLevel + 1) {
                try {
                    // Forzar expansión insertando elementos ficticios
                    for (int i = 0; i < newStructure.getCurrentTableSize(); i++) {
                        String dummyKey = String.format("%0" + dynamicStructure.getKeyLength() + "d",
                                1000000 + i);
                        try {
                            newStructure.insert(dummyKey);
                        } catch (Exception ex) {
                            // Ignorar duplicados
                        }
                    }
                } catch (Exception ex) {
                    break;
                }
            }

            // Limpiar y transferir elementos reales
            newStructure.reset();
            for (int i = 0; i < dynamicStructure.getBuckets().size(); i++) {
                for (Object element : dynamicStructure.getBucketElements(i)) {
                    try {
                        newStructure.insert(element);
                    } catch (Exception ex) {
                        // Continuar con otros elementos
                    }
                }
            }

            // Forzar el nivel correcto
            while (newStructure.getCurrentLevel() < oldLevel + 1) {
                // Crear estructura temporal con el nivel deseado
                DynamicStructure tempStructure = new DynamicStructure(
                        dynamicStructure.getInitialSize(),
                        dynamicStructure.getKeyLength(),
                        "TOTAL"
                );

                // Simular expansiones hasta el nivel deseado
                for (int level = 0; level < oldLevel + 1; level++) {
                    tempStructure.getExpansionHistory().add(
                            new DynamicStructure.ExpansionEvent(level + 1,
                                    (int)(dynamicStructure.getInitialSize() * Math.pow(2, level)),
                                    (int)(dynamicStructure.getInitialSize() * Math.pow(2, level + 1)),
                                    level == oldLevel ? "Expansión forzada" : "Expansión automática")
                    );
                }

                dynamicStructure = tempStructure;

                // Reinsertar elementos
                for (int i = 0; i < newStructure.getBuckets().size(); i++) {
                    for (Object element : newStructure.getBucketElements(i)) {
                        try {
                            dynamicStructure.insert(element);
                        } catch (Exception ex) {
                            // Continuar
                        }
                    }
                }
                break;
            }

            int newLevel = dynamicStructure.getCurrentLevel();
            int newSize = dynamicStructure.getCurrentTableSize();

            logToTerminal(String.format("Expansión forzada: Nivel %d → %d", oldLevel, newLevel), "warning");
            logToTerminal(String.format("Tamaño: %d → %d cubetas", oldSize, newSize), "info");

            updateDisplayValues();
            updateDynamicVisualization();
            updateExpansionHistory();

        } catch (Exception ex) {
            logToTerminal("Error en expansión forzada: " + ex.getMessage(), "error");
        }
    }

    private void updateDisplayValues() {
        if (dynamicStructure != null) {
            int level = dynamicStructure.getCurrentLevel();
            int currentSize = dynamicStructure.getCurrentTableSize();
            double loadFactor = dynamicStructure.getLoadFactor();

            currentSizeLabel.setText(String.format("2^%d * %d = %d",
                    level, dynamicStructure.getInitialSize(), currentSize));
            levelLabel.setText("i = " + level);
            loadFactorLabel.setText(String.format("%.3f", loadFactor));

            // Cambiar color según factor de carga
            if (loadFactor > 0.75) {
                loadFactorLabel.setForeground(Constants.ERROR_COLOR);
            } else if (loadFactor > 0.5) {
                loadFactorLabel.setForeground(Constants.WARNING_COLOR);
            } else {
                loadFactorLabel.setForeground(Constants.SUCCESS_COLOR);
            }
        }
    }

    private void updateDynamicVisualization() {
        dynamicVisualization.setDynamicStructure(dynamicStructure);
    }

    private void updateExpansionHistory() {
        if (dynamicStructure != null) {
            expansionHistoryArea.setText("=== HISTORIAL DE EXPANSIONES ===\n");
            for (DynamicStructure.ExpansionEvent event : dynamicStructure.getExpansionHistory()) {
                expansionHistoryArea.append(String.format("Nivel %d: %d → %d cubetas (%s)\n",
                        event.level, event.oldSize, event.newSize, event.trigger));
            }
            expansionHistoryArea.setCaretPosition(expansionHistoryArea.getDocument().getLength());
        }
    }

    private void enableControls(boolean enabled) {
        insertButton.setEnabled(enabled);
        searchButton.setEnabled(enabled);
        deleteButton.setEnabled(enabled);
        forceExpansionButton.setEnabled(enabled);
    }

    @Override
    protected void exportToCSV() {
        if (dynamicStructure == null) {
            JOptionPane.showMessageDialog(this, "No hay datos para exportar",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar como CSV");

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(fileChooser.getSelectedFile()))) {
                writer.println("#CONFIGURACION");
                writer.println("Algoritmo,Expansion Total");
                writer.println("Tamaño_Inicial," + dynamicStructure.getInitialSize());
                writer.println("Longitud_Clave," + dynamicStructure.getKeyLength());
                writer.println("Nivel_Actual," + dynamicStructure.getCurrentLevel());
                writer.println("Tamaño_Actual," + dynamicStructure.getCurrentTableSize());
                writer.println("Factor_Carga," + dynamicStructure.getLoadFactor());

                writer.println("#BUCKETS");
                for (int i = 0; i < dynamicStructure.getBuckets().size(); i++) {
                    for (Object element : dynamicStructure.getBucketElements(i)) {
                        writer.println(String.format("BUCKET_%d,%s", i, element));
                    }
                }

                writer.println("#HISTORIAL_EXPANSIONES");
                for (DynamicStructure.ExpansionEvent event : dynamicStructure.getExpansionHistory()) {
                    writer.println(String.format("%d,%d,%d,%s",
                            event.level, event.oldSize, event.newSize, event.trigger));
                }

                writer.println("#CONSOLA");
                writer.println(terminalArea.getText().replace("\n", "\\n"));

                logToTerminal("Datos exportados exitosamente", "success");

            } catch (IOException ex) {
                logToTerminal("Error al exportar: " + ex.getMessage(), "error");
            }
        }
    }

    @Override
    protected void importFromCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Importar desde CSV");

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
                        if (parts[0].equals("Tamaño_Inicial")) {
                            initialSizeField.setText(parts[1]);
                        } else if (parts[0].equals("Longitud_Clave")) {
                            keyLengthField.setText(parts[1]);
                        }
                    }
                }

                updateCalculatedValues();
                logToTerminal("Configuración importada. Presione Inicializar para cargar datos", "success");

            } catch (IOException ex) {
                logToTerminal("Error al importar: " + ex.getMessage(), "error");
            }
        }
    }

    @Override
    protected void resetAll() {
        int option = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de que desea resetear toda la estructura?",
                "Confirmar Reset",
                JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            if (dynamicStructure != null) {
                dynamicStructure.reset();
            }

            initialSizeField.setText("4");
            keyLengthField.setText("");
            searchKeyField.setText("");

            updateCalculatedValues();
            enableControls(false);
            terminalArea.setText("");
            expansionHistoryArea.setText("");

            updateDynamicVisualization();
            logToTerminal("Sistema reseteado", "warning");
        }
    }
}
