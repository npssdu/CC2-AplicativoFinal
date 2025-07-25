package busquedas;

import utils.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class BusquedaLineal extends AlgorithmWindow {
    private JTextField sizeField, keyLengthField, searchKeyField;
    private JButton initButton, insertButton, searchButton, deleteButton, sortButton;
    private List<Integer> searchSteps;

    public BusquedaLineal(JFrame parent) {
        super(parent, "Búsqueda Lineal");
        logToTerminal("Sistema de Búsqueda Lineal iniciado", "info");
        searchSteps = new ArrayList<>();
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
        configPanel.add(new JLabel("Tamaño de estructura:"), gbc);
        gbc.gridx = 1;
        sizeField = new JTextField(10);
        sizeField.setPreferredSize(Constants.INPUT_SIZE);
        configPanel.add(sizeField, gbc);

        // Longitud de clave
        gbc.gridx = 2; gbc.gridy = 0;
        configPanel.add(new JLabel("Longitud de clave:"), gbc);
        gbc.gridx = 3;
        keyLengthField = new JTextField(10);
        keyLengthField.setPreferredSize(Constants.INPUT_SIZE);
        configPanel.add(keyLengthField, gbc);

        // Botón inicializar
        gbc.gridx = 4; gbc.gridy = 0;
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

            logToTerminal(String.format("Estructura inicializada: %d elementos, claves de %d dígitos",
                    size, keyLength), "success");
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

            int index = structure.insert(key);
            logToTerminal(String.format("Clave '%s' insertada en índice %d", key, index), "success");
            updateVisualization();
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

            searchSteps.clear();
            int result = performLinearSearch(key);

            if (result != -1) {
                logToTerminal(String.format("Clave '%s' encontrada en índice %d (Comparaciones: %d)",
                        key, result + 1, searchSteps.size()), "success");
                highlightSearchResult(result, true);
            } else {
                logToTerminal(String.format("Clave '%s' no encontrada (Comparaciones: %d)",
                        key, searchSteps.size()), "error");
            }

            // Mostrar pasos de búsqueda
            logSearchSteps(key);

        } catch (Exception ex) {
            logToTerminal("Error en búsqueda: " + ex.getMessage(), "error");
        }
    }

    private int performLinearSearch(String key) {
        Object[] data = structure.getData();

        for (int i = 0; i < data.length; i++) {
            searchSteps.add(i + 1); // Guardar paso para logging

            if (data[i] != null && data[i].toString().equals(key)) {
                return i;
            }
        }

        return -1;
    }

    private void logSearchSteps(String key) {
        logToTerminal("Pasos de búsqueda lineal:", "info");
        for (int i = 0; i < searchSteps.size(); i++) {
            int index = searchSteps.get(i);
            Object value = structure.getAt(index - 1);
            String valueStr = (value == null) ? "null" : value.toString();
            String comparison = valueStr.equals(key) ? "✓ ENCONTRADO" : "✗ No coincide";

            logToTerminal(String.format("  Paso %d: Índice[%d] = '%s' → %s",
                    i + 1, index, valueStr, comparison), "info");
        }
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

    private void sortStructure(ActionEvent e) {
        try {
            structure.sort();
            logToTerminal("Estructura ordenada", "success");
            updateVisualization();

        } catch (Exception ex) {
            logToTerminal("Error al ordenar: " + ex.getMessage(), "error");
        }
    }

    private void highlightSearchResult(int index, boolean found) {
        // Implementar highlighting visual del resultado
        SwingUtilities.invokeLater(() -> {
            updateVisualization();
            // Aquí se podría agregar animación o highlighting especial
        });
    }

    private void enableControls(boolean enabled) {
        insertButton.setEnabled(enabled);
        searchButton.setEnabled(enabled);
        deleteButton.setEnabled(enabled);
        sortButton.setEnabled(enabled);
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
                writer.println("Algoritmo,Busqueda Lineal");
                writer.println("Tamaño," + structure.getSize());
                writer.println("Longitud_Clave," + structure.getKeyLength());

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

            enableControls(false);
            terminalArea.setText("");
            searchSteps.clear();

            updateVisualization();
            logToTerminal("Sistema reseteado", "warning");
        }
    }
}
