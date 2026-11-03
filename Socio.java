import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Superclase abstracta que define las caracteristicas comunes de un Socio.
 * Especializada en Estudiante o Docente.
 * 
 * @author Tobias Nuñez
 */
public abstract class Socio {
    private int dniSocio;
    private String nombre;
    private int diasPrestamo;
    private ArrayList<Prestamo> prestamos;

    /**
     * Constructor principal. Inicializa los datos comunes y la lista de prestamos vacia.
     */
    public Socio(int p_dniSocio, String p_nombre, int p_diasPrestamo) {
        this.setDniSocio(p_dniSocio);
        this.setNombre(p_nombre);
        this.setDiasPrestamo(p_diasPrestamo);
        this.setPrestamos(new ArrayList<Prestamo>());
    }

    /**
     * Constructor sobrecargado para inicializar con una lista existente de prestamos.
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

    protected void setDiasPrestamo(int p_diasPrestamo) {
        this.diasPrestamo = p_diasPrestamo;
    }

    private void setPrestamos(ArrayList<Prestamo> p_prestamos) {
        this.prestamos = p_prestamos;
    }

    public int getDniSocio() {
        return this.dniSocio;
    }

    public String getNombre() {
        return this.nombre;
    }

    public int getDiasPrestamo() {
        return this.diasPrestamo;
    }

    public ArrayList<Prestamo> getPrestamos() {
        return this.prestamos;
    }

    /**
     * Agrega un nuevo prestamo a la lista personal del socio.
     */
    public void agregarPrestamo(Prestamo p_prestamo) {
        this.getPrestamos().add(p_prestamo);
    }

    /**
     * Quita un prestamo de la lista personal del socio.
     */
    public void quitarPrestamo(Prestamo p_prestamo) {
        this.getPrestamos().remove(p_prestamo);
    }

    /**
     * Retorna la cantidad de libros que el socio conserva en su poder (aun sin devolver).
     */
    public int cantLibrosPrestados() {
        int contador = 0;
        for (Prestamo p : this.getPrestamos()) {
            if (p.getFechaDevolucion() == null) {
                contador++;
            }
        }
        return contador;
    }

    /**
     * Verifica que el socio no posea prestamos en su poder que esten vencidos a la fecha de hoy.
     */
    public boolean puedePedir() {
        Calendar hoy = new GregorianCalendar();
        for (Prestamo p : this.getPrestamos()) {
            if (p.getFechaDevolucion() == null && p.vencido(hoy)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Metodo abstracto que obliga a cada subclase a reportar su tipo.
     */
    public abstract String soyDeLaClase();

    /**
     * Retorna la informacion del socio siguiendo el formato requerido.
     */
    @Override
    public String toString() {
        return "D.N.I.: " + this.getDniSocio() + " || " + this.getNombre() + 
               " (" + this.soyDeLaClase() + ") || Libros Prestados: " + this.cantLibrosPrestados();
    }
}
