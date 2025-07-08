package arboles;

import utils.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;

public class ArbolesResiduosMultiples extends AlgorithmWindow {
    private JTextField messageField, individualCharField, mBitsField;
    private JButton buildTreeButton, insertCharButton, searchCharButton, deleteCharButton;
    private TreeVisualization treeVisualization;
    private TreeNode root;

    public ArbolesResiduosMultiples(JFrame parent) {
        super(parent, "Árboles por Residuos Múltiples");
        logToTerminal("Sistema de Árboles por Residuos Múltiples iniciado", "info");
        logToTerminal("Algoritmo: M bits por rama con 2^M enlaces", "info");
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
        configPanel.add(new JLabel("Mensaje:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        messageField = new JTextField(15);
        messageField.setPreferredSize(new Dimension(200, 25));
        configPanel.add(messageField, gbc);

        // M bits
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        configPanel.add(new JLabel("M (bits por rama):"), gbc);
        gbc.gridx = 1;
        mBitsField = new JTextField(10);
        mBitsField.setText("2"); // Valor por defecto
        mBitsField.setPreferredSize(Constants.INPUT_SIZE);
        configPanel.add(mBitsField, gbc);

        // Información de enlaces
        gbc.gridx = 2;
        JLabel infoLabel = new JLabel("(2^M enlaces posibles)");
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        infoLabel.setForeground(Constants.TEXT_COLOR);
        configPanel.add(infoLabel, gbc);

        // Botón construir árbol
        gbc.gridx = 3; gbc.gridy = 0; gbc.gridheight = 2;
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
            String mBitsStr = mBitsField.getText().trim();

            if (message.isEmpty()) {
                throw new Exception("Ingrese un mensaje válido");
            }

            int mBits = Integer.parseInt(mBitsStr);
            if (mBits < 1 || mBits > 5) {
                throw new Exception("M debe estar entre 1 y 5");
            }

            // Reinicializar el árbol
            root = null;
            treeVisualization.setRoot(null);

            int maxEnlaces = (int) Math.pow(2, mBits);

            logToTerminal(String.format("Construyendo árbol por residuos múltiples para: \"%s\"", message), "info");
            logToTerminal(String.format("M = %d bits por rama, %d enlaces posibles por nodo", mBits, maxEnlaces), "info");
            logToTerminal("=== PROCESO DE CONSTRUCCIÓN ===", "info");

            // Obtener caracteres únicos para evitar duplicados innecesarios
            java.util.Set<Character> uniqueChars = new java.util.LinkedHashSet<>();
            for (char c : message.toCharArray()) {
                uniqueChars.add(c);
            }

            // Mostrar tabla de conversión
            logToTerminal("=== TABLA DE CONVERSIÓN ===", "info");
            logToTerminal("ASCII\tLetra\tBinario\t\tSegmentos M=" + mBits, "info");
            logToTerminal("-----\t-----\t-------\t\t-----------------", "info");

            for (Character character : uniqueChars) {
                int ascii = (int) character;
                String binary = Integer.toBinaryString(ascii);
                while (binary.length() % mBits != 0) {
                    binary = "0" + binary;
                }
                String segments = "";
                for (int i = 0; i < binary.length(); i += mBits) {
                    String segment = binary.substring(i, Math.min(i + mBits, binary.length()));
                    while (segment.length() < mBits) {
                        segment += "0";
                    }
                    segments += segment + " ";
                }
                logToTerminal(String.format("%d\t%c\t%s\t\t%s", ascii, character, binary, segments.trim()), "info");
            }

            logToTerminal("\n=== INSERTANDO CARACTERES ===", "info");

            for (Character character : uniqueChars) {
                insertMultipleResiduoNode(character, mBits);
            }

            treeVisualization.setRoot(root);
            logToTerminal("Árbol por residuos múltiples construido exitosamente", "success");
            logToTerminal("Estructura del árbol:", "info");
            showTreeStructure();

        } catch (NumberFormatException ex) {
            logToTerminal("Error: M debe ser un número válido", "error");
        } catch (Exception ex) {
            logToTerminal("Error al construir árbol: " + ex.getMessage(), "error");
        }
    }

    private void insertCharacter(ActionEvent e) {
        try {
            String input = individualCharField.getText().trim().toUpperCase();
            String mBitsStr = mBitsField.getText().trim();

            if (input.isEmpty() || input.length() != 1) {
                throw new Exception("Ingrese un solo carácter");
            }

            int mBits = Integer.parseInt(mBitsStr);
            if (mBits < 1 || mBits > 5) {
                throw new Exception("Configure M válido primero (1-5)");
            }

            char character = input.charAt(0);

            // Verificar si el carácter ya existe
            TreeNode existingNode = searchMultipleResiduoNode(character, mBits);
            if (existingNode != null) {
                throw new Exception(String.format("El carácter '%c' ya existe en el árbol", character));
            }

            insertMultipleResiduoNode(character, mBits);

            treeVisualization.setRoot(root);
            individualCharField.setText("");

        } catch (NumberFormatException ex) {
            logToTerminal("Error: M debe ser un número válido", "error");
        } catch (Exception ex) {
            logToTerminal("Error al insertar: " + ex.getMessage(), "error");
        }
    }

    private void insertMultipleResiduoNode(char character, int mBits) throws Exception {
        int ascii = (int) character;
        String binary = Integer.toBinaryString(ascii);

        logToTerminal(String.format("Insertando '%c': ASCII=%d, Binario original=%s",
                character, ascii, binary), "info");

        // Asegurar que el binario tenga longitud múltiple de mBits
        while (binary.length() % mBits != 0) {
            binary = "0" + binary;
        }

        logToTerminal(String.format("Binario ajustado: %s (longitud: %d)", binary, binary.length()), "info");

        if (root == null) {
            root = new TreeNode();
            root.level = 0;
        }

        TreeNode current = root;
        String pathSegments = "";
        int depth = 0;

        // Procesar de M bits en M bits
        for (int i = 0; i < binary.length(); i += mBits) {
            String segment = binary.substring(i, Math.min(i + mBits, binary.length()));

            // Rellenar con ceros si es necesario
            while (segment.length() < mBits) {
                segment += "0";
            }

            int segmentValue = Integer.parseInt(segment, 2);
            pathSegments += segment + " ";
            depth++;

            logToTerminal(String.format("  Nivel %d - Segmento: %s = %d", depth, segment, segmentValue), "info");

            // Si estamos en el último segmento, crear nodo hoja
            if (i + mBits >= binary.length()) {
                TreeNode leafNode = getOrCreateChild(current, segmentValue, mBits);

                if (leafNode.data != null) {
                    throw new Exception(String.format("Colisión: posición ya ocupada por '%c'", leafNode.data));
                }

                leafNode.data = character;
                leafNode.ascii = ascii;
                leafNode.binaryPath = pathSegments.trim();
                leafNode.isLeaf = true;

                logToTerminal(String.format("Carácter '%c' insertado en nodo hoja", character), "success");
            } else {
                // Nodo intermedio
                current = getOrCreateChild(current, segmentValue, mBits);
                current.isLeaf = false;
            }
        }

        logToTerminal(String.format("Carácter '%c' insertado exitosamente (ruta: %s)",
                character, pathSegments.trim()), "success");
    }

    private TreeNode getOrCreateChild(TreeNode parent, int index, int mBits) {
        // Para simplificar la visualización en TreeVisualization (que solo maneja left/right),
        // vamos a mapear los índices a una estructura binaria
        // Esto es una simplificación - en una implementación completa se usaría un array de hijos

        TreeNode current = parent;

        // Convertir el índice a binario para navegación
        String indexBinary = Integer.toBinaryString(index);
        while (indexBinary.length() < mBits) {
            indexBinary = "0" + indexBinary;
        }

        // Navegar bit por bit para crear la estructura
        for (int i = 0; i < indexBinary.length(); i++) {
            char bit = indexBinary.charAt(i);

            if (bit == '0') {
                if (current.left == null) {
                    current.left = new TreeNode();
                    current.left.parent = current;
                    current.left.level = current.level + 1;
                    current.left.isLeaf = false;
                }
                current = current.left;
            } else {
                if (current.right == null) {
                    current.right = new TreeNode();
                    current.right.parent = current;
                    current.right.level = current.level + 1;
                    current.right.isLeaf = false;
                }
                current = current.right;
            }
        }

        return current;
    }

    private void searchCharacter(ActionEvent e) {
        try {
            String input = individualCharField.getText().trim().toUpperCase();
            String mBitsStr = mBitsField.getText().trim();

            if (input.isEmpty() || input.length() != 1) {
                throw new Exception("Ingrese un solo carácter");
            }

            int mBits = Integer.parseInt(mBitsStr);
            char character = input.charAt(0);

            TreeNode result = searchMultipleResiduoNode(character, mBits);

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

    private TreeNode searchMultipleResiduoNode(char character, int mBits) {
        if (root == null) return null;

        int ascii = (int) character;
        String binary = Integer.toBinaryString(ascii);

        while (binary.length() % mBits != 0) {
            binary = "0" + binary;
        }

        logToTerminal(String.format("Buscando '%c': ruta binaria segmentada", character), "info");

        TreeNode current = root;

        for (int i = 0; i < binary.length(); i += mBits) {
            String segment = binary.substring(i, Math.min(i + mBits, binary.length()));

            while (segment.length() < mBits) {
                segment += "0";
            }

            int segmentValue = Integer.parseInt(segment, 2);
            logToTerminal(String.format("  Navegando por segmento: %s = %d", segment, segmentValue), "info");

            current = getChild(current, segmentValue, mBits);

            if (current == null) {
                logToTerminal("Camino interrumpido", "info");
                return null;
            }
        }

        if (current.data != null && current.data.equals(character)) {
            return current;
        }

        return null;
    }

    private TreeNode getChild(TreeNode parent, int index, int mBits) {
        // Navegar siguiendo la misma lógica que getOrCreateChild pero sin crear nodos
        TreeNode current = parent;

        // Convertir el índice a binario para navegación
        String indexBinary = Integer.toBinaryString(index);
        while (indexBinary.length() < mBits) {
            indexBinary = "0" + indexBinary;
        }

        // Navegar bit por bit
        for (int i = 0; i < indexBinary.length(); i++) {
            char bit = indexBinary.charAt(i);

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

    private void deleteCharacter(ActionEvent e) {
        try {
            String input = individualCharField.getText().trim().toUpperCase();

            if (input.isEmpty() || input.length() != 1) {
                throw new Exception("Ingrese un solo carácter");
            }

            char character = input.charAt(0);
            boolean deleted = deleteMultipleResiduoNode(character);

            if (deleted) {
                logToTerminal(String.format("Carácter '%c' eliminado", character), "success");
                treeVisualization.setRoot(root);
                individualCharField.setText("");
            } else {
                logToTerminal(String.format("Carácter '%c' no encontrado", character), "error");
            }

        } catch (Exception ex) {
            logToTerminal("Error al eliminar: " + ex.getMessage(), "error");
        }
    }

    private boolean deleteMultipleResiduoNode(char character) {
        try {
            int mBits = Integer.parseInt(mBitsField.getText());
            TreeNode node = searchMultipleResiduoNode(character, mBits);

            if (node != null && node.data != null) {
                // Marcar el nodo como eliminado
                node.data = null;
                node.ascii = 0;
                node.binaryPath = "";
                node.isLeaf = false;

                logToTerminal(String.format("Carácter '%c' eliminado (nodo marcado como vacío)", character), "info");
                return true;
            }

            return false;

        } catch (Exception ex) {
            logToTerminal("Error al eliminar nodo: " + ex.getMessage(), "error");
            return false;
        }
    }

    @Override
    protected void exportToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Árbol de Residuos Múltiples como CSV");

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(fileChooser.getSelectedFile()))) {
                writer.println("#CONFIGURACION");
                writer.println("Algoritmo,Arboles Residuos Multiples");
                writer.println("Mensaje," + messageField.getText());
                writer.println("M_Bits," + mBitsField.getText());

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
            writer.println(String.format("NODO,%s,%c,%d,%s",
                    path, node.data, node.ascii, node.binaryPath));
        } else {
            writer.println(String.format("NODO_INTERNO,%s,null,0,", path));
        }

        exportTreeStructure(writer, node.left, path + "0");
        exportTreeStructure(writer, node.right, path + "1");
    }

    @Override
    protected void importFromCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Importar Árbol de Residuos Múltiples desde CSV");

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
                        } else if (parts[0].equals("M_Bits")) {
                            mBitsField.setText(parts[1]);
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
            mBitsField.setText("2");

            treeVisualization.setRoot(null);
            terminalArea.setText("");

            logToTerminal("Árbol de residuos múltiples reseteado", "warning");
        }
    }

    private void showTreeStructure() {
        if (root == null) {
            logToTerminal("=== ESTRUCTURA DEL ÁRBOL ===", "info");
            logToTerminal("Árbol vacío", "warning");
            return;
        }

        logToTerminal("=== ESTRUCTURA DEL ÁRBOL ===", "info");
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
}
