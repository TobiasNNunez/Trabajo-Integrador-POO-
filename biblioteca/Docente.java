package biblioteca;

import java.util.*;

/**
 * Clase que representa a un socio docente de la biblioteca.
 * Comienza con 5 días de préstamo y no tiene límite de cantidad de libros.
 *
 * @author Axel Gabriel Nuñez
 * @version 2026
 */
public class Docente extends Socio {
    private String area;

    /**
     * Inicializa un docente con sus datos y un plazo de préstamo de 5 días.
     *
     * @param p_dniSocio DNI del docente.
     * @param p_nombre Nombre y apellido del docente.
     * @param p_area Área en la que se desempeña el docente.
     */
    public Docente(int p_dniSocio, String p_nombre, String p_area) {
        super(p_dniSocio, p_nombre, 5);
        this.setArea(p_area);
    }
    
    /**
     * Inicializa un docente con sus datos, una colección de préstamos existente
     * y un plazo inicial de préstamo de 5 días.
     *
     * @param p_dniSocio DNI del docente.
     * @param p_nombre Nombre y apellido del docente.
     * @param p_prestamos Colección de préstamos del docente.
     * @param p_area Área en la que se desempeña el docente.
     */
    public Docente(int p_dniSocio, String p_nombre, ArrayList<Prestamo> p_prestamos, String p_area) {
        super(p_dniSocio, p_nombre, 5, p_prestamos);
        this.setArea(p_area);
    }

    private void setArea(String p_area) {
        this.area = p_area;
    }

    public String getArea() {
        return this.area;
    }

    /**
     * Comprueba que el docente nunca haya tenido ni tenga préstamos vencidos.
     * Para los préstamos devueltos utiliza la fecha de devolución y para
     * los pendientes utiliza la fecha actual.
     * La colección de préstamos debe conservar el historial completo.
     *
     * @return true si todos sus préstamos están o fueron devueltos en término,
     *         incluso si todavía no realizó préstamos; false en caso contrario.
     */
    public boolean esResponsable() {
        boolean responsable = true;
        Calendar fechaActual = new GregorianCalendar();

        for (Prestamo unPrestamo: this.getPrestamos()) {
            Calendar unaFecha = unPrestamo.getFechaDevolucion();

            if (unaFecha == null) {
                unaFecha = fechaActual;
            }

            if (unPrestamo.vencido(unaFecha)) {
                responsable = false;
            }
        }

        return responsable;
    }

    /**
     * Adiciona días al plazo de préstamo que tiene actualmente el docente.
     *
     * @param p_dias Cantidad de días que se agregan al plazo actual.
     */
    public void cambiarDiasDePrestamo(int p_dias) {
        this.setDiasPrestamo(this.getDiasPrestamo() + p_dias);
    }

    /**
     * Devuelve el tipo de socio al que pertenece el docente.
     *
     * @return La cadena "Docente".
     */
    @Override
    public String soyDeLaClase() {
        return "Docente";
    }
}