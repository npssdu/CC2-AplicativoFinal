package arboles;

import utils.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.*;
import java.util.List;

public class ArbolesHuffman extends AlgorithmWindow {
    private JTextField messageField, individualCharField;
    private JButton buildTreeButton, insertCharButton, searchCharButton, deleteCharButton;
    private TreeVisualization treeVisualization;
    private TreeNode root;
    private Map<Character, String> huffmanCodes;
    private Map<Character, Integer> frequencies;

    public ArbolesHuffman(JFrame parent) {
        super(parent, "Árboles de Huffman");
        logToTerminal("Sistema de Árboles de Huffman iniciado", "info");
        logToTerminal("Algoritmo: Codificación por frecuencias", "info");
        huffmanCodes = new HashMap<>();
        frequencies = new HashMap<>();
        initializeTreeVisualization();
    }

    private void initializeTreeVisualization() {
        treeVisualization = new TreeVisualization();

        visualizationPanel.removeAll();
        visualizationPanel.setLayout(new BorderLayout());
        visualizationPanel.add(treeVisualization, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.setBackground(Constants.BACKGROUND_COLOR);

        JButton centerButton = createStyledButton("Centrar Vista", Constants.INFO_COLOR);
        centerButton.addActionListener(e -> treeVisualization.centerView());
        controlPanel.add(centerButton);

        JButton showCodesButton = createStyledButton("Mostrar Códigos", Constants.WARNING_COLOR);
        showCodesButton.addActionListener(e -> showHuffmanCodes());
        controlPanel.add(showCodesButton);

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

        // Mensaje completo
        gbc.gridx = 0; gbc.gridy = 0;
        configPanel.add(new JLabel("Mensaje completo:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        messageField = new JTextField(20);
        messageField.setPreferredSize(new Dimension(300, 25));
        configPanel.add(messageField, gbc);

        // Botón construir árbol
        gbc.gridx = 3; gbc.gridwidth = 1;
        buildTreeButton = createStyledButton("Construir Árbol", Constants.PRIMARY_COLOR);
        buildTreeButton.addActionListener(this::buildHuffmanTree);
        configPanel.add(buildTreeButton, gbc);

        return configPanel;
    }

    @Override
    protected JPanel createControlsPanel() {
        JPanel controlsPanel = new JPanel(new GridBagLayout());
        controlsPanel.setBackground(Constants.BACKGROUND_COLOR);
        controlsPanel.setBorder(BorderFactory.createTitledBorder("Controles Individuales"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Campo de carácter individual
        gbc.gridx = 0; gbc.gridy = 0;
        controlsPanel.add(new JLabel("Carácter:"), gbc);
        gbc.gridx = 1;
        individualCharField = new JTextField(5);
        individualCharField.setPreferredSize(Constants.INPUT_SIZE);
        controlsPanel.add(individualCharField, gbc);

        // Botones
        gbc.gridx = 2;
        insertCharButton = createStyledButton("Insertar", Constants.SUCCESS_COLOR);
        insertCharButton.setEnabled(false); // No aplicable para Huffman
        controlsPanel.add(insertCharButton, gbc);

        gbc.gridx = 3;
        searchCharButton = createStyledButton("Buscar", Constants.INFO_COLOR);
        searchCharButton.addActionListener(this::searchCharacter);
        controlsPanel.add(searchCharButton, gbc);

        gbc.gridx = 4;
        deleteCharButton = createStyledButton("Eliminar", Constants.ERROR_COLOR);
        deleteCharButton.setEnabled(false); // No aplicable para Huffman
        controlsPanel.add(deleteCharButton, gbc);

        return controlsPanel;
    }

    private void buildHuffmanTree(ActionEvent e) {
        try {
            String message = messageField.getText().trim().toUpperCase();

            if (message.isEmpty()) {
                throw new Exception("Ingrese un mensaje válido");
            }

            root = null;
            huffmanCodes.clear();
            frequencies.clear();

            logToTerminal(String.format("Construyendo árbol de Huffman para: \"%s\"", message), "info");
            logToTerminal("=== CÁLCULO DE FRECUENCIAS ===", "info");

            // Calcular frecuencias
            calculateFrequencies(message);

            // Construir árbol de Huffman
            buildHuffmanTreeFromFrequencies();

            // Generar códigos
            generateHuffmanCodes();

            treeVisualization.setRoot(root);
            logToTerminal("Árbol de Huffman construido exitosamente", "success");
            showHuffmanCodes();

        } catch (Exception ex) {
            logToTerminal("Error al construir árbol: " + ex.getMessage(), "error");
        }
    }

    private void calculateFrequencies(String message) {
        for (char c : message.toCharArray()) {
            frequencies.put(c, frequencies.getOrDefault(c, 0) + 1);
        }

        logToTerminal("Tabla de frecuencias:", "info");
        List<Character> sortedChars = new ArrayList<>(frequencies.keySet());
        sortedChars.sort(Comparator.comparing(frequencies::get));

        for (char c : sortedChars) {
            int freq = frequencies.get(c);
            double prob = (double) freq / message.length();
            logToTerminal(String.format("  '%c': %d (%.3f)", c, freq, prob), "info");
        }
    }

    private void buildHuffmanTreeFromFrequencies() {
        // Crear cola de prioridad con nodos hoja
        PriorityQueue<TreeNode> pq = new PriorityQueue<>((a, b) -> {
            int freqCompare = Integer.compare(a.frequency, b.frequency);
            if (freqCompare != 0) return freqCompare;

            // En caso de empate, priorizar caracteres individuales
            if (a.data != null && b.data == null) return -1;
            if (a.data == null && b.data != null) return 1;
            if (a.data != null && b.data != null) {
                return Character.compare((Character) a.data, (Character) b.data);
            }
            return 0;
        });

        // Agregar nodos hoja
        for (Map.Entry<Character, Integer> entry : frequencies.entrySet()) {
            TreeNode node = new TreeNode(entry.getKey());
            node.frequency = entry.getValue();
            node.isLeaf = true;
            pq.offer(node);
        }

        logToTerminal("=== PROCESO DE CONSTRUCCIÓN ===", "info");
        int step = 1;

        // Construir árbol
        while (pq.size() > 1) {
            TreeNode left = pq.poll();
            TreeNode right = pq.poll();

            TreeNode parent = new TreeNode();
            parent.frequency = left.frequency + right.frequency;
            parent.left = left;
            parent.right = right;
            parent.isLeaf = false;

            left.parent = parent;
            right.parent = parent;

            String leftDesc = left.data != null ? "'" + left.data + "'" : "grupo";
            String rightDesc = right.data != null ? "'" + right.data + "'" : "grupo";

            logToTerminal(String.format("Paso %d: %s(%d) + %s(%d) = %d",
                    step++, leftDesc, left.frequency, rightDesc, right.frequency, parent.frequency), "info");

            pq.offer(parent);
        }

        root = pq.poll();
    }

    private void generateHuffmanCodes() {
        huffmanCodes.clear();
        if (root != null) {
            if (root.data != null) {
                // Caso especial: solo un carácter
                huffmanCodes.put((Character) root.data, "0");
            } else {
                generateCodesRecursive(root, "");
            }
        }
    }

    private void generateCodesRecursive(TreeNode node, String code) {
        if (node == null) return;

        if (node.data != null) {
            // Nodo hoja
            huffmanCodes.put((Character) node.data, code);
            node.binaryPath = code;
        } else {
            // Nodo interno
            generateCodesRecursive(node.left, code + "0");
            generateCodesRecursive(node.right, code + "1");
        }
    }

    private void showHuffmanCodes() {
        if (huffmanCodes.isEmpty()) {
            logToTerminal("No hay códigos Huffman generados", "warning");
            return;
        }

        logToTerminal("=== CÓDIGOS HUFFMAN GENERADOS ===", "info");

        // Ordenar por longitud de código y luego alfabéticamente
        List<Map.Entry<Character, String>> sortedCodes = new ArrayList<>(huffmanCodes.entrySet());
        sortedCodes.sort((a, b) -> {
            int lengthCompare = Integer.compare(a.getValue().length(), b.getValue().length());
            if (lengthCompare != 0) return lengthCompare;
            return Character.compare(a.getKey(), b.getKey());
        });

        for (Map.Entry<Character, String> entry : sortedCodes) {
            char character = entry.getKey();
            String code = entry.getValue();
            int frequency = frequencies.get(character);

            logToTerminal(String.format("  '%c': %s (freq: %d, bits: %d)",
                    character, code, frequency, code.length()), "info");
        }

        // Calcular eficiencia
        calculateCompressionEfficiency();
    }

    private void calculateCompressionEfficiency() {
        if (huffmanCodes.isEmpty() || frequencies.isEmpty()) return;

        int totalChars = frequencies.values().stream().mapToInt(Integer::intValue).sum();
        int huffmanBits = 0;

        for (Map.Entry<Character, String> entry : huffmanCodes.entrySet()) {
            char character = entry.getKey();
            String code = entry.getValue();
            int frequency = frequencies.get(character);
            huffmanBits += frequency * code.length();
        }

        int originalBits = totalChars * 8; // ASCII estándar
        double compressionRatio = (double) huffmanBits / originalBits;
        double savings = (1 - compressionRatio) * 100;

        logToTerminal("=== ANÁLISIS DE COMPRESIÓN ===", "info");
        logToTerminal(String.format("Bits originales (ASCII): %d", originalBits), "info");
        logToTerminal(String.format("Bits con Huffman: %d", huffmanBits), "info");
        logToTerminal(String.format("Ratio de compresión: %.3f", compressionRatio), "info");
        logToTerminal(String.format("Ahorro: %.1f%%", savings), "success");
    }

    private void searchCharacter(ActionEvent e) {
        try {
            String input = individualCharField.getText().trim().toUpperCase();

            if (input.isEmpty() || input.length() != 1) {
                throw new Exception("Ingrese un solo carácter");
            }

            char character = input.charAt(0);

            if (!huffmanCodes.containsKey(character)) {
                logToTerminal(String.format("Carácter '%c' no está en el árbol", character), "error");
                return;
            }

            String code = huffmanCodes.get(character);
            TreeNode result = searchInHuffmanTree(character);

            if (result != null) {
                logToTerminal(String.format("Carácter '%c' encontrado con código: %s", character, code), "success");
                treeVisualization.highlightNode(result);

                // Mostrar ruta de navegación
                logToTerminal("Ruta de navegación:", "info");
                for (int i = 0; i < code.length(); i++) {
                    char bit = code.charAt(i);
                    logToTerminal(String.format("  Bit %d: '%c' → %s",
                            i + 1, bit, bit == '0' ? "izquierda" : "derecha"), "info");
                }
            } else {
                logToTerminal(String.format("Error en la estructura del árbol", character), "error");
            }

        } catch (Exception ex) {
            logToTerminal("Error en búsqueda: " + ex.getMessage(), "error");
        }
    }

    private TreeNode searchInHuffmanTree(char character) {
        if (root == null || !huffmanCodes.containsKey(character)) return null;

        String code = huffmanCodes.get(character);
        TreeNode current = root;

        for (char bit : code.toCharArray()) {
            if (bit == '0') {
                current = current.left;
            } else {
                current = current.right;
            }

            if (current == null) return null;
        }

        return current;
    }

    @Override
    protected void exportToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Árbol de Huffman como CSV");

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(fileChooser.getSelectedFile()))) {
                writer.println("#CONFIGURACION");
                writer.println("Algoritmo,Arboles Huffman");
                writer.println("Mensaje," + messageField.getText());

                writer.println("#FRECUENCIAS");
                for (Map.Entry<Character, Integer> entry : frequencies.entrySet()) {
                    writer.println(entry.getKey() + "," + entry.getValue());
                }

                writer.println("#CODIGOS_HUFFMAN");
                for (Map.Entry<Character, String> entry : huffmanCodes.entrySet()) {
                    writer.println(entry.getKey() + "," + entry.getValue());
                }

                writer.println("#ESTRUCTURA_ARBOL");
                if (root != null) {
                    exportHuffmanTreeStructure(writer, root, "");
                }

                writer.println("#CONSOLA");
                writer.println(terminalArea.getText().replace("\n", "\\n"));

                logToTerminal("Árbol de Huffman exportado exitosamente", "success");

            } catch (IOException ex) {
                logToTerminal("Error al exportar: " + ex.getMessage(), "error");
            }
        }
    }

    private void exportHuffmanTreeStructure(PrintWriter writer, TreeNode node, String path) {
        if (node == null) return;

        if (node.data != null) {
            writer.println(String.format("HOJA,%s,%c,%d,%s",
                    path, node.data, node.frequency, node.binaryPath));
        } else {
            writer.println(String.format("INTERNO,%s,null,%d,",
                    path, node.frequency));
        }

        exportHuffmanTreeStructure(writer, node.left, path + "0");
        exportHuffmanTreeStructure(writer, node.right, path + "1");
    }

    @Override
    protected void importFromCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Importar Árbol de Huffman desde CSV");

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
                        if (parts[0].equals("Mensaje")) {
                            messageField.setText(parts[1]);
                        }
                    }
                }

                logToTerminal("Configuración importada. Use 'Construir Árbol' para reconstruir", "success");

            } catch (IOException ex) {
                logToTerminal("Error al importar: " + ex.getMessage(), "error");
            }
        }
    }

    @Override
    protected void resetAll() {
        int option = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de que desea resetear el árbol?",
                "Confirmar Reset",
                JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            root = null;
            messageField.setText("");
            individualCharField.setText("");
            huffmanCodes.clear();
            frequencies.clear();

            treeVisualization.setRoot(null);
            terminalArea.setText("");

            logToTerminal("Árbol de Huffman reseteado", "warning");
        }
    }
}
