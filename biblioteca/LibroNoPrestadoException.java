package biblioteca;

/**
 * Indica que se intentó devolver o consultar el préstamo de un ejemplar disponible.
 */
public class LibroNoPrestadoException extends Exception {
    /**
     * Inicializa la excepción con el mensaje de la operación.
     *
     * @param p_mensaje Mensaje que describe por qué no se puede realizar la operación.
     */
    public LibroNoPrestadoException(String p_mensaje) {
        super(p_mensaje);
    }
}
