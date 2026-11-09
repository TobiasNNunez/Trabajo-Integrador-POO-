package biblioteca;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Representa el préstamo de un ejemplar a un socio y conserva las fechas
 * de retiro y de devolución. Una fecha de devolución ausente indica que
 * el préstamo sigue activo.
 *
 * @author Tobias Nuñez
 */
public class Prestamo {
    private Calendar fechaRetiro;
    private Calendar fechaDevolucion;
    private Socio socio;
    private Libro libro;

    /**
     * Crea un préstamo activo con la fecha de devolución en {@code null}.
     *
     * @param p_fechaRetiro Fecha de retiro.
     * @param p_socio Socio que retira el ejemplar.
     * @param p_libro Ejemplar que se presta.
     */
    public Prestamo(Calendar p_fechaRetiro, Socio p_socio, Libro p_libro) {
        this.setFechaRetiro(p_fechaRetiro);
        this.setFechaDevolucion(null);
        this.setSocio(p_socio);
        this.setLibro(p_libro);
    }

    /**
     * Crea un préstamo con las fechas disponibles, por ejemplo al recuperarlo desde SQLite.
     *
     * @param p_fechaRetiro Fecha de retiro.
     * @param p_fechaDevolucion Fecha de devolución, o {@code null} si sigue activo.
     * @param p_socio Socio vinculado al préstamo.
     * @param p_libro Ejemplar vinculado al préstamo.
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

    /**
     * Devuelve la fecha de retiro.
     *
     * @return Fecha de retiro del ejemplar.
     */
    public Calendar getFechaRetiro() {
        return this.fechaRetiro;
    }

    /**
     * Devuelve la fecha de devolución.
     *
     * @return Fecha registrada o {@code null} si el préstamo sigue activo.
     */
    public Calendar getFechaDevolucion() {
        return this.fechaDevolucion;
    }

    /**
     * Devuelve el socio vinculado al préstamo.
     *
     * @return Socio que retiró el ejemplar.
     */
    public Socio getSocio() {
        return this.socio;
    }

    /**
     * Devuelve el ejemplar vinculado al préstamo.
     *
     * @return Ejemplar prestado.
     */
    public Libro getLibro() {
        return this.libro;
    }

    /**
     * Actualiza la fecha de devolución en memoria.
     *
     * @param p_fecha Fecha de devolución que se registra.
     */
    public void registrarFechaDevolucion(Calendar p_fecha) {
        this.setFechaDevolucion(p_fecha);
    }
    
/**
 * Comprueba si el día recibido es posterior al día
 * de vencimiento del préstamo.
 *
 * @param p_fecha Fecha que se desea consultar.
 * @return true si el préstamo está vencido,
 *         false en caso contrario.
 */
public boolean vencido(Calendar p_fecha) {
    Calendar fechaVencimiento = new GregorianCalendar(
        this.getFechaRetiro().get(Calendar.YEAR),
        this.getFechaRetiro().get(Calendar.MONTH),
        this.getFechaRetiro().get(Calendar.DAY_OF_MONTH)
    );

    fechaVencimiento.add(
        Calendar.DATE, this.getSocio().getDiasPrestamo()
    );

    Calendar fechaConsulta = new GregorianCalendar(
        p_fecha.get(Calendar.YEAR),
        p_fecha.get(Calendar.MONTH),
        p_fecha.get(Calendar.DAY_OF_MONTH)
    );

    return fechaConsulta.after(fechaVencimiento);
}


    /**
     * Formatea una fecha para el listado y para el guardado en SQLite.
     *
     * @param p_fecha Fecha que se formatea, o {@code null} si está pendiente.
     * @return Año, mes y día separados por barras, o la leyenda de préstamo activo.
     */
    public String armarFecha(Calendar p_fecha) {
        if (p_fecha == null) {
            return "Ejemplar en préstamo";
        }
        
        int anio = p_fecha.get(Calendar.YEAR);
        int mes = p_fecha.get(Calendar.MONTH) + 1;
        int dia = p_fecha.get(Calendar.DATE);
        
        return anio + "/" + mes + "/" + dia;
    }
    
    /**
     * Describe el préstamo para la consola.
     *
     * @return Texto con las fechas, el título del ejemplar y el nombre del socio.
     */
    @Override
    public String toString() {
        return "Retiro: " + this.armarFecha(this.getFechaRetiro()) + 
               " - Devolución: " + this.armarFecha(this.getFechaDevolucion()) + "\n" +
               "Libro: " + this.getLibro().getTitulo() + "\n" +
               "Socio: " + this.getSocio().getNombre();
    }
}
