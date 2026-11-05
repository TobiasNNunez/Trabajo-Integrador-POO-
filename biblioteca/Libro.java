package biblioteca;

import java.util.ArrayList;

/**
 * Clase que representa un ejemplar de libro perteneciente a una biblioteca.
 * Almacena sus datos bibliograficos y conserva el historial de los
 * prestamos realizados sobre el ejemplar.
 *
 * @author Otero Emmanuel David
 * @version 1.0 (TP7 - Trabajo Final Integrador POO)
 */
public class Libro {

    // 1. Atributos - Visibilidad privada
    private String titulo;
    private int edicion;
    private String editorial;
    private int anio;
    private ArrayList<Prestamo> prestamos;

    // 2. Constructores Sobrecargados (Multiplicidad 0..*)

    /**
     * Constructor para inicializar un libro con sus datos bibliograficos
     * y una coleccion vacia de prestamos.
     *
     * @param p_titulo Titulo del libro.
     * @param p_edicion Numero de edicion del libro.
     * @param p_editorial Editorial del libro.
     * @param p_anio Anio de publicacion del libro.
     */
    public Libro(String p_titulo, int p_edicion,
                 String p_editorial, int p_anio) {
        this.setTitulo(p_titulo);
        this.setEdicion(p_edicion);
        this.setEditorial(p_editorial);
        this.setAnio(p_anio);
        this.setPrestamos(new ArrayList<Prestamo>());
    }

    /**
     * Constructor sobrecargado para inicializar un libro con
     * una coleccion de prestamos existente.
     *
     * @param p_titulo Titulo del libro.
     * @param p_edicion Numero de edicion del libro.
     * @param p_editorial Editorial del libro.
     * @param p_anio Anio de publicacion del libro.
     * @param p_prestamos Coleccion inicial de objetos Prestamo.
     */
    public Libro(String p_titulo, int p_edicion,
                 String p_editorial, int p_anio,
                 ArrayList<Prestamo> p_prestamos) {
        this.setTitulo(p_titulo);
        this.setEdicion(p_edicion);
        this.setEditorial(p_editorial);
        this.setAnio(p_anio);
        this.setPrestamos(p_prestamos);
    }

    // 3. Mutadores (Setters) - Privados

    /**
     * Modifica el titulo del libro.
     *
     * @param p_titulo Nuevo titulo del libro.
     */
    private void setTitulo(String p_titulo) {
        this.titulo = p_titulo;
    }

    /**
     * Modifica el numero de edicion del libro.
     *
     * @param p_edicion Nueva edicion del libro.
     */
    private void setEdicion(int p_edicion) {
        this.edicion = p_edicion;
    }

    /**
     * Modifica la editorial del libro.
     *
     * @param p_editorial Nueva editorial del libro.
     */
    private void setEditorial(String p_editorial) {
        this.editorial = p_editorial;
    }

    /**
     * Modifica el anio de publicacion del libro.
     *
     * @param p_anio Nuevo anio de publicacion.
     */
    private void setAnio(int p_anio) {
        this.anio = p_anio;
    }

    /**
     * Modifica la coleccion de prestamos del libro.
     *
     * @param p_prestamos Lista de objetos Prestamo.
     */
    private void setPrestamos(ArrayList<Prestamo> p_prestamos) {
        this.prestamos = p_prestamos;
    }

    // 4. Observadores (Getters) - Publicos

    /**
     * Devuelve el titulo del libro.
     *
     * @return Titulo del libro.
     */
    public String getTitulo() {
        return this.titulo;
    }

    /**
     * Devuelve el numero de edicion del libro.
     *
     * @return Edicion del libro.
     */
    public int getEdicion() {
        return this.edicion;
    }

    /**
     * Devuelve la editorial del libro.
     *
     * @return Editorial del libro.
     */
    public String getEditorial() {
        return this.editorial;
    }

    /**
     * Devuelve el anio de publicacion del libro.
     *
     * @return Anio de publicacion.
     */
    public int getAnio() {
        return this.anio;
    }

    /**
     * Devuelve la coleccion de prestamos realizados sobre el ejemplar.
     *
     * @return ArrayList de objetos Prestamo.
     */
    public ArrayList<Prestamo> getPrestamos() {
        return this.prestamos;
    }

    // 5. Metodos de Gestion de Coleccion

    /**
     * Agrega un prestamo al historial utilizando un nombre alternativo.
     *
     * @param p_prestamo Objeto Prestamo a incorporar.
     */
    public void agregarPrestamo(Prestamo p_prestamo) {
        this.getPrestamos().add(p_prestamo);
    }

    /**
     * Elimina un prestamo especifico de la coleccion.
     * Se utiliza solamente para corregir registros incorrectos,
     * no para registrar devoluciones.
     *
     * @param p_prestamo Objeto Prestamo a eliminar.
     */
    public void removePrestamo(Prestamo p_prestamo) {
        this.getPrestamos().remove(p_prestamo);
    }

    // 6. Metodos de Comportamiento

    /**
     * Devuelve el ultimo prestamo registrado sobre el ejemplar.
     * Si no existen prestamos, devuelve null.
     *
     * @return Ultimo objeto Prestamo o null si no hay registros.
     */
    public Prestamo ultimoPrestamo() {
        Prestamo ultimo = null;

        for (Prestamo unPrestamo: this.getPrestamos()) {
            ultimo = unPrestamo;
        }

        return ultimo;
    }

    /**
     * Devuelve el ultimo prestamo utilizando el nombre alternativo
     * empleado en el ejemplo de la catedra.
     *
     * @return Ultimo objeto Prestamo o null si no hay registros.
     */
    public Prestamo getPrestamo() {
        return this.ultimoPrestamo();
    }

    /**
     * Determina si el ejemplar se encuentra actualmente prestado.
     * Consulta la fecha de devolucion del ultimo prestamo.
     * Si no existe una fecha de devolucion, el ejemplar
     * continua prestado.
     *
     * @return true si el ejemplar esta prestado,
     *         false si esta disponible.
     */
    public boolean prestado() {
        Prestamo ultimo = this.ultimoPrestamo();

        if (ultimo != null) {
            return ultimo.getFechaDevolucion() == null;
        }

        return false;
    }

    /**
     * Devuelve una representacion textual del libro,
     * respetando el formato solicitado en la consigna.
     *
     * @return Cadena con el formato "Titulo: <titulo>".
     */
    @Override
    public String toString() {
        return "Titulo: " + this.getTitulo();
    }
}
