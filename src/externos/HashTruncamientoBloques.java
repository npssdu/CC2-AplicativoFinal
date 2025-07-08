package externos;

import utils.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;

public class HashTruncamientoBloques extends AlgorithmWindow {
    private JTextField totalElementsField, keyLengthField, searchKeyField, positionsField;
    private JLabel numBlocksLabel, elementsPerBlockLabel;
    private JComboBox<String> collisionMethodCombo;
    private JButton initButton, insertButton, searchButton, deleteButton, resetHashButton;
    private BlockVisualization blockVisualization;
    private BlockStructure blockStructure;

    public HashTruncamientoBloques(JFrame parent) {
        super(parent, "Hash Truncamiento por Bloques");
        logToTerminal("Sistema Hash Truncamiento por Bloques iniciado", "info");
        logToTerminal("Método: Extracción de posiciones específicas en bloques", "info");
        logToTerminal("Fórmula: Bloques = √(n), hash = (dígitos_extraídos % n) + 1", "info");
        initializeBlockVisualization();
        showTestCases();
    }

    private void initializeBlockVisualization() {
        blockVisualization = new BlockVisualization();

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

    private void showTestCases() {
        logToTerminal("=== CASOS DE PRUEBA INTEGRADOS ===", "info");
        logToTerminal("1. Clave: 10203040, Posiciones: 1,4,7 → Resultado esperado: 105", "info");
        logToTerminal("2. Clave: 25303540, Posiciones: 1,4,7 → Resultado esperado: 205", "info");
        logToTerminal("3. Clave: 50153028, Posiciones: 1,4,7 → Resultado esperado: 553", "info");
        logToTerminal("=====================================", "info");
    }

    @Override
    protected JPanel createConfigurationPanel() {
        JPanel configPanel = new JPanel(new GridBagLayout());
        configPanel.setBackground(Constants.BACKGROUND_COLOR);
        configPanel.setBorder(BorderFactory.createTitledBorder("Configuración"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Total elementos
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

        // Información calculada - Primera fila
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

        // Posiciones de dígitos
        gbc.gridx = 0; gbc.gridy = 2;
        configPanel.add(new JLabel("Posiciones (ej: 1,4,7):"), gbc);
        gbc.gridx = 1;
        positionsField = new JTextField(10);
        positionsField.setText("1,4,7"); // Valor por defecto
        positionsField.setPreferredSize(Constants.INPUT_SIZE);
        configPanel.add(positionsField, gbc);

        // Método de colisión
        gbc.gridx = 2; gbc.gridy = 2;
        configPanel.add(new JLabel("Método de colisión:"), gbc);
        gbc.gridx = 3;
        collisionMethodCombo = new JComboBox<>(new String[]{
                "Sondeo Lineal", "Sondeo Cuadrático", "Estructuras Anidadas", "Estructuras Enlazadas"
        });
        collisionMethodCombo.setPreferredSize(Constants.INPUT_SIZE);
        configPanel.add(collisionMethodCombo, gbc);

        // Botón inicializar
        gbc.gridx = 4; gbc.gridy = 1; gbc.gridheight = 2;
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
        resetHashButton = createStyledButton("Reset Hash", Constants.WARNING_COLOR);
        resetHashButton.setEnabled(false);
        resetHashButton.addActionListener(this::resetHash);
        controlsPanel.add(resetHashButton, gbc);

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
            String positionsStr = positionsField.getText().trim();

            if (totalElements <= 0 || keyLength <= 0) {
                throw new NumberFormatException("Los valores deben ser positivos");
            }

            if (totalElements < 4) {
                throw new Exception("El número total de elementos debe ser al menos 4");
            }

            // Validar posiciones
            validatePositions(positionsStr, keyLength);

            blockStructure = new BlockStructure(totalElements, keyLength);
            blockStructure.initialize();

            enableControls(true);

            String collisionMethod = (String) collisionMethodCombo.getSelectedItem();
            logToTerminal(String.format("Tabla hash por bloques inicializada: %d elementos", totalElements), "success");
            logToTerminal(String.format("Distribución: %d bloques de %d elementos",
                    blockStructure.getNumBlocks(), blockStructure.getElementsPerBlock()), "info");
            logToTerminal(String.format("Truncamiento: posiciones %s con %s",
                    positionsStr, collisionMethod), "info");
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

    private void validatePositions(String positionsStr, int keyLength) throws Exception {
        if (positionsStr.isEmpty()) {
            throw new Exception("Debe especificar al menos una posición");
        }

        String[] positions = positionsStr.split(",");
        for (String pos : positions) {
            try {
                int position = Integer.parseInt(pos.trim());
                if (position < 1 || position > keyLength) {
                    throw new Exception(String.format("Posición %d fuera de rango (1-%d)",
                            position, keyLength));
                }
            } catch (NumberFormatException e) {
                throw new Exception("Formato de posiciones inválido. Use: 1,4,7");
            }
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

            // Calcular hash por truncamiento
            int hashValue = calculateHashTruncamiento(key);
            BlockStructure.BlockPosition position = blockStructure.getHashPosition(hashValue);

            logToTerminal(String.format("Posición calculada: bloque %d, posición %d",
                    position.blockIndex + 1, position.position + 1), "info");

            // Verificar colisión y resolver
            BlockStructure.BlockPosition finalPosition = resolveCollision(key, position);

            blockStructure.insertAt(key, finalPosition.blockIndex, finalPosition.position);
            logToTerminal(String.format("Clave '%s' insertada en bloque %d, posición %d",
                    key, finalPosition.blockIndex + 1, finalPosition.position + 1), "success");

            updateBlockVisualization();
            blockVisualization.highlightPosition(finalPosition.blockIndex, finalPosition.position);
            searchKeyField.setText("");

        } catch (Exception ex) {
            logToTerminal("Error al insertar: " + ex.getMessage(), "error");
        }
    }

    private int calculateHashTruncamiento(String key) throws Exception {
        String positionsStr = positionsField.getText().trim();
        String[] positionArray = positionsStr.split(",");

        logToTerminal(String.format("Calculando hash por truncamiento para clave: %s", key), "info");
        logToTerminal(String.format("Posiciones a extraer: %s", positionsStr), "info");

        StringBuilder extractedDigits = new StringBuilder();

        for (String posStr : positionArray) {
            int position = Integer.parseInt(posStr.trim());

            if (position <= key.length()) {
                char digit = key.charAt(position - 1); // Convertir a índice base 0
                extractedDigits.append(digit);
                logToTerminal(String.format("Posición %d: '%c'", position, digit), "info");
            } else {
                logToTerminal(String.format("Posición %d fuera de rango, ignorada", position), "warning");
            }
        }

        if (extractedDigits.length() == 0) {
            throw new Exception("No se pudieron extraer dígitos válidos");
        }

        String extractedStr = extractedDigits.toString();
        int extractedValue = Integer.parseInt(extractedStr);

        logToTerminal(String.format("Dígitos extraídos: %s = %d", extractedStr, extractedValue), "info");

        int hashValue = (extractedValue % blockStructure.getTotalElements()) + 1;
        logToTerminal(String.format("Hash calculado: (%d %% %d) + 1 = %d",
                extractedValue, blockStructure.getTotalElements(), hashValue), "info");

        return hashValue;
    }

    private BlockStructure.BlockPosition resolveCollision(String key, BlockStructure.BlockPosition originalPosition) throws Exception {
        String collisionMethod = (String) collisionMethodCombo.getSelectedItem();

        if (blockStructure.getAt(originalPosition.blockIndex, originalPosition.position) == null) {
            return originalPosition;
        }

        logToTerminal(String.format("Colisión detectada en bloque %d, posición %d",
                originalPosition.blockIndex + 1, originalPosition.position + 1), "warning");

        switch (collisionMethod) {
            case "Sondeo Lineal":
                return resolveLinearProbing(key, originalPosition);
            case "Sondeo Cuadrático":
                return resolveQuadraticProbing(key, originalPosition);
            case "Estructuras Anidadas":
                return resolveNestedStructures(key, originalPosition);
            case "Estructuras Enlazadas":
                return resolveLinkedStructures(key, originalPosition);
            default:
                throw new Exception("Método de colisión no implementado");
        }
    }

    private BlockStructure.BlockPosition resolveLinearProbing(String key, BlockStructure.BlockPosition original) throws Exception {
        int totalElements = blockStructure.getTotalElements();
        int attempts = 0;

        for (int offset = 1; offset < totalElements; offset++) {
            int newAbsolutePos = ((original.blockIndex * blockStructure.getElementsPerBlock() +
                    original.position + offset) % totalElements);

            int newBlock = newAbsolutePos / blockStructure.getElementsPerBlock();
            int newPos = newAbsolutePos % blockStructure.getElementsPerBlock();

            attempts++;
            logToTerminal(String.format("Sondeo lineal: intento %d → bloque %d, posición %d",
                    attempts, newBlock + 1, newPos + 1), "info");

            if (blockStructure.getAt(newBlock, newPos) == null) {
                logToTerminal(String.format("Posición libre encontrada en intento %d", attempts), "success");
                return new BlockStructure.BlockPosition(newBlock, newPos);
            }

            if (attempts >= totalElements) {
                throw new Exception("Estructura llena - no se puede insertar");
            }
        }

        throw new Exception("No se pudo resolver la colisión");
    }

    private BlockStructure.BlockPosition resolveQuadraticProbing(String key, BlockStructure.BlockPosition original) throws Exception {
        int totalElements = blockStructure.getTotalElements();
        int originalAbsPos = original.blockIndex * blockStructure.getElementsPerBlock() + original.position;

        for (int attempts = 1; attempts <= totalElements; attempts++) {
            int newAbsolutePos = (originalAbsPos + attempts * attempts) % totalElements;
            int newBlock = newAbsolutePos / blockStructure.getElementsPerBlock();
            int newPos = newAbsolutePos % blockStructure.getElementsPerBlock();

            logToTerminal(String.format("Sondeo cuadrático: intento %d² → bloque %d, posición %d",
                    attempts, newBlock + 1, newPos + 1), "info");

            if (blockStructure.getAt(newBlock, newPos) == null) {
                logToTerminal(String.format("Posición libre encontrada en intento %d²", attempts), "success");
                return new BlockStructure.BlockPosition(newBlock, newPos);
            }
        }

        throw new Exception("No se pudo resolver la colisión con sondeo cuadrático");
    }

    private BlockStructure.BlockPosition resolveNestedStructures(String key, BlockStructure.BlockPosition original) throws Exception {
        for (int blockOffset = 0; blockOffset < blockStructure.getNumBlocks(); blockOffset++) {
            int targetBlock = (original.blockIndex + blockOffset) % blockStructure.getNumBlocks();

            for (int posOffset = 0; posOffset < blockStructure.getElementsPerBlock(); posOffset++) {
                int targetPos = (original.position + posOffset) % blockStructure.getElementsPerBlock();

                if (blockStructure.getAt(targetBlock, targetPos) == null) {
                    logToTerminal(String.format("Estructura anidada: bloque %d, posición %d disponible",
                            targetBlock + 1, targetPos + 1), "info");
                    return new BlockStructure.BlockPosition(targetBlock, targetPos);
                }
            }
        }

        throw new Exception("No hay espacio en estructuras anidadas");
    }

    private BlockStructure.BlockPosition resolveLinkedStructures(String key, BlockStructure.BlockPosition original) throws Exception {
        return resolveLinearProbing(key, original);
    }

    private void searchKey(ActionEvent e) {
        try {
            String key = searchKeyField.getText().trim();

            if (key.isEmpty()) {
                throw new Exception("Ingrese una clave válida");
            }

            logToTerminal(String.format("Buscando clave '%s' con hash truncamiento por bloques", key), "info");

            // Calcular hash original
            int hashValue = calculateHashTruncamiento(key);
            BlockStructure.BlockPosition originalPosition = blockStructure.getHashPosition(hashValue);

            // Buscar usando el mismo método de resolución de colisiones
            BlockStructure.BlockPosition foundPosition = findKey(key, originalPosition);

            if (foundPosition != null) {
                logToTerminal(String.format("Clave '%s' encontrada en bloque %d, posición %d",
                        key, foundPosition.blockIndex + 1, foundPosition.position + 1), "success");
                blockVisualization.highlightPosition(foundPosition.blockIndex, foundPosition.position);
            } else {
                logToTerminal(String.format("Clave '%s' no encontrada", key), "error");
            }

        } catch (Exception ex) {
            logToTerminal("Error en búsqueda: " + ex.getMessage(), "error");
        }
    }

    private BlockStructure.BlockPosition findKey(String key, BlockStructure.BlockPosition originalPosition) {
        String collisionMethod = (String) collisionMethodCombo.getSelectedItem();

        // Verificar posición original
        if (key.equals(blockStructure.getAt(originalPosition.blockIndex, originalPosition.position))) {
            return originalPosition;
        }

        // Buscar según método de colisión
        try {
            switch (collisionMethod) {
                case "Sondeo Lineal":
                    return findWithLinearProbing(key, originalPosition);
                case "Sondeo Cuadrático":
                    return findWithQuadraticProbing(key, originalPosition);
                default:
                    return findWithLinearProbing(key, originalPosition);
            }
        } catch (Exception e) {
            return null;
        }
    }

    private BlockStructure.BlockPosition findWithLinearProbing(String key, BlockStructure.BlockPosition original) {
        int totalElements = blockStructure.getTotalElements();

        for (int offset = 1; offset < totalElements; offset++) {
            int newAbsolutePos = ((original.blockIndex * blockStructure.getElementsPerBlock() +
                    original.position + offset) % totalElements);

            int newBlock = newAbsolutePos / blockStructure.getElementsPerBlock();
            int newPos = newAbsolutePos % blockStructure.getElementsPerBlock();

            if (key.equals(blockStructure.getAt(newBlock, newPos))) {
                return new BlockStructure.BlockPosition(newBlock, newPos);
            }
        }

        return null;
    }

    private BlockStructure.BlockPosition findWithQuadraticProbing(String key, BlockStructure.BlockPosition original) {
        int totalElements = blockStructure.getTotalElements();
        int originalAbsPos = original.blockIndex * blockStructure.getElementsPerBlock() + original.position;

        for (int attempts = 1; attempts <= totalElements; attempts++) {
            int newAbsolutePos = (originalAbsPos + attempts * attempts) % totalElements;
            int newBlock = newAbsolutePos / blockStructure.getElementsPerBlock();
            int newPos = newAbsolutePos % blockStructure.getElementsPerBlock();

            if (key.equals(blockStructure.getAt(newBlock, newPos))) {
                return new BlockStructure.BlockPosition(newBlock, newPos);
            }
        }

        return null;
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

    private void resetHash(ActionEvent e) {
        if (blockStructure != null && blockStructure.isInitialized()) {
            blockStructure.initialize();
            updateBlockVisualization();
            logToTerminal("Tabla hash por bloques limpiada", "warning");
        }
    }

    private void updateBlockVisualization() {
        blockVisualization.setBlockStructure(blockStructure);
    }

    private void enableControls(boolean enabled) {
        insertButton.setEnabled(enabled);
        searchButton.setEnabled(enabled);
        deleteButton.setEnabled(enabled);
        resetHashButton.setEnabled(enabled);
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
                writer.println("Algoritmo,Hash Truncamiento Bloques");
                writer.println("Total_Elementos," + blockStructure.getTotalElements());
                writer.println("Longitud_Clave," + blockStructure.getKeyLength());
                writer.println("Posiciones," + positionsField.getText());
                writer.println("Metodo_Colision," + collisionMethodCombo.getSelectedItem());

                writer.println("#DATOS_BLOQUES");
                Object[][] blocks = blockStructure.getBlocks();
                for (int i = 0; i < blocks.length; i++) {
                    for (int j = 0; j < blocks[i].length; j++) {
                        writer.println(String.format("BLOQUE_%d,POSICION_%d,%s",
                                i + 1, j + 1, blocks[i][j] == null ? "" : blocks[i][j]));
                    }
                }

                writer.println("#CASOS_PRUEBA");
                writer.println("10203040,1|4|7,105");
                writer.println("25303540,1|4|7,205");
                writer.println("50153028,1|4|7,553");

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
                        } else if (parts[0].equals("Longitud_Clave")) {
                            keyLengthField.setText(parts[1]);
                        } else if (parts[0].equals("Posiciones")) {
                            positionsField.setText(parts[1]);
                        } else if (parts[0].equals("Metodo_Colision")) {
                            collisionMethodCombo.setSelectedItem(parts[1]);
                        }
                    }
                }

                updateBlockInfo();
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
            positionsField.setText("1,4,7");
            collisionMethodCombo.setSelectedIndex(0);

            updateBlockInfo();
            enableControls(false);
            terminalArea.setText("");

            updateBlockVisualization();
            logToTerminal("Sistema reseteado", "warning");
        }
    }
}
