# TP7 — Trabajo Integrador de Programación Orientada a Objetos

## Gestión de Biblioteca

Proyecto académico para desarrollar en Java una aplicación que permita administrar los libros, los socios y los préstamos de una biblioteca. El trabajo integra los conceptos de programación orientada a objetos abordados en la materia y se desarrolla en equipo utilizando BlueJ, Trello y GitHub.

**Estado inicial:** preparación del repositorio. La implementación de las clases y sus verificaciones se incorporará durante el desarrollo del trabajo.

## Integrantes

- Miño Martin
- Nuñez Axel Gabriel
- Nuñez Tobias Nahuel
- Otero Emmanuel David
- Gaitán Pereyra Julio Emilio
- Prieto Cesar Gabriel

## Objetivo y alcance

Implementar el diseño provisto en el enunciado del TP7, respetando su diagrama UML y las reglas de negocio de la biblioteca.

El sistema deberá permitir:

- Registrar libros y socios de tipo estudiante o docente.
- Gestionar préstamos y devoluciones de ejemplares.
- Aplicar las condiciones de préstamo correspondientes a cada tipo de socio.
- Consultar los préstamos actuales que se encuentren vencidos.
- Identificar al socio que tiene un ejemplar en su poder.
- Obtener la cantidad de socios de cada tipo.
- Listar los libros, los socios y los títulos disponibles en el catálogo sin repetir títulos.
- Identificar a los docentes que siempre cumplieron con los plazos de devolución.
- Informar mediante una excepción las operaciones previstas sobre libros que no se encuentran prestados.

## Tecnologías y herramientas

| Herramienta | Utilización |
| --- | --- |
| Java | Implementación de las clases y la lógica de la aplicación. |
| BlueJ | Desarrollo, compilación y ejecución del proyecto académico. |
| Trello | Organización de tareas, seguimiento del desarrollo y revisión entre integrantes. |
| Git | Control de versiones del código y la documentación. |
| GitHub | Repositorio compartido del equipo. |

Todas las clases deberán pertenecer al paquete `biblioteca`. Se utilizarán colecciones genéricas y el tipo `Calendar` para las fechas, conforme al diseño solicitado.

## Clases previstas

| Clase | Responsabilidad principal |
| --- | --- |
| `Socio` | Clase abstracta con los datos y comportamientos comunes de los socios. |
| `Estudiante` | Especialización de socio con carrera y condiciones particulares de préstamo. |
| `Docente` | Especialización de socio con área, días de préstamo y evaluación de responsabilidad. |
| `Libro` | Representación de un ejemplar y del historial de sus préstamos. |
| `Prestamo` | Relación entre un socio y un libro, con fechas de retiro y devolución. |
| `Biblioteca` | Administración de socios, libros, préstamos, devoluciones, consultas y listados. |
| `GestionBiblioteca` | Creación de objetos y demostración del funcionamiento integrado de la aplicación. |
| `LibroNoPrestadoException` | Excepción para las situaciones indicadas en el enunciado en las que un ejemplar no está prestado. |

Los constructores y métodos auxiliares necesarios se coordinarán entre los responsables para mantener compatibles las clases de los distintos subgrupos.

## Organización del equipo

La distribución de trabajo indicada por la consigna es la siguiente:

| Subgrupo | Integrantes | Clases asignadas |
| --- | --- | --- |
| 1 | 1 | `Prestamo` y `Socio`. |
| 2 | 1 | `Estudiante` y `Docente`. |
| 3 | 1 | `Libro`. |
| 4 | 3 | `Biblioteca`. |
| Equipo completo | 6 | `GestionBiblioteca` e integración del trabajo. |

La implementación de `LibroNoPrestadoException` se coordinará con los responsables de `Biblioteca`.

## Seguimiento en Trello

El tablero del equipo se organiza en cuatro listas:

1. **Lista de tareas:** clases y actividades pendientes, con responsables y checklist de métodos.
2. **En proceso:** tareas en desarrollo, con documentación de las decisiones tomadas y de los conceptos utilizados.
3. **Auditoría:** tareas desarrolladas que otro integrante revisa para registrar observaciones y posibles correcciones.
4. **Hecho:** tareas revisadas y dadas por terminadas.

Cada tarjeta permite registrar el alcance de la tarea, sus avances y la explicación de cómo se implementaron sus métodos y por qué se eligió esa solución. Las correcciones se documentarán para conservar el seguimiento de las revisiones.

## Apertura y ejecución en BlueJ

Estas instrucciones se aplicarán cuando los archivos del proyecto estén incorporados al repositorio:

1. Tener instalado BlueJ con un JDK compatible.
2. Abrir desde BlueJ la carpeta del proyecto que contenga el archivo `package.bluej`.
3. Compilar las clases y resolver los errores que se detecten.
4. Ejecutar el punto de entrada definido en `GestionBiblioteca` para comprobar los escenarios y listados del trabajo.

En el estado inicial del repositorio todavía no se incluye una aplicación ejecutable.

## Verificación prevista

Durante el desarrollo se comprobarán:

- La compilación del proyecto completo en BlueJ.
- La correspondencia entre las clases y el diseño UML.
- La coherencia de los registros de préstamos y devoluciones.
- Los casos normales y los límites de las reglas de préstamo.
- El tratamiento de vencimientos y devoluciones realizadas en la fecha límite.
- Las consultas y los formatos de listados solicitados.
- El lanzamiento y manejo de `LibroNoPrestadoException`.
- La revisión de las clases por otros integrantes y la resolución de las observaciones.