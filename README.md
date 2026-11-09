
# TP7 — Trabajo Integrador de Programación Orientada a Objetos

## Gestión de Biblioteca

Aplicación Java con menú gráfico Swing y menú de consola para administrar ejemplares, socios y préstamos de una biblioteca. El modelo usa colecciones genéricas y fechas con `Calendar`. La aplicación incorpora persistencia local con SQLite a través de JDBC.

> **Estado verificado.** Con Java 21 y el driver incluido, el programa inicializa una base nueva y recupera los datos tras reiniciar. En una copia temporal se verificaron las altas, los préstamos, las devoluciones, el premio docente y la respuesta ante fallos de escritura.

## Integrantes

- Miño Martin
- Nuñez Axel Gabriel
- Nuñez Tobias Nahuel
- Otero Emmanuel David
- Prieto Cesar Gabriel
- Elias Torres

## Funciones del menú

| Opciones | Operaciones |
| --- | --- |
| 1–3 | Registrar un ejemplar, un estudiante o un docente. |
| 4–7 | Prestar, devolver, consultar quién tiene un ejemplar y buscar un socio por DNI. |
| 8–11 | Listar ejemplares y socios, contar socios por tipo y listar títulos sin repetir. |
| 12–14 | Listar préstamos vencidos, listar docentes responsables y sumar días de préstamo a un docente responsable. |
| 15–16 | Quitar un ejemplar por ID o un socio por DNI. |
| 0 | Salir. |

En la consola, los préstamos, las devoluciones y las consultas de titular permiten elegir un ejemplar por su número en la lista. Para quitarlo, se usa su ID de SQLite. Dos ejemplares pueden compartir el mismo título y tener historiales diferentes. Los estudiantes tienen un plazo inicial de 20 días y un máximo de tres préstamos activos. Los docentes tienen un plazo inicial de cinco días. Un préstamo activo vencido impide nuevos retiros a cualquier socio.

Para quitar un ejemplar o un socio desde la interfaz gráfica, seleccioná su fila en la pestaña correspondiente y confirmá la operación. Solo se pueden quitar registros sin historial de préstamos. Esto conserva también los préstamos que ya fueron devueltos.

## Estructura del proyecto

```text
raíz-del-proyecto/
├── package.bluej                       # Proyecto de BlueJ
├── README.md
├── README.TXT                          # Cómo ejecutar los dos menús desde BlueJ
├── biblioteca/
│   ├── GestionBiblioteca.java          # Menú y datos de ejemplo
│   ├── Interfaz.java                   # Menú gráfico y conexión con Biblioteca
│   ├── Interfaz.form                   # Diseño editable en Swing UI Designer
│   ├── Biblioteca.java                 # Operaciones y consultas del dominio
│   ├── Libro.java                      # Ejemplar e historial de préstamos
│   ├── Prestamo.java                   # Socio, ejemplar y fechas
│   ├── Socio.java                      # Clase abstracta
│   ├── Estudiante.java
│   ├── Docente.java
│   ├── LibroNoPrestadoException.java
│   └── persistencia/
│       ├── ConexionBD.java             # Conexión SQLite e inicialización
│       ├── RepositorioBiblioteca.java  # Consultas, escrituras y carga de objetos
│       ├── schema.sql
│       └── sqlite-jdbc-3.54.1.0.jar
├── doc/                                # JavaDoc HTML generado desde BlueJ
│   ├── index.html                      # Página de inicio de la documentación JavaDoc
│   └── biblioteca/                     # Documentación HTML de las clases y paquetes
└── docs/                               # Informes técnicos y documentación complementaria en Markdown
```

Los archivos `package.bluej` de los paquetes y los `.ctxt` guardan información del entorno BlueJ. Las clases del modelo y el menú pertenecen a `biblioteca`. Las clases de acceso a datos están en el subpaquete `biblioteca.persistencia`.

### Documentación del proyecto

- **[`doc/`](doc/index.html):** documentación de referencia de las clases y métodos en formato HTML, generada automáticamente a partir de los comentarios JavaDoc (`/** ... */`) del código fuente mediante BlueJ. Para consultarla, abrí `doc/index.html` en un navegador web. Se incluyen las páginas de las clases, sus constructores, métodos y relaciones de paquetes.

- **[`docs/`](docs/):** documentación complementaria redactada por el equipo en archivos Markdown (`.md`). Incluye explicaciones de implementación por clase, cambios incorporados para la persistencia, detalles de JDBC y SQLite, y documentación de la interfaz gráfica.

Las dos carpetas se incluyen para facilitar la revisión: `doc/` permite consultar la documentación técnica generada desde Java y `docs/` registra las decisiones y explicaciones del desarrollo.

## Ejecución en BlueJ

1. Abrí la carpeta raíz del proyecto (en el ZIP se llama `Trabajo-Integrador-POO--main`), que contiene el archivo `package.bluej`.
2. Configurá el driver `biblioteca/persistencia/sqlite-jdbc-3.54.1.0.jar` en **Preferencias → Librerías** y reiniciá BlueJ. También podés colocar una copia del JAR en una carpeta `+libs` en la raíz del proyecto antes de abrir el proyecto. [FAQ de BlueJ](https://bluej.org/faq.html)
3. Entrá al paquete `biblioteca` y compilá el proyecto.
4. Elegí uno de los dos ejecutables del proyecto. Hacé clic derecho en la clase, seleccioná `main(String[] args)` y aceptá `{}`. `GestionBiblioteca` abre la consola de BlueJ e `Interfaz` abre el menú gráfico con las pestañas **Libros**, **Socios** y **Préstamos**.

[Instrucciones para ejecutar en BlueJ](README.TXT)

Para modificar el diseño en IntelliJ, abrí `Interfaz.form` y compilá con **Editor → GUI Designer → Generate GUI into → Java source files** antes de volver a compilar desde BlueJ.

## Persistencia

La aplicación usa `biblioteca/persistencia/biblioteca.db` y `biblioteca/persistencia/schema.sql`, con rutas relativas a la carpeta de ejecución. SQLite guarda los ejemplares, los socios y el historial de préstamos. Un préstamo activo tiene `fecha_devolucion` en `NULL`.

Los dos menús aplican el [esquema SQL](biblioteca/persistencia/schema.sql) al iniciar. La interfaz gráfica carga los registros existentes y presenta tablas vacías cuando se crea una base nueva. El menú de consola precarga cuatro ejemplares, cuatro socios y dos préstamos de ejemplo únicamente cuando el archivo SQLite no existía al iniciar y ambos listados están vacíos. Si se vacía una base existente mediante las bajas, al reabrirla se cargan sus datos tal como se guardaron. El esquema impide más de un préstamo activo por ejemplar y la conexión habilita las claves foráneas.

Las operaciones de escritura guardan primero en SQLite y después actualizan los objetos en memoria. Un fallo de inicialización o carga impide abrir el menú. Durante el uso, un fallo de escritura cancela esa opción y el menú permite seguir trabajando. El flujo está pensado para una sola sesión del programa.

[Resumen de persistencia y conceptos JDBC](docs/doc_resumen_persistencia_sqlite.md)

## Organización del equipo

| Subgrupo | Clases asignadas |
| --- | --- |
| 1 | `Prestamo` y `Socio` |
| 2 | `Estudiante` y `Docente` |
| 3 | `Libro` |
| 4 | `Biblioteca` |
| Equipo completo | `GestionBiblioteca` e integración |

La consigna organiza el seguimiento en Trello mediante cuatro listas. **Lista de tareas** reúne las actividades y responsables. **En proceso** registra el desarrollo. **Auditoría** reúne las observaciones de revisión. **Hecho** registra las tareas revisadas. Git y GitHub se usan para el control de versiones.

## Enlaces de trabajo colaborativo

- **[Tablero de Trello — TP7 Integrador POO](https://trello.com/b/sX7FR0kO/tp7-integrador-poo):** organización de las tareas, responsables, estados de avance y auditoría.
- **[Repositorio de GitHub — Trabajo Integrador POO](https://github.com/TobiasNNunez/Trabajo-Integrador-POO-):** código fuente, documentación y seguimiento de versiones del equipo.

## Enlace de ficha autoevaluativa grupal
- **[Ficha Autoevaluativa Grupal](https://github.com/TobiasNNunez/Trabajo-Integrador-POO-/blob/main/docs/Ficha%20Autoevaluacion%20Grupal.docx.pdf):** documento PDF
realizado por los estudiantes del grupo.
