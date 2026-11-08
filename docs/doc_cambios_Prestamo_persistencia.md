# Documentación de Cambios — Clase Prestamo

**Clase:** `biblioteca.Prestamo`  
**Paquete:** `biblioteca`  
**Responsable de la revisión:** Tobias Nuñez

---

## 1. Contexto del Cambio

La clase `Prestamo` vincula un `Socio` con un `Libro` y registra las fechas de retiro y devolución como objetos `Calendar`.

En la versión original, `armarFecha(Calendar p_fecha)` era privado y `toString()` lo usaba al mostrar un préstamo.

Las columnas de fecha en SQLite usan texto. El repositorio puede reutilizar `armarFecha(...)` para guardar la fecha de retiro y la fecha de una devolución registrada.

---

## 2. Modificaciones Realizadas

### Cambio de Visibilidad de `armarFecha`

El método pasó de `private` a `public` para que `RepositorioBiblioteca` pueda llamarlo al guardar un préstamo o registrar su devolución.

```java
public String armarFecha(Calendar p_fecha) {
    if (p_fecha == null) {
        return "Ejemplar en préstamo";
    }
    
    int anio = p_fecha.get(Calendar.YEAR);
    int mes = p_fecha.get(Calendar.MONTH) + 1;
    int dia = p_fecha.get(Calendar.DATE);
    
    return anio + "/" + mes + "/" + dia;
}
```

La cadena usa año, mes y día separados por barras, por ejemplo `2026/3/5`. El método suma uno al mes de `Calendar` porque enero se representa con `0`. El texto `Ejemplar en préstamo` se usa al mostrar una devolución pendiente en consola. SQLite representa esa condición con `NULL` en `fecha_devolucion`.

---

## 3. Justificación y Conceptos de POO Aplicados

- **Reutilización de código.** `toString()` y `RepositorioBiblioteca` usan la misma conversión de una fecha a texto.
- **Principio del experto en información.** `Prestamo` conoce las fechas del retiro y la devolución y proporciona una forma de representarlas.
- **Coherencia de formato.** Al cargar una fila, el repositorio separa la fecha guardada por `/` y construye un `GregorianCalendar` con año, mes y día.
- **Separación entre formato y lógica del préstamo.** `armarFecha(...)` convierte fechas para mostrarlas o guardarlas. `vencido(...)` aplica la regla de vencimiento sobre los objetos `Calendar`.

---
