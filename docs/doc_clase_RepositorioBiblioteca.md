# Documentación de Clase Agregada — RepositorioBiblioteca

**Clase:** `biblioteca.persistencia.RepositorioBiblioteca`  
**Paquete:** `biblioteca.persistencia`  
**Responsable:** Equipo completo

---

## 1. Responsabilidad Principal

La clase `RepositorioBiblioteca` cumple la función de **objeto de acceso a datos (DAO)**. Reconstruye los socios, ejemplares y préstamos de `Biblioteca` a partir de las filas de SQLite. También ejecuta las escrituras que solicita `Biblioteca`, por lo que las sentencias de lectura y escritura quedan concentradas en el repositorio.

---

## 2. Métodos de Carga (Hidratación en Memoria)

### `cargarTodo(Biblioteca p_biblioteca)`

Al arrancar, `GestionBiblioteca` carga una instancia nueva de `Biblioteca` si la tabla `libros` ya contiene filas. El repositorio incorpora los datos en el siguiente orden, de modo que los objetos de cada préstamo estén disponibles al reconstruirlo.

1. `cargarLibros(p_biblioteca)`
2. `cargarSocios(p_biblioteca)`
3. `cargarPrestamos(p_biblioteca)`

Cada paso abre una conexión y ejecuta un `SELECT` fijo mediante `Statement.executeQuery(...)`. Con `ResultSet.next()` avanza fila a fila y con `getInt(...)` y `getString(...)` lee los valores de las columnas.

### `cargarLibros(Biblioteca p_biblioteca)`

- Ejecuta `SELECT id, titulo, edicion, editorial, anio FROM libros`. Por cada fila crea un `Libro` con el constructor que recibe su ID y lo añade a `p_biblioteca.getLibros()`.

### `cargarSocios(Biblioteca p_biblioteca)`

- Ejecuta `SELECT dni, nombre, dias_prestamo, tipo, carrera, area FROM socios` y usa la columna `tipo` para elegir la subclase que agrega a la colección de socios.
  - Si es `"Estudiante"`, instancia `new Estudiante(dni, nombre, carrera)`.
  - Si es `"Docente"`, usa `new Docente(dni, nombre, area, dias)` para conservar el plazo que figura en SQLite.

### `cargarPrestamos(Biblioteca p_biblioteca)`

Ejecuta `SELECT id_libro, dni_socio, fecha_retiro, fecha_devolucion FROM prestamos` y reconstruye cada relación en memoria.

1. Convierte el texto de las fechas a `Calendar` mediante `stringACalendar(...)`.
2. Busca los objetos ya cargados con `p_biblioteca.buscarSocio(dniSocio)` y `p_biblioteca.buscarLibro(idLibro)`.
3. Cuando encuentra ambos objetos, crea `new Prestamo(retiro, devolucion, socio, libro)` y agrega esa misma instancia a las colecciones del socio y del libro mediante `agregarPrestamo(...)`.

---

## 3. Métodos de Persistencia (Mutaciones en SQLite)

Los `INSERT` y `UPDATE` usan `PreparedStatement` para mantener los valores separados del SQL. Se asignan a cada `?` mediante `setInt(...)` o `setString(...)`. El método auxiliar `ejecutarUnaFila(...)` comprueba que `executeUpdate()` afecte exactamente una fila. `Biblioteca` actualiza su estado en memoria cuando la escritura se completa sin error.

- **`insertarLibro(titulo, edicion, editorial, anio)`** guarda el ejemplar y luego consulta `SELECT MAX(id) FROM libros` con un `Statement` en la misma conexión. En el uso previsto, una sola sesión escribe en la base y el máximo corresponde al ejemplar recién insertado. La consulta observa el mayor ID de la tabla en ese momento y puede devolver el ID de otro libro si hay escrituras de otras sesiones entre las dos sentencias.
- **`guardarSocio(Socio p_socio)`** inserta los datos comunes en `socios`. Obtiene el tipo con `soyDeLaClase()` y accede a `carrera` o `area` según la subclase.
- **`guardarPrestamo(Prestamo p_prestamo)`** registra el ID del libro, el DNI del socio y la fecha de retiro de `armarFecha(...)`. Usa `NULL` para la devolución mientras el préstamo está activo. Las claves foráneas protegen las referencias y el índice único parcial impide un segundo préstamo activo para el ejemplar.
- **`registrarDevolucion(Libro p_libro, Calendar p_fechaDevolucion)`** actualiza la fecha de la fila que coincide con el ID del ejemplar y cumple `fecha_devolucion IS NULL`. El índice garantiza que exista como máximo una fila activa por ejemplar. La comprobación de filas afectadas permite detectar una devolución sin préstamo activo en SQLite.
- **`actualizarDiasDocente(Docente p_docente, int p_nuevoPlazo)`** guarda el nuevo plazo en `socios` para el DNI del docente. `Biblioteca` calcula ese valor y actualiza al docente en memoria después de guardarlo.
- **`estaVacia()`** ejecuta `SELECT COUNT(*) FROM libros` al arrancar. Si el conteo da cero, la aplicación precarga los datos de ejemplo. La decisión se basa en la tabla `libros`.

---

## 4. Métodos Auxiliares de Tratamiento de Fechas

`stringACalendar(String p_fechaStr)` devuelve `null` cuando el texto es nulo, vacío o igual a `"Ejemplar en préstamo"`. Un `NULL` de SQLite llega como `null` al leerlo con `getString(...)`, lo que representa una devolución pendiente en memoria.

Para fechas guardadas como `año/mes/día`, separa las tres partes con `split("/")`, las convierte mediante `Integer.parseInt(...)` y crea un `GregorianCalendar` con `mes - 1`, porque `Calendar` numera enero con cero.

---

## 5. Conceptos de POO Aplicados

1. **Reconstrucción del modelo.** Los constructores inicializan cada instancia y `agregarPrestamo(...)` enlaza los préstamos. El repositorio incorpora los ejemplares y socios a las colecciones de `Biblioteca`.
2. **Separación de responsabilidades.** `Socio`, `Libro` y `Prestamo` expresan el modelo de negocio. El repositorio traduce los datos y realiza las consultas y escrituras JDBC.
3. **Excepciones y recursos JDBC.** El repositorio convierte `SQLException` en `IllegalStateException` con un mensaje de operación y conserva la excepción original como causa. Esto permite que la consola informe el error. En el recorrido normal cierra las sentencias y los resultados explícitamente. El `finally` intenta cerrar la conexión también si hay un error. El driver SQLite JDBC libera las sentencias que queden pendientes cuando cierra la conexión.
