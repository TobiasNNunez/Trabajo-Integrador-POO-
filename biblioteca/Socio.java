package biblioteca;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Representa a un socio con su DNI, su plazo y su historial de préstamos.
 * Las subclases aportan el tipo de socio y sus condiciones particulares.
 *
 * @author Tobias Nuñez
 */
public abstract class Socio {
    private int dniSocio;
    private String nombre;
    private int diasPrestamo;
    private ArrayList<Prestamo> prestamos;

    /**
     * Inicializa un socio con un historial vacío.
     *
     * @param p_dniSocio DNI del socio.
     * @param p_nombre Nombre y apellido del socio.
     * @param p_diasPrestamo Plazo de préstamo en días.
     */
    public Socio(int p_dniSocio, String p_nombre, int p_diasPrestamo) {
        this.setDniSocio(p_dniSocio);
        this.setNombre(p_nombre);
        this.setDiasPrestamo(p_diasPrestamo);
        this.setPrestamos(new ArrayList<Prestamo>());
    }

    /**
     * Inicializa un socio con un historial ya disponible.
     *
     * @param p_dniSocio DNI del socio.
     * @param p_nombre Nombre y apellido del socio.
     * @param p_diasPrestamo Plazo de préstamo en días.
     * @param p_prestamos Colección que se incorpora como historial.
     */
    public Socio(int p_dniSocio, String p_nombre, int p_diasPrestamo, ArrayList<Prestamo> p_prestamos) {
        this.setDniSocio(p_dniSocio);
        this.setNombre(p_nombre);
        this.setDiasPrestamo(p_diasPrestamo);
        this.setPrestamos(p_prestamos);
    }

    private void setDniSocio(int p_dniSocio) {
        this.dniSocio = p_dniSocio;
    }

    private void setNombre(String p_nombre) {
        this.nombre = p_nombre;
    }

    /**
     * Asigna el plazo de préstamo del socio.
     *
     * @param p_diasPrestamo Plazo en días.
     */
    protected void setDiasPrestamo(int p_diasPrestamo) {
        this.diasPrestamo = p_diasPrestamo;
    }

    private void setPrestamos(ArrayList<Prestamo> p_prestamos) {
        this.prestamos = p_prestamos;
    }

    /**
     * Devuelve el DNI del socio.
     *
     * @return DNI del socio.
     */
    public int getDniSocio() {
        return this.dniSocio;
    }

    /**
     * Devuelve el nombre del socio.
     *
     * @return Nombre y apellido del socio.
     */
    public String getNombre() {
        return this.nombre;
    }

    /**
     * Devuelve el plazo vigente.
     *
     * @return Cantidad de días de préstamo.
     */
    public int getDiasPrestamo() {
        return this.diasPrestamo;
    }

    /**
     * Devuelve la colección de préstamos activos y devueltos.
     *
     * @return Historial de préstamos del socio.
     */
    public ArrayList<Prestamo> getPrestamos() {
        return this.prestamos;
    }

    /**
     * Agrega un préstamo al historial del socio.
     *
     * @param p_prestamo Préstamo que se incorpora.
     */
    public void agregarPrestamo(Prestamo p_prestamo) {
        this.getPrestamos().add(p_prestamo);
    }

    /**
     * Cuenta los préstamos cuya fecha de devolución sigue pendiente.
     *
     * @return Cantidad de ejemplares que están en poder del socio.
     */
    public int cantLibrosPrestados() {
        int contador = 0;
        for (Prestamo p: this.getPrestamos()) {
            if (p.getFechaDevolucion() == null) {
                contador++;
            }
        }
        return contador;
    }

    /**
     * Evalúa la regla común de préstamo para todos los socios.
     *
     * @return {@code true} si el socio carece de préstamos activos vencidos.
     */
    public boolean puedePedir() {
        Calendar hoy = new GregorianCalendar();
        for (Prestamo p: this.getPrestamos()) {
            if (p.getFechaDevolucion() == null && p.vencido(hoy)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Identifica el tipo concreto de socio en los listados y en el repositorio.
     *
     * @return Nombre del tipo de socio.
     */
    public abstract String soyDeLaClase();

    /**
     * Describe al socio para los listados.
     *
     * @return Texto con el DNI, el nombre, el tipo y la cantidad de préstamos activos.
     */
    @Override
    public String toString() {
        return "D.N.I.: " + this.getDniSocio() + " || " + this.getNombre() + 
               " (" + this.soyDeLaClase() + ") || Libros Prestados: " + this.cantLibrosPrestados();
    }
}
