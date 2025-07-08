package externos;

import utils.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;

public class BusquedaLinealBloques extends AlgorithmWindow {
    private JTextField totalElementsField, keyLengthField, searchKeyField;
    private JLabel numBlocksLabel, elementsPerBlockLabel;
    private JButton initButton, insertButton, searchButton, deleteButton, sortButton;
    private BlockVisualization blockVisualization;
    private BlockStructure blockStructure;

    public BusquedaLinealBloques(JFrame parent) {
        super(parent, "Búsqueda Lineal por Bloques");
        logToTerminal("Sistema de Búsqueda Lineal por Bloques iniciado", "info");
        logToTerminal("Algoritmo: Búsqueda secuencial entre y dentro de bloques", "info");
        logToTerminal("Fórmula: Bloques = √(n), Elementos por bloque = n / bloques", "info");
        initializeBlockVisualization();
    }

    private void initializeBlockVisualization() {
        blockVisualization = new BlockVisualization();

        // Reemplazar el panel de visualización
        visualizationPanel.removeAll();
        visualizationPanel.setLayout(new BorderLayout());
        visualizationPanel.add(blockVisualization, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.setBackground(Constants.BACKGROUND_COLOR);

        JButton clearHighlightButton = createStyledButton("Limpiar Resaltado", Constants.WARNING_COLOR);
        clearHighlightButton.addActionListener(e -> blockVisualization.clearHighlight());
        controlPanel.add(clearHighlightButton);

        visualizationPanel.add(controlPanel, BorderLayout.SOUTH);
    }

    @Override
    protected JPanel createConfigurationPanel() {
        JPanel configPanel = new JPanel(new GridBagLayout());
        configPanel.setBackground(Constants.BACKGROUND_COLOR);
        configPanel.setBorder(BorderFactory.createTitledBorder("Configuración"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Número total de elementos
        gbc.gridx = 0; gbc.gridy = 0;
        configPanel.add(new JLabel("Total elementos (n):"), gbc);
        gbc.gridx = 1;
        totalElementsField = new JTextField(10);
        totalElementsField.setPreferredSize(Constants.INPUT_SIZE);
        totalElementsField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                updateBlockInfo();
            }
        });
        configPanel.add(totalElementsField, gbc);

        // Longitud de clave
        gbc.gridx = 2; gbc.gridy = 0;
        configPanel.add(new JLabel("Longitud de clave:"), gbc);
        gbc.gridx = 3;
        keyLengthField = new JTextField(10);
        keyLengthField.setPreferredSize(Constants.INPUT_SIZE);
        configPanel.add(keyLengthField, gbc);

        // Información calculada
        gbc.gridx = 0; gbc.gridy = 1;
        configPanel.add(new JLabel("Bloques:"), gbc);
        gbc.gridx = 1;
        numBlocksLabel = new JLabel("√(n) = ?");
        numBlocksLabel.setFont(Constants.MONO_FONT);
        configPanel.add(numBlocksLabel, gbc);

        gbc.gridx = 2; gbc.gridy = 1;
        configPanel.add(new JLabel("Elementos/bloque:"), gbc);
        gbc.gridx = 3;
        elementsPerBlockLabel = new JLabel("n/bloques = ?");
        elementsPerBlockLabel.setFont(Constants.MONO_FONT);
        configPanel.add(elementsPerBlockLabel, gbc);

        // Botón inicializar
        gbc.gridx = 4; gbc.gridy = 0; gbc.gridheight = 2;
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
        sortButton = createStyledButton("Ordenar", Constants.WARNING_COLOR);
        sortButton.setEnabled(false);
        sortButton.addActionListener(this::sortStructure);
        controlsPanel.add(sortButton, gbc);

        return controlsPanel;
    }

    private void updateBlockInfo() {
        try {
            int totalElements = Integer.parseInt(totalElementsField.getText().trim());
            if (totalElements > 0) {
                int numBlocks = (int) Math.floor(Math.sqrt(totalElements));
                int elementsPerBlock = totalElements / numBlocks;

                numBlocksLabel.setText("√(" + totalElements + ") = " + numBlocks);
                elementsPerBlockLabel.setText(totalElements + "/" + numBlocks + " = " + elementsPerBlock);
            }
        } catch (NumberFormatException e) {
            numBlocksLabel.setText("√(n) = ?");
            elementsPerBlockLabel.setText("n/bloques = ?");
        }
    }

    private void initializeStructure(ActionEvent e) {
        try {
            int totalElements = Integer.parseInt(totalElementsField.getText().trim());
            int keyLength = Integer.parseInt(keyLengthField.getText().trim());

            if (totalElements <= 0 || keyLength <= 0) {
                throw new NumberFormatException("Los valores deben ser positivos");
            }

            if (totalElements < 4) {
                throw new Exception("El número total de elementos debe ser al menos 4");
            }

            blockStructure = new BlockStructure(totalElements, keyLength);
            blockStructure.initialize();

            // Habilitar controles
            enableControls(true);

            logToTerminal(String.format("Estructura por bloques inicializada: %d elementos totales", totalElements), "success");
            logToTerminal(String.format("Distribución: %d bloques de %d elementos cada uno",
                    blockStructure.getNumBlocks(), blockStructure.getElementsPerBlock()), "info");
            updateBlockVisualization();

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

            if (key.length() != blockStructure.getKeyLength()) {
                throw new Exception(String.format("La clave debe tener %d dígitos",
                        blockStructure.getKeyLength()));
            }

            // Buscar primera posición libre
            BlockStructure.BlockPosition position = findFirstEmptyPosition();

            if (position == null) {
                throw new Exception("Estructura llena");
            }

            blockStructure.insertAt(key, position.blockIndex, position.position);
            logToTerminal(String.format("Clave '%s' insertada en bloque %d, posición %d",
                    key, position.blockIndex + 1, position.position + 1), "success");

            updateBlockVisualization();
            blockVisualization.highlightPosition(position.blockIndex, position.position);
            searchKeyField.setText("");

        } catch (Exception ex) {
            logToTerminal("Error al insertar: " + ex.getMessage(), "error");
        }
    }

    private BlockStructure.BlockPosition findFirstEmptyPosition() {
        Object[][] blocks = blockStructure.getBlocks();

        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks[i].length; j++) {
                if (blocks[i][j] == null) {
                    return new BlockStructure.BlockPosition(i, j);
                }
            }
        }
        return null;
    }

    private void searchKey(ActionEvent e) {
        try {
            String key = searchKeyField.getText().trim();

            if (key.isEmpty()) {
                throw new Exception("Ingrese una clave válida");
            }

            logToTerminal(String.format("Iniciando búsqueda lineal por bloques para: '%s'", key), "info");
            logToTerminal("=== PROCESO DE BÚSQUEDA ===", "info");

            BlockStructure.SearchResult result = blockStructure.linearSearch(key);

            // Mostrar pasos de búsqueda
            logSearchSteps(result.steps);

            // Animar búsqueda
            animateSearch(result.steps);

            if (result.found) {
                logToTerminal(String.format("Clave '%s' encontrada en bloque %d, posición %d",
                        key, result.blockIndex + 1, result.position + 1), "success");
                logToTerminal(String.format("Total de accesos: %d", result.steps.size()), "info");
                blockVisualization.highlightPosition(result.blockIndex, result.position);
            } else {
                logToTerminal(String.format("Clave '%s' no encontrada", key), "error");
                logToTerminal(String.format("Total de accesos: %d", result.steps.size()), "info");
            }

        } catch (Exception ex) {
            logToTerminal("Error en búsqueda: " + ex.getMessage(), "error");
        }
    }

    private void logSearchSteps(java.util.List<BlockStructure.SearchStep> steps) {
        int blockAccesses = 0;
        int elementAccesses = 0;

        for (BlockStructure.SearchStep step : steps) {
            if (step.type == BlockStructure.SearchStep.Type.BLOCK_ACCESS) {
                blockAccesses++;
                logToTerminal(String.format("Acceso %d: %s", blockAccesses, step.description), "info");
            } else {
                elementAccesses++;
                logToTerminal(String.format("  Elemento %d: %s", elementAccesses, step.description), "info");
            }
        }
    }

    private void animateSearch(java.util.List<BlockStructure.SearchStep> steps) {
        blockVisualization.showSearchSteps(steps);

        Timer timer = new Timer(500, null);
        final int[] currentStep = {0};

        timer.addActionListener(e -> {
            if (currentStep[0] < steps.size()) {
                blockVisualization.setCurrentStep(currentStep[0]);
                currentStep[0]++;
            } else {
                timer.stop();
                blockVisualization.setCurrentStep(-1);
            }
        });

        timer.start();
    }

    private void deleteKey(ActionEvent e) {
        try {
            String key = searchKeyField.getText().trim();

            if (key.isEmpty()) {
                throw new Exception("Ingrese una clave válida");
            }

            boolean removed = blockStructure.remove(key);

            if (removed) {
                logToTerminal(String.format("Clave '%s' eliminada", key), "success");
                updateBlockVisualization();
                searchKeyField.setText("");
            } else {
                logToTerminal(String.format("Clave '%s' no encontrada", key), "error");
            }

        } catch (Exception ex) {
            logToTerminal("Error al eliminar: " + ex.getMessage(), "error");
        }
    }

    private void sortStructure(ActionEvent e) {
        try {
            blockStructure.sort();
            logToTerminal("Estructura ordenada", "success");
            updateBlockVisualization();

        } catch (Exception ex) {
            logToTerminal("Error al ordenar: " + ex.getMessage(), "error");
        }
    }

    private void updateBlockVisualization() {
        blockVisualization.setBlockStructure(blockStructure);
    }

    private void enableControls(boolean enabled) {
        insertButton.setEnabled(enabled);
        searchButton.setEnabled(enabled);
        deleteButton.setEnabled(enabled);
        sortButton.setEnabled(enabled);
    }

    @Override
    protected void exportToCSV() {
        if (blockStructure == null || !blockStructure.isInitialized()) {
            JOptionPane.showMessageDialog(this, "No hay datos para exportar",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar como CSV");

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(fileChooser.getSelectedFile()))) {
                writer.println("#CONFIGURACION");
                writer.println("Algoritmo,Busqueda Lineal Bloques");
                writer.println("Total_Elementos," + blockStructure.getTotalElements());
                writer.println("Longitud_Clave," + blockStructure.getKeyLength());
                writer.println("Num_Bloques," + blockStructure.getNumBlocks());
                writer.println("Elementos_Por_Bloque," + blockStructure.getElementsPerBlock());

                writer.println("#DATOS_BLOQUES");
                Object[][] blocks = blockStructure.getBlocks();
                for (int i = 0; i < blocks.length; i++) {
                    for (int j = 0; j < blocks[i].length; j++) {
                        writer.println(String.format("BLOQUE_%d,POSICION_%d,%s",
                                i + 1, j + 1, blocks[i][j] == null ? "" : blocks[i][j]));
                    }
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
                        if (parts[0].equals("Total_Elementos")) {
                            totalElementsField.setText(parts[1]);
                            updateBlockInfo();
                        } else if (parts[0].equals("Longitud_Clave")) {
                            keyLengthField.setText(parts[1]);
                        }
                    }
                }

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
            if (blockStructure != null) {
                blockStructure.reset();
            }

            totalElementsField.setText("");
            keyLengthField.setText("");
            searchKeyField.setText("");

            numBlocksLabel.setText("√(n) = ?");
            elementsPerBlockLabel.setText("n/bloques = ?");

            enableControls(false);
            terminalArea.setText("");

            updateBlockVisualization();
            logToTerminal("Sistema reseteado", "warning");
        }
    }
}
