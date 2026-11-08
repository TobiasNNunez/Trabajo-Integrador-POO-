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

El arranque comprueba si el archivo SQLite existía, inicializa el esquema y carga los registros. `precargarDatos(...)` se ejecuta únicamente cuando faltaba el archivo al iniciar y las colecciones de ejemplares y socios están vacías. Así una base existente que se vació mediante las bajas permanece vacía al reabrirla.

```java
try {
    boolean baseNueva = !ConexionBD.existeBaseDeDatos();
    ConexionBD.inicializarTablas();
    unaBiblioteca.cargarDesdeBaseDeDatos();

    if (baseNueva && unaBiblioteca.getLibros().isEmpty() && unaBiblioteca.getSocios().isEmpty()) {
        precargarDatos(unaBiblioteca);
        System.out.println("Base de datos creada y precargada con datos de ejemplo.");
    } else {
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

### 3. Bajas de ejemplares y socios

La opción **15** pide el ID del ejemplar en SQLite. La opción **16** pide el DNI del socio. Ambas muestran el registro encontrado y piden confirmar con `S` o `N`. Con la confirmación, llaman a `Biblioteca.quitarLibro(...)` o `Biblioteca.quitarSocio(...)`. El menú informa si el registro no existe o si la baja es rechazada por historial de préstamos o un error de persistencia.

---

## 3. Justificación y Conceptos de POO Aplicados

- **Ciclo de vida persistente.** Una instalación nueva iniciada desde la consola dispone de ejemplos para probar el sistema. Las sesiones posteriores operan sobre los objetos recuperados de SQLite.
- **Comunicación con el usuario.** Los manejadores informan qué ocurrió al iniciar o al ejecutar una opción. Las confirmaciones de las operaciones aparecen después de completar la llamada a `Biblioteca`.
- **Separación de responsabilidades.** `GestionBiblioteca` coordina la entrada y la salida de consola y solicita el arranque a `ConexionBD`. `Biblioteca` coordina las operaciones y el repositorio se ocupa del SQL.

---
