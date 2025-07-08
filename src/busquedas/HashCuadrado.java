package busquedas;

import utils.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;

public class HashCuadrado extends AlgorithmWindow {
    private JTextField sizeField, keyLengthField, searchKeyField;
    private JComboBox<String> collisionMethodCombo;
    private JButton initButton, insertButton, searchButton, deleteButton, resetHashButton;

    public HashCuadrado(JFrame parent) {
        super(parent, "Hash Cuadrado");
        logToTerminal("Sistema Hash Cuadrado iniciado", "info");
        logToTerminal("Método: Cuadrado medio - extracción de dígitos centrales", "info");
    }

    @Override
    protected JPanel createConfigurationPanel() {
        JPanel configPanel = new JPanel(new GridBagLayout());
        configPanel.setBackground(Constants.BACKGROUND_COLOR);
        configPanel.setBorder(BorderFactory.createTitledBorder("Configuración"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Tamaño de estructura
        gbc.gridx = 0; gbc.gridy = 0;
        configPanel.add(new JLabel("Tamaño de tabla:"), gbc);
        gbc.gridx = 1;
        sizeField = new JTextField(10);
        sizeField.setPreferredSize(Constants.INPUT_SIZE);
        configPanel.add(sizeField, gbc);

        // Longitud de clave
        gbc.gridx = 0; gbc.gridy = 1;
        configPanel.add(new JLabel("Longitud de clave:"), gbc);
        gbc.gridx = 1;
        keyLengthField = new JTextField(10);
        keyLengthField.setPreferredSize(Constants.INPUT_SIZE);
        configPanel.add(keyLengthField, gbc);

        // Método de colisión
        gbc.gridx = 0; gbc.gridy = 2;
        configPanel.add(new JLabel("Método de colisión:"), gbc);
        gbc.gridx = 1;
        collisionMethodCombo = new JComboBox<>(new String[]{
                "Sondeo Lineal", "Sondeo Cuadrático", "Estructuras Anidadas", "Estructuras Enlazadas"
        });
        collisionMethodCombo.setPreferredSize(Constants.INPUT_SIZE);
        configPanel.add(collisionMethodCombo, gbc);

        // Botón inicializar
        gbc.gridx = 2; gbc.gridy = 1;
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

    private void initializeStructure(ActionEvent e) {
        try {
            int size = Integer.parseInt(sizeField.getText().trim());
            int keyLength = Integer.parseInt(keyLengthField.getText().trim());

            if (size <= 0 || keyLength <= 0) {
                throw new NumberFormatException("Los valores deben ser positivos");
            }

            structure = new DataStructure(size, keyLength);
            structure.initialize();

            // Habilitar controles
            enableControls(true);

            String collisionMethod = (String) collisionMethodCombo.getSelectedItem();
            logToTerminal(String.format("Tabla hash inicializada: %d posiciones, claves de %d dígitos",
                    size, keyLength), "success");
            logToTerminal("Método: Hash Cuadrado con " + collisionMethod, "info");
            updateVisualization();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Ingrese valores numéricos válidos",
                    "Error de configuración",
                    JOptionPane.ERROR_MESSAGE);
            logToTerminal("Error en configuración: " + ex.getMessage(), "error");
        }
    }

    private void insertKey(ActionEvent e) {
        try {
            String key = searchKeyField.getText().trim();

            if (key.isEmpty()) {
                throw new Exception("Ingrese una clave válida");
            }

            if (key.length() != structure.getKeyLength()) {
                throw new Exception(String.format("La clave debe tener %d dígitos",
                        structure.getKeyLength()));
            }

            // Calcular hash cuadrado
            int hashValue = calculateHashCuadrado(key);
            int originalIndex = hashValue - 1; // Convertir a índice base 0

            // Verificar colisión y resolver
            int finalIndex = resolveCollision(key, originalIndex);

            structure.insertAt(key, finalIndex);
            logToTerminal(String.format("Clave '%s' insertada en índice %d", key, finalIndex + 1), "success");
            updateVisualization();
            searchKeyField.setText("");

        } catch (Exception ex) {
            logToTerminal("Error al insertar: " + ex.getMessage(), "error");
        }
    }

    private int calculateHashCuadrado(String key) {
        long numericKey = Long.parseLong(key);
        long squared = numericKey * numericKey;
        String squaredStr = String.valueOf(squared);

        logToTerminal(String.format("Clave: %s", key), "info");
        logToTerminal(String.format("Cuadrado: %s² = %s", key, squaredStr), "info");

        // Extraer dígitos centrales
        int length = squaredStr.length();
        int start = length / 4;
        int end = length - start;

        if (start >= end) {
            start = 0;
            end = Math.min(3, length); // Tomar hasta 3 dígitos si el número es muy pequeño
        }

        String middleDigits = squaredStr.substring(start, end);
        logToTerminal(String.format("Dígitos centrales extraídos: %s", middleDigits), "info");

        int middleValue = Integer.parseInt(middleDigits);
        int hashValue = (middleValue % structure.getSize()) + 1;

        logToTerminal(String.format("Hash calculado: (%s %% %d) + 1 = %d",
                middleDigits, structure.getSize(), hashValue), "info");

        return hashValue;
    }

    private int resolveCollision(String key, int originalIndex) throws Exception {
        String collisionMethod = (String) collisionMethodCombo.getSelectedItem();

        if (structure.getAt(originalIndex) == null) {
            // No hay colisión
            return originalIndex;
        }

        logToTerminal(String.format("Colisión detectada en índice %d", originalIndex + 1), "warning");

        switch (collisionMethod) {
            case "Sondeo Lineal":
                return resolveLinearProbing(key, originalIndex);
            case "Sondeo Cuadrático":
                return resolveQuadraticProbing(key, originalIndex);
            case "Estructuras Anidadas":
                return resolveNestedStructures(key, originalIndex);
            case "Estructuras Enlazadas":
                return resolveLinkedStructures(key, originalIndex);
            default:
                throw new Exception("Método de colisión no implementado");
        }
    }

    private int resolveLinearProbing(String key, int originalIndex) throws Exception {
        int index = originalIndex;
        int attempts = 0;

        do {
            index = (index + 1) % structure.getSize(); // Comportamiento circular
            attempts++;

            logToTerminal(String.format("Sondeo lineal: intento %d, índice %d",
                    attempts, index + 1), "info");

            if (structure.getAt(index) == null) {
                logToTerminal(String.format("Posición libre encontrada en índice %d", index + 1), "success");
                return index;
            }

            if (attempts >= structure.getSize()) {
                throw new Exception("Tabla llena - no se puede insertar");
            }

        } while (index != originalIndex);

        throw new Exception("No se pudo resolver la colisión");
    }

    private int resolveQuadraticProbing(String key, int originalIndex) throws Exception {
        int attempts = 1;

        while (attempts <= structure.getSize()) {
            int index = (originalIndex + attempts * attempts) % structure.getSize();

            logToTerminal(String.format("Sondeo cuadrático: intento %d², índice %d",
                    attempts, index + 1), "info");

            if (structure.getAt(index) == null) {
                logToTerminal(String.format("Posición libre encontrada en índice %d", index + 1), "success");
                return index;
            }

            attempts++;
        }

        throw new Exception("No se pudo resolver la colisión con sondeo cuadrático");
    }

    private int resolveNestedStructures(String key, int originalIndex) throws Exception {
        for (int offset = 1; offset < structure.getSize(); offset++) {
            int index = (originalIndex + offset) % structure.getSize();

            if (structure.getAt(index) == null) {
                logToTerminal(String.format("Estructura anidada: posición %d disponible", index + 1), "info");
                return index;
            }
        }

        throw new Exception("No hay espacio en estructuras anidadas");
    }

    private int resolveLinkedStructures(String key, int originalIndex) throws Exception {
        int index = originalIndex;
        int attempts = 0;

        do {
            index = (index + 1) % structure.getSize();
            attempts++;

            logToTerminal(String.format("Estructura enlazada: enlace %d, índice %d",
                    attempts, index + 1), "info");

            if (structure.getAt(index) == null) {
                return index;
            }

        } while (attempts < structure.getSize());

        throw new Exception("No se pudo enlazar estructura");
    }

    private void searchKey(ActionEvent e) {
        try {
            String key = searchKeyField.getText().trim();

            if (key.isEmpty()) {
                throw new Exception("Ingrese una clave válida");
            }

            // Calcular hash original
            int hashValue = calculateHashCuadrado(key);
            int originalIndex = hashValue - 1;

            logToTerminal(String.format("Buscando clave '%s'", key), "info");

            // Buscar usando el mismo método de resolución de colisiones
            int foundIndex = findKey(key, originalIndex);

            if (foundIndex != -1) {
                logToTerminal(String.format("Clave '%s' encontrada en índice %d", key, foundIndex + 1), "success");
                highlightSearchResult(foundIndex, true);
            } else {
                logToTerminal(String.format("Clave '%s' no encontrada", key), "error");
            }

        } catch (Exception ex) {
            logToTerminal("Error en búsqueda: " + ex.getMessage(), "error");
        }
    }

    private int findKey(String key, int originalIndex) {
        String collisionMethod = (String) collisionMethodCombo.getSelectedItem();

        // Verificar posición original
        if (key.equals(structure.getAt(originalIndex))) {
            return originalIndex;
        }

        // Buscar según método de colisión
        switch (collisionMethod) {
            case "Sondeo Lineal":
                return findWithLinearProbing(key, originalIndex);
            case "Sondeo Cuadrático":
                return findWithQuadraticProbing(key, originalIndex);
            default:
                return findWithLinearProbing(key, originalIndex); // Fallback
        }
    }

    private int findWithLinearProbing(String key, int originalIndex) {
        int index = originalIndex;
        int attempts = 0;

        do {
            if (key.equals(structure.getAt(index))) {
                return index;
            }

            index = (index + 1) % structure.getSize();
            attempts++;

        } while (index != originalIndex && attempts < structure.getSize());

        return -1;
    }

    private int findWithQuadraticProbing(String key, int originalIndex) {
        for (int attempts = 1; attempts <= structure.getSize(); attempts++) {
            int index = (originalIndex + attempts * attempts) % structure.getSize();

            if (key.equals(structure.getAt(index))) {
                return index;
            }
        }

        return -1;
    }

    private void deleteKey(ActionEvent e) {
        try {
            String key = searchKeyField.getText().trim();

            if (key.isEmpty()) {
                throw new Exception("Ingrese una clave válida");
            }

            boolean removed = structure.remove(key);

            if (removed) {
                logToTerminal(String.format("Clave '%s' eliminada", key), "success");
                updateVisualization();
                searchKeyField.setText("");
            } else {
                logToTerminal(String.format("Clave '%s' no encontrada", key), "error");
            }

        } catch (Exception ex) {
            logToTerminal("Error al eliminar: " + ex.getMessage(), "error");
        }
    }

    private void resetHash(ActionEvent e) {
        if (structure != null && structure.isInitialized()) {
            structure.initialize();
            updateVisualization();
            logToTerminal("Tabla hash limpiada", "warning");
        }
    }

    private void highlightSearchResult(int index, boolean found) {
        SwingUtilities.invokeLater(() -> {
            updateVisualization();
        });
    }

    private void enableControls(boolean enabled) {
        insertButton.setEnabled(enabled);
        searchButton.setEnabled(enabled);
        deleteButton.setEnabled(enabled);
        resetHashButton.setEnabled(enabled);
    }

    @Override
    protected void exportToCSV() {
        if (structure == null || !structure.isInitialized()) {
            JOptionPane.showMessageDialog(this, "No hay datos para exportar",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar como CSV");

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(fileChooser.getSelectedFile()))) {
                writer.println("#CONFIGURACION");
                writer.println("Algoritmo,Hash Cuadrado");
                writer.println("Tamaño," + structure.getSize());
                writer.println("Longitud_Clave," + structure.getKeyLength());
                writer.println("Metodo_Colision," + collisionMethodCombo.getSelectedItem());

                writer.println("#DATOS");
                Object[] data = structure.getData();
                for (int i = 0; i < data.length; i++) {
                    writer.println((i + 1) + "," + (data[i] == null ? "" : data[i]));
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
                        if (parts[0].equals("Tamaño")) {
                            sizeField.setText(parts[1]);
                        } else if (parts[0].equals("Longitud_Clave")) {
                            keyLengthField.setText(parts[1]);
                        } else if (parts[0].equals("Metodo_Colision")) {
                            collisionMethodCombo.setSelectedItem(parts[1]);
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
            if (structure != null) {
                structure.reset();
            }

            sizeField.setText("");
            keyLengthField.setText("");
            searchKeyField.setText("");
            collisionMethodCombo.setSelectedIndex(0);

            enableControls(false);
            terminalArea.setText("");

            updateVisualization();
            logToTerminal("Sistema reseteado", "warning");
        }
    }
}
