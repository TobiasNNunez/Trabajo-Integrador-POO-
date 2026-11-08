# Documentación de Clase Agregada — ConexionBD

**Clase:** `biblioteca.persistencia.ConexionBD`  
**Paquete:** `biblioteca.persistencia`  
**Responsable:** Equipo Completo

---

## 1. Responsabilidad Principal

La clase `ConexionBD` abre conexiones JDBC con la base local SQLite y prepara sus tablas e índice a partir de `schema.sql` antes de que la aplicación cargue los datos.

Pertenece al subpaquete `biblioteca.persistencia`, aislando los detalles de infraestructura y acceso a archivos de la lógica de negocio de la biblioteca.

---

## 2. Atributos de Clase (Constantes)

- **`RUTA_BD`** contiene `biblioteca/persistencia/biblioteca.db` y se usa para comprobar si el archivo existe.
- **`URL`** combina `jdbc:sqlite:` con `RUTA_BD` e indica al driver qué archivo local debe abrir.
- **`RUTA_SQL`** contiene `biblioteca/persistencia/schema.sql` y señala el archivo con las sentencias de creación de tablas e índice. Las dos rutas se interpretan desde el directorio de ejecución.

---

## 3. Métodos Implementados

### `existeBaseDeDatos()`

Comprueba si el archivo SQLite ya existe antes de abrir una conexión. `GestionBiblioteca` usa este dato al decidir si corresponde precargar los ejemplos en un primer arranque.

### `obtenerConexion()`

- **Qué hace.** Devuelve una `Connection` abierta y configurada para trabajar con `biblioteca.db`.
- **Cómo funciona.** `DriverManager.getConnection(URL)` abre la conexión. Un `Statement` ejecuta `PRAGMA foreign_keys = ON` y se cierra antes de devolverla. Si falla la configuración, el método intenta cerrar la conexión y relanza la `SQLException`.
- **Por qué se hace en cada apertura.** SQLite aplica `PRAGMA foreign_keys` por conexión. Activarlo aquí permite que cada operación haga cumplir las referencias de los préstamos a sus ejemplares y socios. El método estático también encapsula la URL y la configuración en un único lugar.

### `inicializarTablas()`

- **Qué hace.** Prepara las tablas `libros`, `socios` y `prestamos` y el índice único de préstamos activos por ejemplar.
- **Cómo funciona.**

  1. Comprueba que exista `schema.sql` y abre un `FileReader` envuelto en un `BufferedReader`.
  2. Lee con `readLine()` y omite las líneas vacías o que empiezan con `--`. Conserva un salto al incorporar cada línea de SQL para que los comentarios al final de una línea terminen allí.
  3. Al llegar a una línea que termina en `;`, ejecuta la sentencia acumulada con `Statement.execute(...)`. Si queda una sentencia incompleta al final del archivo, informa el error.
  4. En la ejecución normal cierra el `Statement` explícitamente. En `finally` llama a `cerrarConexion(...)` y cierra el `BufferedReader`, que también cierra el `FileReader` que envuelve.

- **Manejo de errores.** Convierte `IOException` y `SQLException` en `IllegalStateException` y adjunta la excepción original como causa. `GestionBiblioteca` muestra el mensaje y detiene el inicio. También recibe una `IllegalStateException` si falta el archivo o el script deja una sentencia incompleta.

### `cerrarConexion(Connection p_con)`

- **Qué hace.** Intenta cerrar una conexión que llegó a abrirse. Si el cierre lanza una `SQLException`, informa el problema. Este método permite compartir la misma lógica de limpieza con `RepositorioBiblioteca`.

---

## 4. Justificación de la Solución

- **Esquema externo.** `schema.sql` reúne las definiciones de la base y permite revisarlas por separado. Las sentencias `IF NOT EXISTS` permiten volver a ejecutarlo en cada arranque.
- **Integridad de préstamos.** Las claves foráneas exigen que los préstamos hagan referencia a un libro y a un socio existentes. El índice único parcial sobre `prestamos.id_libro` permite un solo préstamo activo por ejemplar y conserva el historial de préstamos ya devueltos.

---
