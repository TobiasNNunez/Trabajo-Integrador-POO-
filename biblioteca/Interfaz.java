package biblioteca;

import biblioteca.persistencia.ConexionBD;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Menú gráfico para gestionar los ejemplares, socios y préstamos del TP.
 * Presenta los datos de {@link Biblioteca} en tablas Swing y delega en ella
 * las operaciones que se guardan en SQLite.
 */
public class Interfaz implements ActionListener {
    private JPanel panelPrincipal;
    private JTabbedPane tabbedPane1;
    private JLabel titulo;
    private JPanel pestañaLibros;
    private JTable tablaLibros;
    private JPanel pestañaSocios;
    private JTable tablaSocios;
    private JPanel pestañaPrestamos;
    private JTable tablaPrestamos;
    private JLabel etiquetaEstado;
    private JButton botonRegistrarEjemplar;
    private JButton botonQuitarEjemplar;
    private JButton botonTitulos;
    private JButton botonTitular;
    private JButton botonRegistrarSocio;
    private JButton botonQuitarSocio;
    private JButton botonBuscarSocio;
    private JButton botonCantidadSocios;
    private JButton botonDocentesResponsables;
    private JButton botonPremiarDocente;
    private JButton botonPrestar;
    private JButton botonDevolver;
    private JCheckBox soloVencidos;

    private static JFrame ventanaActiva;
    private final Biblioteca biblioteca;
    private final DefaultTableModel modeloLibros;
    private final DefaultTableModel modeloSocios;
    private final DefaultTableModel modeloPrestamos;

    /**
     * Prepara los modelos de las tablas, conecta los botones y muestra los datos iniciales.
     *
     * @param biblioteca Biblioteca con los registros cargados desde SQLite.
     */
    private Interfaz(Biblioteca biblioteca) {
        this.biblioteca = biblioteca;
        modeloLibros = prepararTabla(tablaLibros,
                new String[]{"ID", "Título", "Edición", "Editorial", "Año", "Estado"},
                new Class<?>[]{Integer.class, String.class, Integer.class, String.class, Integer.class, String.class});
        modeloSocios = prepararTabla(tablaSocios,
                new String[]{"DNI", "Nombre y apellido", "Tipo", "Carrera / Área", "Plazo (días)", "Prestados"},
                new Class<?>[]{Integer.class, String.class, String.class, String.class, Integer.class, Integer.class});
        modeloPrestamos = prepararTabla(tablaPrestamos,
                new String[]{"ID ejemplar", "Título", "DNI", "Socio", "Retiro", "Estado"},
                new Class<?>[]{Integer.class, String.class, Integer.class, String.class, Date.class, String.class});
        ajustarColumnas(tablaLibros, new int[]{54, 290, 70, 185, 60, 100});
        ajustarColumnas(tablaSocios, new int[]{92, 200, 95, 224, 85, 74});
        ajustarColumnas(tablaPrestamos, new int[]{80, 296, 82, 175, 105, 82});
        configurarAcciones();
        actualizarTablas("");
    }

    /**
     * Inicia el menú gráfico en el hilo de eventos de Swing.
     *
     * @param args Argumentos de inicio, reservados para el punto de entrada.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                abrirVentana();
            }
        });
    }

    /**
     * Trae al frente la ventana abierta o crea una sesión nueva.
     * Aplica el esquema de SQLite, carga los registros y muestra un aviso si falla el arranque.
     */
    private static void abrirVentana() {
        if (ventanaActiva != null && ventanaActiva.isDisplayable()) {
            ventanaActiva.toFront();
            return;
        }

        Biblioteca biblioteca = new Biblioteca("Biblioteca Central");
        try {
            ConexionBD.inicializarTablas();
            biblioteca.cargarDesdeBaseDeDatos();
        } catch (IllegalStateException e) {
            mostrarErrorDeInicio(e.getMessage());
            return;
        } catch (IllegalArgumentException e) {
            mostrarErrorDeInicio(e.getMessage());
            return;
        }

        Interfaz interfaz = new Interfaz(biblioteca);
        JFrame ventana = new JFrame("TP7 · " + biblioteca.getNombre());
        ventana.setContentPane(interfaz.panelPrincipal);
        ventana.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ventana.setMinimumSize(new Dimension(760, 440));
        ventana.setSize(920, 540);
        ventana.setLocationRelativeTo(null);
        ventanaActiva = ventana;
        ventana.setVisible(true);
    }

    /**
     * Muestra el motivo por el que no se pudo abrir la biblioteca.
     *
     * @param mensaje Detalle del error de inicialización o carga.
     */
    private static void mostrarErrorDeInicio(String mensaje) {
        JOptionPane.showMessageDialog(null, "No se pudo abrir la base de datos.\n" + mensaje,
                "Gestión de Biblioteca", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Configura una tabla de solo lectura con ordenamiento y selección de una fila.
     *
     * @param tabla Tabla que se configura.
     * @param columnas Encabezados de las columnas en el orden del modelo.
     * @param tipos Tipo de dato de cada columna, usado para ordenar y mostrar sus valores.
     * @return Modelo que recibe las filas de la tabla.
     */
    private static DefaultTableModel prepararTabla(JTable tabla, String[] columnas, Class<?>[] tipos) {
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            /**
             * Indica si una celda admite edición directa en la tabla.
             *
             * @param fila Índice de la fila en el modelo.
             * @param columna Índice de la columna en el modelo.
             * @return {@code false}, porque las operaciones se realizan desde los botones.
             */
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }

            /**
             * Devuelve el tipo configurado para una columna.
             *
             * @param columna Índice de la columna en el modelo.
             * @return Clase de los valores de la columna.
             */
            @Override
            public Class<?> getColumnClass(int columna) {
                return tipos[columna];
            }
        };
        tabla.setModel(modelo);
        tabla.setAutoCreateRowSorter(true);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        return modelo;
    }

    /**
     * Asigna el ancho inicial preferido de cada columna.
     *
     * @param tabla Tabla que contiene las columnas.
     * @param anchos Anchos en píxeles, en el orden de las columnas.
     */
    private static void ajustarColumnas(JTable tabla, int[] anchos) {
        for (int i = 0; i < anchos.length; i++) {
            tabla.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }
    }

    /** Asocia los botones y el filtro de vencimientos con las operaciones del menú. */
    private void configurarAcciones() {
        botonRegistrarEjemplar.addActionListener(this);
        botonQuitarEjemplar.addActionListener(this);
        botonTitulos.addActionListener(this);
        botonTitular.addActionListener(this);
        botonRegistrarSocio.addActionListener(this);
        botonQuitarSocio.addActionListener(this);
        botonBuscarSocio.addActionListener(this);
        botonCantidadSocios.addActionListener(this);
        botonDocentesResponsables.addActionListener(this);
        botonPremiarDocente.addActionListener(this);
        botonPrestar.addActionListener(this);
        botonDevolver.addActionListener(this);
        soloVencidos.addActionListener(this);
    }

    /**
     * Ejecuta la operación asociada al botón o al filtro que originó el evento.
     *
     * @param evento Acción realizada en un control del menú.
     */
    @Override
    public void actionPerformed(ActionEvent evento) {
        Object origen = evento.getSource();
        if (origen == botonRegistrarEjemplar) {
            registrarEjemplar();
        } else if (origen == botonQuitarEjemplar) {
            quitarEjemplar();
        } else if (origen == botonTitulos) {
            mostrarListado("Títulos únicos",
                    biblioteca.getLibros().isEmpty() ? "El catálogo está vacío." : biblioteca.listaDeTitulos());
        } else if (origen == botonTitular) {
            consultarTitular();
        } else if (origen == botonRegistrarSocio) {
            registrarSocio();
        } else if (origen == botonQuitarSocio) {
            quitarSocio();
        } else if (origen == botonBuscarSocio) {
            buscarSocio();
        } else if (origen == botonCantidadSocios) {
            mostrarCantidades();
        } else if (origen == botonDocentesResponsables) {
            mostrarListado("Docentes responsables", biblioteca.docentesResponsables().isEmpty()
                    ? "No hay docentes responsables para mostrar." : biblioteca.listaDeDocentesResponsables());
        } else if (origen == botonPremiarDocente) {
            premiarDocente();
        } else if (origen == botonPrestar) {
            prestarEjemplar();
        } else if (origen == botonDevolver) {
            devolverEjemplar();
        } else if (origen == soloVencidos) {
            actualizarPrestamos(soloVencidos.isSelected() ? "Solo préstamos vencidos" : "");
        }
    }

    /**
     * Actualiza las tres tablas con las colecciones de la biblioteca en memoria.
     *
     * @param mensaje Confirmación para la barra de estado o cadena vacía para mostrar solo el resumen.
     */
    private void actualizarTablas(String mensaje) {
        modeloLibros.setRowCount(0);
        for (Libro libro : biblioteca.getLibros()) {
            modeloLibros.addRow(new Object[]{libro.getId(), libro.getTitulo(), libro.getEdicion(),
                    libro.getEditorial(), libro.getAnio(), libro.prestado() ? "Prestado" : "Disponible"});
        }

        modeloSocios.setRowCount(0);
        for (Socio socio : biblioteca.getSocios()) {
            modeloSocios.addRow(new Object[]{socio.getDniSocio(), socio.getNombre(), socio.soyDeLaClase(),
                    carreraOArea(socio), socio.getDiasPrestamo(), socio.cantLibrosPrestados()});
        }
        actualizarPrestamos(mensaje);
    }

    /**
     * Muestra los préstamos activos, aplica el filtro de vencidos y actualiza el resumen.
     *
     * @param mensaje Texto adicional para la barra de estado o cadena vacía.
     */
    private void actualizarPrestamos(String mensaje) {
        modeloPrestamos.setRowCount(0);
        Set<Prestamo> vencidos = new HashSet<>(biblioteca.prestamosVencidos());
        int activos = 0;
        for (Libro libro : biblioteca.getLibros()) {
            Prestamo prestamo = libro.getPrestamo();
            if (prestamo != null && prestamo.getFechaDevolucion() == null) {
                activos++;
                boolean vencido = vencidos.contains(prestamo);
                if (!soloVencidos.isSelected() || vencido) {
                    Socio socio = prestamo.getSocio();
                    modeloPrestamos.addRow(new Object[]{libro.getId(), libro.getTitulo(), socio.getDniSocio(),
                            socio.getNombre(), prestamo.getFechaRetiro().getTime(), vencido ? "Vencido" : "En plazo"});
                }
            }
        }
        String resumen = biblioteca.getLibros().size() + " ejemplares   ·   "
                + biblioteca.getSocios().size() + " socios   ·   "
                + activos + " préstamos activos   ·   " + vencidos.size() + " vencidos";
        etiquetaEstado.setText(mensaje.isEmpty() ? resumen : mensaje + "   |   " + resumen);
    }

    /** Solicita los datos del ejemplar y delega su alta en la biblioteca. */
    private void registrarEjemplar() {
        JTextField tituloLibro = new JTextField(24);
        JTextField edicion = new JTextField(8);
        JTextField editorial = new JTextField(24);
        JTextField anio = new JTextField(8);
        Object[] campos = {"Título", tituloLibro, "Edición", edicion, "Editorial", editorial, "Año", anio};
        confirmarYGuardar("Registrar ejemplar", campos, new Runnable() {
            @Override
            public void run() {
                biblioteca.nuevoLibro(
                        textoRequerido(tituloLibro, "Título"), enteroPositivo(edicion.getText(), "Edición"),
                        textoRequerido(editorial, "Editorial"), enteroPositivo(anio.getText(), "Año"));
            }
        }, "Ejemplar registrado");
    }

    /** Pide confirmación y quita el ejemplar seleccionado del catálogo. */
    private void quitarEjemplar() {
        Integer id = claveSeleccionada(tablaLibros, "Seleccioná el ejemplar que querés quitar.");
        if (id == null || !confirmarBaja("Quitar ejemplar", "¿Quitar el ejemplar #" + id + " del catálogo?")) {
            return;
        }
        try {
            if (!biblioteca.quitarLibro(id)) {
                avisar("No se encontró el ejemplar seleccionado.");
                return;
            }
        } catch (IllegalStateException e) {
            mostrarError(e);
            return;
        }
        actualizarTablas("Ejemplar quitado");
    }

    /** Solicita el tipo y los datos del socio y registra el alta si el DNI está disponible. */
    private void registrarSocio() {
        JComboBox<String> tipo = new JComboBox<>(new String[]{"Estudiante", "Docente"});
        JTextField dni = new JTextField(12);
        JTextField nombre = new JTextField(24);
        JTextField detalle = new JTextField(24);
        JLabel etiquetaDetalle = new JLabel("Carrera");
        tipo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evento) {
                etiquetaDetalle.setText(tipo.getSelectedIndex() == 0 ? "Carrera" : "Área");
            }
        });
        Object[] campos = {"Tipo de socio", tipo, "DNI", dni, "Nombre y apellido", nombre, etiquetaDetalle, detalle};

        confirmarYGuardar("Registrar socio", campos, new Runnable() {
            @Override
            public void run() {
                int documento = enteroPositivo(dni.getText(), "DNI");
                if (biblioteca.buscarSocio(documento) != null) {
                    throw new IllegalArgumentException("Ya existe un socio con ese DNI.");
                }
                String nombreCompleto = textoRequerido(nombre, "Nombre y apellido");
                String carreraOArea = textoRequerido(detalle, etiquetaDetalle.getText());
                if (tipo.getSelectedIndex() == 0) {
                    biblioteca.nuevoSocioEstudiante(documento, nombreCompleto, carreraOArea);
                } else {
                    biblioteca.nuevoSocioDocente(documento, nombreCompleto, carreraOArea);
                }
            }
        }, "Socio registrado");
    }

    /** Pide confirmación y quita al socio seleccionado del padrón. */
    private void quitarSocio() {
        Integer dni = claveSeleccionada(tablaSocios, "Seleccioná el socio que querés quitar.");
        if (dni == null || !confirmarBaja("Quitar socio", "¿Quitar al socio con DNI " + dni + " del padrón?")) {
            return;
        }
        try {
            if (!biblioteca.quitarSocio(dni)) {
                avisar("No se encontró el socio seleccionado.");
                return;
            }
        } catch (IllegalStateException e) {
            mostrarError(e);
            return;
        }
        actualizarTablas("Socio quitado");
    }

    /**
     * Pide confirmación de la baja con Cancelar como opción inicial.
     *
     * @param tituloDialogo Título del diálogo de confirmación.
     * @param mensaje Indicación del registro que se quitará.
     * @return {@code true} si el usuario elige Quitar.
     */
    private boolean confirmarBaja(String tituloDialogo, String mensaje) {
        Object[] opciones = {"Quitar", "Cancelar"};
        return JOptionPane.showOptionDialog(panelPrincipal, mensaje, tituloDialogo,
                JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, opciones, opciones[1]) == 0;
    }

    /**
     * Permite elegir un ejemplar disponible e identificar al socio por DNI.
     * La biblioteca comprueba si el socio puede realizar el retiro.
     */
    private void prestarEjemplar() {
        ArrayList<Ejemplar> disponibles = new ArrayList<>();
        for (Libro libro : biblioteca.getLibros()) {
            if (!libro.prestado()) {
                disponibles.add(new Ejemplar(libro));
            }
        }
        if (disponibles.isEmpty() || biblioteca.getSocios().isEmpty()) {
            avisar("Para prestar un libro necesitás un ejemplar disponible y un socio registrado.");
            return;
        }

        JComboBox<Ejemplar> ejemplares = new JComboBox<>(disponibles.toArray(new Ejemplar[disponibles.size()]));
        JTextField dni = new JTextField(12);
        Object[] campos = {"Ejemplar disponible", ejemplares, "DNI del socio", dni};
        confirmarYGuardar("Prestar ejemplar", campos, new Runnable() {
            @Override
            public void run() {
                Socio socio = biblioteca.buscarSocio(entero(dni.getText(), "DNI"));
                if (socio == null) {
                    throw new IllegalArgumentException("No se encontró un socio con ese DNI.");
                }
                Libro libro = disponibles.get(ejemplares.getSelectedIndex()).getLibro();
                if (!biblioteca.prestarLibro(Calendar.getInstance(), socio, libro)) {
                    throw new IllegalArgumentException("El socio no puede pedir otro libro o el ejemplar ya está prestado.");
                }
            }
        }, "Préstamo registrado");
    }

    /** Solicita confirmación y registra la devolución del préstamo seleccionado. */
    private void devolverEjemplar() {
        Integer id = claveSeleccionada(tablaPrestamos, "Seleccioná el préstamo que querés devolver.");
        if (id == null) {
            return;
        }
        Libro libro = biblioteca.buscarLibro(id);
        int opcion = JOptionPane.showConfirmDialog(panelPrincipal,
                "¿Registrar la devolución del ejemplar #" + id + " de «" + libro.getTitulo() + "»?",
                "Registrar devolución", JOptionPane.YES_NO_OPTION);
        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            biblioteca.devolverLibro(libro);
        } catch (LibroNoPrestadoException e) {
            avisar(e.getMessage());
            return;
        } catch (IllegalStateException e) {
            mostrarError(e);
            return;
        }
        actualizarTablas("Devolución registrada");
    }

    /** Muestra quién tiene el ejemplar seleccionado o informa que está disponible. */
    private void consultarTitular() {
        Integer id = claveSeleccionada(tablaLibros, "Seleccioná el ejemplar que querés consultar.");
        if (id == null) {
            return;
        }
        Libro libro = biblioteca.buscarLibro(id);
        try {
            informar("Titular del préstamo", "El ejemplar #" + id + " está prestado a "
                    + biblioteca.quienTieneElLibro(libro) + ".");
        } catch (LibroNoPrestadoException e) {
            informar("Titular del préstamo", e.getMessage());
        }
    }

    /** Busca al socio por DNI, selecciona su fila y muestra sus datos. */
    private void buscarSocio() {
        String dni = JOptionPane.showInputDialog(panelPrincipal, "DNI del socio", "Buscar socio",
                JOptionPane.QUESTION_MESSAGE);
        if (dni == null) {
            return;
        }

        Socio socio;
        try {
            socio = biblioteca.buscarSocio(entero(dni, "DNI"));
        } catch (IllegalArgumentException e) {
            avisar(e.getMessage());
            return;
        }
        if (socio == null) {
            avisar("No se encontró un socio con ese DNI.");
            return;
        }

        for (int fila = 0; fila < modeloSocios.getRowCount(); fila++) {
            if (modeloSocios.getValueAt(fila, 0).equals(socio.getDniSocio())) {
                int visible = tablaSocios.convertRowIndexToView(fila);
                tablaSocios.setRowSelectionInterval(visible, visible);
                tablaSocios.scrollRectToVisible(tablaSocios.getCellRect(visible, 0, true));
                break;
            }
        }
        informar("Datos del socio", socio.getNombre() + " · " + socio.soyDeLaClase()
                + "\nDNI: " + socio.getDniSocio() + "\nCarrera / Área: " + carreraOArea(socio)
                + "\nPlazo: " + socio.getDiasPrestamo() + " días"
                + "\nEjemplares prestados: " + socio.cantLibrosPrestados());
    }

    /** Muestra las cantidades de estudiantes y docentes registrados en la biblioteca. */
    private void mostrarCantidades() {
        informar("Cantidad de socios", "Estudiantes: " + biblioteca.cantidadDeSociosPorTipo("Estudiante")
                + "\nDocentes: " + biblioteca.cantidadDeSociosPorTipo("Docente"));
    }

    /** Valida la responsabilidad del docente seleccionado y permite aumentar su plazo de préstamo. */
    private void premiarDocente() {
        Integer dni = claveSeleccionada(tablaSocios, "Seleccioná un docente en la tabla de socios.");
        if (dni == null) {
            return;
        }
        Socio socio = biblioteca.buscarSocio(dni);
        if (!(socio instanceof Docente) || !((Docente) socio).esResponsable()) {
            avisar("El premio se aplica a docentes que cumplen la condición de responsabilidad.");
            return;
        }
        Docente docente = (Docente) socio;

        JTextField dias = new JTextField(8);
        Object[] campos = {docente.getNombre() + " · Plazo actual: " + docente.getDiasPrestamo() + " días",
                "Días a agregar", dias};
        confirmarYGuardar("Premiar docente", campos, new Runnable() {
            @Override
            public void run() {
                int aumento = enteroPositivo(dias.getText(), "Días a agregar");
                if ((long) docente.getDiasPrestamo() + aumento > Integer.MAX_VALUE) {
                    throw new IllegalArgumentException("El nuevo plazo supera el máximo permitido.");
                }
                biblioteca.actualizarDiasDocente(docente, aumento);
            }
        }, "Plazo actualizado");
    }

    /**
     * Solicita confirmación antes de ejecutar una operación y actualiza las tablas tras el guardado.
     * Los errores de validación permiten corregir los campos. Un error de persistencia cierra la operación.
     *
     * @param tituloDialogo Título de la ventana de confirmación.
     * @param campos Etiquetas y controles que se muestran en el diálogo.
     * @param operacion Acción que valida los datos y solicita su guardado a la biblioteca.
     * @param mensaje Confirmación que se muestra en la barra de estado al guardar.
     */
    private void confirmarYGuardar(String tituloDialogo, Object[] campos, Runnable operacion, String mensaje) {
        while (JOptionPane.showConfirmDialog(panelPrincipal, campos, tituloDialogo,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
            try {
                operacion.run();
            } catch (IllegalArgumentException e) {
                avisar(e.getMessage());
                continue;
            } catch (IllegalStateException e) {
                mostrarError(e);
                return;
            }
            actualizarTablas(mensaje);
            return;
        }
    }

    /**
     * Lee la clave de la fila seleccionada en el modelo, aun cuando se haya ordenado la tabla.
     *
     * @param tabla Tabla cuya primera columna del modelo contiene la clave.
     * @param indicacion Mensaje que se muestra cuando falta seleccionar una fila.
     * @return ID del ejemplar o DNI del socio, según la tabla, o {@code null} si falta la selección.
     */
    private Integer claveSeleccionada(JTable tabla, String indicacion) {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            avisar(indicacion);
            return null;
        }
        return (Integer) tabla.getModel().getValueAt(tabla.convertRowIndexToModel(fila), 0);
    }

    /**
     * Obtiene el dato de la especialidad correspondiente al tipo de socio.
     *
     * @param socio Estudiante o docente de la biblioteca.
     * @return Carrera del estudiante o área del docente.
     */
    private static String carreraOArea(Socio socio) {
        if (socio instanceof Estudiante) {
            Estudiante estudiante = (Estudiante) socio;
            return estudiante.getCarrera();
        }
        return ((Docente) socio).getArea();
    }

    /**
     * Lee un campo obligatorio y quita los espacios de sus extremos.
     *
     * @param campo Control de texto del que se obtiene el valor.
     * @param nombre Nombre del campo que se incluye en el aviso de validación.
     * @return Texto ingresado con los espacios de los extremos eliminados.
     * @throws IllegalArgumentException Si el valor queda vacío.
     */
    private static String textoRequerido(JTextField campo, String nombre) {
        String texto = campo.getText().strip();
        if (texto.isEmpty()) {
            throw new IllegalArgumentException("Completá el campo «" + nombre + "».");
        }
        return texto;
    }

    /**
     * Convierte el texto ingresado en un número entero.
     *
     * @param texto Texto recibido del formulario.
     * @param nombre Nombre del campo que se incluye en el aviso de validación.
     * @return Valor convertido a {@code int}.
     * @throws IllegalArgumentException Si el texto queda vacío, tiene un formato inválido
     *         o representa un valor fuera del rango de {@code int}.
     */
    private static int entero(String texto, String nombre) {
        try {
            return Integer.parseInt(texto.strip());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Ingresá un número entero en «" + nombre + "».");
        }
    }

    /**
     * Convierte el texto en un entero mayor que cero.
     *
     * @param texto Texto recibido del formulario.
     * @param nombre Nombre del campo que se incluye en el aviso de validación.
     * @return Valor entero positivo.
     * @throws IllegalArgumentException Si el valor queda fuera del rango de {@code int},
     *         tiene un formato inválido o es menor o igual a cero.
     */
    private static int enteroPositivo(String texto, String nombre) {
        int valor = entero(texto, nombre);
        if (valor <= 0) {
            throw new IllegalArgumentException("El valor de «" + nombre + "» debe ser mayor que cero.");
        }
        return valor;
    }

    /**
     * Muestra un listado de solo lectura con desplazamiento.
     *
     * @param tituloDialogo Título de la ventana.
     * @param contenido Texto del listado.
     */
    private void mostrarListado(String tituloDialogo, String contenido) {
        JTextArea texto = new JTextArea(contenido, 10, 40);
        texto.setEditable(false);
        texto.setLineWrap(true);
        texto.setWrapStyleWord(true);
        texto.setCaretPosition(0);
        informar(tituloDialogo, new JScrollPane(texto));
    }

    /**
     * Abre un diálogo informativo desde la ventana de la biblioteca.
     *
     * @param tituloDialogo Título del diálogo.
     * @param mensaje Texto o componente Swing que se muestra.
     */
    private void informar(String tituloDialogo, Object mensaje) {
        JOptionPane.showMessageDialog(panelPrincipal, mensaje, tituloDialogo, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Muestra un aviso para orientar al usuario cuando falta un dato o una selección.
     *
     * @param mensaje Indicación que se muestra.
     */
    private void avisar(String mensaje) {
        JOptionPane.showMessageDialog(panelPrincipal, mensaje, "Revisá la operación", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Informa que el guardado falló y muestra el detalle devuelto por la persistencia.
     *
     * @param error Excepción de la operación de guardado.
     */
    private void mostrarError(IllegalStateException error) {
        JOptionPane.showMessageDialog(panelPrincipal, "No se pudo guardar la operación.\n" + error.getMessage(),
                "Error de guardado", JOptionPane.ERROR_MESSAGE);
    }

    /** Opción del selector de préstamos que conserva el ejemplar asociado. */
    private static class Ejemplar {
        private final Libro libro;

        /**
         * Crea una opción para el ejemplar disponible.
         *
         * @param libro Ejemplar que representa esta opción.
         */
        private Ejemplar(Libro libro) {
            this.libro = libro;
        }

        /**
         * Obtiene el ejemplar asociado a la opción.
         *
         * @return Ejemplar que se puede prestar.
         */
        private Libro getLibro() {
            return libro;
        }

        /**
         * Distingue ejemplares que pueden compartir el mismo título.
         *
         * @return Identificador y título que se muestran en el selector.
         */
        @Override
        public String toString() {
            return "#" + libro.getId() + " · " + libro.getTitulo();
        }
    }

    {
        $$$setupUI$$$();
    }

    /**
     * Inicializa los componentes y la distribución visual de {@code Interfaz.form}.
     * El diseñador puede regenerar este método al compilar el formulario.
     */
    private void $$$setupUI$$$() {
        java.awt.Color superficie = new java.awt.Color(0xFFFFFF);
        java.awt.Color texto = new java.awt.Color(0x172D42);
        java.awt.Color lineas = new java.awt.Color(0xE3EAF1);
        java.awt.Color seleccion = new java.awt.Color(0xDCEBFA);

        panelPrincipal = new JPanel(new BorderLayout(0, 12));
        panelPrincipal.setBackground(new java.awt.Color(0xF4F7FB));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(16, 20, 12, 20));

        titulo = new JLabel("Gestión de Biblioteca");
        titulo.setFont(titulo.getFont().deriveFont(java.awt.Font.BOLD, 26f));
        titulo.setForeground(new java.awt.Color(0x163854));
        panelPrincipal.add(titulo, BorderLayout.NORTH);

        tabbedPane1 = new JTabbedPane();
        panelPrincipal.add(tabbedPane1, BorderLayout.CENTER);

        pestañaLibros = new JPanel(new BorderLayout(0, 6));
        pestañaLibros.setBackground(superficie);
        pestañaLibros.setBorder(BorderFactory.createEmptyBorder(10, 10, 6, 10));

        tablaLibros = new JTable();
        tablaLibros.setBackground(superficie);
        tablaLibros.setForeground(texto);
        tablaLibros.setGridColor(lineas);
        tablaLibros.setSelectionBackground(seleccion);
        tablaLibros.setSelectionForeground(texto);
        tablaLibros.setFillsViewportHeight(true);
        tablaLibros.setRowHeight(25);
        tablaLibros.setIntercellSpacing(new Dimension(8, 1));
        tablaLibros.setShowHorizontalLines(true);
        tablaLibros.setShowVerticalLines(false);
        pestañaLibros.add(new JScrollPane(tablaLibros), BorderLayout.CENTER);

        JPanel accionesLibros = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 5));
        accionesLibros.setBackground(superficie);
        botonRegistrarEjemplar = new JButton("Registrar ejemplar");
        botonRegistrarEjemplar.setToolTipText("Agregar un ejemplar al catálogo");
        botonRegistrarEjemplar.setFont(botonRegistrarEjemplar.getFont().deriveFont(java.awt.Font.BOLD));
        accionesLibros.add(botonRegistrarEjemplar);
        botonQuitarEjemplar = new JButton("Quitar ejemplar");
        botonQuitarEjemplar.setToolTipText("Quitar del catálogo el ejemplar seleccionado");
        accionesLibros.add(botonQuitarEjemplar);
        botonTitulos = new JButton("Títulos únicos");
        botonTitulos.setToolTipText("Ver los títulos del catálogo sin repetir");
        accionesLibros.add(botonTitulos);
        botonTitular = new JButton("Consultar titular");
        botonTitular.setToolTipText("Ver quién tiene prestado el ejemplar seleccionado");
        accionesLibros.add(botonTitular);
        pestañaLibros.add(accionesLibros, BorderLayout.SOUTH);
        tabbedPane1.addTab("Libros", pestañaLibros);

        pestañaSocios = new JPanel(new BorderLayout(0, 6));
        pestañaSocios.setBackground(superficie);
        pestañaSocios.setBorder(BorderFactory.createEmptyBorder(10, 10, 6, 10));

        tablaSocios = new JTable();
        tablaSocios.setBackground(superficie);
        tablaSocios.setForeground(texto);
        tablaSocios.setGridColor(lineas);
        tablaSocios.setSelectionBackground(seleccion);
        tablaSocios.setSelectionForeground(texto);
        tablaSocios.setFillsViewportHeight(true);
        tablaSocios.setRowHeight(25);
        tablaSocios.setIntercellSpacing(new Dimension(8, 1));
        tablaSocios.setShowHorizontalLines(true);
        tablaSocios.setShowVerticalLines(false);
        pestañaSocios.add(new JScrollPane(tablaSocios), BorderLayout.CENTER);

        JPanel accionesSocios = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 5));
        accionesSocios.setBackground(superficie);
        botonRegistrarSocio = new JButton("Registrar socio");
        botonRegistrarSocio.setToolTipText("Inscribir a un estudiante o docente");
        botonRegistrarSocio.setFont(botonRegistrarSocio.getFont().deriveFont(java.awt.Font.BOLD));
        accionesSocios.add(botonRegistrarSocio);
        botonQuitarSocio = new JButton("Quitar socio");
        botonQuitarSocio.setToolTipText("Quitar del padrón el socio seleccionado");
        accionesSocios.add(botonQuitarSocio);
        botonBuscarSocio = new JButton("Buscar DNI");
        botonBuscarSocio.setToolTipText("Encontrar un socio por su DNI");
        accionesSocios.add(botonBuscarSocio);
        botonCantidadSocios = new JButton("Cantidades");
        botonCantidadSocios.setToolTipText("Consultar la cantidad de estudiantes y docentes");
        accionesSocios.add(botonCantidadSocios);
        botonDocentesResponsables = new JButton("Responsables");
        botonDocentesResponsables.setToolTipText("Consultar docentes responsables según su historial de préstamos");
        accionesSocios.add(botonDocentesResponsables);
        botonPremiarDocente = new JButton("Premiar");
        botonPremiarDocente.setToolTipText("Aumentar el plazo de préstamo del docente seleccionado");
        accionesSocios.add(botonPremiarDocente);
        pestañaSocios.add(accionesSocios, BorderLayout.SOUTH);
        tabbedPane1.addTab("Socios", pestañaSocios);

        pestañaPrestamos = new JPanel(new BorderLayout(0, 6));
        pestañaPrestamos.setBackground(superficie);
        pestañaPrestamos.setBorder(BorderFactory.createEmptyBorder(10, 10, 6, 10));

        tablaPrestamos = new JTable();
        tablaPrestamos.setBackground(superficie);
        tablaPrestamos.setForeground(texto);
        tablaPrestamos.setGridColor(lineas);
        tablaPrestamos.setSelectionBackground(seleccion);
        tablaPrestamos.setSelectionForeground(texto);
        tablaPrestamos.setFillsViewportHeight(true);
        tablaPrestamos.setRowHeight(25);
        tablaPrestamos.setIntercellSpacing(new Dimension(8, 1));
        tablaPrestamos.setShowHorizontalLines(true);
        tablaPrestamos.setShowVerticalLines(false);
        pestañaPrestamos.add(new JScrollPane(tablaPrestamos), BorderLayout.CENTER);

        JPanel accionesPrestamos = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 5));
        accionesPrestamos.setBackground(superficie);
        soloVencidos = new JCheckBox("Solo vencidos");
        soloVencidos.setToolTipText("Mostrar solo los préstamos activos fuera de plazo");
        soloVencidos.setBackground(superficie);
        soloVencidos.setForeground(texto);
        accionesPrestamos.add(soloVencidos);
        botonPrestar = new JButton("Prestar");
        botonPrestar.setToolTipText("Registrar un nuevo préstamo");
        botonPrestar.setFont(botonPrestar.getFont().deriveFont(java.awt.Font.BOLD));
        accionesPrestamos.add(botonPrestar);
        botonDevolver = new JButton("Devolver");
        botonDevolver.setToolTipText("Registrar la devolución del préstamo seleccionado");
        accionesPrestamos.add(botonDevolver);
        pestañaPrestamos.add(accionesPrestamos, BorderLayout.SOUTH);
        tabbedPane1.addTab("Préstamos", pestañaPrestamos);

        etiquetaEstado = new JLabel("Biblioteca lista");
        etiquetaEstado.setForeground(new java.awt.Color(0x425B72));
        panelPrincipal.add(etiquetaEstado, BorderLayout.SOUTH);
    }
}
