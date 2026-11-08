# Documentación de Cambios — Clase Biblioteca

**Clase:** `biblioteca.Biblioteca`  
**Paquete:** `biblioteca`  
**Responsables de la revisión:** Miño Martin  / Prieto Cesar

---

## 1. Contexto del Cambio

La clase `Biblioteca` administra los socios, los ejemplares y sus préstamos. En la versión original, las altas incorporaban objetos a `ArrayList<Socio>` o `ArrayList<Libro>`. Los retiros y devoluciones actualizaban los historiales de esos objetos durante la sesión.

Con SQLite, `Biblioteca` coordina las altas, las bajas, los préstamos, las devoluciones y los aumentos del plazo de docentes con `RepositorioBiblioteca`. Las colecciones en memoria sirven para aplicar las reglas de negocio y generar los listados del menú.

---

## 2. Modificaciones Realizadas

### 1. Incorporación del Objeto Colaborador `repositorio`
- El atributo privado guarda el objeto encargado de la persistencia.
  ```java
  private RepositorioBiblioteca repositorio;
  ```
- El mutador privado asigna el repositorio y el observador público devuelve la instancia.
  ```java
  private void setRepositorio(RepositorioBiblioteca p_repositorio) {
      this.repositorio = p_repositorio;
  }

  public RepositorioBiblioteca getRepositorio() {
      return this.repositorio;
  }
  ```
- El constructor crea el repositorio al inicializar las colecciones vacías.
  ```java
  public Biblioteca(String p_nombre) {
      this.setNombre(p_nombre);
      this.setSocios(new ArrayList<Socio>());
      this.setLibros(new ArrayList<Libro>());
      this.setRepositorio(new RepositorioBiblioteca());
  }
  ```

### 2. Adaptación de Métodos de Negocio Mutadores
`Biblioteca` aplica primero las reglas de negocio que correspondan. Después delega la escritura al repositorio mediante `this.getRepositorio()` y actualiza los objetos cuando la llamada termina correctamente.

- **`nuevoLibro(...)`** solicita la inserción y recibe el ID de SQLite. Crea un `Libro` con ese ID y lo agrega a `ArrayList<Libro>`.
- **`nuevoSocioEstudiante(...)` y `nuevoSocioDocente(...)`** crean el socio, llaman a `guardarSocio(...)` y, una vez guardado, lo agregan a `ArrayList<Socio>`.
- **`quitarLibro(int p_id)` y `quitarSocio(int p_dni)`** buscan el registro por ID de ejemplar o DNI. Devuelven `false` si no existe. Si hay historial de préstamos, lanzan `IllegalStateException`. En caso contrario, solicitan la baja en SQLite, retiran el objeto de su colección y devuelven `true`.
- **`prestarLibro(...)`** verifica la disponibilidad del ejemplar y la habilitación del socio con `!p_libro.prestado() && p_socio.puedePedir()`. Si se cumplen las condiciones, guarda un `Prestamo` y agrega esa misma instancia a los dos historiales con `agregarPrestamo(...)`. Devuelve `true` cuando completa el préstamo y `false` cuando las reglas lo rechazan.
- **`devolverLibro(...)`** obtiene la fecha actual y solicita al repositorio actualizar la fila del préstamo activo. Luego registra la misma fecha en el `Prestamo` del ejemplar. Si el ejemplar está disponible, lanza `LibroNoPrestadoException`, la excepción de negocio que también usa `quienTieneElLibro(...)` en esa situación.

Si el repositorio lanza `IllegalStateException` por un error de persistencia, la llamada se interrumpe antes de actualizar la colección o el objeto. La excepción llega al menú, que muestra el mensaje de error.

### 3. Nuevos Métodos Incorporados
- **`buscarLibro(int p_id)`** recorre los ejemplares y compara `l.getId() == p_id`. El repositorio lo usa junto con `buscarSocio(int p_dni)` para vincular cada préstamo recuperado con sus objetos. Devuelve `null` si el ID no existe en la colección.
- **`cargarDesdeBaseDeDatos()`** llama a `this.getRepositorio().cargarTodo(this)` para cargar los ejemplares y socios y reconstruir sus historiales al arrancar.
- **`actualizarDiasDocente(Docente p_docente, int p_dias)`** calcula el nuevo plazo a partir del actual y el aumento pedido. Primero guarda el total en SQLite y después aplica el aumento al objeto con `cambiarDiasDePrestamo(p_dias)`.
- **`baseDeDatosVacia()`** consulta al repositorio si la tabla `libros` está vacía. El arranque actual carga ambas colecciones y la consola decide si corresponde precargar los ejemplos según la existencia previa del archivo y el contenido de esas colecciones.

---

## 3. Justificación y Conceptos de POO Aplicados

- **Colaboración entre objetos y delegación.** `Biblioteca` coordina las operaciones del dominio y `RepositorioBiblioteca` ejecuta las sentencias SQL. Cada clase concentra así una responsabilidad distinta.
- **Principio de acceso uniforme.** El constructor asigna el colaborador con `setRepositorio(...)` y las operaciones de la biblioteca lo consultan mediante `this.getRepositorio()`.
- **Consultas y polimorfismo en memoria.** `prestamosVencidos()`, `docentesResponsables()` y `cantidadDeSociosPorTipo()` recorren los objetos en sus colecciones. Cada socio aporta su propio comportamiento mediante métodos como `puedePedir()` y `soyDeLaClase()`.

---
