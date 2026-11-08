# Documentación de Cambios — Clase Libro

**Clase:** `biblioteca.Libro`  
**Paquete:** `biblioteca`  
**Responsable de la revisión:** Emmanuel David Otero

---

## 1. Contexto del Cambio

En el diseño original del TP7, cada ejemplar era una instancia de `Libro` con su propia colección de préstamos. Dos ejemplares con los mismos datos bibliográficos se distinguían por sus referencias. El menú permitía elegirlos por su posición en `ArrayList<Libro>` durante la sesión.

SQLite asigna a cada fila de `libros` una clave primaria autoincremental. La clase incorpora ese identificador para que el repositorio pueda reconocer el ejemplar al recuperar su historial en otra sesión.

---

## 2. Modificaciones Realizadas

### 1. Nuevo Atributo de Instancia

El atributo `id` representa la clave primaria del ejemplar. La tabla `prestamos` guarda ese valor en `id_libro` al registrar un retiro.

```java
private int id;
```

### 2. Mutador y Observador (Accessors)

- El mutador **privado** permite que los constructores asignen el ID.
  ```java
  private void setId(int p_id) {
      this.id = p_id;
  }
  ```
- El observador **público** permite consultar el ID. `Biblioteca.buscarLibro(...)` y el repositorio lo usan para vincular los préstamos con su ejemplar.
  ```java
  public int getId() {
      return this.id;
  }
  ```

### 3. Inicialización Explícita en Constructores Existentes

Las dos firmas originales inicializan `id` mediante `this.setId(0)`. El cero representa una instancia creada directamente en memoria, sin un ID de SQLite.

- `Libro(String p_titulo, int p_edicion, String p_editorial, int p_anio)` inicia la colección de préstamos vacía.
- `Libro(String p_titulo, int p_edicion, String p_editorial, int p_anio, ArrayList<Prestamo> p_prestamos)` recibe una colección de préstamos existente.

### 4. Nuevos Constructores Sobrecargados con ID

- **Con ID e historial vacío.** `Biblioteca` usa esta firma después de insertar un ejemplar y el repositorio la usa al recuperar las filas de `libros`, antes de cargar los préstamos.
  ```java
  public Libro(int p_id, String p_titulo, int p_edicion, String p_editorial, int p_anio) {
      this.setId(p_id);
      this.setTitulo(p_titulo);
      this.setEdicion(p_edicion);
      this.setEditorial(p_editorial);
      this.setAnio(p_anio);
      this.setPrestamos(new ArrayList<Prestamo>());
  }
  ```
- **Con ID e historial existente.** Esta sobrecarga permite entregar la colección al construir el objeto.
  ```java
  public Libro(int p_id, String p_titulo, int p_edicion, String p_editorial, int p_anio, ArrayList<Prestamo> p_prestamos) {
      this.setId(p_id);
      this.setTitulo(p_titulo);
      this.setEdicion(p_edicion);
      this.setEditorial(p_editorial);
      this.setAnio(p_anio);
      this.setPrestamos(p_prestamos);
  }
  ```

---

## 3. Justificación y Conceptos de POO Aplicados

- **Identidad de objeto e identidad relacional.** La referencia identifica una instancia durante la ejecución. El `id` identifica la fila de SQLite y vincula cada historial con el ejemplar que le corresponde.
- **Encapsulamiento.** Los constructores asignan el identificador mediante `setId(...)` y las demás clases lo consultan mediante `getId()`.
- **Sobrecarga de constructores.** Las firmas originales permiten crear ejemplares desde el banco de objetos de BlueJ. Las firmas con `id` permiten representar ejemplares asociados a filas de SQLite.

---
