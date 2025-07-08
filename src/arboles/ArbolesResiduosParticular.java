package arboles;

import utils.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;

public class ArbolesResiduosParticular extends AlgorithmWindow {
    private JTextField messageField, individualCharField, moduloField;
    private JButton buildTreeButton, insertCharButton, searchCharButton, deleteCharButton;
    private TreeVisualization treeVisualization;
    private TreeNode root;

    public ArbolesResiduosParticular(JFrame parent) {
        super(parent, "Árboles por Residuos Particular");
        logToTerminal("Sistema de Árboles por Residuos Particular iniciado", "info");
        logToTerminal("Algoritmo: Módulo fijo definido por usuario", "info");
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

        // Módulo
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        configPanel.add(new JLabel("Módulo:"), gbc);
        gbc.gridx = 1;
        moduloField = new JTextField(10);
        moduloField.setText("10"); // Valor por defecto
        moduloField.setPreferredSize(Constants.INPUT_SIZE);
        configPanel.add(moduloField, gbc);

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
            String moduloStr = moduloField.getText().trim();

            if (message.isEmpty()) {
                throw new Exception("Ingrese un mensaje válido");
            }

            int modulo = Integer.parseInt(moduloStr);
            if (modulo <= 1) {
                throw new Exception("El módulo debe ser mayor que 1");
            }

            // Reinicializar el árbol
            root = null;
            treeVisualization.setRoot(null);

            logToTerminal(String.format("Construyendo árbol por residuos para: \"%s\"", message), "info");
            logToTerminal(String.format("Módulo fijo: %d", modulo), "info");
            logToTerminal("=== PROCESO DE CONSTRUCCIÓN ===", "info");

            // Mostrar tabla de conversión
            logToTerminal("=== TABLA DE CONVERSIÓN ===", "info");
            logToTerminal("ASCII\tLetra\tResiduo", "info");
            logToTerminal("-----\t-----\t-------", "info");

            // Obtener caracteres únicos para evitar duplicados innecesarios
            java.util.Set<Character> uniqueChars = new java.util.LinkedHashSet<>();
            for (char c : message.toCharArray()) {
                uniqueChars.add(c);
            }

            // Mostrar tabla primero
            for (Character character : uniqueChars) {
                int ascii = (int) character;
                int residuo = ascii % modulo;
                logToTerminal(String.format("%d\t%c\t%d", ascii, character, residuo), "info");
            }

            logToTerminal("\n=== INSERTANDO CARACTERES ===", "info");

            // Insertar cada carácter único
            for (Character character : uniqueChars) {
                insertResiduoNode(character, modulo);
            }

            // Actualizar la visualización
            treeVisualization.setRoot(root);
            logToTerminal("Árbol por residuos construido exitosamente", "success");
            logToTerminal("Estructura del árbol:", "info");
            showTreeStructure();

        } catch (NumberFormatException ex) {
            logToTerminal("Error: El módulo debe ser un número válido", "error");
        } catch (Exception ex) {
            logToTerminal("Error al construir árbol: " + ex.getMessage(), "error");
        }
    }

    private void insertCharacter(ActionEvent e) {
        try {
            String input = individualCharField.getText().trim().toUpperCase();
            String moduloStr = moduloField.getText().trim();

            if (input.isEmpty() || input.length() != 1) {
                throw new Exception("Ingrese un solo carácter");
            }

            int modulo = Integer.parseInt(moduloStr);
            if (modulo <= 1) {
                throw new Exception("Configure un módulo válido primero (mayor que 1)");
            }

            char character = input.charAt(0);

            // Verificar si el carácter ya existe
            TreeNode existingNode = searchResiduoNode(character, modulo);
            if (existingNode != null) {
                throw new Exception(String.format("El carácter '%c' ya existe en el árbol", character));
            }

            insertResiduoNode(character, modulo);

            treeVisualization.setRoot(root);
            individualCharField.setText("");

        } catch (NumberFormatException ex) {
            logToTerminal("Error: El módulo debe ser un número válido", "error");
        } catch (Exception ex) {
            logToTerminal("Error al insertar: " + ex.getMessage(), "error");
        }
    }

    private void insertResiduoNode(char character, int modulo) throws Exception {
        int ascii = (int) character;
        int residuo = ascii % modulo;

        logToTerminal(String.format("Insertando '%c': ASCII=%d, %d %% %d = %d",
                character, ascii, ascii, modulo, residuo), "info");

        TreeNode newNode = new TreeNode(character);
        newNode.ascii = ascii;

        // Insertar en ABB basado en el residuo calculado
        if (root == null) {
            root = newNode;
        } else {
            insertInBST(root, newNode, residuo, modulo);
        }

        logToTerminal(String.format("Carácter '%c' insertado con residuo %d", character, residuo), "success");
    }

    private void insertInBST(TreeNode node, TreeNode newNode, int newKey, int modulo) {
        while (true) {
            // Calcular la clave del nodo actual
            int currentKey = ((Character) node.data).charValue() % modulo;

            if (newKey < currentKey) {
                // Insertar a la izquierda
                if (node.left == null) {
                    node.left = newNode;
                    newNode.parent = node;
                    break;
                } else {
                    node = node.left;
                }
            } else {
                // Insertar a la derecha (incluye duplicados)
                if (node.right == null) {
                    node.right = newNode;
                    newNode.parent = node;
                    break;
                } else {
                    node = node.right;
                }
            }
        }
    }

    private void searchCharacter(ActionEvent e) {
        try {
            String input = individualCharField.getText().trim().toUpperCase();
            String moduloStr = moduloField.getText().trim();

            if (input.isEmpty() || input.length() != 1) {
                throw new Exception("Ingrese un solo carácter");
            }

            int modulo = Integer.parseInt(moduloStr);
            char character = input.charAt(0);

            TreeNode result = searchResiduoNode(character, modulo);

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

    private TreeNode searchResiduoNode(char character, int modulo) {
        if (root == null) return null;

        int ascii = (int) character;
        int residuo = ascii % modulo;

        logToTerminal(String.format("Buscando '%c': residuo %d", character, residuo), "info");

        return searchInBST(root, character, residuo, modulo);
    }

    private TreeNode searchInBST(TreeNode node, char target, int targetKey, int modulo) {
        if (node == null || node.data == null) return null;

        // Verificar si es el nodo buscado
        if (node.data instanceof Character && ((Character) node.data).charValue() == target) {
            return node;
        }

        // Calcular la clave del nodo actual
        int nodeKey = ((Character) node.data).charValue() % modulo;

        if (targetKey < nodeKey) {
            return searchInBST(node.left, target, targetKey, modulo);
        } else if (targetKey > nodeKey) {
            return searchInBST(node.right, target, targetKey, modulo);
        } else {
            // Misma clave, buscar en ambos lados por si hay duplicados
            TreeNode leftResult = searchInBST(node.left, target, targetKey, modulo);
            if (leftResult != null) return leftResult;
            return searchInBST(node.right, target, targetKey, modulo);
        }
    }

    private void deleteCharacter(ActionEvent e) {
        try {
            String input = individualCharField.getText().trim().toUpperCase();

            if (input.isEmpty() || input.length() != 1) {
                throw new Exception("Ingrese un solo carácter");
            }

            char character = input.charAt(0);
            boolean deleted = deleteResiduoNode(character);

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

    private boolean deleteResiduoNode(char character) {
        try {
            int modulo = Integer.parseInt(moduloField.getText());
            TreeNode nodeToDelete = searchResiduoNode(character, modulo);

            if (nodeToDelete == null) {
                return false;
            }

            // Implementación simplificada: marcar como eliminado
            // En una implementación completa se reestructuraría el BST
            nodeToDelete.data = null;

            logToTerminal(String.format("Nodo '%c' marcado como eliminado", character), "info");
            return true;

        } catch (Exception ex) {
            logToTerminal("Error al eliminar nodo: " + ex.getMessage(), "error");
            return false;
        }
    }

    @Override
    protected void exportToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Árbol de Residuos como CSV");

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(fileChooser.getSelectedFile()))) {
                writer.println("#CONFIGURACION");
                writer.println("Algoritmo,Arboles Residuos Particular");
                writer.println("Mensaje," + messageField.getText());
                writer.println("Modulo," + moduloField.getText());

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
            try {
                int modulo = Integer.parseInt(moduloField.getText());
                int ascii = ((Character) node.data).charValue();
                int residuo = ascii % modulo;
                writer.println(String.format("NODO,%s,%c,%d,%d",
                        path, node.data, ascii, residuo));
            } catch (Exception ex) {
                writer.println(String.format("NODO,%s,ERROR,0,0", path));
            }
        } else {
            writer.println(String.format("NODO_ELIMINADO,%s,null,0,0", path));
        }

        exportTreeStructure(writer, node.left, path + "L");
        exportTreeStructure(writer, node.right, path + "R");
    }

    @Override
    protected void importFromCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Importar Árbol de Residuos desde CSV");

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
                        } else if (parts[0].equals("Modulo")) {
                            moduloField.setText(parts[1]);
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
            moduloField.setText("10");

            treeVisualization.setRoot(null);
            terminalArea.setText("");

            logToTerminal("Árbol de residuos reseteado", "warning");
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
            int modulo = Integer.parseInt(moduloField.getText());
            int residuo = ((Character) node.data).charValue() % modulo;
            nodeInfo = String.format("'%c' (ASCII: %d, Residuo: %d)",
                    node.data, ((Character) node.data).charValue(), residuo);
        } else {
            nodeInfo = "[Nodo interno]";
        }

        String connector = isLast ? "└── " : "├── ";
        logToTerminal(prefix + connector + nodeInfo, "info");

        String newPrefix = prefix + (isLast ? "    " : "│   ");

        boolean hasLeft = node.left != null;
        boolean hasRight = node.right != null;

        if (hasLeft) {
            logToTerminal(newPrefix + "├── izquierda:", "info");
            showNodeStructure(node.left, newPrefix + "│   ", !hasRight);
        }
        if (hasRight) {
            logToTerminal(newPrefix + (hasLeft ? "└── " : "├── ") + "derecha:", "info");
            showNodeStructure(node.right, newPrefix + (hasLeft ? "    " : "│   "), true);
        }
    }
}
