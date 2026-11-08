# Documentación de Cambios — Clase GestionBiblioteca

**Clase:** `biblioteca.GestionBiblioteca`  
**Paquete:** `biblioteca`  
**Responsables de la revisión:** Equipo Completo

---

## 1. Contexto del Cambio

`GestionBiblioteca` contiene el punto de entrada `main` y usa `Scanner` para leer las opciones y los datos ingresados por consola.

En la versión original, cada sesión comenzaba con `precargarDatos(unaBiblioteca)`. Los cuatro socios, los cuatro ejemplares y los préstamos de ejemplo se recreaban en cada ejecución. Las altas y demás cambios duraban hasta el cierre de la sesión.

---

## 2. Modificaciones Realizadas

### 1. Arranque con Persistencia Condicional en `main`

El arranque inicializa el esquema de SQLite y consulta si la tabla `libros` está vacía. En ese caso, `precargarDatos(...)` registra los ejemplos a través de `Biblioteca`. Si hay ejemplares, `cargarDesdeBaseDeDatos()` recupera los datos que dejó la sesión anterior.

```java
try {
    ConexionBD.inicializarTablas();

    if (unaBiblioteca.baseDeDatosVacia()) {
        precargarDatos(unaBiblioteca);
        System.out.println("Base de datos creada y precargada con datos de ejemplo.");
    } else {
        unaBiblioteca.cargarDesdeBaseDeDatos();
        System.out.println("Datos cargados exitosamente desde la base de datos sqLite.");
    }
} catch (IllegalStateException e) {
    System.out.println(e.getMessage());
    return;
}
```

El `catch` del arranque informa el fallo de persistencia y termina `main` antes de presentar el menú. Durante el uso del programa, otro `try` rodea `ejecutarOpcion(...)`. Si la opción falla por `IllegalStateException`, el menú muestra el mensaje y queda disponible para otra operación. Al devolver un ejemplar o consultar su titular, los métodos de consola capturan `LibroNoPrestadoException` para informar la condición de negocio específica.

### 2. Persistencia del Plazo Actualizado en `premiarDocente`

En la opción 14, `GestionBiblioteca` verifica que el socio sea un docente responsable y que el aumento sea positivo. Luego delega la actualización en `Biblioteca`.

```java
p_biblioteca.actualizarDiasDocente(unDocente, dias);
```

`Biblioteca` guarda el plazo resultante en `socios` y después aplica el aumento al objeto. La consola muestra el nuevo plazo cuando la operación termina correctamente.

---

## 3. Justificación y Conceptos de POO Aplicados

- **Ciclo de vida persistente.** Una instalación con la tabla `libros` vacía dispone de ejemplos para probar el sistema. En una sesión posterior con ejemplares guardados, el menú vuelve a operar sobre los objetos recuperados de SQLite.
- **Comunicación con el usuario.** Los manejadores informan qué ocurrió al iniciar o al ejecutar una opción. Las confirmaciones de las operaciones aparecen después de completar la llamada a `Biblioteca`.
- **Separación de responsabilidades.** `GestionBiblioteca` coordina la entrada y la salida de consola y solicita el arranque a `ConexionBD`. `Biblioteca` coordina las operaciones y el repositorio se ocupa del SQL.

---
