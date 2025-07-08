import javax.swing.*;

import utils.AlgorithmWindow;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class SimuladorPrincipal extends JFrame {

    private static final Color PRIMARY_COLOR = new Color(33, 128, 141);
    private static final Color BACKGROUND_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(51, 51, 51);
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 16);
    private static final Font SUBTITLE_FONT = new Font("Arial", Font.BOLD, 14);

    private JTabbedPane tabbedPane;

    public SimuladorPrincipal() {
        initializeWindow();
        createTabbedPanes();
        setupMenuBar();
        setupWindow();
    }

    private void initializeWindow() {
        setTitle("Simulador de Algoritmos - Ciencias de la Computación II");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_COLOR);
    }

    private void createTabbedPanes() {
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 12));
        tabbedPane.setBackground(BACKGROUND_COLOR);
        tabbedPane.setForeground(TEXT_COLOR);

        // Crear y agregar todas las pestañas
        tabbedPane.addTab("Búsquedas Internas", createBusquedasInternasPanel());
        tabbedPane.addTab("Árboles", createArbolesPanel());
        tabbedPane.addTab("Búsquedas Externas", createBusquedasExternasPanel());
        tabbedPane.addTab("Estructuras Dinámicas", createEstructurasDinamicasPanel());
        tabbedPane.addTab("Índices", createIndicesPanel());
        tabbedPane.addTab("Grafos", createGrafosPanel());

        // Personalizar apariencia de las pestañas
        tabbedPane.setTabPlacement(JTabbedPane.TOP);
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createBusquedasInternasPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);

        // Header
        JPanel headerPanel = createHeaderPanel("Búsquedas Internas",
                "Algoritmos de búsqueda y funciones hash");

        // Contenido principal
        JPanel contentPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Sección Algoritmos de Búsqueda
        JPanel searchSection = createAlgorithmSection("Algoritmos de Búsqueda",
                new String[]{"Búsqueda Lineal", "Búsqueda Binaria"});

        // Sección Funciones Hash
        JPanel hashSection = createAlgorithmSection("Funciones Hash",
                new String[]{"Hash Mod", "Hash Cuadrado", "Hash Plegamiento", "Hash Truncamiento"});

        contentPanel.add(searchSection);
        contentPanel.add(hashSection);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createArbolesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);

        // Header
        JPanel headerPanel = createHeaderPanel("Árboles",
                "Algoritmos de árboles digitales y de búsqueda");

        // Contenido principal
        JPanel contentPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Crear tarjetas para cada tipo de árbol
        contentPanel.add(createAlgorithmCard("Árboles Digitales",
                "Últimos 5 bits del código ASCII"));
        contentPanel.add(createAlgorithmCard("Árboles por Residuos Particular",
                "Módulo fijo definido por usuario"));
        contentPanel.add(createAlgorithmCard("Árboles por Residuos Múltiples",
                "M bits por rama con 2^M enlaces"));
        contentPanel.add(createAlgorithmCard("Árboles de Huffman",
                "Codificación por frecuencias"));

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createBusquedasExternasPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);

        // Header
        JPanel headerPanel = createHeaderPanel("Búsquedas Externas",
                "Algoritmos de búsqueda por bloques");

        // Contenido principal
        JPanel contentPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Sección Algoritmos por Bloques
        JPanel searchBlocksSection = createAlgorithmSection("Algoritmos de Búsqueda por Bloques",
                new String[]{"Búsqueda Lineal por Bloques", "Búsqueda Binaria por Bloques"});

        // Sección Funciones Hash por Bloques
        JPanel hashBlocksSection = createAlgorithmSection("Funciones Hash por Bloques",
                new String[]{"Hash Mod por Bloques", "Hash Cuadrado por Bloques",
                        "Hash Plegamiento por Bloques", "Hash Truncamiento por Bloques"});

        contentPanel.add(searchBlocksSection);
        contentPanel.add(hashBlocksSection);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createEstructurasDinamicasPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);

        // Header
        JPanel headerPanel = createHeaderPanel("Estructuras Dinámicas",
                "Algoritmos de hashing dinámico");

        // Contenido principal
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 20));
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Crear tarjetas para cada tipo de expansión
        contentPanel.add(createAlgorithmCard("Expansión Total",
                "Duplicación de cubetas: T = 2^i * N"));
        contentPanel.add(createAlgorithmCard("Expansión Parcial",
                "Incremento del 50%: T = 1.5^i * N"));

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createIndicesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);

        // Header
        JPanel headerPanel = createHeaderPanel("Índices",
                "Simulación de estructuras de índices");

        // Contenido principal
        JPanel contentPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Crear tarjetas para cada tipo de índice
        contentPanel.add(createAlgorithmCard("Índice Primario",
                "Un registro de índice por bloque de datos"));
        contentPanel.add(createAlgorithmCard("Índice Secundario",
                "Un registro de índice por registro de datos"));
        contentPanel.add(createAlgorithmCard("Multinivel Primario",
                "Múltiples niveles basados en estructura primaria"));
        contentPanel.add(createAlgorithmCard("Multinivel Secundario",
                "Múltiples niveles basados en estructura secundaria"));

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createGrafosPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);

        // Header
        JPanel headerPanel = createHeaderPanel("Teoría de Grafos",
                "Simulador de operaciones entre grafos: conjuntos y productos");

        // Contenido principal
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel central con el simulador
        JPanel simulatorPanel = new JPanel(new BorderLayout());
        simulatorPanel.setBackground(Color.WHITE);
        simulatorPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel simulatorTitle = new JLabel("Simulador Interactivo de Operaciones de Grafos", SwingConstants.CENTER);
        simulatorTitle.setFont(new Font("Arial", Font.BOLD, 18));
        simulatorTitle.setForeground(PRIMARY_COLOR);

        JLabel simulatorDesc = new JLabel("<html><div style='text-align: center;'>" +
                "Explore operaciones de conjuntos (unión, intersección, suma anillo) y<br>" +
                "operaciones de producto (cartesiano, tensorial, composición) entre grafos" +
                "</div></html>", SwingConstants.CENTER);
        simulatorDesc.setFont(new Font("Arial", Font.PLAIN, 12));
        simulatorDesc.setForeground(new Color(102, 102, 102));

        JButton openSimulatorBtn = new JButton("Abrir Simulador de Grafos");
        openSimulatorBtn.setBackground(PRIMARY_COLOR);
        openSimulatorBtn.setForeground(Color.WHITE);
        openSimulatorBtn.setFont(new Font("Arial", Font.BOLD, 14));
        openSimulatorBtn.setBorderPainted(false);
        openSimulatorBtn.setFocusPainted(false);
        openSimulatorBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        openSimulatorBtn.addActionListener(e -> openAlgorithmWindow("Operaciones de Grafos"));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(openSimulatorBtn);

        simulatorPanel.add(simulatorTitle, BorderLayout.NORTH);
        simulatorPanel.add(simulatorDesc, BorderLayout.CENTER);
        simulatorPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Panel informativo con las operaciones
        JPanel infoPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        infoPanel.setBackground(BACKGROUND_COLOR);
        infoPanel.setBorder(BorderFactory.createTitledBorder("Operaciones Disponibles"));

        // Operaciones de conjuntos
        infoPanel.add(createOperationCard("Unión", "G₁ ∪ G₂'"));
        infoPanel.add(createOperationCard("Intersección", "G₁ ∩ G₂'"));
        infoPanel.add(createOperationCard("Suma Anillo", "G₁ ⊕ G₂'"));

        // Operaciones de producto
        infoPanel.add(createOperationCard("Producto Cartesiano", "G₁ × G₂"));
        infoPanel.add(createOperationCard("Producto Tensorial", "G₁ ⊗ G₂"));
        infoPanel.add(createOperationCard("Composición", "G₁ ∘ G₂"));

        contentPanel.add(simulatorPanel, BorderLayout.CENTER);
        contentPanel.add(infoPanel, BorderLayout.SOUTH);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createHeaderPanel(String title, String subtitle) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);

        JLabel subtitleLabel = new JLabel(subtitle, SwingConstants.CENTER);
        subtitleLabel.setFont(SUBTITLE_FONT);
        subtitleLabel.setForeground(TEXT_COLOR);

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);

        return headerPanel;
    }

    private JPanel createAlgorithmSection(String sectionTitle, String[] algorithms) {
        JPanel section = new JPanel(new BorderLayout());
        section.setBackground(BACKGROUND_COLOR);
        section.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
                sectionTitle,
                0, 0, SUBTITLE_FONT, PRIMARY_COLOR));

        JPanel algorithmsPanel = new JPanel(new GridLayout(1, algorithms.length, 10, 10));
        algorithmsPanel.setBackground(BACKGROUND_COLOR);
        algorithmsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (String algorithm : algorithms) {
            algorithmsPanel.add(createAlgorithmButton(algorithm));
        }

        section.add(algorithmsPanel, BorderLayout.CENTER);
        return section;
    }

    private JPanel createAlgorithmCard(String title, String description) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        // Efecto hover simulado
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                        BorderFactory.createEmptyBorder(14, 14, 14, 14)));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
                        BorderFactory.createEmptyBorder(15, 15, 15, 15)));
            }
        });

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(SUBTITLE_FONT);
        titleLabel.setForeground(TEXT_COLOR);

        JLabel descLabel = new JLabel("<html><div style='text-align: center;'>" + description + "</div></html>");
        descLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        descLabel.setForeground(new Color(102, 102, 102));

        JButton openButton = new JButton("Abrir Simulador");
        openButton.setBackground(PRIMARY_COLOR);
        openButton.setForeground(Color.WHITE);
        openButton.setBorderPainted(false);
        openButton.setFocusPainted(false);
        openButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        openButton.addActionListener(e -> openAlgorithmWindow(title));

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(descLabel, BorderLayout.CENTER);
        card.add(openButton, BorderLayout.SOUTH);

        return card;
    }

    private JButton createAlgorithmButton(String algorithmName) {
        JButton button = new JButton(algorithmName);
        button.setFont(new Font("Arial", Font.PLAIN, 11));
        button.setBackground(Color.WHITE);
        button.setForeground(TEXT_COLOR);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);

        // Efecto hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_COLOR);
                button.setForeground(Color.WHITE);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.WHITE);
                button.setForeground(TEXT_COLOR);
            }
        });

        button.addActionListener(e -> openAlgorithmWindow(algorithmName));

        return button;
    }

    private JPanel createOperationCard(String operationName, String notation) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        JLabel nameLabel = new JLabel(operationName, SwingConstants.CENTER);
        nameLabel.setFont(SUBTITLE_FONT);
        nameLabel.setForeground(PRIMARY_COLOR);

        JLabel notationLabel = new JLabel(notation, SwingConstants.CENTER);
        notationLabel.setFont(new Font("Arial", Font.BOLD, 16));
        notationLabel.setForeground(TEXT_COLOR);

        card.add(nameLabel, BorderLayout.NORTH);
        card.add(notationLabel, BorderLayout.CENTER);

        return card;
    }

    private void openAlgorithmWindow(String algorithmName) {
        AlgorithmWindow algorithmWindow = null;

        try {
            switch (algorithmName) {
                case "Búsqueda Lineal":
                    algorithmWindow = new busquedas.BusquedaLineal(this);
                    break;
                case "Búsqueda Binaria":
                    algorithmWindow = new busquedas.BusquedaBinaria(this);
                    break;
                case "Hash Mod":
                    algorithmWindow = new busquedas.HashMod(this);
                    break;
                case "Hash Cuadrado":
                    algorithmWindow = new busquedas.HashCuadrado(this);
                    break;
                case "Hash Plegamiento":
                    algorithmWindow = new busquedas.HashPlegamiento(this);
                    break;
                case "Hash Truncamiento":
                    algorithmWindow = new busquedas.HashTruncamiento(this);
                    break;
                case "Árboles Digitales":
                    algorithmWindow = new arboles.ArbolesDigitales(this);
                    break;
                case "Árboles por Residuos Particular":
                    algorithmWindow = new arboles.ArbolesResiduosParticular(this);
                    break;
                case "Árboles por Residuos Múltiples":
                    algorithmWindow = new arboles.ArbolesResiduosMultiples(this);
                    break;
                case "Árboles de Huffman":
                    algorithmWindow = new arboles.ArbolesHuffman(this);
                    break;
                case "Búsqueda Lineal por Bloques":
                    algorithmWindow = new externos.BusquedaLinealBloques(this);
                    break;
                case "Búsqueda Binaria por Bloques":
                    algorithmWindow = new externos.BusquedaBinariaBloques(this);
                    break;
                case "Hash Mod por Bloques":
                    algorithmWindow = new externos.HashModBloques(this);
                    break;
                case "Hash Cuadrado por Bloques":
                    algorithmWindow = new externos.HashCuadradoBloques(this);
                    break;
                case "Hash Plegamiento por Bloques":
                    algorithmWindow = new externos.HashPlegamientoBloques(this);
                    break;
                case "Hash Truncamiento por Bloques":
                    algorithmWindow = new externos.HashTruncamientoBloques(this);
                    break;
                case "Expansión Total":
                    algorithmWindow = new dinamicas.ExpansionTotal(this);
                    break;
                case "Expansión Parcial":
                    algorithmWindow = new dinamicas.ExpansionParcial(this);
                    break;
                case "Índice Primario":
                    algorithmWindow = new indices.IndicePrimario(this);
                    break;
                case "Índice Secundario":
                    algorithmWindow = new indices.IndiceSecundario(this);
                    break;
                case "Multinivel Primario":
                    algorithmWindow = new indices.MultinivelPrimario(this);
                    break;
                case "Multinivel Secundario":
                    algorithmWindow = new indices.MultinivelSecundario(this);
                    break;
                case "Operaciones de Grafos":
                    algorithmWindow = new grafos.GrafosWindow(this);
                    break;
                default:
                    // Crear ventana placeholder para algoritmos no implementados
                    JDialog placeholder = new JDialog(this, algorithmName, true);
                    placeholder.setSize(400, 200);
                    placeholder.setLocationRelativeTo(this);
                    placeholder.add(new JLabel("Algoritmo: " + algorithmName + " (En desarrollo)",
                            SwingConstants.CENTER));
                    placeholder.setVisible(true);
                    return;
            }

            if (algorithmWindow != null) {
                algorithmWindow.setVisible(true);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al abrir " + algorithmName + ": " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(BACKGROUND_COLOR);

        // Menú Archivo
        JMenu fileMenu = new JMenu("Archivo");
        fileMenu.add(new JMenuItem("Nuevo Proyecto"));
        fileMenu.add(new JMenuItem("Abrir Proyecto"));
        fileMenu.add(new JMenuItem("Guardar Proyecto"));
        fileMenu.addSeparator();
        fileMenu.add(new JMenuItem("Salir"));

        // Menú Herramientas
        JMenu toolsMenu = new JMenu("Herramientas");
        toolsMenu.add(new JMenuItem("Casos de Prueba"));
        toolsMenu.add(new JMenuItem("Configuración"));

        // Menú Ayuda
        JMenu helpMenu = new JMenu("Ayuda");
        helpMenu.add(new JMenuItem("Manual de Usuario"));
        helpMenu.add(new JMenuItem("Acerca de"));

        menuBar.add(fileMenu);
        menuBar.add(toolsMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    private void setupWindow() {
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1000, 700));
        setLocationRelativeTo(null);

        // Establecer icono de la ventana
        setWindowIcon();

        // Confirmar al cerrar
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int option = JOptionPane.showConfirmDialog(
                        SimuladorPrincipal.this,
                        "¿Está seguro de que desea cerrar el simulador?",
                        "Confirmar salida",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);

                if (option == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });
    }

    private void setWindowIcon() {
        try {
            // Cargar imagen del icono PNG
            BufferedImage iconImage = null;
            URL iconUrl = getClass().getResource("/resources/icono-ventana.png");
            if (iconUrl != null) {
                iconImage = ImageIO.read(iconUrl);
            } else {
                File iconFile = new File("resources/icono-ventana.png");
                if (iconFile.exists()) {
                    iconImage = ImageIO.read(iconFile);
                }
            }

            if (iconImage != null) {
                // Crear diferentes tamaños para mejor compatibilidad con diferentes sistemas
                java.util.List<Image> iconImages = new java.util.ArrayList<>();
                int[] sizes = {16, 20, 24, 32, 40, 48, 64, 128};

                for (int size : sizes) {
                    Image scaledIcon = iconImage.getScaledInstance(size, size, Image.SCALE_SMOOTH);
                    iconImages.add(scaledIcon);
                }

                // Establecer los iconos de la ventana (para diferentes tamaños)
                setIconImages(iconImages);
                System.out.println("Icono de ventana del simulador principal establecido exitosamente: icono-ventana.png");
            } else {
                System.err.println("No se pudo cargar la imagen del icono: icono-ventana.png");
            }
        } catch (IOException e) {
            System.err.println("Error al cargar el icono de la ventana del simulador principal: " + e.getMessage());
        }
    }


}
