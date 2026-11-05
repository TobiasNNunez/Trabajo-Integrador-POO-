package biblioteca;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Representa el prestamo de un libro a un socio de la biblioteca.
 * Vincula el libro y el socio, administrando las fechas y su vencimiento.
 * 
 * @author Tobias Nuñez
 */
public class Prestamo {
    private Calendar fechaRetiro;
    private Calendar fechaDevolucion;
    private Socio socio;
    private Libro libro;

    /**
     * Constructor para un prestamo nuevo. Inicia con fecha de devolucion en null (activo).
     */
    public Prestamo(Calendar p_fechaRetiro, Socio p_socio, Libro p_libro) {
        this.setFechaRetiro(p_fechaRetiro);
        this.setFechaDevolucion(null);
        this.setSocio(p_socio);
        this.setLibro(p_libro);
    }

    /**
     * Constructor sobrecargado para prestamos con fecha de devolucion ya registrada.
     */
    public Prestamo(Calendar p_fechaRetiro, Calendar p_fechaDevolucion, Socio p_socio, Libro p_libro) {
        this.setFechaRetiro(p_fechaRetiro);
        this.setFechaDevolucion(p_fechaDevolucion);
        this.setSocio(p_socio);
        this.setLibro(p_libro);
    }

    private void setFechaRetiro(Calendar p_fechaRetiro) {
        this.fechaRetiro = p_fechaRetiro;
    }

    private void setFechaDevolucion(Calendar p_fechaDevolucion) {
        this.fechaDevolucion = p_fechaDevolucion;
    }

    private void setSocio(Socio p_socio) {
        this.socio = p_socio;
    }

    private void setLibro(Libro p_libro) {
        this.libro = p_libro;
    }

    public Calendar getFechaRetiro() {
        return this.fechaRetiro;
    }

    public Calendar getFechaDevolucion() {
        return this.fechaDevolucion;
    }

    public Socio getSocio() {
        return this.socio;
    }

    public Libro getLibro() {
        return this.libro;
    }

    /**
     * Registra la fecha en la que se devuelve el ejemplar a la biblioteca.
     */
    public void registrarFechaDevolucion(Calendar p_fecha) {
        this.setFechaDevolucion(p_fecha);
    }

    /**
     * Evalua si el prestamo esta vencido respecto a una fecha dada.
     * Crea un Calendar nuevo con año/mes/dia de retiro para no modificar la fecha original.
     */
    public boolean vencido(Calendar p_fecha) {
        int anioRetiro = this.getFechaRetiro().get(Calendar.YEAR);
        int mesRetiro = this.getFechaRetiro().get(Calendar.MONTH);
        int diaRetiro = this.getFechaRetiro().get(Calendar.DAY_OF_MONTH);
        
        Calendar fechaVencimiento = new GregorianCalendar(anioRetiro, mesRetiro, diaRetiro);
        fechaVencimiento.add(Calendar.DATE, this.getSocio().getDiasPrestamo());
        
        return p_fecha.after(fechaVencimiento);
    }

    /**
     * Formatea la fecha a AAAA/MM/DD o indica si la devolucion sigue pendiente.
     */
    private String armarFecha(Calendar p_fecha) {
        if (p_fecha == null) {
            return "Ejemplar en préstamo";
        }
        
        int anio = p_fecha.get(Calendar.YEAR);
        int mes = p_fecha.get(Calendar.MONTH) + 1;
        int dia = p_fecha.get(Calendar.DATE);
        
        return anio + "/" + mes + "/" + dia;
    }

    /**
     * Retorna la informacion del prestamo con el formato requerido.
     */
    @Override
    public String toString() {
        return "Retiro: " + this.armarFecha(this.getFechaRetiro()) + 
               " - Devolución: " + this.armarFecha(this.getFechaDevolucion()) + "\n" +
               "Libro: " + this.getLibro().getTitulo() + "\n" +
               "Socio: " + this.getSocio().getNombre();
    }
}
