package biblioteca.persistencia;

import biblioteca.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Lee y escribe datos de la biblioteca en SQLite mediante JDBC.
 * Reconstruye los objetos del modelo al cargar y encadena los fallos de JDBC
 * como {@link IllegalStateException} para que los maneje la aplicación.
 * Cada operación cierra su conexión en un bloque {@code finally}.
 */
public class RepositorioBiblioteca {
    // Metodos de carga (Lectura desde SQLite)
    
    /**
     * Carga primero los ejemplares, después los socios y finalmente los préstamos
     * en una biblioteca con sus colecciones vacías.
     *
     * @param p_biblioteca Biblioteca de destino.
     * @throws IllegalStateException Si falla alguna consulta a SQLite.
     */
    public void cargarTodo(Biblioteca p_biblioteca) {
        this.cargarLibros(p_biblioteca);
        this.cargarSocios(p_biblioteca);
        this.cargarPrestamos(p_biblioteca);
    }
    
    /**
     * Lee los ejemplares y los reconstruye con su identificador de SQLite.
     *
     * @param p_biblioteca Biblioteca donde se agregan los ejemplares.
     * @throws IllegalStateException Si falla la lectura.
     */
    private void cargarLibros(Biblioteca p_biblioteca) {
        String sql = "SELECT id, titulo, edicion, editorial, anio FROM libros";
        
        Connection con = null;
        try {
            con = ConexionBD.obtenerConexion();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                int id = rs.getInt("id");
                String titulo = rs.getString("titulo");
                int edicion = rs.getInt("edicion");
                String editorial = rs.getString("editorial");
                int anio = rs.getInt("anio");
                
                Libro unLibro = new Libro(id, titulo, edicion, editorial, anio);
                p_biblioteca.getLibros().add(unLibro);
            }
            
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Error al cargar libros: " + e.getMessage(), e);
        } finally {
            ConexionBD.cerrarConexion(con);
        }
    }
    
    /**
     * Reconstruye estudiantes o docentes según la columna {@code tipo}.
     * En los docentes recupera también el plazo de préstamo guardado.
     *
     * @param p_biblioteca Biblioteca donde se agregan los socios.
     * @throws IllegalStateException Si falla la lectura.
     */
    private void cargarSocios(Biblioteca p_biblioteca) {
        String sql = "SELECT dni, nombre, dias_prestamo, tipo, carrera, area FROM socios;";
        
        Connection con = null;
        try {
            con = ConexionBD.obtenerConexion();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                int dni = rs.getInt("dni");
                String nombre = rs.getString("nombre");
                int dias = rs.getInt("dias_prestamo");
                String tipo = rs.getString("tipo");
                
                if (tipo.equalsIgnoreCase("Estudiante")) {
                    String carrera = rs.getString("carrera");
                    
                    Estudiante est = new Estudiante(dni, nombre, carrera);
                    p_biblioteca.getSocios().add(est);
                } else if (tipo.equalsIgnoreCase("Docente")) {
                    String area = rs.getString("area");
                    
                    Docente doc = new Docente(dni, nombre, area, dias);
                    p_biblioteca.getSocios().add(doc);
                }
            }
            
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Error al cargar socios" + e.getMessage(), e);
        } finally {
            ConexionBD.cerrarConexion(con);
        }
    }
    
    /**
     * Reconstruye los préstamos y asocia la misma instancia al socio y al
     * ejemplar cuando encuentra ambos objetos por sus claves.
     *
     * @param p_biblioteca Biblioteca con los socios y los ejemplares ya cargados.
     * @throws IllegalStateException Si falla una consulta a SQLite.
     */
    private void cargarPrestamos(Biblioteca p_biblioteca) {
        String sql = "SELECT id_libro, dni_socio, fecha_retiro, fecha_devolucion FROM prestamos;";
        
        Connection con = null;
        try {
            con = ConexionBD.obtenerConexion();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                int idLibro = rs.getInt("id_libro");
                int dniSocio = rs.getInt("dni_socio");
                String strRetiro = rs.getString("fecha_retiro");
                String strDevolucion = rs.getString("fecha_devolucion");
                
                Calendar retiro = this.stringACalendar(strRetiro);
                Calendar devolucion = this.stringACalendar(strDevolucion);
                
                Socio socio = p_biblioteca.buscarSocio(dniSocio);
                Libro libro = p_biblioteca.buscarLibro(idLibro);
                
                if (socio != null && libro != null) {
                    Prestamo prestamo = new Prestamo(retiro, devolucion, socio, libro);
                    socio.agregarPrestamo(prestamo);
                    libro.agregarPrestamo(prestamo);
                }
            }
            
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Error al cargar prestamos: " + e.getMessage(), e);
        } finally {
            ConexionBD.cerrarConexion(con);
        }
    }
    
    // Metodos de persistencia (Guardado)
    
    /**
     * Inserta un ejemplar. En el flujo de una sola sesión, consulta el mayor
     * identificador de la tabla después del alta.
     *
     * @param p_titulo Título del libro.
     * @param p_edicion Número de edición.
     * @param p_editorial Nombre de la editorial.
     * @param p_anio Año de publicación.
     * @return Identificador recuperado de SQLite.
     * @throws IllegalStateException Si la inserción no afecta una fila o falla una operación JDBC.
     */
    public int insertarLibro(String p_titulo, int p_edicion, String p_editorial, int p_anio) {
        String sqlInsert = "INSERT INTO libros (titulo, edicion, editorial, anio) VALUES (?, ?, ?, ?);";
        String sqlMaxId = "SELECT MAX(id) FROM libros;";
        int idGenerado = 0;
        
        Connection con = null;
        try {
            con = ConexionBD.obtenerConexion();
            PreparedStatement ps = con.prepareStatement(sqlInsert);
            
            ps.setString(1, p_titulo);
            ps.setInt(2, p_edicion);
            ps.setString(3, p_editorial);
            ps.setInt(4, p_anio);
            
             ejecutarUnaFila(ps);
            
            ps.close();
            
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sqlMaxId);
            
            if (rs.next()) {
                idGenerado = rs.getInt(1);
            }
            
            rs.close();
            stmt.close();
            con.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Error al insertar libro en BD: " + e.getMessage(), e);
        } finally {
            ConexionBD.cerrarConexion(con);
        }
        
        return idGenerado;
    }
    
    /**
     * Inserta un socio con su DNI, plazo, tipo y el dato de su subclase.
     *
     * @param p_socio Estudiante o docente que se guarda.
     * @throws IllegalStateException Si la inserción no afecta una fila o falla una operación JDBC.
     */
    public void guardarSocio(Socio p_socio) {
        String sql = "INSERT INTO socios (dni, nombre, dias_prestamo, tipo, carrera, area) VALUES (?, ?, ?, ?, ?, ?);";
        
        Connection con = null;
        try {
            con = ConexionBD.obtenerConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            
            ps.setInt(1, p_socio.getDniSocio());
            ps.setString(2, p_socio.getNombre());
            ps.setInt(3, p_socio.getDiasPrestamo());
            ps.setString(4, p_socio.soyDeLaClase());
            
            if (p_socio.soyDeLaClase().equalsIgnoreCase("Estudiante")) {
                Estudiante est = (Estudiante) p_socio;
                ps.setString(5, est.getCarrera());
                ps.setString(6, null);
            } else if (p_socio.soyDeLaClase().equalsIgnoreCase("Docente")) {
                Docente doc = (Docente) p_socio;
                ps.setString(5, null);
                ps.setString(6, doc.getArea());
            }
            
             ejecutarUnaFila(ps);
            
            ps.close();
            con.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Error al guardar socio en BD: " + e.getMessage(), e);
        } finally {
            ConexionBD.cerrarConexion(con);
        }
    }
    
    /**
     * Inserta un préstamo activo con el ID del ejemplar, el DNI del socio y
     * la fecha de retiro formateada por {@link Prestamo#armarFecha(Calendar)}.
     * La fecha de devolución queda en {@code NULL}.
     *
     * @param p_prestamo Préstamo activo que se guarda.
     * @throws IllegalStateException Si la inserción no afecta una fila o SQLite la rechaza.
     */
    public void guardarPrestamo(Prestamo p_prestamo) {
        String sql = "INSERT INTO prestamos (id_libro, dni_socio, fecha_retiro, fecha_devolucion) VALUES (?, ?, ?, NULL);";
        
        Connection con = null;
        try {
            con = ConexionBD.obtenerConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            
            ps.setInt(1, p_prestamo.getLibro().getId());
            ps.setInt(2, p_prestamo.getSocio().getDniSocio());
            ps.setString(3, p_prestamo.armarFecha(p_prestamo.getFechaRetiro()));
            
             ejecutarUnaFila(ps);
            
            ps.close();
            con.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Error al guardar prestamo en BD: " + e.getMessage(), e);
        } finally {
            ConexionBD.cerrarConexion(con);
        }
    }
    
    /**
     * Guarda la fecha de devolución en la fila del préstamo activo del ejemplar.
     *
     * @param p_libro Ejemplar devuelto.
     * @param p_fechaDevolucion Fecha de devolución.
     * @throws IllegalStateException Si la actualización no afecta una fila o falla en SQLite.
     */
    public void registrarDevolucion(Libro p_libro, Calendar p_fechaDevolucion) {
        String sql = "UPDATE prestamos SET fecha_devolucion = ? WHERE id_libro = ? AND fecha_devolucion IS NULL;";
        
        Connection con = null;
        try {
            con = ConexionBD.obtenerConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            
            ps.setString(1, p_libro.ultimoPrestamo().armarFecha(p_fechaDevolucion));
            ps.setInt(2, p_libro.getId());
            
             ejecutarUnaFila(ps);
            
            ps.close();
            con.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Error al registrar devolucion en BD: " + e.getMessage(), e);
        } finally {
            ConexionBD.cerrarConexion(con);
        }
    }
    
    /**
     * Guarda el nuevo plazo de préstamo en la fila identificada por el DNI.
     *
     * @param p_docente Docente cuyo DNI identifica la fila.
     * @param p_nuevoPlazo Plazo total en días que se guarda.
     * @throws IllegalStateException Si la actualización no afecta una fila o falla en SQLite.
     */
    public void actualizarDiasDocente(Docente p_docente, int p_nuevoPlazo) {
        String sql = "UPDATE socios SET dias_prestamo = ? WHERE dni = ?;";
        
        Connection con = null;
        try {
            con = ConexionBD.obtenerConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            
            ps.setInt(1, p_nuevoPlazo);
            ps.setInt(2, p_docente.getDniSocio());
            
             ejecutarUnaFila(ps);
            
            ps.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Error al actualizar dias de docente en BD: " + e.getMessage(), e);
        } finally {
            ConexionBD.cerrarConexion(con);
        }
    }
    
    /**
     * Cuenta los ejemplares guardados para decidir si se precargan los ejemplos.
     *
     * @return {@code true} si la tabla de libros está vacía.
     * @throws IllegalStateException Si falla la consulta.
     */
    public boolean estaVacia() {
        String sql = "SELECT COUNT(*) FROM libros;";
        boolean vacia = true;
        
        Connection con = null;
        try {
             con = ConexionBD.obtenerConexion();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            if (rs.next()) {
                vacia = (rs.getInt(1) == 0);
            }
            
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Error de SQLite: " + e.getMessage(), e);
        } finally {
            ConexionBD.cerrarConexion(con);
        }
        
        return vacia;
    }
    
    // Metodo auxiliar manejo de fechas (STRING --> CALENDAR)
    
    /**
     * Convierte el texto de fecha de SQLite a un {@link GregorianCalendar}
     * a medianoche. Interpreta el texto vacío o la leyenda de préstamo activo
     * como una fecha ausente.
     *
     * @param p_fechaStr Texto con año, mes y día separados por barras, o {@code null}.
     * @return Fecha convertida o {@code null} si no hay fecha.
     */
    private Calendar stringACalendar(String p_fechaStr) {
        if (p_fechaStr == null || p_fechaStr.trim().isEmpty() || p_fechaStr.equals("Ejemplar en préstamo")) {
            return null;
        }
        
        String[] partes = p_fechaStr.split("/");
        int anio = Integer.parseInt(partes[0]);
        int mes = Integer.parseInt(partes[1]) - 1; // En Calendar enero es 0
        int dia = Integer.parseInt(partes[2]);
        
        return new GregorianCalendar(anio, mes, dia);
    }
    
    /**
     * Ejecuta una escritura que debe afectar exactamente una fila.
     *
     * @param ps Sentencia preparada con sus parámetros asignados.
     * @throws SQLException Si falla la escritura o afecta una cantidad distinta de una fila.
     */
    private void ejecutarUnaFila(PreparedStatement ps) throws SQLException {
        if (ps.executeUpdate() != 1) {
            throw new SQLException("La operacion debia modificar una fila.");
        }
    }
}