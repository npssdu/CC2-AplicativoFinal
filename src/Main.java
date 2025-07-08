import javax.swing.*;

/**
 * Clase principal para ejecutar el Simulador de Algoritmos - Ciencias de la Computación II
 *
 * Esta clase contiene el método main que inicializa la aplicación y muestra el SplashScreen.
 *
 * @author Alicia Pineda Quiroga, Nelson David Posso Suarez, Jhonathan De La Torre
 * @version 1.0
 * @since 2025
 */
public class Main {

    /**
     * Método principal de la aplicación.
     * Configura el Look and Feel del sistema y muestra el SplashScreen inicial.
     *
     * @param args Argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        // Configurar Look and Feel del sistema para mejor integración visual
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            System.out.println("Simulador de Algoritmos - CC2 iniciando...");
        } catch (ClassNotFoundException e) {
            System.err.println("Error: No se pudo encontrar la clase Look and Feel del sistema");
            e.printStackTrace();
        } catch (InstantiationException e) {
            System.err.println("Error: No se pudo instanciar el Look and Feel del sistema");
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            System.err.println("Error: Acceso ilegal al Look and Feel del sistema");
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            System.err.println("Error: Look and Feel no soportado, usando el predeterminado");
            e.printStackTrace();
        }

        // Crear y mostrar la ventana de SplashScreen en el Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                // Crear y mostrar el SplashScreen
                SplashScreen splashScreen = new SplashScreen();
                splashScreen.setVisible(true);

                System.out.println("SplashScreen mostrado exitosamente");

            } catch (Exception e) {
                System.err.println("Error crítico al inicializar la aplicación: " + e.getMessage());
                e.printStackTrace();

                // Mostrar mensaje de error al usuario
                JOptionPane.showMessageDialog(
                        null,
                        "Error crítico al inicializar la aplicación:\n" + e.getMessage() +
                                "\n\nLa aplicación se cerrará.",
                        "Error de Inicialización",
                        JOptionPane.ERROR_MESSAGE
                );

                // Salir de la aplicación
                System.exit(1);
            }
        });
    }
}
