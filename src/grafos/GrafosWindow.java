package grafos;

import utils.AlgorithmWindow;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class GrafosWindow extends AlgorithmWindow {

    // Colores y fuentes - Armonía con la aplicación principal
    private static final Color PRIMARY_COLOR = new Color(33, 128, 141); // #21808d - Color principal de la aplicación
    private static final Color PRIMARY_HOVER = new Color(29, 112, 128); // Color hover
    private static final Color TEXT_COLOR = new Color(51, 51, 51); // #333333 - Color de texto principal
    private static final Color SECONDARY_TEXT = new Color(100, 116, 139); // Color de texto secundario
    private static final Color PANEL_COLOR = new Color(248, 250, 252); // slate-50
    private static final Color BORDER_COLOR = new Color(203, 213, 225); // Color de bordes
    private static final Color BACKGROUND_LIGHT = new Color(241, 245, 249); // Fondo claro

    // Componentes de la interfaz
    private JRadioButton conjuntosRadio, productoRadio;
    private JPanel infoPanel;

    // Componentes para entrada de grafos
    private JTextArea g1NodesArea, g1EdgesArea, g2NodesArea, g2EdgesArea;
    private JLabel g1NotationLabel, g2NotationLabel, resultNotationLabel;
    private JButton updateButton;

    // Botones de operaciones
    private JButton unionBtn, intersectionBtn, ringSumBtn;
    private JButton cartesianBtn, tensorBtn, compositionBtn;
    private JButton resetViewBtn;

    // Visualizador de grafos
    private GraphVisualizer visualizer;

    // Estado del simulador
    private Graph g1Set, g2Set, g1Product, g2Product;
    private Map<String, Graph> results;
    private String currentScenario = "product";
    private String currentOperation = "cartesian";

    public GrafosWindow(JFrame parent) {
        super(parent, "Visualizador de Operaciones de Grafos");
        initializeGraphs();
        setupCustomComponents();
        initializeDefaultGraphs();
        updateVisualization();
    }

    private void initializeGraphs() {
        results = new HashMap<>();

        // Grafos para operaciones de conjuntos
        g1Set = new Graph(Arrays.asList("a", "b", "c"),
                Arrays.asList(new String[]{"a", "b"}, new String[]{"a", "c"}));
        g2Set = new Graph(Arrays.asList("a", "c", "d"),
                Arrays.asList(new String[]{"a", "d"}, new String[]{"a", "c"}, new String[]{"c", "d"}));

        // Grafos para operaciones de producto
        g1Product = new Graph(Arrays.asList("a", "b", "c"),
                Arrays.asList(new String[]{"a", "b"}, new String[]{"a", "c"}));
        g2Product = new Graph(Arrays.asList("d", "e", "f"),
                Arrays.asList(new String[]{"d", "e"}, new String[]{"e", "f"}, new String[]{"f", "d"}));
    }

    private void setupCustomComponents() {
        // Limpiar el panel principal y configurar layout personalizado
        getContentPane().removeAll();

        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(PANEL_COLOR);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Header
        JPanel headerPanel = createHeaderPanel();

        // Crear panel lateral izquierdo para controles
        JPanel leftPanel = createLeftControlsPanel();

        // Panel central para visualización
        JPanel centerPanel = createVisualizationPanel();

        // Organizar componentes con mejor distribución del espacio
        mainContainer.add(headerPanel, BorderLayout.NORTH);
        mainContainer.add(leftPanel, BorderLayout.WEST);
        mainContainer.add(centerPanel, BorderLayout.CENTER);

        add(mainContainer);

        // Maximizar ventana para usar toda la pantalla
        ((JFrame) SwingUtilities.getWindowAncestor(this)).setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(getParent());
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel titleLabel = new JLabel("Visualizador de Operaciones de Grafos", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(PRIMARY_COLOR);

        JLabel subtitleLabel = new JLabel("Una herramienta interactiva para crear grafos y explorar operaciones de conjuntos y productos", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(SECONDARY_TEXT);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(subtitleLabel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createScenarioSelector() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel("1. Seleccione un Escenario de Operación");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(TEXT_COLOR);

        JPanel radioPanel = new JPanel(new GridLayout(2, 1, 0, 8));
        radioPanel.setBackground(Color.WHITE);

        ButtonGroup scenarioGroup = new ButtonGroup();
        conjuntosRadio = new JRadioButton("🔗 Operaciones de Conjuntos");
        productoRadio = new JRadioButton("⊗ Operaciones de Producto", true);

        // Styling para radio buttons
        conjuntosRadio.setBackground(Color.WHITE);
        productoRadio.setBackground(Color.WHITE);
        conjuntosRadio.setFont(new Font("Arial", Font.BOLD, 12));
        productoRadio.setFont(new Font("Arial", Font.BOLD, 12));
        conjuntosRadio.setForeground(TEXT_COLOR);
        productoRadio.setForeground(TEXT_COLOR);

        scenarioGroup.add(conjuntosRadio);
        scenarioGroup.add(productoRadio);

        radioPanel.add(conjuntosRadio);
        radioPanel.add(productoRadio);

        // Listeners para cambio de escenario
        conjuntosRadio.addActionListener(e -> changeScenario("set"));
        productoRadio.addActionListener(e -> changeScenario("product"));

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(radioPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createInputGraphsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel("2. Defina los Grafos");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(TEXT_COLOR);

        JPanel graphsPanel = new JPanel(new GridLayout(2, 1, 0, 15));
        graphsPanel.setBackground(Color.WHITE);

        // Panel para G1
        JPanel g1Panel = createGraphInputPanel("Grafo G₁", true);
        // Panel para G2
        JPanel g2Panel = createGraphInputPanel("Grafo G₂", false);

        graphsPanel.add(g1Panel);
        graphsPanel.add(g2Panel);

        // Botón de actualización estilizado
        updateButton = new JButton("🔄 Actualizar y Calcular");
        updateButton.setBackground(PRIMARY_COLOR);
        updateButton.setForeground(Color.WHITE);
        updateButton.setFont(new Font("Arial", Font.BOLD, 12));
        updateButton.setBorderPainted(false);
        updateButton.setFocusPainted(false);
        updateButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        updateButton.setPreferredSize(new Dimension(200, 35));
        updateButton.addActionListener(e -> updateGraphs());

        // Efecto hover para el botón
        updateButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                updateButton.setBackground(PRIMARY_HOVER);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                updateButton.setBackground(PRIMARY_COLOR);
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(updateButton);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(graphsPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createGraphInputPanel(String title, boolean isG1) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 13));
        titleLabel.setForeground(PRIMARY_COLOR);

        JPanel inputPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        inputPanel.setBackground(Color.WHITE);

        // Etiqueta y campo para vértices
        JLabel nodesLabel = new JLabel("Vértices (ej: a,b,c):");
        nodesLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        nodesLabel.setForeground(TEXT_COLOR);

        JTextArea nodesArea = new JTextArea(1, 20);
        nodesArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        nodesArea.setFont(new Font("Arial", Font.PLAIN, 11));

        // Etiqueta y campo para aristas
        JLabel edgesLabel = new JLabel("Aristas (ej: a-b):");
        edgesLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        edgesLabel.setForeground(TEXT_COLOR);

        JTextArea edgesArea = new JTextArea(2, 20);
        edgesArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        edgesArea.setFont(new Font("Arial", Font.PLAIN, 11));

        // Asignar a variables de clase
        if (isG1) {
            g1NodesArea = nodesArea;
            g1EdgesArea = edgesArea;
        } else {
            g2NodesArea = nodesArea;
            g2EdgesArea = edgesArea;
        }

        inputPanel.add(nodesLabel);
        inputPanel.add(new JScrollPane(nodesArea));
        inputPanel.add(edgesLabel);
        inputPanel.add(new JScrollPane(edgesArea));

        // Panel para notación
        JLabel notationLabel = new JLabel();
        notationLabel.setFont(new Font("Courier", Font.PLAIN, 9));
        notationLabel.setForeground(SECONDARY_TEXT);
        notationLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(6, 6, 6, 6)
        ));
        notationLabel.setOpaque(true);
        notationLabel.setBackground(BACKGROUND_LIGHT);

        if (isG1) {
            g1NotationLabel = notationLabel;
        } else {
            g2NotationLabel = notationLabel;
        }

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(inputPanel, BorderLayout.CENTER);
        panel.add(notationLabel, BorderLayout.SOUTH);

        return panel;
    }

    private void initializeDefaultGraphs() {
        // Inicializar campos de texto con valores por defecto
        g1NodesArea.setText("a, b, c");
        g1EdgesArea.setText("a-b\na-c");
        g2NodesArea.setText("a, c, d");
        g2EdgesArea.setText("a-d\na-c\nc-d");

        // Actualizar las visualizaciones
        updateGraphs();
        changeScenario("product");
    }

    private void changeScenario(String scenario) {
        currentScenario = scenario;

        // Actualizar campos de texto según el escenario
        if ("set".equals(scenario)) {
            g1NodesArea.setText(String.join(", ", g1Set.getNodes()));
            g1EdgesArea.setText(String.join("\n", g1Set.getEdges().stream()
                    .map(edge -> edge[0] + "-" + edge[1])
                    .toArray(String[]::new)));
            g2NodesArea.setText(String.join(", ", g2Set.getNodes()));
            g2EdgesArea.setText(String.join("\n", g2Set.getEdges().stream()
                    .map(edge -> edge[0] + "-" + edge[1])
                    .toArray(String[]::new)));
        } else {
            g1NodesArea.setText(String.join(", ", g1Product.getNodes()));
            g1EdgesArea.setText(String.join("\n", g1Product.getEdges().stream()
                    .map(edge -> edge[0] + "-" + edge[1])
                    .toArray(String[]::new)));
            g2NodesArea.setText(String.join(", ", g2Product.getNodes()));
            g2EdgesArea.setText(String.join("\n", g2Product.getEdges().stream()
                    .map(edge -> edge[0] + "-" + edge[1])
                    .toArray(String[]::new)));
        }

        // Mostrar/ocultar botones según el escenario
        unionBtn.setVisible("set".equals(scenario));
        intersectionBtn.setVisible("set".equals(scenario));
        ringSumBtn.setVisible("set".equals(scenario));
        cartesianBtn.setVisible("product".equals(scenario));
        tensorBtn.setVisible("product".equals(scenario));
        compositionBtn.setVisible("product".equals(scenario));

        // Establecer operación por defecto
        if ("set".equals(scenario)) {
            setOperation("union");
        } else {
            setOperation("cartesian");
        }

        updateGraphs();
    }

    private void updateGraphs() {
        // Parsear entrada de grafos
        Graph g1 = parseGraphInput(g1NodesArea.getText(), g1EdgesArea.getText());
        Graph g2 = parseGraphInput(g2NodesArea.getText(), g2EdgesArea.getText());

        // Actualizar notaciones
        g1NotationLabel.setText(formatNotation("G₁", g1));
        g2NotationLabel.setText(formatNotation("G₂", g2));

        // Calcular operaciones
        GraphOperations operations = new GraphOperations();
        if ("set".equals(currentScenario)) {
            results.putAll(operations.calculateSetOperations(g1, g2));
        } else {
            results.putAll(operations.calculateProductOperations(g1, g2));
        }

        updateVisualization();
    }

    private Graph parseGraphInput(String nodesText, String edgesText) {
        // Parsear nodos
        List<String> nodes = Arrays.stream(nodesText.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .collect(java.util.stream.Collectors.toList());

        // Parsear aristas
        List<String[]> edges = Arrays.stream(edgesText.split("\n"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(s -> s.split("-"))
                .filter(parts -> parts.length == 2 &&
                        nodes.contains(parts[0].trim()) &&
                        nodes.contains(parts[1].trim()))
                .map(parts -> new String[]{parts[0].trim(), parts[1].trim()})
                .collect(java.util.stream.Collectors.toList());

        return new Graph(nodes, edges);
    }

    private String formatNotation(String name, Graph graph) {
        if (graph == null || graph.getNodes().isEmpty()) {
            return "<html><b>" + name + " = (V, E)</b><br>Datos no disponibles</html>";
        }

        String nodesStr = "{" + String.join(", ", graph.getNodes()) + "}";
        String edgesStr = "{" + String.join(", ", graph.getEdges().stream()
                .map(edge -> "(" + edge[0] + "," + edge[1] + ")")
                .toArray(String[]::new)) + "}";

        return "<html><b>" + name + " = (V, E)</b><br>" +
                "<b>Vértices V:</b> " + nodesStr + "<br>" +
                "<b>Aristas E:</b> " + edgesStr + "</html>";
    }

    private void setOperation(String operation) {
        currentOperation = operation;

        // Actualizar apariencia de botones
        resetButtonStyles();

        JButton activeButton = null;
        switch (operation) {
            case "union": activeButton = unionBtn; break;
            case "intersection": activeButton = intersectionBtn; break;
            case "ringSum": activeButton = ringSumBtn; break;
            case "cartesian": activeButton = cartesianBtn; break;
            case "tensor": activeButton = tensorBtn; break;
            case "composition": activeButton = compositionBtn; break;
        }

        if (activeButton != null) {
            activeButton.setBackground(PRIMARY_COLOR);
            activeButton.setForeground(Color.WHITE);
        }

        updateVisualization();
    }

    private void resetButtonStyles() {
        JButton[] buttons = {unionBtn, intersectionBtn, ringSumBtn, cartesianBtn, tensorBtn, compositionBtn};
        for (JButton button : buttons) {
            button.setBackground(Color.WHITE);
            button.setForeground(TEXT_COLOR);
        }
    }

    protected void updateVisualization() {
        Graph resultGraph = results.get(currentOperation);
        if (resultGraph != null) {
            visualizer.setGraph(resultGraph, "product".equals(currentScenario));
            resultNotationLabel.setText(formatNotation("G_resultado", resultGraph));
        }
    }

    private void resetView() {
        if (visualizer != null) {
            visualizer.resetView();
        }
    }

    @Override
    protected JPanel createConfigurationPanel() {
        return new JPanel(); // Panel vacío, ya que la configuración está integrada
    }

    @Override
    protected JPanel createControlsPanel() {
        return new JPanel(); // Panel vacío, ya que los controles están integrados
    }

    @Override
    protected void resetAll() {
        initializeDefaultGraphs();
        resetView();
    }

    @Override
    protected void exportToCSV() {
        // Implementar exportación si es necesario
        JOptionPane.showMessageDialog(this, "Función de exportación no implementada", "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    protected void importFromCSV() {
        // Implementar importación si es necesario
        JOptionPane.showMessageDialog(this, "Función de importación no implementada", "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private JPanel createLeftControlsPanel() {
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(PANEL_COLOR);
        leftPanel.setPreferredSize(new Dimension(350, 0));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel contenedor vertical
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(PANEL_COLOR);

        // Selector de escenario
        JPanel scenarioPanel = createScenarioSelector();

        // Separador
        Component separator1 = Box.createRigidArea(new Dimension(0, 15));

        // Panel de entrada de grafos
        JPanel inputPanel = createInputGraphsPanel();

        // Separador
        Component separator2 = Box.createRigidArea(new Dimension(0, 15));

        // Panel de operaciones
        JPanel operationsPanel = createOperationsPanel();

        container.add(scenarioPanel);
        container.add(separator1);
        container.add(inputPanel);
        container.add(separator2);
        container.add(operationsPanel);

        leftPanel.add(container, BorderLayout.NORTH);

        return leftPanel;
    }

    private JPanel createVisualizationPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Título con botón de reset
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Visualización Interactiva de Grafos");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_COLOR);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        resetViewBtn = new JButton("⟲ Reiniciar Vista");
        resetViewBtn.setFont(new Font("Arial", Font.BOLD, 12));
        resetViewBtn.setBackground(BACKGROUND_LIGHT);
        resetViewBtn.setForeground(TEXT_COLOR);
        resetViewBtn.setBorderPainted(false);
        resetViewBtn.setFocusPainted(false);
        resetViewBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        resetViewBtn.setToolTipText("Reiniciar zoom y posición");
        resetViewBtn.addActionListener(e -> resetView());

        buttonPanel.add(resetViewBtn);
        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.add(buttonPanel, BorderLayout.EAST);

        // Visualizador optimizado para pantalla completa
        visualizer = new GraphVisualizer();
        visualizer.setBackground(BACKGROUND_LIGHT);
        visualizer.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 2));

        // Panel de información resultado optimizado
        infoPanel = createOptimizedInfoPanel();

        // Panel contenedor para visualizador e info
        JPanel mainVisualizationPanel = new JPanel(new BorderLayout());
        mainVisualizationPanel.setBackground(Color.WHITE);
        mainVisualizationPanel.add(visualizer, BorderLayout.CENTER);
        mainVisualizationPanel.add(infoPanel, BorderLayout.SOUTH);

        panel.add(titlePanel, BorderLayout.NORTH);
        panel.add(mainVisualizationPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createOperationsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15) //
        ));

        JLabel titleLabel = new JLabel("3. Operaciones Disponibles");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(TEXT_COLOR);

        // Panel de botones con mejor organización
        JPanel buttonsPanel = new JPanel(new GridLayout(0, 1, 5, 8));
        buttonsPanel.setBackground(Color.WHITE);

        // Botones para operaciones de conjuntos
        unionBtn = createStyledOperationButton("∪ Unión");
        intersectionBtn = createStyledOperationButton("∩ Intersección");
        ringSumBtn = createStyledOperationButton("⊕ Suma Anillo");

        // Botones para operaciones de producto
        cartesianBtn = createStyledOperationButton("× Producto Cartesiano");
        tensorBtn = createStyledOperationButton("⊗ Producto Tensorial");
        compositionBtn = createStyledOperationButton("∘ Composición");

        // Agregar listeners
        unionBtn.addActionListener(e -> setOperation("union"));
        intersectionBtn.addActionListener(e -> setOperation("intersection"));
        ringSumBtn.addActionListener(e -> setOperation("ringSum"));
        cartesianBtn.addActionListener(e -> setOperation("cartesian"));
        tensorBtn.addActionListener(e -> setOperation("tensor"));
        compositionBtn.addActionListener(e -> setOperation("composition"));

        buttonsPanel.add(unionBtn);
        buttonsPanel.add(intersectionBtn);
        buttonsPanel.add(ringSumBtn);
        buttonsPanel.add(cartesianBtn);
        buttonsPanel.add(tensorBtn);
        buttonsPanel.add(compositionBtn);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(buttonsPanel, BorderLayout.CENTER);

        return panel;
    }

    private JButton createStyledOperationButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(Color.WHITE);
        button.setForeground(TEXT_COLOR);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(280, 35));

        // Efecto hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (!button.getBackground().equals(PRIMARY_COLOR)) {
                    button.setBackground(BACKGROUND_LIGHT);
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!button.getBackground().equals(PRIMARY_COLOR)) {
                    button.setBackground(Color.WHITE);
                }
            }
        });

        return button;
    }

    private JPanel createOptimizedInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_LIGHT);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        panel.setPreferredSize(new Dimension(0, 120));

        JLabel titleLabel = new JLabel("Información del Resultado");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 13));
        titleLabel.setForeground(TEXT_COLOR);

        resultNotationLabel = new JLabel();
        resultNotationLabel.setFont(new Font("Courier", Font.PLAIN, 11));
        resultNotationLabel.setForeground(SECONDARY_TEXT);
        resultNotationLabel.setVerticalAlignment(SwingConstants.TOP);
        resultNotationLabel.setOpaque(false);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(resultNotationLabel, BorderLayout.CENTER);

        return panel;
    }
}
