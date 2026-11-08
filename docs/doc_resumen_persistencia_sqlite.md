# Resumen de persistencia con SQLite

## 1. Qué se guarda

En el diseño original del TP7, los ejemplares, socios y préstamos existían en la memoria mientras se ejecutaba el programa. La persistencia permite guardar sus datos y reconstruir los objetos al iniciar una nueva sesión. SQLite almacena la información en el archivo local `biblioteca/persistencia/biblioteca.db`.

## 2. Cómo se organiza el código

| Componente | Responsabilidad |
| --- | --- |
| `GestionBiblioteca` | Arranca la aplicación y presenta el menú de consola. |
| `Biblioteca` y clases del modelo | Mantienen las colecciones de objetos y aplican las reglas de préstamos y devoluciones. |
| [`ConexionBD`](../biblioteca/persistencia/ConexionBD.java) | Abre conexiones con SQLite y lee el esquema con `FileReader` y `BufferedReader`. |
| [`RepositorioBiblioteca`](../biblioteca/persistencia/RepositorioBiblioteca.java) | Ejecuta consultas, inserciones y actualizaciones. Al cargar, convierte filas en objetos del modelo. |
| [`schema.sql`](../biblioteca/persistencia/schema.sql) | Define las tablas `libros`, `socios` y `prestamos`, sus claves y el índice de préstamos activos. |
| `sqlite-jdbc-3.54.1.0.jar` | Contiene el driver que implementa JDBC para SQLite. Debe estar en el classpath al ejecutar. |

El modelo y el menú se encuentran en el paquete `biblioteca`. El acceso a SQLite se concentra en `biblioteca.persistencia`. Las reglas siguen usando objetos, colecciones genéricas y fechas con `Calendar`.

## 3. Qué son las interfaces JDBC usadas en el proyecto

JDBC es la API de Java para acceder a bases de datos. Las cuatro interfaces pertenecen a `java.sql` y el driver de SQLite proporciona sus implementaciones.

### `Connection`

Representa una sesión abierta con la base de datos. `ConexionBD.obtenerConexion()` la obtiene con `DriverManager.getConnection(URL)` usando la URL `jdbc:sqlite:biblioteca/persistencia/biblioteca.db`. A partir de la conexión se crean las sentencias con `createStatement()` o `prepareStatement(sql)`. El repositorio abre una conexión por operación, incluso una por cada tabla durante la carga. `ConexionBD` habilita las claves foráneas al abrirla. Las escrituras se confirman individualmente mediante la confirmación automática de JDBC. [API de Java 21](https://docs.oracle.com/en/java/javase/21/docs/api/java.sql/java/sql/Connection.html)

### `Statement`

Ejecuta SQL que se entrega como texto al llamar al método de ejecución. `ConexionBD.inicializarTablas()` usa `stmt.execute(sentenciaActual)` para aplicar las sentencias `CREATE TABLE` y `CREATE UNIQUE INDEX` de `schema.sql`, y usa `Statement` también al configurar las claves foráneas. El repositorio usa `stmt.executeQuery(sql)` en consultas fijas, como `SELECT COUNT(*) FROM libros`, y recibe un `ResultSet` con las filas de la consulta. [API de Java 21](https://docs.oracle.com/en/java/javase/21/docs/api/java.sql/java/sql/Statement.html)

### `PreparedStatement`

Es una especialización de `Statement` que se crea con la sentencia SQL ya definida. Los signos `?` marcan posiciones para los datos. Los parámetros se numeran desde 1 y se asignan con métodos tipados como `setString()` y `setInt()`. El driver recibe los valores por separado del SQL, así que un nombre con apóstrofe, como `O'Connor`, se guarda como texto y las entradas no se interpretan como instrucciones SQL. [API de Java 21](https://docs.oracle.com/en/java/javase/21/docs/api/java.sql/java/sql/PreparedStatement.html)

`RepositorioBiblioteca` lo usa para insertar ejemplares, socios y préstamos, y para actualizar devoluciones y plazos de docentes. Por ejemplo, `actualizarDiasDocente()` prepara `UPDATE socios SET dias_prestamo = ? WHERE dni = ?`, carga primero el plazo y luego el DNI con `setInt()`, y llama a `ejecutarUnaFila(ps)`. Este auxiliar ejecuta `executeUpdate()` y compara la cantidad de filas afectadas con `1`. Cero filas puede indicar que el registro que se esperaba actualizar ya no está disponible. Una cantidad distinta de una se informa como error al llamador.

### `ResultSet`

Es el resultado de una consulta y permite leer sus filas mediante un cursor. Antes de leer la primera fila hay que llamar a `next()`. Cada llamada avanza a la fila siguiente y devuelve `false` cuando se acaban. En `cargarLibros()`, el repositorio recorre `while (rs.next())` y obtiene valores con `rs.getInt("id")` y `rs.getString("titulo")`. En `cargarPrestamos()`, `rs.getString("fecha_devolucion")` devuelve `null` si la columna contiene `NULL`, lo que representa un préstamo activo. [API de Java 21](https://docs.oracle.com/en/java/javase/21/docs/api/java.sql/java/sql/ResultSet.html)

En una lectura, el flujo es **abrir la conexión → ejecutar la consulta → recorrer sus filas → construir objetos Java**. Al completar la operación se cierran explícitamente el `ResultSet` y la sentencia. El repositorio cierra la conexión en `finally`, también cuando la operación falla. El inicializador usa el mismo mecanismo para la conexión y el lector del esquema.

## 4. Cómo se representan los datos

| Tabla | Datos |
| --- | --- |
| `libros` | Una fila por ejemplar. El `id` autoincremental distingue ejemplares aunque tengan el mismo título. |
| `socios` | Una fila por DNI. La columna `tipo` distingue `Estudiante` de `Docente`. Se guarda la carrera o el área según el tipo, y el plazo en `dias_prestamo`. |
| `prestamos` | Una fila por retiro, con referencias al ejemplar y al socio. La devolución se guarda en esa misma fila. |

El esquema declara claves foráneas con `ON DELETE RESTRICT`. `ConexionBD.obtenerConexion()` ejecuta `PRAGMA foreign_keys = ON` en cada conexión para que SQLite las compruebe. [Documentación de SQLite](https://www.sqlite.org/foreignkeys.html#fk_enable)

El índice único `ux_prestamo_activo_por_libro` incluye las filas con `fecha_devolucion IS NULL`. Esto permite conservar el historial de un ejemplar y limita a uno sus préstamos activos. Si una base existente tiene duplicados activos, la creación del índice falla y el programa interrumpe el arranque. [Índices parciales de SQLite](https://sqlite.org/partialindex.html#unique_partial_indexes)

El código guarda las fechas como texto con barras y sin rellenar mes ni día con ceros, por ejemplo `2026/11/6` o `2026/3/5`. La escritura usa `Prestamo.armarFecha()` y la lectura separa los valores por `/` y crea un `GregorianCalendar` a medianoche. `NULL` en `fecha_devolucion` indica que el ejemplar sigue prestado.

## 5. Cuándo se leen y se escriben los datos

1. Al arrancar, `GestionBiblioteca` llama a `ConexionBD.inicializarTablas()`. El lector conserva el salto al final de cada línea para que los comentarios SQL `--` terminen donde corresponde. El inicializador ejecuta las sentencias del esquema a medida que encuentra el punto y coma.
2. Si el conteo de filas de la tabla `libros` da cero, se ejecuta `precargarDatos()`. Cada alta se confirma por separado. Si la precarga se interrumpe, los registros guardados hasta ese momento permanecen en SQLite.
3. Cuando existen ejemplares, el repositorio lee primero libros, después socios y finalmente préstamos. Cada préstamo recuperado se agrega al ejemplar y al socio mediante la misma instancia de `Prestamo`.
4. Durante el uso del menú, `Biblioteca` evalúa las reglas con los objetos en memoria y delega la escritura al repositorio. Tras el guardado, incorpora el nuevo objeto o actualiza el existente. Si la escritura es rechazada, la excepción interrumpe el método antes del cambio en memoria.

El flujo está pensado para una sesión. La devolución identifica el préstamo activo por el ID del ejemplar. El índice protege contra dos préstamos activos, pero una sesión con datos anteriores puede referirse a un préstamo distinto del que SQLite considera activo.

## 6. Manejo de excepciones según el Tema 8

### Excepciones verificadas y no verificadas

| Excepción | Clasificación | Dónde se usa |
| --- | --- | --- |
| `IOException` | Verificada o comprobada | Puede producirse al abrir, leer o cerrar `schema.sql`. |
| `SQLException` | Verificada o comprobada | Puede producirse en JDBC. El repositorio también la lanza si una escritura afecta una cantidad distinta de una fila. |
| `LibroNoPrestadoException` | Verificada y definida por el proyecto | `Biblioteca` la declara con `throws` al devolver un ejemplar o consultar quién lo tiene. La lanza cuando el ejemplar está disponible. |
| `IllegalStateException` | No verificada. Hereda de `RuntimeException`. | La capa de persistencia la usa para informar fallos del esquema o de una operación de SQLite. |

Una excepción verificada debe capturarse o declararse con `throws`. Una no verificada puede propagarse sin aparecer en la firma y el llamador puede capturarla con `catch`. `throws` declara qué puede ocurrir en un método. `throw` lanza la excepción en el punto donde se detecta el problema.

### Qué hace cada capa ante una excepción

- **Captura y relanzamiento.** Si falla la configuración de las claves foráneas, `obtenerConexion()` captura la `SQLException`, intenta cerrar la conexión y vuelve a lanzar la misma excepción mediante `throw e`.
- **Encadenamiento.** El inicializador y el repositorio capturan la excepción verificada y lanzan una nueva `IllegalStateException(mensaje, e)`. El mensaje agrega el contexto de la operación y `e` queda como causa. `Biblioteca` deja que el error llegue al menú. Si falta el archivo del esquema o queda una sentencia incompleta, el inicializador genera la `IllegalStateException` directamente.
- **Manejo en el nivel que puede responder.** Si falla el arranque, el `catch` de `main` muestra el motivo y termina la sesión con `return`. Es una finalización controlada ante un fallo que impide abrir el menú. Durante el uso, el `catch` del menú informa el error, da por terminada esa opción y permite elegir otra. Es una recuperación del flujo. Ante un rechazo de escritura, la aplicación conserva el estado previo de los objetos y las escrituras anteriores ya confirmadas. El usuario decide si intenta la operación de nuevo.
- **Captura específica de una regla del dominio.** Si se intenta devolver un libro disponible o consultar su titular, `GestionBiblioteca` captura `LibroNoPrestadoException` en el método de esa opción y muestra su mensaje. En cambio, el rechazo normal de un nuevo préstamo por disponibilidad o por las condiciones del socio se comunica con `false`.
- **Limpieza en `finally`.** El bloque ejecuta el cierre al salir del `try` o del `catch`, incluso si hay un `throw`. La variable de conexión se declara antes del `try` para compartir su alcance con `finally`. Si la apertura falla, conserva `null` y el auxiliar comprueba ese valor antes de cerrar. Los errores de cierre se informan en la capa que libera el recurso.