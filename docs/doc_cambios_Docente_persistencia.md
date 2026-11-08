# Documentación de Cambios — Clase Docente

**Clase:** `biblioteca.Docente`  
**Paquete:** `biblioteca`  
**Responsable de la revisión:** Axel Gabriel Nuñez  

---

## 1. Contexto del Cambio

En la versión original del TP7, los constructores de `Docente` inicializaban el plazo de préstamo en cinco días. El aumento que otorgaba el menú a un docente responsable se aplicaba al objeto durante la sesión.

SQLite guarda el plazo vigente en `socios.dias_prestamo`. Al recuperar un docente en otra sesión, el repositorio debe poder construirlo con ese plazo. Por ejemplo, un docente premiado con tres días se carga con los ocho días que quedaron guardados.

---

## 2. Métodos Modificados / Incorporados

### Constructor Sobrecargado con Días de Préstamo

El constructor recibe el plazo como argumento junto con los datos del docente.

```java
public Docente(int p_dniSocio, String p_nombre, String p_area, int p_diasPrestamo) {
    super(p_dniSocio, p_nombre, p_diasPrestamo);
    this.setArea(p_area);
}
```

`RepositorioBiblioteca` lee la fila del docente, pasa el valor de `dias_prestamo` al constructor y reconstruye el historial de préstamos en la etapa siguiente de la carga.

---

## 3. Justificación y Conceptos de POO Aplicados

- **Sobrecarga de constructores.** Las firmas permiten elegir cómo inicializar el plazo según el origen del objeto.
  1. `Docente(dni, nombre, area)` asigna los cinco días iniciales en un alta nueva.
  2. `Docente(dni, nombre, area, diasPrestamo)` asigna el plazo que el repositorio recupera de SQLite.
- **Herencia y reutilización con `super`.** `Socio` inicializa el DNI, el nombre, el plazo y la colección de préstamos. `Docente` asigna su dato específico mediante `setArea(...)`.
- **Encapsulamiento.** La reconstrucción establece el plazo en el constructor. El modelo controla los aumentos posteriores mediante `cambiarDiasDePrestamo(...)` y el mutador protegido de `Socio`.

---
