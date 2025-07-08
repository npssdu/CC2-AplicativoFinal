import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class SplashScreen extends JFrame {

    private static final Color PRIMARY_COLOR = new Color(33, 128, 141); // #21808d
    private static final Color BACKGROUND_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(51, 51, 51); // #333333
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 18);
    private static final Font SUBTITLE_FONT = new Font("Arial", Font.BOLD, 14);
    private static final Font BODY_FONT = new Font("Arial", Font.PLAIN, 12);
    private static final Font COPYRIGHT_FONT = new Font("Arial", Font.PLAIN, 10);

    private JButton iniciarButton;
    private ImageIcon logoIcon;

    public SplashScreen() {
        initializeComponents();
        setupLayout();
        setupWindow();
    }

    private void initializeComponents() {
        // Cargar logo de la Universidad Distrital
        try {
            // Intentar cargar desde diferentes ubicaciones y formatos
            BufferedImage logoImage = null;
            URL logoUrl = getClass().getResource("/resources/Escudo_UD PNG.png");
            if (logoUrl != null) {
                logoImage = ImageIO.read(logoUrl);
            } else {
                logoUrl = getClass().getResource("/resources/Escudo_UD PNG.png");
                if (logoUrl != null) {
                    logoImage = ImageIO.read(logoUrl);
                } else {
                    File logoFile = new File("Escudo_UD PNG.png");
                    if (logoFile.exists()) {
                        logoImage = ImageIO.read(logoFile);
                    } else {
                        logoFile = new File("Escudo_UD PNG.png");
                        if (logoFile.exists()) {
                            logoImage = ImageIO.read(logoFile);
                        }
                    }
                }
            }
            if (logoImage != null) {
                // Redimensionar logo a tamaño apropiado
                Image scaledLogo = logoImage.getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                logoIcon = new ImageIcon(scaledLogo);
            } else {
                // Logo placeholder si no se encuentra el archivo
                logoIcon = createPlaceholderLogo();
            }
        } catch (IOException e) {
            System.err.println("Error al cargar el logo: " + e.getMessage());
            logoIcon = createPlaceholderLogo();
        }

        // Botón Iniciar
        iniciarButton = new JButton("INICIAR");
        iniciarButton.setFont(new Font("Arial", Font.BOLD, 14));
        iniciarButton.setBackground(PRIMARY_COLOR);
        iniciarButton.setForeground(Color.WHITE);
        iniciarButton.setFocusPainted(false);
        iniciarButton.setBorderPainted(false);
        iniciarButton.setPreferredSize(new Dimension(120, 40));
        iniciarButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Efecto hover para el botón
        iniciarButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                iniciarButton.setBackground(new Color(29, 112, 128)); // Color más oscuro
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                iniciarButton.setBackground(PRIMARY_COLOR);
            }
        });

        // Acción del botón
        iniciarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirSimulador();
            }
        });
    }

    private ImageIcon createPlaceholderLogo() {
        // Crear un logo placeholder si no se encuentra el archivo
        BufferedImage placeholder = new BufferedImage(120, 120, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = placeholder.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fondo
        g2d.setColor(PRIMARY_COLOR);
        g2d.fillOval(10, 10, 100, 100);

        // Texto UD
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        FontMetrics fm = g2d.getFontMetrics();
        String text = "UD";
        int x = (120 - fm.stringWidth(text)) / 2;
        int y = (120 + fm.getAscent()) / 2;
        g2d.drawString(text, x, y);

        g2d.dispose();
        return new ImageIcon(placeholder);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        // Panel principal
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));

        // Logo
        JLabel logoLabel = new JLabel(logoIcon);
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Espacio después del logo
        Component logoSpacing = Box.createRigidArea(new Dimension(0, 30));

        // Panel de información de integrantes
        JPanel integrantesPanel = createIntegrantesPanel();
        integrantesPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Espacio antes de la información institucional
        Component institutionalSpacing = Box.createRigidArea(new Dimension(0, 25));

        // Panel de información institucional
        JPanel institutionalPanel = createInstitutionalPanel();
        institutionalPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Espacio antes del botón
        Component buttonSpacing = Box.createRigidArea(new Dimension(0, 30));

        // Botón
        iniciarButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Espacio antes del copyright
        Component copyrightSpacing = Box.createRigidArea(new Dimension(0, 25));

        // Copyright
        JLabel copyrightLabel = createCopyrightLabel();
        copyrightLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Agregar todos los componentes
        mainPanel.add(logoLabel);
        mainPanel.add(logoSpacing);
        mainPanel.add(integrantesPanel);
        mainPanel.add(institutionalSpacing);
        mainPanel.add(institutionalPanel);
        mainPanel.add(buttonSpacing);
        mainPanel.add(iniciarButton);
        mainPanel.add(copyrightSpacing);
        mainPanel.add(copyrightLabel);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createIntegrantesPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BACKGROUND_COLOR);

        // Título del proyecto
        JLabel tituloLabel = new JLabel("Simulador de Algoritmos - Ciencias de la Computación II");
        tituloLabel.setFont(TITLE_FONT);
        tituloLabel.setForeground(PRIMARY_COLOR);
        tituloLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Espacio
        Component spacing1 = Box.createRigidArea(new Dimension(0, 20));

        // Integrantes
        String[] integrantes = {
                "Alicia Pineda Quiroga (20222020047)",
                "Nelson David Posso Suarez (20212020132)",
                "Jhonathan De La Torre (20222020033)"
        };

        panel.add(tituloLabel);
        panel.add(spacing1);

        for (String integrante : integrantes) {
            JLabel integranteLabel = new JLabel(integrante);
            integranteLabel.setFont(BODY_FONT);
            integranteLabel.setForeground(TEXT_COLOR);
            integranteLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(integranteLabel);
            panel.add(Box.createRigidArea(new Dimension(0, 8)));
        }

        return panel;
    }

    private JPanel createInstitutionalPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BACKGROUND_COLOR);

        // Universidad
        JLabel universidadLabel = new JLabel("Universidad Distrital Francisco José de Caldas");
        universidadLabel.setFont(SUBTITLE_FONT);
        universidadLabel.setForeground(PRIMARY_COLOR);
        universidadLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Facultad
        JLabel facultadLabel = new JLabel("Ingeniería de Sistemas");
        facultadLabel.setFont(BODY_FONT);
        facultadLabel.setForeground(TEXT_COLOR);
        facultadLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Año
        JLabel yearLabel = new JLabel("2025");
        yearLabel.setFont(BODY_FONT);
        yearLabel.setForeground(TEXT_COLOR);
        yearLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(universidadLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(facultadLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(yearLabel);

        return panel;
    }

    private JLabel createCopyrightLabel() {
        JLabel copyrightLabel = new JLabel("© 2025 Todos los derechos reservados.");
        copyrightLabel.setFont(COPYRIGHT_FONT);
        copyrightLabel.setForeground(new Color(128, 128, 128));
        return copyrightLabel;
    }

    private void setupWindow() {
        setTitle("Simulador de Algoritmos - CC2");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Establecer icono de la ventana
        setWindowIcon();

        pack();

        // Centrar en la pantalla
        setLocationRelativeTo(null);

        // Tamaño mínimo
        setMinimumSize(new Dimension(500, 600));
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
                System.out.println("Icono de ventana establecido exitosamente: icono-ventana.png");
            } else {
                System.err.println("No se pudo cargar la imagen del icono: icono-ventana.png");
            }
        } catch (IOException e) {
            System.err.println("Error al cargar el icono de la ventana: " + e.getMessage());
        }
    }

    private void abrirSimulador() {
        // Mostrar mensaje de carga
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        iniciarButton.setText("Cargando...");
        iniciarButton.setEnabled(false);

        // Timer para simular carga y abrir simulador principal
        Timer timer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Crear y mostrar ventana principal del simulador
                SwingUtilities.invokeLater(() -> {
                    try {
                        SimuladorPrincipal simulador = new SimuladorPrincipal();
                        simulador.setVisible(true);

                        // Cerrar splash screen
                        SplashScreen.this.dispose();

                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(SplashScreen.this,
                                "Error al inicializar el simulador: " + ex.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);

                        // Restaurar botón en caso de error
                        iniciarButton.setText("INICIAR");
                        iniciarButton.setEnabled(true);
                        setCursor(Cursor.getDefaultCursor());
                    }
                });
            }
        });

        timer.setRepeats(false);
        timer.start();
    }
}
