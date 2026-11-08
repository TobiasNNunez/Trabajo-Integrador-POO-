# TP7 — Trabajo Integrador de Programación Orientada a Objetos

## Gestión de Biblioteca

Aplicación Java de consola para administrar ejemplares, socios y préstamos de una biblioteca. El modelo usa colecciones genéricas y fechas con `Calendar`. La aplicación incorpora persistencia local con SQLite a través de JDBC.

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
| 0 | Salir. |

Cada ejemplar se elige por su número en la lista. Dos ejemplares pueden compartir el mismo título y tener historiales diferentes. Los estudiantes tienen un plazo inicial de 20 días y un máximo de tres préstamos activos. Los docentes tienen un plazo inicial de cinco días. Un préstamo activo vencido impide nuevos retiros a cualquier socio.

## Estructura del proyecto

```text
TP7/
├── package.bluej                       # Proyecto de BlueJ
├── README.md
├── biblioteca/
│   ├── GestionBiblioteca.java          # Menú y datos de ejemplo
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
└── docs/                              # Documentación por clase y resumen persistencia
```

Los archivos `package.bluej` de los paquetes y los `.ctxt` guardan información del entorno BlueJ. Las clases del modelo y el menú pertenecen a `biblioteca`. Las clases de acceso a datos están en el subpaquete `biblioteca.persistencia`.

## Ejecución en BlueJ

1. Abrí la carpeta `TP7`, que contiene el archivo `package.bluej`.
2. Configurá el driver `biblioteca/persistencia/sqlite-jdbc-3.54.1.0.jar` en **Preferencias → Librerías** y reiniciá BlueJ. También podés colocar una copia del JAR en una carpeta `+libs` en la raíz de `TP7` antes de abrir el proyecto. [FAQ de BlueJ](https://bluej.org/faq.html)
3. Entrá al paquete `biblioteca`, compilá el proyecto y ejecutá `main(String[] args)` desde el menú contextual de `GestionBiblioteca`.
4. Usá el menú de la terminal de BlueJ. La opción `0` termina la sesión.

## Persistencia

La aplicación usa `biblioteca/persistencia/biblioteca.db` y `biblioteca/persistencia/schema.sql`, con rutas relativas a la carpeta de ejecución. SQLite guarda los ejemplares, los socios y el historial de préstamos. Un préstamo activo tiene `fecha_devolucion` en `NULL`.

Al iniciar, el programa aplica el [esquema SQL](biblioteca/persistencia/schema.sql) y consulta si la tabla `libros` está vacía. En una base nueva guarda cuatro ejemplares, cuatro socios y dos préstamos de ejemplo. Si ya hay ejemplares, reconstruye las colecciones en memoria a partir de las tres tablas. El esquema impide más de un préstamo activo por ejemplar y la conexión habilita las claves foráneas.

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