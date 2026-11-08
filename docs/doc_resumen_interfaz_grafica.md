# Resumen Funcional — Interfaz Gráfica de Usuario (GUI)

**Sistema:** Gestión de Biblioteca — Trabajo Integrador (TP7)  
**Paquete:** `biblioteca`  
**Clase principal:** [`Interfaz.java`](../biblioteca/Interfaz.java)  
**Formulario visual:** [`Interfaz.form`](../biblioteca/Interfaz.form)  

---

## 1. Propósito de la Interfaz Gráfica

La interfaz gráfica desarrollada con **Java Swing** y el diseñador visual **IntelliJ Swing UI Designer** ofrece una experiencia moderna, intuitiva y centralizada para la administración de la biblioteca. Constituye una evolución respecto al menú tradicional de consola ([`GestionBiblioteca.java`](../biblioteca/GestionBiblioteca.java)), integrando en una única ventana interactiva todas las operaciones sobre ejemplares, socios y préstamos, con sincronización inmediata con la base de datos local SQLite (`biblioteca.db`).

---

## 2. Organización Espacial y Entorno de Trabajo

La ventana principal (`JFrame`) se divide visualmente en tres zonas claramente estructuradas mediante gestores de diseño (*Layout Managers*):

```
┌────────────────────────────────────────────────────────────────────────┐
│  Gestión de Biblioteca (Cabecera Norte)                                │
├────────────────────────────────────────────────────────────────────────┤
│  [ Libros ]   [ Socios ]   [ Préstamos ] (JTabbedPane Central)         │
│ ┌────────────────────────────────────────────────────────────────────┐ │
│ │                                                                    │ │
│ │  Tabla interactiva con ordenamiento de columnas                    │ │
│ │  (JTable dentro de JScrollPane con barra de desplazamiento)        │ │
│ │                                                                    │ │
│ └────────────────────────────────────────────────────────────────────┘ │
│                               [ Botones de acción alineados a la der.] │
├────────────────────────────────────────────────────────────────────────┤
│  5 ejemplares · 4 socios · 2 préstamos activos · 1 vencido (Sur)      │
└────────────────────────────────────────────────────────────────────────┘
```

1. **Cabecera superior (Zona Norte):** Presenta el título institucional *"Gestión de Biblioteca"* en tipografía destacada de 26 puntos y color azul marino (`#163854`).
2. **Cuerpo central (Zona Centro):** Un contenedor de pestañas (`JTabbedPane`) organiza la funcionalidad del sistema en tres módulos temáticos: **Libros**, **Socios** y **Préstamos**. Cada pestaña aloja una tabla de solo lectura con desplazamiento automático y una barra inferior de botones contextuales.
3. **Barra de estado (Zona Sur):** Una etiqueta inferior (`etiquetaEstado`) informa en tiempo real los contadores globales del sistema (ejemplares totales, socios inscriptos, préstamos en curso y préstamos vencidos), complementada con mensajes de confirmación tras cada operación (por ejemplo: *"Préstamo registrado"*, *"Devolución registrada"*, etc.).

---

## 3. Módulos y Funcionalidades por Pestaña

### 3.1. Pestaña «Libros» — Catálogo de Ejemplares

La sección de libros permite visualizar, registrar y auditar el catálogo bibliográfico de la institución.

* **Grilla visual del catálogo:**  
  Muestra todos los ejemplares registrados en la biblioteca en una tabla interactiva con las siguientes columnas:
  - **ID:** Número identificador único autoincremental de cada ejemplar en SQLite.
  - **Título:** Nombre de la obra.
  - **Edición:** Número de edición del libro.
  - **Editorial:** Empresa editora.
  - **Año:** Año de publicación.
  - **Estado:** Indicador visual de disponibilidad (*"Disponible"* o *"Prestado"*).

* **Operaciones disponibles en la barra de herramientas:**
  1. **Registrar ejemplar:**  
     Despliega un formulario emergente para incorporar un nuevo libro al catálogo. Solicita título, edición, editorial y año de publicación. Valida que los campos no estén vacíos y que la edición y el año sean números enteros positivos. Una vez confirmado, guarda el libro en SQLite y lo incorpora inmediatamente a la grilla.
  2. **Títulos únicos:**  
     Genera un listado consolidado con los títulos de los libros registrados, eliminando repeticiones y contabilizando cuántos ejemplares existen de cada obra. Se presenta en un cuadro de diálogo con área de texto desplazable.
  3. **Consultar titular:**  
     Al seleccionar cualquier ejemplar de la tabla y pulsar este botón, la interfaz informa quién es el socio que tiene actualmente prestado ese libro. Si el ejemplar se encuentra en la biblioteca, la aplicación captura la excepción `LibroNoPrestadoException` y avisa amigablemente que el ejemplar está disponible.
  4. **Quitar ejemplar**. Seleccioná el ejemplar en la tabla y confirmá la baja. Solo se pueden quitar ejemplares sin historial de préstamos.

---

### 3.2. Pestaña «Socios» — Padrón de Estudiantes y Docentes

La sección de socios administra a los usuarios facultados para solicitar retiros de material bibliográfico.

* **Grilla visual del padrón:**  
  Presenta a todos los socios inscriptos con sus datos característicos:
  - **DNI:** Documento nacional de identidad (clave única).
  - **Nombre y apellido:** Datos personales del socio.
  - **Tipo:** Categoría polimórfica del socio (*"Estudiante"* o *"Docente"*).
  - **Carrera / Área:** Dato específico según el tipo (carrera universitaria para estudiantes, área académica de desempeño para docentes).
  - **Plazo (días):** Plazo inicial autorizado para la devolución de libros (20 días para estudiantes, 5 o más días para docentes).
  - **Prestados:** Cantidad de libros que el socio tiene actualmente en su poder sin devolver.

* **Operaciones disponibles en la barra de herramientas:**
  1. **Registrar socio:**  
     Presenta un formulario inteligente con un selector de categoría (*Estudiante* o *Docente*). La etiqueta del campo de especialización cambia dinámicamente entre *"Carrera"* y *"Área"* según la opción elegida. Valida que el DNI sea numérico positivo, que no se encuentre duplicado en el sistema y que todos los textos sean provistos.
  2. **Buscar DNI:**  
     Permite ingresar un documento para localizar a un socio rápidamente. Si existe, la tabla de socios **se desplaza automáticamente y selecciona la fila correspondiente** (incluso si la tabla fue ordenada previamente por otra columna) y abre una ficha de detalle con toda su información.
  3. **Cantidades:**  
     Muestra un reporte estadístico con el conteo exacto de estudiantes y docentes registrados, utilizando el enlace dinámico y polimorfismo de `soyDeLaClase()`.
  4. **Responsables:**  
     Emite un reporte con el listado de docentes que han cumplido estrictamente con las fechas de devolución de todos sus préstamos históricos.
  5. **Premiar:**  
     Permite seleccionar un docente en la tabla y verificar su condición de responsabilidad. Si califica, habilita un diálogo para otorgarle días de préstamo adicionales, actualizando el nuevo plazo en SQLite y en la grilla.
  6. **Quitar socio**. Seleccioná el socio en la tabla y confirmá la baja. Solo se pueden quitar socios sin historial de préstamos.

---

### 3.3. Pestaña «Préstamos» — Control de Retiros y Vencimientos

La sección de préstamos monitorea las transacciones activas de la biblioteca y aplica los mecanismos de control de mora.

* **Grilla visual de préstamos activos:**  
  Lista exclusivamente los préstamos vigentes (no devueltos) con la siguiente información:
  - **ID ejemplar:** Identificador del libro retirado.
  - **Título:** Título del libro en préstamo.
  - **DNI:** Documento del socio que retiró el material.
  - **Socio:** Nombre y apellido del titular.
  - **Retiro:** Fecha exacta en que se efectuó el préstamo.
  - **Estado:** Clasificación temporal del préstamo (*"En plazo"* o *"Vencido"*).

* **Filtro dinámico de mora:**  
  - **Casilla «Solo vencidos»:** Al marcarla, la grilla se filtra en tiempo real para mostrar únicamente aquellos préstamos cuya fecha límite de devolución ya ha caducado respecto a la fecha actual, facilitando auditorías de morosidad instantáneas.

* **Operaciones disponibles en la barra de herramientas:**
  1. **Prestar:**  
     Asistente guiado para concretar un nuevo préstamo. Ofrece un combo desplegable con **únicamente los ejemplares disponibles** (identificados por `#ID · Título`) y solicita el DNI del socio solicitante.  
     Aplica automáticamente todas las reglas de negocio del modelo:
     - Verifica que el socio no haya alcanzado su tope de préstamos (máximo 3 para estudiantes).
     - Comprueba que el socio no tenga ningún préstamo activo vencido.
     - Si el socio está inhabilitado, muestra una advertencia descriptiva y rechaza el préstamo sin alterar la base de datos.
  2. **Devolver:**  
     Permite registrar la restitución de un libro seleccionando la fila del préstamo en la tabla. Tras solicitar confirmación al usuario para prevenir errores involuntarios, estampa la fecha actual como fecha de devolución en SQLite y libera el ejemplar para futuros retiros.

---

## 4. Características de Usabilidad y Experiencia de Usuario (UX)

La interfaz gráfica incorpora patrones de diseño ergonómicos orientados a la robustez y comodidad del operador:

* **Ordenamiento de columnas tipado (*Table Sorting*):**  
  Al hacer clic sobre el encabezado de cualquier columna, las filas se ordenan automáticamente de forma ascendente o descendente. Los campos numéricos (ID, DNI, plazos) se ordenan según su valor matemático y las fechas cronológicamente.
* **Formularios con persistencia de entrada:**  
  Si el usuario se equivoca al cargar un campo (por ejemplo, escribe letras en un casillero numérico o deja un campo en blanco), la aplicación muestra una advertencia explicativa y **mantiene el diálogo abierto con los datos ya ingresados**, evitando que deba volver a escribir todo desde cero.
* **Prevención de inconsistencias (Solo lectura en grillas):**  
  Las celdas de las tablas no permiten edición directa sobre la grilla. Toda mutación pasa obligatoriamente por los botones de acción, garantizando que ninguna regla de negocio sea vulnerada.
* **Ventana única protegida (*Single Instance Window*):**  
  Si la interfaz gráfica ya se encuentra abierta y el usuario intenta ejecutar nuevamente el método `main`, la aplicación no duplica la ventana en memoria: detecta la ventana activa existente y la trae al frente con `toFront()`.
* **Mensajes descriptivos de error:**  
  Distingue con claridad entre advertencias de negocio (por ejemplo, socio con préstamos vencidos) y errores de base de datos (por ejemplo, desconexión de SQLite), presentando al usuario mensajes limpios y comprensibles.
* **Tooltips informativos:**  
  Cada botón y control dispone de descripciones emergentes que guían al usuario sobre la función exacta de cada elemento.

---

## 5. Matriz de Equivalencia: Consola vs. Interfaz Gráfica

La tabla muestra cómo se ejecuta cada opción de la consola (`GestionBiblioteca`) desde el menú gráfico (`Interfaz`).

| Opción de Consola | Operación en `GestionBiblioteca` | Equivalente en la Interfaz Gráfica (`Interfaz`) |
| :---: | :--- | :--- |
| **1** | Registrar un libro | Pestaña **Libros** $\to$ Botón **«Registrar ejemplar»** |
| **2** | Registrar un estudiante | Pestaña **Socios** $\to$ Botón **«Registrar socio»** (Tipo: *Estudiante*) |
| **3** | Registrar un docente | Pestaña **Socios** $\to$ Botón **«Registrar socio»** (Tipo: *Docente*) |
| **4** | Prestar un libro | Pestaña **Préstamos** $\to$ Botón **«Prestar»** (Selector de ejemplares disponibles) |
| **5** | Devolver un libro | Pestaña **Préstamos** $\to$ Selección de fila y botón **«Devolver»** |
| **6** | Consultar quién tiene un ejemplar | Pestaña **Libros** $\to$ Selección de fila y botón **«Consultar titular»** |
| **7** | Buscar un socio por DNI | Pestaña **Socios** $\to$ Botón **«Buscar DNI»** (con scroll y foco visual en grilla) |
| **8** | Listar libros | Pestaña **Libros** $\to$ Grilla interactiva visible permanentemente |
| **9** | Listar socios | Pestaña **Socios** $\to$ Grilla interactiva visible permanentemente |
| **10** | Consultar cantidad de socios por tipo | Pestaña **Socios** $\to$ Botón **«Cantidades»** |
| **11** | Listar títulos sin repetir | Pestaña **Libros** $\to$ Botón **«Títulos únicos»** |
| **12** | Listar préstamos vencidos | Pestaña **Préstamos** $\to$ Casilla de verificación **«Solo vencidos»** |
| **13** | Listar docentes responsables | Pestaña **Socios** $\to$ Botón **«Responsables»** |
| **14** | Premiar a un docente responsable | Pestaña **Socios** $\to$ Selección de docente y botón **«Premiar»** |
| **15** | Quitar un ejemplar por ID | Pestaña **Libros** $\to$ Selección de fila y botón **«Quitar ejemplar»** |
| **16** | Quitar un socio por DNI | Pestaña **Socios** $\to$ Selección de fila y botón **«Quitar socio»** |
| **0** | Salir | Cierre de ventana principal (`DISPOSE_ON_CLOSE`) |

---

## 6. Conclusión

La interfaz gráfica creada con IntelliJ y Swing Designer no solo cumple integralmente con la totalidad de los requerimientos funcionales del Trabajo Práctico N° 7, sino que optimiza los flujos de trabajo del operador mediante tablas interactivas, ordenamientos inmediatos, filtros de morosidad en tiempo real y formularios protegidos contra errores de entrada, manteniendo una separación estricta entre la presentación gráfica, las reglas de negocio y la persistencia en base de datos.
