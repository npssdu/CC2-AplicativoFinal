package arboles;

import utils.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ArbolesDigitales extends AlgorithmWindow {
    private JTextField messageField, individualCharField;
    private JButton buildTreeButton, insertCharButton, searchCharButton, deleteCharButton;
    private TreeVisualization treeVisualization;
    private TreeNode root;
    private List<Character> insertedCharacters;

    public ArbolesDigitales(JFrame parent) {
        super(parent, "Árboles Digitales");
        logToTerminal("Sistema de Árboles Digitales iniciado", "info");
        logToTerminal("Algoritmo: Últimos 5 bits del código ASCII", "info");
        insertedCharacters = new ArrayList<>();
        initializeTreeVisualization();
    }

    private void initializeTreeVisualization() {
        treeVisualization = new TreeVisualization();

        // Reemplazar el panel de visualización por defecto
        visualizationPanel.removeAll();
        visualizationPanel.setLayout(new BorderLayout());
        visualizationPanel.add(treeVisualization, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.setBackground(Constants.BACKGROUND_COLOR);

        JButton centerButton = createStyledButton("Centrar Vista", Constants.INFO_COLOR);
        centerButton.addActionListener(e -> treeVisualization.centerView());
        controlPanel.add(centerButton);

        JButton showStructureButton = createStyledButton("Mostrar Estructura", Constants.WARNING_COLOR);
        showStructureButton.addActionListener(e -> showTreeStructure());
        controlPanel.add(showStructureButton);

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
        buildTreeButton.addActionListener(this::buildTree);
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
        insertCharButton.addActionListener(this::insertCharacter);
        controlsPanel.add(insertCharButton, gbc);

        gbc.gridx = 3;
        searchCharButton = createStyledButton("Buscar", Constants.INFO_COLOR);
        searchCharButton.addActionListener(this::searchCharacter);
        controlsPanel.add(searchCharButton, gbc);

        gbc.gridx = 4;
        deleteCharButton = createStyledButton("Eliminar", Constants.ERROR_COLOR);
        deleteCharButton.addActionListener(this::deleteCharacter);
        controlsPanel.add(deleteCharButton, gbc);

        return controlsPanel;
    }

    private void buildTree(ActionEvent e) {
        try {
            String message = messageField.getText().trim().toUpperCase();

            if (message.isEmpty()) {
                throw new Exception("Ingrese un mensaje válido");
            }

            // Reinicializar estructura
            root = null;
            insertedCharacters.clear();
            treeVisualization.setRoot(null);

            logToTerminal(String.format("Construyendo árbol digital para: \"%s\"", message), "info");
            logToTerminal("=== PROCESO DE CONSTRUCCIÓN ===", "info");

            // Obtener caracteres únicos y ordenarlos alfabéticamente para consistencia
            java.util.Set<Character> uniqueChars = new java.util.LinkedHashSet<>();
            for (char c : message.toCharArray()) {
                uniqueChars.add(c);
            }

            // Mostrar tabla de conversión antes de insertar
            logToTerminal("=== TABLA DE CONVERSIÓN ===", "info");
            logToTerminal("ASCII\tLetra\tBinario\t\tÚltimos 5 bits", "info");
            logToTerminal("-----\t-----\t-------\t\t--------------", "info");

            for (Character character : uniqueChars) {
                int ascii = (int) character;
                String fullBinary = Integer.toBinaryString(ascii);
                String last5Bits = getLast5Bits(fullBinary);
                logToTerminal(String.format("%d\t%c\t%s\t\t%s", ascii, character, fullBinary, last5Bits), "info");
            }

            logToTerminal("\n=== INSERTANDO CARACTERES ===", "info");

            // Insertar cada carácter único del mensaje
            for (Character character : uniqueChars) {
                insertDigitalNode(character);
                insertedCharacters.add(character);
            }

            treeVisualization.setRoot(root);
            logToTerminal("Árbol digital construido exitosamente", "success");
            logToTerminal("Use el mouse para navegar: arrastrar (mover), rueda (zoom), clic (seleccionar)", "info");
            showTreeStructure();

        } catch (Exception ex) {
            logToTerminal("Error al construir árbol: " + ex.getMessage(), "error");
        }
    }

    private void insertCharacter(ActionEvent e) {
        try {
            String input = individualCharField.getText().trim().toUpperCase();

            if (input.isEmpty() || input.length() != 1) {
                throw new Exception("Ingrese un solo carácter");
            }

            char character = input.charAt(0);

            if (insertedCharacters.contains(character)) {
                throw new Exception(String.format("El carácter '%c' ya existe en el árbol", character));
            }

            insertDigitalNode(character);
            insertedCharacters.add(character);

            treeVisualization.setRoot(root);
            individualCharField.setText("");

        } catch (Exception ex) {
            logToTerminal("Error al insertar: " + ex.getMessage(), "error");
        }
    }

    private void insertDigitalNode(char character) throws Exception {
        int ascii = (int) character;
        String fullBinary = Integer.toBinaryString(ascii);
        String last5Bits = getLast5Bits(fullBinary);

        logToTerminal(String.format("Insertando '%c': ASCII=%d, Binario=%s, LSB5=%s",
                character, ascii, fullBinary, last5Bits), "info");

        // Verificar si ya existe un nodo con este camino
        TreeNode existingNode = findNodeByPath(last5Bits);
        if (existingNode != null && existingNode.data != null) {
            throw new Exception(String.format("Colisión: ya existe el carácter '%c' en la ruta %s",
                    existingNode.data, last5Bits));
        }

        if (root == null) {
            root = new TreeNode();
            root.level = 0;
        }

        TreeNode current = root;

        // Navegar/crear el camino hasta el nodo final
        for (int i = 0; i < last5Bits.length(); i++) {
            char bit = last5Bits.charAt(i);

            logToTerminal(String.format("  Bit %d: '%c' → %s",
                    i + 1, bit, bit == '0' ? "izquierda" : "derecha"), "info");

            if (i == last5Bits.length() - 1) {
                // Último bit - aquí va el carácter
                TreeNode leafNode = new TreeNode(character);
                leafNode.ascii = ascii;
                leafNode.binaryPath = last5Bits;
                leafNode.level = current.level + 1;
                leafNode.parent = current;
                leafNode.isLeaf = true;

                if (bit == '0') {
                    if (current.left != null) {
                        throw new Exception("Posición ya ocupada en ruta izquierda");
                    }
                    current.left = leafNode;
                } else {
                    if (current.right != null) {
                        throw new Exception("Posición ya ocupada en ruta derecha");
                    }
                    current.right = leafNode;
                }

                logToTerminal(String.format("Carácter '%c' insertado en posición final", character), "success");

            } else {
                // Bit intermedio - crear o navegar nodo interno
                TreeNode nextNode;

                if (bit == '0') {
                    if (current.left == null) {
                        current.left = new TreeNode(); // Nodo interno sin data
                        current.left.parent = current;
                        current.left.level = current.level + 1;
                        current.left.isLeaf = false;
                    }
                    nextNode = current.left;
                } else {
                    if (current.right == null) {
                        current.right = new TreeNode(); // Nodo interno sin data
                        current.right.parent = current;
                        current.right.level = current.level + 1;
                        current.right.isLeaf = false;
                    }
                    nextNode = current.right;
                }

                // Verificar que no sea un nodo hoja existente
                if (nextNode.data != null) {
                    throw new Exception(String.format("Colisión: el camino está bloqueado por '%c'", nextNode.data));
                }

                current = nextNode;
            }
        }

        logToTerminal(String.format("Carácter '%c' insertado exitosamente (ruta: %s)",
                character, last5Bits), "success");
    }

    private String getLast5Bits(String binary) {
        if (binary.length() <= 5) {
            // Rellenar con ceros a la izquierda si es necesario
            while (binary.length() < 5) {
                binary = "0" + binary;
            }
            return binary;
        } else {
            // Tomar los últimos 5 bits
            return binary.substring(binary.length() - 5);
        }
    }

    private TreeNode findNodeByPath(String binaryPath) {
        if (root == null) return null;

        TreeNode current = root;

        for (char bit : binaryPath.toCharArray()) {
            if (bit == '0') {
                current = current.left;
            } else {
                current = current.right;
            }

            if (current == null) {
                return null;
            }
        }

        return current;
    }

    private void searchCharacter(ActionEvent e) {
        try {
            String input = individualCharField.getText().trim().toUpperCase();

            if (input.isEmpty() || input.length() != 1) {
                throw new Exception("Ingrese un solo carácter");
            }

            char character = input.charAt(0);
            TreeNode result = searchDigitalNode(character);

            if (result != null) {
                logToTerminal(String.format("Carácter '%c' encontrado", character), "success");
                treeVisualization.highlightNode(result);
            } else {
                logToTerminal(String.format("Carácter '%c' no encontrado", character), "error");
            }

        } catch (Exception ex) {
            logToTerminal("Error en búsqueda: " + ex.getMessage(), "error");
        }
    }

    private TreeNode searchDigitalNode(char character) {
        if (root == null) return null;

        int ascii = (int) character;
        String fullBinary = Integer.toBinaryString(ascii);
        String last5Bits = getLast5Bits(fullBinary);

        logToTerminal(String.format("Buscando '%c': ruta binaria %s", character, last5Bits), "info");

        TreeNode node = findNodeByPath(last5Bits);

        if (node != null && node.data != null && node.data.equals(character)) {
            return node;
        }

        return null;
    }

    private void deleteCharacter(ActionEvent e) {
        try {
            String input = individualCharField.getText().trim().toUpperCase();

            if (input.isEmpty() || input.length() != 1) {
                throw new Exception("Ingrese un solo carácter");
            }

            char character = input.charAt(0);
            boolean deleted = deleteDigitalNode(character);

            if (deleted) {
                logToTerminal(String.format("Carácter '%c' eliminado", character), "success");
                insertedCharacters.remove(Character.valueOf(character));
                treeVisualization.setRoot(root);
                individualCharField.setText("");
            } else {
                logToTerminal(String.format("Carácter '%c' no encontrado", character), "error");
            }

        } catch (Exception ex) {
            logToTerminal("Error al eliminar: " + ex.getMessage(), "error");
        }
    }

    private boolean deleteDigitalNode(char character) {
        TreeNode node = searchDigitalNode(character);
        if (node == null) return false;

        // Remover el nodo hoja
        TreeNode parent = node.parent;
        if (parent != null) {
            if (parent.left == node) {
                parent.left = null;
            } else {
                parent.right = null;
            }

            // Limpiar nodos internos innecesarios
            cleanupEmptyInternalNodes(parent);
        } else {
            // Es la raíz
            root = null;
        }

        return true;
    }

    private void cleanupEmptyInternalNodes(TreeNode node) {
        if (node == null || node == root) return;

        // Solo limpiar si es un nodo interno sin hijos y sin data
        if (node.data == null && node.left == null && node.right == null) {
            TreeNode parent = node.parent;
            if (parent != null) {
                if (parent.left == node) {
                    parent.left = null;
                } else {
                    parent.right = null;
                }

                // Continuar limpieza hacia arriba
                cleanupEmptyInternalNodes(parent);
            }
        }
    }

    private void showTreeStructure() {
        if (root == null) {
            logToTerminal("=== ESTRUCTURA DEL ÁRBOL ===", "info");
            logToTerminal("Árbol vacío", "warning");
            return;
        }

        logToTerminal("=== ESTRUCTURA DEL ÁRBOL ===", "info");
        logToTerminal("Caracteres insertados: " + insertedCharacters.toString(), "info");
        logToTerminal("Estructura detallada:", "info");

        showNodeStructure(root, "", true);
    }

    private void showNodeStructure(TreeNode node, String prefix, boolean isLast) {
        if (node == null) return;

        String nodeInfo;
        if (node.data != null) {
            nodeInfo = String.format("'%c' (ASCII: %d, Ruta: %s)",
                    node.data, node.ascii, node.binaryPath);
        } else {
            nodeInfo = "[Nodo interno]";
        }

        String connector = isLast ? "└── " : "├── ";
        logToTerminal(prefix + connector + nodeInfo, "info");

        String newPrefix = prefix + (isLast ? "    " : "│   ");

        boolean hasLeft = node.left != null;
        boolean hasRight = node.right != null;

        if (hasLeft) {
            logToTerminal(newPrefix + "├── 0 (izquierda):", "info");
            showNodeStructure(node.left, newPrefix + "│   ", !hasRight);
        }
        if (hasRight) {
            logToTerminal(newPrefix + (hasLeft ? "└── " : "├── ") + "1 (derecha):", "info");
            showNodeStructure(node.right, newPrefix + (hasLeft ? "    " : "│   "), true);
        }
    }

    @Override
    protected void exportToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Árbol Digital como CSV");

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(fileChooser.getSelectedFile()))) {
                writer.println("#CONFIGURACION");
                writer.println("Algoritmo,Arboles Digitales");
                writer.println("Mensaje," + messageField.getText());
                writer.println("Caracteres_Insertados," + String.join("", insertedCharacters.stream().map(String::valueOf).toArray(String[]::new)));

                writer.println("#ESTRUCTURA_ARBOL");
                if (root != null) {
                    exportTreeStructure(writer, root, "");
                }

                writer.println("#CONSOLA");
                writer.println(terminalArea.getText().replace("\n", "\\n"));

                logToTerminal("Árbol exportado exitosamente", "success");

            } catch (IOException ex) {
                logToTerminal("Error al exportar: " + ex.getMessage(), "error");
            }
        }
    }

    private void exportTreeStructure(PrintWriter writer, TreeNode node, String path) {
        if (node == null) return;

        if (node.data != null) {
            writer.println(String.format("HOJA,%s,%c,%d,%s",
                    path, node.data, node.ascii, node.binaryPath));
        } else {
            writer.println(String.format("INTERNO,%s,null,0,", path));
        }

        exportTreeStructure(writer, node.left, path + "0");
        exportTreeStructure(writer, node.right, path + "1");
    }

    @Override
    protected void importFromCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Importar Árbol Digital desde CSV");

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
                            messageField.setText(parts.length > 1 ? parts[1] : "");
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
            insertedCharacters.clear();

            treeVisualization.setRoot(null);
            terminalArea.setText("");

            logToTerminal("Árbol digital reseteado", "warning");
        }
    }
}
