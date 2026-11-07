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

## Documentación — Clase Biblioteca (Últimos 7 métodos) y LibroNoPrestadoException

**Implementación realizada**

Se implementó la clase `LibroNoPrestadoException` y los últimos siete métodos de consulta, búsqueda y generación de reportes de la clase `Biblioteca` dentro del paquete `biblioteca` (`docentesResponsables`, `quienTieneElLibro`, `buscarSocio`, `listaDeSocios`, `listaDeLibros`, `listaDeTitulos` y `listaDeDocentesResponsables`).

Se aplicaron los conceptos de herencia, manejo de excepciones verificadas (`throws` y `throw`), colecciones genéricas, reflexión de tipos (`instanceof` y *downcasting*), colaboración entre objetos, polimorfismo y redefinición de `toString()`.

### Clase: LibroNoPrestadoException

**Qué hace:** Define una excepción verificada que indica que se intentó devolver o consultar el préstamo de un ejemplar que se encuentra disponible en la biblioteca.

**Cómo se implementó y qué conceptos de POO se aplicaron:** La clase extiende de `Exception` (herencia) e implementa el constructor `LibroNoPrestadoException(String p_mensaje)` invocando a `super(p_mensaje)`.

**Por qué se eligió esa solución:** Permite representar una condición de error propia del dominio y exigir su tratamiento mediante el mecanismo de excepciones de Java.

**Caso comprobado y resultado:** Al lanzarse la excepción, conserva y devuelve correctamente el mensaje recibido por parámetro.

### Método: docentesResponsables()

**Qué hace:** Obtiene y retorna una colección con todos los socios de tipo `Docente` que nunca tuvieron ni tienen un préstamo vencido.

**Cómo se implementó y qué conceptos de POO se aplicaron:** Se recorre `this.getSocios()` con un `for-each`, se verifica si el socio es instancia de `Docente` mediante `instanceof`, se realiza *downcasting* (`(Docente) s`) y se consulta `unDocente.esResponsable()`.

**Por qué se eligió esa solución:** Como `esResponsable()` es un método propio de la subclase `Docente` y no de `Socio`, es necesario comprobar el tipo real del objeto antes de realizar el casteo e invocar el método.

**Caso comprobado y resultado:** Retorna únicamente los docentes responsables, excluyendo a los estudiantes y a los docentes con préstamos vencidos.

### Método: quienTieneElLibro(Libro p_libro)

**Qué hace:** Devuelve el nombre del socio que tiene prestado el ejemplar indicado, o lanza `LibroNoPrestadoException` si el ejemplar se encuentra disponible.

**Cómo se implementó y qué conceptos de POO se aplicaron:** Se evalúa `p_libro.prestado()`. Si está prestado, se obtiene el nombre mediante colaboración entre objetos (`p_libro.ultimoPrestamo().getSocio().getNombre()`). Si no está prestado, se lanza `throw new LibroNoPrestadoException("El libro se encuentra en la biblioteca")`.

**Por qué se eligió esa solución:** Cumple con la firma y el manejo de excepciones requeridos por el enunciado cuando se consulta un ejemplar que no está prestado.

**Caso comprobado y resultado:** Con un libro prestado retorna el nombre del socio; con un libro disponible en biblioteca lanza `LibroNoPrestadoException`.

### Método: buscarSocio(int p_dni)

**Qué hace:** Busca un socio registrado en la biblioteca a partir de su número de DNI y lo retorna, o devuelve `null` si no existe.

**Cómo se implementó y qué conceptos de POO se aplicaron:** Se recorre la colección `this.getSocios()` con un `for-each` comparando `s.getDniSocio() == p_dni`.

**Por qué se eligió esa solución:** Permite localizar directamente la referencia de cualquier socio (estudiante o docente) mediante su identificador único.

**Caso comprobado y resultado:** Con un DNI registrado retorna el objeto `Socio` correspondiente; con un DNI inexistente retorna `null`.

### Método: listaDeSocios()

**Qué hace:** Genera un listado numerado de los socios registrados junto con el total de socios de tipo `Estudiante` y `Docente`.

**Cómo se implementó y qué conceptos de POO se aplicaron:** Se recorre `this.getSocios()` concatenando el número de orden y `s.toString()` (polimorfismo), y al final se invoca `this.cantidadDeSociosPorTipo("Estudiante")` y `this.cantidadDeSociosPorTipo("Docente")`.

**Por qué se eligió esa solución:** Reutiliza los métodos polimórficos de las subclases y el conteo por tipo, respetando el formato exacto de salida solicitado por la cátedra.

**Caso comprobado y resultado:** Devuelve la cadena formateada con todos los socios numerados y el resumen de cantidades por tipo entre líneas de asteriscos.

### Método: listaDeLibros()

**Qué hace:** Genera un listado numerado de todos los ejemplares de la biblioteca indicando para cada uno si se encuentra prestado (`Si` o `No`).

**Cómo se implementó y qué conceptos de POO se aplicaron:** Se recorre `this.getLibros()` consultando `l.prestado()` para definir el estado `"Si"` o `"No"`, y se concatena con `l.toString()`.

**Por qué se eligió esa solución:** Delega la consulta del estado de préstamo en cada ejemplar y construye el listado con el formato requerido.

**Caso comprobado y resultado:** Devuelve el listado numerado mostrando los datos de cada libro y `Prestado: (Si)` o `Prestado: (No)` según corresponda.

### Método: listaDeTitulos()

**Qué hace:** Genera un listado con los títulos de los libros de la biblioteca sin repetir aquellos que poseen múltiples ejemplares.

**Cómo se implementó y qué conceptos de POO se aplicaron:** Se utiliza una lista auxiliar `ArrayList<String> titulos` y, al recorrer `this.getLibros()`, se verifica `!titulos.contains(titulo)` antes de agregarlo y concatenarlo al resultado.

**Por qué se eligió esa solución:** Permite filtrar títulos repetidos de forma sencilla manteniendo el orden en que fueron registrados en la biblioteca.

**Caso comprobado y resultado:** Si existen dos o más ejemplares con el mismo título, el título aparece una única vez en el listado.

### Método: listaDeDocentesResponsables()

**Qué hace:** Genera un listado con los datos de todos los docentes responsables registrados en la biblioteca.

**Cómo se implementó y qué conceptos de POO se aplicaron:** Se invoca al método `this.docentesResponsables()` y se recorre la colección resultante con un `for-each`, concatenando `"* " + unDocente.toString()`.

**Por qué se eligió esa solución:** Reutiliza la lógica de filtrado ya implementada en `docentesResponsables()` y formatea la salida de acuerdo con el enunciado.

**Caso comprobado y resultado:** Devuelve el listado precedido por asteriscos únicamente con los docentes que no registran vencimientos.

### Validación realizada

Se comprobaron los siete métodos de consulta, búsqueda y listados junto con el lanzamiento de `LibroNoPrestadoException`, verificando el filtrado de docentes responsables, la búsqueda por DNI, la exclusión de títulos repetidos y el formato de cada reporte.

**Integración pendiente:** Compilar y ejecutar el proyecto completo con las clases definitivas de los compañeros y obtener la revisión de otro integrante.
