## Documentación de Emmanuel Otero — Clase Libro

**Implementación realizada**

Se implementó la clase `Libro` dentro del paquete `biblioteca`, utilizando `ArrayList<Prestamo>` para conservar el historial de préstamos.

Se aplicaron los conceptos de encapsulamiento, constructores, sobrecarga, colecciones genéricas, colaboración entre objetos y redefinición de métodos.

### Método: Constructor Libro(...)

**Qué hace:** Inicializa el título, edición, editorial y año. Crea la colección de préstamos vacía.

**Cómo se implementó y qué conceptos de POO se aplicaron:** Se utilizan setters privados mediante `this` y se inicializa la colección con `new ArrayList<Prestamo>()`.

**Por qué se eligió esa solución:** Respeta el estilo de implementación de la cátedra y permite agregar préstamos sin inicializar la colección posteriormente.

**Caso comprobado y resultado:** Un ejemplar recién creado tiene cero préstamos y se encuentra disponible.

### Método: addPrestamo(Prestamo)

**Qué hace:** Incorpora un préstamo al historial del ejemplar.

**Cómo se implementó y qué conceptos de POO se aplicaron:** Se utiliza `this.getPrestamos().add(p_prestamo)`, aplicando colecciones genéricas y colaboración entre objetos.

**Por qué se eligió esa solución:** Permite conservar todos los préstamos anteriores y registrar los nuevos al final de la colección.

**Caso comprobado y resultado:** Al agregar un segundo préstamo, el historial conserva ambos registros.

### Método: ultimoPrestamo()

**Qué hace:** Retorna el último préstamo registrado. Si el historial está vacío, retorna `null`.

**Cómo se implementó y qué conceptos de POO se aplicaron:** Se recorre la colección con un `for-each`, guardando sucesivamente cada referencia en la variable `ultimo`.

**Por qué se eligió esa solución:** Permite identificar el último elemento registrado mediante una implementación sencilla y acorde con el estilo de la cátedra.

**Caso comprobado y resultado:** Sin préstamos retorna `null`. Con dos préstamos retorna el segundo.

### Método: prestado()

**Qué hace:** Determina si el ejemplar se encuentra actualmente prestado.

**Cómo se implementó y qué conceptos de POO se aplicaron:** Se consulta `ultimoPrestamo()` y, si existe, se obtiene su fecha de devolución mediante `getFechaDevolucion()`.

**Por qué se eligió esa solución:** Un ejemplar puede tener préstamos anteriores sin estar actualmente prestado. Su estado depende de la devolución del préstamo más reciente.

**Caso comprobado y resultado:** Sin préstamos retorna `false`; prestado retorna `true`; devuelto retorna `false`; prestado nuevamente retorna `true`.

### Método: toString()

**Qué hace:** Devuelve la representación textual del ejemplar.

**Cómo se implementó y qué conceptos de POO se aplicaron:** Se redefine el método `toString()` heredado de `Object`, utilizando `getTitulo()`.

**Por qué se eligió esa solución:** Respeta el formato establecido por el enunciado.

**Caso comprobado y resultado:** Devuelve `Titulo: Programando con JAVA`.

### Validación realizada

Se realizaron 16 pruebas aisladas con resultados correctos, verificando la creación de ejemplares, préstamos activos, devoluciones, conservación del historial y estados independientes de dos ejemplares con el mismo título.

**Integración pendiente:** Compilar y ejecutar el proyecto completo con las clases definitivas de los compañeros y obtener la revisión de otro integrante.
