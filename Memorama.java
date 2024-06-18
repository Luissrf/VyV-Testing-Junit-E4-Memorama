import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.*;

public class Memorama extends JFrame {
    private final ArrayList<JButton> botones = new ArrayList<>();
    private final String[] principios = {
            "Principio 1", "Principio 2", "Principio 3",
            "Principio 4", "Principio 5", "Principio 6", "Principio 7",
            "VERIFICACIÓN", "VALIDACIÓN"
    };
    private final String[] definiciones = {
            "Las pruebas muestran la presencia de defectos", "Las pruebas exhaustivas son imposibles", "Pruebas tempranas",
            "Agrupación de defectos", "Paradoja de los pesticidas", "Las pruebas dependen del contexto", "Falacia de ausencia de errores",
            "¿Se construyó el producto correctamente?", "¿Es el producto correcto?"
    };
    private final String[] valores;
    private JButton primerBoton, segundoBoton;
    private int primerIndice, segundoIndice;
    private boolean verificandoCoincidencia = false;
    private static final Color COLOR_DEFECTO = new Color(0, 51, 102); // Azul oscuro
    private static final Color COLOR_TEXTO = Color.WHITE; // Color blanco para el texto
    private static final Color COLOR_COINCIDENCIA = new Color(0, 255, 0); // Color verde para las cartas encontradas

    public Memorama() {
        setTitle("Memorando de Verificación de Software");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelJuego = new JPanel(new GridLayout(6, 3, 10, 10));
        panelJuego.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        valores = new String[18];
        for (int i = 0; i < 9; i++) {
            valores[i] = principios[i];
            valores[i + 9] = definiciones[i];
        }
        
        // Crear y mezclar posiciones de las cartas
        ArrayList<Integer> posiciones = new ArrayList<>();
        for (int i = 0; i < 18; i++) {
            posiciones.add(i);
        }
        Collections.shuffle(posiciones);

        // Limpiar y recrear los botones
        botones.clear();
        panelJuego.removeAll();

        for (int i = 0; i < 18; i++) {
            int indice = posiciones.get(i);
            JButton boton = crearBotonCarta(indice);
            botones.add(boton);
            panelJuego.add(boton);
        }

        panelPrincipal.add(panelJuego, BorderLayout.CENTER);
        panelPrincipal.add(crearPanelBotones(), BorderLayout.SOUTH);

        add(panelPrincipal);
        setVisible(true);
    }

    private JButton crearBotonCarta(int indice) {
        JButton boton = new JButton("?");
        boton.setFont(new Font("Arial", Font.BOLD, 16));
        boton.setFocusPainted(false);
        boton.setActionCommand(String.valueOf(indice));
        boton.addActionListener((ActionEvent e) -> {
            actionPerformed(e);
        });
        boton.setBackground(COLOR_DEFECTO);
        boton.setForeground(COLOR_TEXTO);
        return boton;
    }

    private JPanel crearPanelBotones() {
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JButton botonReiniciar = new JButton("Reiniciar");
        botonReiniciar.addActionListener(e -> reiniciarJuego());
        panelBotones.add(botonReiniciar);

        JButton botonSalir = new JButton("Salir");
        botonSalir.addActionListener(e -> System.exit(0));
        panelBotones.add(botonSalir);

        JButton botonMostrarCartas = new JButton("Mostrar Cartas");
        botonMostrarCartas.addActionListener(e -> mostrarTodasLasCartas());
        panelBotones.add(botonMostrarCartas);

        return panelBotones;
    }

    private void reiniciarJuego() {
        // Crear y mezclar posiciones de las cartas
        ArrayList<Integer> posiciones = new ArrayList<>();
        for (int i = 0; i < 18; i++) {
            posiciones.add(i);
        }
        Collections.shuffle(posiciones);

        // Limpiar y recrear los botones
        botones.clear();
        getContentPane().removeAll();

        JPanel panelJuego = new JPanel(new GridLayout(6, 3, 10, 10));
        panelJuego.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (int i = 0; i < 18; i++) {
            int indice = posiciones.get(i);
            JButton boton = crearBotonCarta(indice);
            botones.add(boton);
            panelJuego.add(boton);
        }

        getContentPane().add(panelJuego, BorderLayout.CENTER);
        getContentPane().add(crearPanelBotones(), BorderLayout.SOUTH);

        revalidate(); // Revalidar el contenido del frame
        repaint(); // Repintar el frame
    }

    private void mostrarTodasLasCartas() {
        for (int i = 0; i < botones.size(); i++) {
            JButton boton = botones.get(i);
            int indice = Integer.parseInt(boton.getActionCommand());
            boton.setText(valores[indice]);
        }
        Timer temporizador = new Timer(4000, (ActionEvent e) -> {
            for (JButton boton : botones) {
                if (boton.isEnabled()) {
                    boton.setText("?");
                    boton.setBackground(COLOR_DEFECTO);
                    boton.setForeground(COLOR_TEXTO);
                }
            }
        });
        temporizador.setRepeats(false);
        temporizador.start();
    }

    private void actionPerformed(ActionEvent e) {
        JButton botonClickeado = (JButton) e.getSource();
        int indice = Integer.parseInt(botonClickeado.getActionCommand());
        botonClickeado.setText(valores[indice]);

        if (!verificandoCoincidencia) {
            primerBoton = botonClickeado;
            primerIndice = indice;
            verificandoCoincidencia = true;
        } else {
            segundoBoton = botonClickeado;
            segundoIndice = indice;

            if (esCoincidencia(primerIndice, segundoIndice)) {
                primerBoton.setEnabled(false);
                segundoBoton.setEnabled(false);
                mostrarCoincidencia(primerBoton, segundoBoton); // Mostrar la coincidencia antes de eliminar las cartas
                Timer temporizador = new Timer(1500, (ActionEvent evt) -> {
                    eliminarBoton(primerBoton);
                    eliminarBoton(segundoBoton);
                    verificarFinJuego(); // Verificar si se completó el juego
                });
                temporizador.setRepeats(false);
                temporizador.start();
            } else {
                Timer temporizador = new Timer(500, (ActionEvent e1) -> {
                    primerBoton.setText("?");
                    segundoBoton.setText("?");
                    primerBoton.setBackground(COLOR_DEFECTO);
                    segundoBoton.setBackground(COLOR_DEFECTO);
                    primerBoton.setForeground(COLOR_TEXTO);
                    segundoBoton.setForeground(COLOR_TEXTO);
                });
                temporizador.setRepeats(false);
                temporizador.start();
            }
            verificandoCoincidencia = false;
        }
    }

    private void mostrarCoincidencia(JButton boton1, JButton boton2) {
        boton1.setBackground(COLOR_COINCIDENCIA);
        boton2.setBackground(COLOR_COINCIDENCIA);
        boton1.setForeground(Color.BLACK);
        boton2.setForeground(Color.BLACK);
    }

    private void eliminarBoton(JButton boton) {
        boton.setVisible(false); // Ocultar el botón
        botones.remove(boton); // Remover el botón de la lista de botones
        getContentPane().validate(); // Validar el contenedor para actualizar la interfaz
    }

    private boolean esCoincidencia(int indice1, int indice2) {
        String valor1 = valores[indice1];
        String valor2 = valores[indice2];

        for (int i = 0; i < 9; i++) {
            if ((valor1.equals(principios[i]) && valor2.equals(definiciones[i])) ||
                    (valor2.equals(principios[i]) && valor1.equals(definiciones[i]))) {
                return true;
            }
        }
        return false;
    }

    private void verificarFinJuego() {
        if (botones.isEmpty()) {
            JOptionPane.showMessageDialog(this, "¡Felicidades! Has completado el juego.", "Fin del Juego", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Memorama::new);
    }
}
