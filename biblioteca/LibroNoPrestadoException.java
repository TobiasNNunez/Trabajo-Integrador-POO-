package biblioteca;

/**
 * Excepción verificada del dominio. Indica que se intentó devolver un
 * ejemplar disponible o consultar quién lo tiene prestado.
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
