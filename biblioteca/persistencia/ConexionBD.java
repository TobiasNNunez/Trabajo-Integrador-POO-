package biblioteca.persistencia;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Abre conexiones JDBC con SQLite y aplica el esquema al iniciar la aplicación.
 * Las rutas del archivo de datos y del esquema son relativas a la carpeta de ejecución.
 */
public class ConexionBD {
    private static final String RUTA_BD = "biblioteca/persistencia/biblioteca.db";
    private static final String URL = "jdbc:sqlite:" + RUTA_BD;
    private static final String RUTA_SQL = "biblioteca/persistencia/schema.sql";

    /**
     * Comprueba si el archivo de SQLite ya existe antes de abrir la conexión.
     *
     * @return {@code true} si el archivo de la base está presente.
     */
    public static boolean existeBaseDeDatos() {
        return new File(RUTA_BD).isFile();
    }
    
    /**
     * Abre una conexión con el archivo SQLite y habilita las claves foráneas
     * para esa conexión. El método que recibe la conexión se encarga de cerrarla.
     *
     * @return Conexión abierta y configurada.
     * @throws SQLException Si falla la apertura o la configuración de la conexión.
     */
    public static Connection obtenerConexion() throws SQLException {
        Connection con = DriverManager.getConnection(URL);
        try {
            Statement stmt = con.createStatement();
            stmt.execute("PRAGMA foreign_keys = ON");
            stmt.close();
            return con;
        } catch (SQLException e) {
            cerrarConexion(con);
            throw e;
        }
    }
    
    /**
     * Lee el esquema línea por línea y conserva los saltos que delimitan los
     * comentarios SQL. Ejecuta las sentencias completas, incluidas las de creación
     * de tablas e índice. Cierra la conexión y el lector en el bloque {@code finally}.
     *
     * @throws IllegalStateException Si falta el esquema, queda una sentencia incompleta
     *         o falla la lectura o la ejecución del SQL.
     */
    public static void inicializarTablas() {
        File archivo = new File(RUTA_SQL);
        
        if (!archivo.exists()) {
            throw new IllegalStateException("No se encontró el archivo: " + RUTA_SQL);
        }
        
        BufferedReader br = null;
        Connection con = null;
        try {
            br = new BufferedReader(new FileReader(archivo));
            con = obtenerConexion();
            Statement stmt = con.createStatement();
            
            String linea;
            String sentenciaActual = "";
            
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                // Ignora lineas en blanco y comentarios de SQL
                if (!linea.isEmpty() && !linea.startsWith("--")) {
                    sentenciaActual = sentenciaActual + " " + linea + "\n";
                    if (linea.endsWith(";")) {
                        stmt.execute(sentenciaActual);
                        sentenciaActual = "";
                    }
                }
            }
            
            if (!sentenciaActual.isEmpty()) {
                throw new IllegalStateException("Hay una sentencia SQL Sin terminar en " + RUTA_SQL);
            }
            
            stmt.close();
        } catch (IOException e) {
             throw new IllegalStateException("Error de lectura en schema.sql:" + e.getMessage(), e);
        } catch (SQLException e) {
             throw new IllegalStateException("Error SQL al inicializar tablas: " + e.getMessage(), e);
        } finally {
            cerrarConexion(con);
            
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    System.out.println("Error al cerrar el esquema: " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Intenta cerrar una conexión abierta. Si el cierre falla, muestra el
     * mensaje en la consola.
     *
     * @param p_con Conexión que se intenta cerrar, o {@code null} si no se abrió.
     */
    static void cerrarConexion(Connection p_con) {
        if (p_con != null) {
            try {
                p_con.close();
            } catch (SQLException e) {
                System.out.println("Error al cerrar la conexion: " + e.getMessage());
            }
        }
    }
}
