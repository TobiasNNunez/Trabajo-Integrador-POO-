import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Biblioteca {
    private String nombre;
    private ArrayList<Socio> socios; // Multiplicidad 0..*
    private ArrayList<Libro> libros; // Multiplicidad 0..*

    public Biblioteca(String p_nombre) {
        this.setNombre(p_nombre);
        this.setSocios(new ArrayList<Socio>());
        this.setLibros(new ArrayList<Libro>());
    }

    private void setNombre(String p_nombre) {
        this.nombre = p_nombre;
    }

    private void setSocios(ArrayList<Socio> p_socios) {
        this.socios = p_socios;
    }

    private void setLibros(ArrayList<Libro> p_libros) {
        this.libros = p_libros;
    }

    public String getNombre() {
        return this.nombre;
    }

    public ArrayList<Socio> getSocios() {
        return this.socios;
    }

    public ArrayList<Libro> getLibros() {
        return this.libros;
    }

    // Métodos para dar de alta libros y socios
    public void nuevoLibro(String p_titulo, int p_edicion, String p_editorial, int p_anio) {
        Libro unLibro = new Libro(p_titulo, p_edicion, p_editorial, p_anio);
        this.getLibros().add(unLibro);
    }

    public void nuevoSocioEstudiante(int p_dniSocio, String p_nombre, String p_carrera) {
        Estudiante unEstudiante = new Estudiante(p_dniSocio, p_nombre, p_carrera);
        this.getSocios().add(unEstudiante);
    }

    public void nuevoSocioDocente(int p_dniSocio, String p_nombre, String p_area) {
        Docente unDocente = new Docente(p_dniSocio, p_nombre, p_area);
        this.getSocios().add(unDocente);
    }

    public boolean prestarLibro(Calendar p_fechaRetiro, Socio p_socio, Libro p_libro) {
        if (!p_libro.prestado() && p_socio.puedePedir()) {
            Prestamo unPrestamo = new Prestamo(p_fechaRetiro, p_socio, p_libro);
            p_socio.getPrestamos().add(unPrestamo);
            p_libro.getPrestamos().add(unPrestamo);
            return true;
        }
        return false;
    }

    public void devolverLibro(Libro p_libro) {
        if (p_libro.prestado()) {
            Calendar hoy = new GregorianCalendar();
            p_libro.ultimoPrestamo().registrarFechaDevolucion(hoy);
        }
    }

    public int cantidadDeSociosPorTipo(String p_objeto) {
        int cantidad = 0;
        for (Socio s : this.getSocios()) {
            if (s.soyDeLaClase().equalsIgnoreCase(p_objeto)) {
                cantidad++;
            }
        }
        return cantidad;
    }

    public ArrayList<Prestamo> prestamosVencidos() {
        ArrayList<Prestamo> vencidos = new ArrayList<Prestamo>();
        Calendar hoy = new GregorianCalendar();
        
        for (Libro l : this.getLibros()) {
            if (l.prestado() && l.ultimoPrestamo().vencido(hoy)) {
                vencidos.add(l.ultimoPrestamo());
            }
        }
        return vencidos;
    }
}