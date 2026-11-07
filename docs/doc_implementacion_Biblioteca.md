## Documentación — Clase Biblioteca

**Implementación realizada**

Se implementó la estructura base de la clase `Biblioteca` dentro del paquete `biblioteca`, incluyendo sus atributos, constructor, métodos de acceso y los primeros siete métodos de negocio (`nuevoLibro`, `nuevoSocioEstudiante`, `nuevoSocioDocente`, `prestarLibro`, `devolverLibro`, `cantidadDeSociosPorTipo` y `prestamosVencidos`), utilizando `ArrayList<Socio>` y `ArrayList<Libro>`.

Se aplicaron los conceptos de encapsulamiento, constructores, colecciones genéricas, herencia, polimorfismo, colaboración entre objetos y manejo de excepciones (`throws` y `throw`).

### Método: Constructor Biblioteca(String p_nombre)

**Qué hace:** Inicializa el nombre de la biblioteca y crea las colecciones de socios y libros vacías.

**Cómo se implementó y qué conceptos de POO se aplicaron:** Se utilizan setters privados mediante `this` y se inicializan las colecciones con `new ArrayList<Socio>()` y `new ArrayList<Libro>()`.

**Por qué se eligió esa solución:** Respeta el estilo de implementación de la cátedra para la multiplicidad `0..*` y permite registrar socios y libros sin errores de referencias nulas.

**Caso comprobado y resultado:** Una biblioteca recién creada tiene su nombre asignado y cero socios y libros registrados.

### Método: nuevoLibro(String p_titulo, int p_edicion, String p_editorial, int p_anio)

**Qué hace:** Crea un ejemplar de `Libro` y lo incorpora a la colección de la biblioteca.

**Cómo se implementó y qué conceptos de POO se aplicaron:** Se instancia `new Libro(...)` y se agrega mediante `this.getLibros().add(unLibro)`, aplicando colecciones genéricas y colaboración entre objetos.

**Por qué se eligió esa solución:** Centraliza el alta de ejemplares directamente desde la biblioteca según la firma del modelo.

**Caso comprobado y resultado:** Al registrar un libro, la colección de libros incrementa su cantidad de elementos y conserva el nuevo ejemplar.

### Método: nuevoSocioEstudiante(int p_dniSocio, String p_nombre, String p_carrera)

**Qué hace:** Crea y registra un socio de tipo `Estudiante` en la colección de socios.

**Cómo se implementó y qué conceptos de POO se aplicaron:** Se instancia `new Estudiante(...)` y se incorpora en `this.getSocios().add(unEstudiante)`, aplicando herencia y polimorfismo de inclusión (*upcasting*).

**Por qué se eligió esa solución:** Permite gestionar distintos tipos de socios de forma unificada dentro de una misma colección `ArrayList<Socio>`.

**Caso comprobado y resultado:** El socio estudiante queda registrado correctamente en la lista de socios de la biblioteca.

### Método: nuevoSocioDocente(int p_dniSocio, String p_nombre, String p_area)

**Qué hace:** Crea y registra un socio de tipo `Docente` en la colección de socios.

**Cómo se implementó y qué conceptos de POO se aplicaron:** Se instancia `new Docente(...)` y se agrega mediante `this.getSocios().add(unDocente)`, aplicando herencia y polimorfismo de inclusión (*upcasting*).

**Por qué se eligió esa solución:** Aprovecha la jerarquía de clases de `Socio` para almacenar docentes en la colección general de socios.

**Caso comprobado y resultado:** El socio docente se incorpora a la colección y puede ser consultado o recibir préstamos.

### Método: prestarLibro(Calendar p_fechaRetiro, Socio p_socio, Libro p_libro)

**Qué hace:** Registra un préstamo vinculándolo al socio y al libro si el ejemplar está disponible y el socio se encuentra habilitado para pedir.

**Cómo se implementó y qué conceptos de POO se aplicaron:** Se evalúa `!p_libro.prestado() && p_socio.puedePedir()` aplicando delegación de responsabilidades y polimorfismo. Si se cumple, se instancia `new Prestamo(...)` y se añade a las colecciones del socio y del ejemplar.

**Por qué se eligió esa solución:** Garantiza el cumplimiento de las reglas de negocio antes de registrar el préstamo y mantiene la consistencia en ambos objetos.

**Caso comprobado y resultado:** Si el libro está disponible y el socio puede pedir, retorna `true` y el libro figura como prestado; en caso contrario retorna `false`.

### Método: devolverLibro(Libro p_libro)

**Qué hace:** Registra la fecha actual como fecha de devolución del préstamo activo del ejemplar, o lanza `LibroNoPrestadoException` si el libro ya se encuentra en la biblioteca.

**Cómo se implementó y qué conceptos de POO se aplicaron:** Se verifica `p_libro.prestado()`. Si es verdadero, se crea la fecha actual con `new GregorianCalendar()` y se invoca `p_libro.ultimoPrestamo().registrarFechaDevolucion(hoy)`. Si es falso, se lanza `throw new LibroNoPrestadoException(...)`.

**Por qué se eligió esa solución:** Actualiza el estado del último préstamo conservando el historial e informa mediante una excepción verificada cuando la devolución es inválida.

**Caso comprobado y resultado:** Un ejemplar prestado pasa a estar disponible tras la devolución; si no estaba prestado, lanza `LibroNoPrestadoException`.

### Método: cantidadDeSociosPorTipo(String p_objeto)

**Qué hace:** Cuenta y devuelve la cantidad de socios registrados del tipo indicado por parámetro.

**Cómo se implementó y qué conceptos de POO se aplicaron:** Se recorre `this.getSocios()` con un `for-each` y se compara `s.soyDeLaClase().equalsIgnoreCase(p_objeto)`, aplicando polimorfismo y enlace dinámico.

**Por qué se eligió esa solución:** Permite contabilizar cualquier subtipo de `Socio` sin acoplar el método a comprobaciones manuales de clase.

**Caso comprobado y resultado:** Retorna la cantidad exacta de socios de tipo `"Estudiante"` y `"Docente"`.

### Método: prestamosVencidos()

**Qué hace:** Obtiene y devuelve una colección con todos los préstamos vigentes que se encuentran vencidos a la fecha actual.

**Cómo se implementó y qué conceptos de POO se aplicaron:** Se recorre `this.getLibros()` verificando en cada ejemplar `l.prestado() && l.ultimoPrestamo().vencido(hoy)`, agregando los préstamos vencidos a un nuevo `ArrayList<Prestamo>`.

**Por qué se eligió esa solución:** Delega el cálculo del vencimiento en `Prestamo` y filtra únicamente los préstamos aún no devueltos.

**Caso comprobado y resultado:** Retorna solo los préstamos activos cuya fecha de vencimiento fue superada.

### Validación realizada

Se comprobaron las altas de libros y socios, el otorgamiento y rechazo de préstamos, la devolución de ejemplares, el conteo por tipo de socio y la obtención de préstamos vencidos.

**Integración pendiente:** Incorporar la segunda parte de los métodos de `Biblioteca` y realizar las pruebas integrales del paquete.