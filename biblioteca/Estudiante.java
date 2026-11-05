package biblioteca;

import java.util.ArrayList;

/**
 * Clase que representa a un socio estudiante de la biblioteca,
 * con un plazo de 20 días y un máximo de 3 libros prestados a la vez.
 *
 * @author Axel Gabriel Nuñez
 * @version 2026
 */
public class Estudiante extends Socio {
    private String carrera;

    /**
     * Inicializa un estudiante con sus datos y un plazo de préstamo de 20 días.
     *
     * @param p_dniSocio DNI del estudiante.
     * @param p_nombre Nombre y apellido del estudiante.
     * @param p_carrera Carrera que cursa el estudiante.
     */
    public Estudiante(int p_dniSocio, String p_nombre, String p_carrera) {
        super(p_dniSocio, p_nombre, 20);
        this.setCarrera(p_carrera);
    }

    /**
     * Inicializa un estudiante con una colección de préstamos existente
     * y un plazo de préstamo de 20 días.
     *
     * @param p_dniSocio DNI del estudiante.
     * @param p_nombre Nombre y apellido del estudiante.
     * @param p_carrera Carrera que cursa el estudiante.
     * @param p_prestamos Colección de préstamos del estudiante, distinta de null.
     */
    public Estudiante(int p_dniSocio, String p_nombre, String p_carrera, ArrayList<Prestamo> p_prestamos) {
        super(p_dniSocio, p_nombre, 20, p_prestamos);
        this.setCarrera(p_carrera);
    }

    private void setCarrera(String p_carrera) {
        this.carrera = p_carrera;
    }

    public String getCarrera() {
        return this.carrera;
    }

    /**
     * Comprueba que el estudiante no tenga préstamos vencidos y que posea
     * menos de 3 libros, para no superar ese máximo al realizar otro préstamo.
     *
     * @return true si puede pedir otro libro, false en caso contrario.
     */
    @Override
    public boolean puedePedir() {
        return super.puedePedir() && this.cantLibrosPrestados() < 3;
    }

    /**
     * Devuelve el tipo de socio al que pertenece el estudiante.
     *
     * @return La cadena "Estudiante".
     */
    @Override
    public String soyDeLaClase() {
        return "Estudiante";
    }
}
