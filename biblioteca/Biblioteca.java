package biblioteca;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import biblioteca.persistencia.RepositorioBiblioteca;

/**
 * Administra socios, ejemplares y préstamos. Aplica las reglas sobre las
 * colecciones en memoria y delega las escrituras en {@link RepositorioBiblioteca}.
 * Las operaciones de guardado actualizan los objetos después de escribir en SQLite.
 */
public class Biblioteca {
    private String nombre;
    private ArrayList<Socio> socios; // Multiplicidad 0..*
    private ArrayList<Libro> libros; // Multiplicidad 0..*
    private RepositorioBiblioteca repositorio;

    /**
     * Inicializa las colecciones vacías y crea el repositorio de persistencia.
     *
     * @param p_nombre Nombre de la biblioteca.
     */
    public Biblioteca(String p_nombre) {
        this.setNombre(p_nombre);
        this.setSocios(new ArrayList<Socio>());
        this.setLibros(new ArrayList<Libro>());
        this.setRepositorio(new RepositorioBiblioteca());
    }

    /**
     * Asigna el nombre de la biblioteca.
     *
     * @param p_nombre Nombre de la biblioteca.
     */
    private void setNombre(String p_nombre) {
        this.nombre = p_nombre;
    }

    /**
     * Asigna la colección de socios.
     *
     * @param p_socios Colección de socios de la biblioteca.
     */
    private void setSocios(ArrayList<Socio> p_socios) {
        this.socios = p_socios;
    }

    /**
     * Asigna la colección de ejemplares.
     *
     * @param p_libros Colección de libros de la biblioteca.
     */
    private void setLibros(ArrayList<Libro> p_libros) {
        this.libros = p_libros;
    }
    
    /**
     * Asigna el objeto encargado de la persistencia de datos.
     * 
     * @param p_repositorio Instancia de RepositorioBiblioteca.
     */
    private void setRepositorio(RepositorioBiblioteca p_repositorio) {
        this.repositorio = p_repositorio;
    }

    /**
     * Devuelve el nombre de la biblioteca.
     *
     * @return Nombre de la biblioteca.
     */
    public String getNombre() {
        return this.nombre;
    }

    /**
     * Devuelve la colección de socios registrados.
     *
     * @return Colección de socios.
     */
    public ArrayList<Socio> getSocios() {
        return this.socios;
    }

    /**
     * Devuelve la colección de ejemplares registrados.
     *
     * @return Colección de libros.
     */
    public ArrayList<Libro> getLibros() {
        return this.libros;
    }
    
    /**
     * Devuelve el repositorio de persistencia de la biblioteca.
     * 
     * @return Objeto RepositorioBiblioteca.
     */
    public RepositorioBiblioteca getRepositorio() {
        return this.repositorio;
    }

    // Métodos para dar de alta libros y socios
    /**
     * Guarda un ejemplar en SQLite y lo incorpora a la colección con su identificador.
     *
     * @param p_titulo Título del libro.
     * @param p_edicion Número de edición.
     * @param p_editorial Editorial del libro.
     * @param p_anio Año de publicación.
     * @throws IllegalStateException Si falla la inserción o la consulta del identificador.
     */
    public void nuevoLibro(String p_titulo, int p_edicion, String p_editorial, int p_anio) {
        int idGenerado = this.getRepositorio().insertarLibro(p_titulo, p_edicion, p_editorial, p_anio);
        
        Libro unLibro = new Libro(idGenerado, p_titulo, p_edicion, p_editorial, p_anio);
        this.getLibros().add(unLibro);
    }

    /**
     * Crea un estudiante y lo incorpora a la colección después de guardarlo.
     *
     * @param p_dniSocio DNI del estudiante.
     * @param p_nombre Nombre y apellido del estudiante.
     * @param p_carrera Carrera que cursa.
     * @throws IllegalStateException Si falla el guardado en SQLite.
     */
    public void nuevoSocioEstudiante(int p_dniSocio, String p_nombre, String p_carrera) {
        Estudiante unEstudiante = new Estudiante(p_dniSocio, p_nombre, p_carrera);
        
        this.getRepositorio().guardarSocio(unEstudiante);
        
        this.getSocios().add(unEstudiante);
    }

    /**
     * Crea un docente y lo incorpora a la colección después de guardarlo.
     *
     * @param p_dniSocio DNI del docente.
     * @param p_nombre Nombre y apellido del docente.
     * @param p_area Área en la que se desempeña.
     * @throws IllegalStateException Si falla el guardado en SQLite.
     */
    public void nuevoSocioDocente(int p_dniSocio, String p_nombre, String p_area) {
        Docente unDocente = new Docente(p_dniSocio, p_nombre, p_area);
        
        this.getRepositorio().guardarSocio(unDocente);
        
        this.getSocios().add(unDocente);
    }

    /**
     * Comprueba la disponibilidad del ejemplar y si el socio puede pedirlo.
     * Tras guardarlo en SQLite, agrega el mismo préstamo al socio y al ejemplar.
     *
     * @param p_fechaRetiro Fecha de retiro.
     * @param p_socio Socio que solicita el préstamo.
     * @param p_libro Ejemplar solicitado.
     * @return {@code true} si se registró el préstamo, o {@code false} si el ejemplar
     *         está ocupado o el socio no puede pedirlo.
     * @throws IllegalStateException Si falla el guardado en SQLite.
     */
    public boolean prestarLibro(Calendar p_fechaRetiro, Socio p_socio, Libro p_libro) {
        if (!p_libro.prestado() && p_socio.puedePedir()) {
            Prestamo unPrestamo = new Prestamo(p_fechaRetiro, p_socio, p_libro);
            
            this.getRepositorio().guardarPrestamo(unPrestamo);
            
            p_socio.agregarPrestamo(unPrestamo);
            p_libro.agregarPrestamo(unPrestamo);
            
            return true;
        }
        
        return false;
    }

    /**
     * Guarda en SQLite la devolución del préstamo activo y luego registra
     * la fecha actual en la instancia compartida por el socio y el ejemplar.
     *
     * @param p_libro Ejemplar que se devuelve.
     * @throws LibroNoPrestadoException Si el ejemplar está disponible.
     * @throws IllegalStateException Si falla la actualización en SQLite o no se afecta una fila.
     */
    public void devolverLibro(Libro p_libro) throws LibroNoPrestadoException {
        if (p_libro.prestado()) {
            Calendar hoy = new GregorianCalendar();
            
            this.getRepositorio().registrarDevolucion(p_libro, hoy);
            
            p_libro.ultimoPrestamo().registrarFechaDevolucion(hoy);
        } else {
            throw new LibroNoPrestadoException("El libro " + p_libro.getTitulo()
                    + " no se puede devolver ya que se encuentra en la biblioteca");
        }
    }

    /**
     * Cuenta los socios del tipo indicado, sin distinguir mayúsculas y minúsculas.
     *
     * @param p_objeto Tipo de socio buscado, por ejemplo Estudiante o Docente.
     * @return Cantidad de socios de ese tipo.
     */
    public int cantidadDeSociosPorTipo(String p_objeto) {
        int cantidad = 0;
        for (Socio s: this.getSocios()) {
            if (s.soyDeLaClase().equalsIgnoreCase(p_objeto)) {
                cantidad++;
            }
        }
        return cantidad;
    }

    /**
     * Obtiene los préstamos que siguen pendientes y están vencidos a la fecha actual.
     *
     * @return Colección de préstamos actuales vencidos.
     */
    public ArrayList<Prestamo> prestamosVencidos() {
        ArrayList<Prestamo> vencidos = new ArrayList<Prestamo>();
        Calendar hoy = new GregorianCalendar();
        
        for (Libro l: this.getLibros()) {
            if (l.prestado() && l.ultimoPrestamo().vencido(hoy)) {
                vencidos.add(l.ultimoPrestamo());
            }
        }
        return vencidos;
    }

    /**
     * Obtiene los docentes que nunca tuvieron ni tienen un préstamo vencido.
     *
     * @return Colección de docentes responsables.
     */
    public ArrayList<Docente> docentesResponsables() {
        ArrayList<Docente> responsables = new ArrayList<Docente>();
        for (Socio s: this.getSocios()) {
            if (s instanceof Docente) {
                Docente unDocente = (Docente) s;
                if (unDocente.esResponsable()) {
                    responsables.add(unDocente);
                }
            }
        }
        return responsables;
    }

    /**
     * Consulta el nombre del socio que tiene prestado el ejemplar indicado.
     *
     * @param p_libro Ejemplar que se quiere consultar.
     * @return Nombre del socio que tiene el ejemplar.
     * @throws LibroNoPrestadoException Si el ejemplar está disponible.
     */
    public String quienTieneElLibro(Libro p_libro) throws LibroNoPrestadoException {
        if (p_libro.prestado()) {
            return p_libro.ultimoPrestamo().getSocio().getNombre();
        }
        throw new LibroNoPrestadoException("El libro se encuentra en la biblioteca");
    }

    /**
     * Busca un socio registrado por su DNI.
     *
     * @param p_dni DNI del socio buscado.
     * @return Socio encontrado o {@code null} si no existe.
     */
    public Socio buscarSocio(int p_dni) {
        for (Socio s: this.getSocios()) {
            if (s.getDniSocio() == p_dni) {
                return s;
            }
        }
        return null;
    }

    /**
     * Genera un listado numerado de los socios y sus cantidades por tipo.
     *
     * @return Listado de socios con los totales de estudiantes y docentes.
     */
    public String listaDeSocios() {
        String lista = "Lista de Socios:\n\n";
        int numero = 1;
        for (Socio s: this.getSocios()) {
            lista += numero + ") " + s.toString() + "\n";
            numero++;
        }

        String separador = "**************************************\n";
        lista += separador;
        lista += " Cantidad de Socios del tipo Estudiante: " + this.cantidadDeSociosPorTipo("Estudiante") + "\n";
        lista += " Cantidad de Socios del tipo Docente: " + this.cantidadDeSociosPorTipo("Docente") + "\n";
        lista += separador;
        return lista;
    }

    /**
     * Genera un listado numerado de los ejemplares con su estado de préstamo.
     *
     * @return Listado de libros con la indicación de si están prestados.
     */
    public String listaDeLibros() {
        String lista = "Lista de Libros:\n\n";
        int numero = 1;
        for (Libro l: this.getLibros()) {
            String prestado = "No";
            if (l.prestado()) {
                prestado = "Si";
            }
            lista += numero + ") " + l.toString() + " || Prestado: (" + prestado + ")\n";
            numero++;
        }
        return lista;
    }

    /**
     * Genera un listado de títulos sin repetir los de varios ejemplares.
     *
     * @return Listado de títulos en el orden en que aparecen en la biblioteca.
     */
    public String listaDeTitulos() {
        ArrayList<String> titulos = new ArrayList<String>();
        String lista = "Lista de Títulos:\n\n";
        for (Libro l: this.getLibros()) {
            String titulo = l.getTitulo();
            if (!titulos.contains(titulo)) {
                titulos.add(titulo);
                lista += titulo + "\n";
            }
        }
        return lista;
    }

    /**
     * Genera un listado con los datos de los docentes responsables.
     *
     * @return Listado de docentes responsables, con un asterisco por cada docente.
     */
    public String listaDeDocentesResponsables() {
        String lista = "Lista de Docentes Responsables:\n\n";
        for (Docente unDocente: this.docentesResponsables()) {
            lista += "* " + unDocente.toString() + "\n";
        }
        return lista;
    }
    
    /**
     * Busca un ejemplar por el identificador asignado en SQLite.
     *
     * @param p_id Identificador del ejemplar.
     * @return Ejemplar encontrado o {@code null} si no está en la colección.
     */
    public Libro buscarLibro(int p_id) {
        for (Libro l: this.getLibros()) {
            if (l.getId() == p_id) {
                return l;
            }
        }
        return null;
    }
    
    /**
     * Reconstruye el catálogo de socios, ejemplares y préstamos en las
     * colecciones de una biblioteca recién creada.
     *
     * @throws IllegalStateException Si falla una consulta a SQLite.
     */
    public void cargarDesdeBaseDeDatos() {
        this.getRepositorio().cargarTodo(this);
    }
    
    /**
     * Calcula el plazo aumentado, lo guarda en SQLite y aplica el aumento en memoria.
     *
     * @param p_docente Docente al que se le concede el aumento.
     * @param p_dias Días que se agregan al plazo actual.
     * @throws IllegalStateException Si falla la actualización en SQLite o no se afecta una fila.
     */
    public void actualizarDiasDocente(Docente p_docente, int p_dias) {
        int nuevoPlazo = p_docente.getDiasPrestamo() + p_dias;
        
        this.getRepositorio().actualizarDiasDocente(p_docente, nuevoPlazo);
        
        p_docente.cambiarDiasDePrestamo(p_dias);
    }
    
    /**
     * Consulta si la tabla de ejemplares está vacía para decidir la precarga.
     *
     * @return {@code true} si la tabla de libros no tiene registros.
     * @throws IllegalStateException Si falla la consulta a SQLite.
     */
    public boolean baseDeDatosVacia() {
        return this.getRepositorio().estaVacia();
    }
}
