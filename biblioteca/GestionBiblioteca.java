package biblioteca;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Scanner;

/**
 * Clase ejecutable para gestionar los libros, socios y préstamos de una biblioteca
 * mediante un menú de consola.
 */
public class GestionBiblioteca {
    /**
     * Carga datos de ejemplo y presenta el menú de gestión de la biblioteca.
     *
     * @param args Argumentos de la línea de comandos, no utilizados.
     */
    public static void main(String[] args) {
        Scanner teclado = new Scanner(System.in);
        Biblioteca unaBiblioteca = new Biblioteca("Biblioteca Central");

        precargarDatos(unaBiblioteca);
        System.out.println("Se cargaron los datos de ejemplo.");

        int opcion;
        do {
            mostrarMenu(unaBiblioteca.getNombre());
            System.out.print("Seleccione una opción: ");
            opcion = teclado.nextInt();
            teclado.nextLine();
            System.out.println();

            ejecutarOpcion(unaBiblioteca, teclado, opcion);
        } while (opcion != 0);

        System.out.println("Fin de la gestión de la biblioteca.");
    }

    /**
     * Muestra las operaciones disponibles en el menú principal.
     *
     * @param p_nombre Nombre de la biblioteca.
     */
    private static void mostrarMenu(String p_nombre) {
        System.out.println("\n==============================================");
        System.out.println("           GESTIÓN DE BIBLIOTECA");
        System.out.println("              " + p_nombre);
        System.out.println("==============================================");
        System.out.println(" 1. Registrar un libro");
        System.out.println(" 2. Registrar un estudiante");
        System.out.println(" 3. Registrar un docente");
        System.out.println(" 4. Prestar un libro");
        System.out.println(" 5. Devolver un libro");
        System.out.println(" 6. Consultar quién tiene un ejemplar");
        System.out.println(" 7. Buscar un socio por DNI");
        System.out.println(" 8. Listar libros");
        System.out.println(" 9. Listar socios");
        System.out.println("10. Consultar cantidad de socios por tipo");
        System.out.println("11. Listar títulos sin repetir");
        System.out.println("12. Listar préstamos vencidos");
        System.out.println("13. Listar docentes responsables");
        System.out.println("14. Premiar a un docente responsable");
        System.out.println(" 0. Salir");
        System.out.println("==============================================");
    }

    /**
     * Ejecuta la operación elegida y utiliza los métodos de la biblioteca
     * para las altas, los préstamos, las devoluciones y las consultas.
     *
     * @param p_biblioteca Biblioteca que se está administrando.
     * @param p_teclado Entrada de consola.
     * @param p_opcion Opción seleccionada.
     */
    private static void ejecutarOpcion(Biblioteca p_biblioteca, Scanner p_teclado, int p_opcion) {
        switch (p_opcion) {
            case 1:
                registrarLibro(p_biblioteca, p_teclado);
                break;
            case 2:
                registrarSocio(p_biblioteca, p_teclado, false);
                break;
            case 3:
                registrarSocio(p_biblioteca, p_teclado, true);
                break;
            case 4:
                prestarLibro(p_biblioteca, p_teclado);
                break;
            case 5:
                devolverLibro(p_biblioteca, p_teclado);
                break;
            case 6:
                consultarTitular(p_biblioteca, p_teclado);
                break;
            case 7:
                mostrarSocio(p_biblioteca, p_teclado);
                break;
            case 8:
                System.out.print(p_biblioteca.listaDeLibros());
                break;
            case 9:
                System.out.print(p_biblioteca.listaDeSocios());
                break;
            case 10:
                System.out.println("Cantidad de estudiantes: " + p_biblioteca.cantidadDeSociosPorTipo("Estudiante"));
                System.out.println("Cantidad de docentes: " + p_biblioteca.cantidadDeSociosPorTipo("Docente"));
                break;
            case 11:
                System.out.print(p_biblioteca.listaDeTitulos());
                break;
            case 12:
                mostrarVencidos(p_biblioteca);
                break;
            case 13:
                System.out.print(p_biblioteca.listaDeDocentesResponsables());
                break;
            case 14:
                premiarDocente(p_biblioteca, p_teclado);
                break;
            case 0:
                break;
            default:
                System.out.println("Opción no válida. Seleccione un número entre 0 y 14.");
                break;
        }
    }

    /**
     * Carga socios y ejemplares para demostrar las consultas del integrador.
     * Incluye dos ejemplares del mismo título y un préstamo vencido.
     *
     * @param p_biblioteca Biblioteca donde se registran los datos.
     */
    private static void precargarDatos(Biblioteca p_biblioteca) {
        p_biblioteca.nuevoSocioEstudiante(1001, "Ana Torres", "Sistemas");
        p_biblioteca.nuevoSocioEstudiante(1002, "Lucas Medina", "Informática");
        p_biblioteca.nuevoSocioDocente(1003, "Elena Ruiz", "Programación");
        p_biblioteca.nuevoSocioDocente(1004, "Marcos Díaz", "Bases de datos");

        p_biblioteca.nuevoLibro("Programando con JAVA", 1, "Editorial Universitaria", 2022);
        p_biblioteca.nuevoLibro("Programando con JAVA", 2, "Editorial Universitaria", 2024);
        p_biblioteca.nuevoLibro("Introducción a las bases de datos", 1, "Ediciones Técnicas", 2021);
        p_biblioteca.nuevoLibro("Programación Orientada a Objetos", 1, "Ediciones Técnicas", 2023);

        Socio estudiante = p_biblioteca.buscarSocio(1001);
        Libro libroJava = p_biblioteca.getLibros().get(0);
        p_biblioteca.prestarLibro(new GregorianCalendar(), estudiante, libroJava);

        Calendar retiroVencido = new GregorianCalendar();
        retiroVencido.add(Calendar.DATE, -30);
        Socio docenteConAtraso = p_biblioteca.buscarSocio(1004);
        Libro libroVencido = p_biblioteca.getLibros().get(2);
        p_biblioteca.prestarLibro(retiroVencido, docenteConAtraso, libroVencido);
    }

    private static void registrarLibro(Biblioteca p_biblioteca, Scanner p_teclado) {
        System.out.print("Título: ");
        String titulo = p_teclado.nextLine();
        
        System.out.print("Número de edición: ");
        int edicion = p_teclado.nextInt();
        p_teclado.nextLine();
        
        System.out.print("Editorial: ");
        String editorial = p_teclado.nextLine();
        
        System.out.print("Año de publicación: ");
        int anio = p_teclado.nextInt();
        p_teclado.nextLine();

        p_biblioteca.nuevoLibro(titulo, edicion, editorial, anio);
        System.out.println("Libro registrado correctamente.");
    }

    private static void registrarSocio(Biblioteca p_biblioteca, Scanner p_teclado, boolean p_esDocente) {
        System.out.print("DNI: ");
        int dni = p_teclado.nextInt();
        p_teclado.nextLine();
        
        if (p_biblioteca.buscarSocio(dni) != null) {
            System.out.println("Ya existe un socio con ese DNI.");
            return;
        }

        System.out.print("Nombre y apellido: ");
        String nombre = p_teclado.nextLine();
        
        if (p_esDocente) {
            System.out.print("Área: ");
            String area = p_teclado.nextLine();
            p_biblioteca.nuevoSocioDocente(dni, nombre, area);
        } else {
            System.out.print("Carrera: ");
            String carrera = p_teclado.nextLine();
            p_biblioteca.nuevoSocioEstudiante(dni, nombre, carrera);
        }
        
        System.out.println("Socio registrado correctamente.");
    }

    private static void prestarLibro(Biblioteca p_biblioteca, Scanner p_teclado) {
        Socio unSocio = pedirSocio(p_biblioteca, p_teclado);
        
        if (unSocio == null) {
            return;
        }

        Libro unLibro = elegirEjemplar(p_biblioteca, p_teclado);
        
        if (unLibro == null) {
            return;
        }

        if (p_biblioteca.prestarLibro(new GregorianCalendar(), unSocio, unLibro)) {
            System.out.println("Préstamo registrado para " + unSocio.getNombre() + ".");
        } else if (unLibro.prestado()) {
            System.out.println("El ejemplar ya está prestado.");
        } else {
            System.out.println("El socio no está habilitado para pedir otro libro.");
        }
    }

    private static void devolverLibro(Biblioteca p_biblioteca, Scanner p_teclado) {
        Libro unLibro = elegirEjemplar(p_biblioteca, p_teclado);
        
        if (unLibro != null) {
            try {
                p_biblioteca.devolverLibro(unLibro);
                System.out.println("Devolución registrada correctamente.");
            } catch (LibroNoPrestadoException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static void consultarTitular(Biblioteca p_biblioteca, Scanner p_teclado) {
        Libro unLibro = elegirEjemplar(p_biblioteca, p_teclado);
        
        if (unLibro != null) {
            try {
                String nombre = p_biblioteca.quienTieneElLibro(unLibro);
                System.out.println("El ejemplar de \"" + unLibro.getTitulo() + "\" está prestado a " + nombre + ".");
            } catch (LibroNoPrestadoException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static void mostrarSocio(Biblioteca p_biblioteca, Scanner p_teclado) {
        Socio unSocio = pedirSocio(p_biblioteca, p_teclado);
        
        if (unSocio != null) {
            System.out.println(unSocio.toString());
            System.out.println("Días de préstamo: " + unSocio.getDiasPrestamo());
        }
    }

    private static void mostrarVencidos(Biblioteca p_biblioteca) {
        ArrayList<Prestamo> vencidos = p_biblioteca.prestamosVencidos();
        
        if (vencidos.isEmpty()) {
            System.out.println("No hay préstamos vencidos.");
        } else {
            System.out.println("Préstamos vencidos:");
            for (Prestamo unPrestamo: vencidos) {
                System.out.println(unPrestamo.toString() + "\n");
            }
        }
    }

    /**
     * Agrega días de préstamo a un docente que cumple la condición de responsabilidad.
     *
     * @param p_biblioteca Biblioteca que contiene al socio.
     * @param p_teclado Entrada de consola.
     */
    private static void premiarDocente(Biblioteca p_biblioteca, Scanner p_teclado) {
        Socio unSocio = pedirSocio(p_biblioteca, p_teclado);
        
        if (unSocio == null) {
            return;
        }
        
        if (!(unSocio instanceof Docente)) {
            System.out.println("El socio seleccionado no es docente.");
            return;
        }

        Docente unDocente = (Docente) unSocio;
        if (!unDocente.esResponsable()) {
            System.out.println("El docente no cumple la condición de responsabilidad.");
            return;
        }

        System.out.print("Días a agregar: ");
        int dias = p_teclado.nextInt();
        p_teclado.nextLine();
        
        if (dias <= 0) {
            System.out.println("La cantidad de días debe ser mayor que cero.");
            return;
        }

        unDocente.cambiarDiasDePrestamo(dias);
        System.out.println("Nuevo plazo de préstamo de " + unDocente.getNombre()
                + ": " + unDocente.getDiasPrestamo() + " días.");
    }

    private static Socio pedirSocio(Biblioteca p_biblioteca, Scanner p_teclado) {
        System.out.print("DNI del socio: ");
        int dni = p_teclado.nextInt();
        p_teclado.nextLine();
        
        Socio unSocio = p_biblioteca.buscarSocio(dni);
        
        if (unSocio == null) {
            System.out.println("No se encontró un socio con ese DNI.");
        }
        
        return unSocio;
    }

    /**
     * Permite elegir un ejemplar por número, aunque varios tengan el mismo título.
     *
     * @param p_biblioteca Biblioteca que contiene los ejemplares.
     * @param p_teclado Entrada de consola.
     * @return Ejemplar seleccionado o {@code null} si se cancela la consulta o no hay libros.
     */
    private static Libro elegirEjemplar(Biblioteca p_biblioteca, Scanner p_teclado) {
        ArrayList<Libro> libros = p_biblioteca.getLibros();
        
        if (libros.isEmpty()) {
            System.out.println("No hay libros registrados.");
            return null;
        }

        System.out.println("Ejemplares registrados:");
        int numero = 1;
        for (Libro unLibro: libros) {
            String estado = "Disponible";
            
            if (unLibro.prestado()) {
                estado = "Prestado";
            }
            
            System.out.println(numero + ") " + unLibro.getTitulo() + " | Edición " + unLibro.getEdicion()
                    + " | " + unLibro.getEditorial() + " | " + unLibro.getAnio() + " | " + estado);
            numero++;
        }

        System.out.print("Número de ejemplar (0 para cancelar): ");
        int seleccion = p_teclado.nextInt();
        p_teclado.nextLine();
        
        if (seleccion == 0) {
            return null;
        }
        
        if (seleccion < 1 || seleccion > libros.size()) {
            System.out.println("El número de ejemplar no es válido.");
            return null;
        }
        
        return libros.get(seleccion - 1);
    }
}
