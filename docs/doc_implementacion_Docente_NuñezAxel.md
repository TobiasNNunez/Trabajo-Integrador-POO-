# Implementación de Docente

**Método:** `esResponsable()`

**Qué hace:** Determina si el docente nunca tuvo ni tiene un préstamo vencido. Tiene en cuenta tanto los préstamos devueltos como los que siguen en su poder.

**Cómo se implementó y qué conceptos de POO se aplicaron:** Se inicializa una variable booleana `responsable` en `true` y se obtiene la fecha actual mediante `new GregorianCalendar()`. Se recorre la colección heredada de préstamos con un `for-each`. Para cada préstamo se toma su fecha de devolución; si es `null`, se utiliza la fecha actual. Se envía esa fecha a `unPrestamo.vencido(unaFecha)` y, si devuelve `true`, se cambia `responsable` a `false`. Se utiliza una colección de objetos, herencia y colaboración entre `Docente` y `Prestamo` mediante mensajes.

**Por qué se eligió esa solución:** Cada préstamo es el encargado de determinar su vencimiento. La clase `Docente` solo elige con qué fecha corresponde evaluarlo. Un préstamo devuelto a tiempo no debe aparecer como vencido por compararlo con la fecha de hoy. La variable booleana permite conservar el resultado negativo aunque después se recorran otros préstamos en término.

**Caso comprobado y resultado:** En la prueba de escritorio, una colección vacía devuelve `true`. Si todos los préstamos resultan no vencidos al evaluarlos con la fecha correspondiente, también devuelve `true`. Una devolución tardía hace que el resultado final sea `false`, incluso si los préstamos siguientes fueron devueltos a tiempo. Un préstamo pendiente vencido también produce `false`. El caso de una devolución realizada durante el día del vencimiento requiere revisar el tratamiento de las horas en `Prestamo.vencido()`, detallado al final de este documento.

---

**Método:** `cambiarDiasDePrestamo(int p_dias)`

**Qué hace:** Agrega una cantidad de días al plazo de préstamo que posee actualmente el docente.

**Cómo se implementó y qué conceptos de POO se aplicaron:** Se obtiene el plazo mediante `this.getDiasPrestamo()`, se suma `p_dias` y se actualiza el resultado mediante `this.setDiasPrestamo(...)`. Se accede al estado heredado a través de métodos, respetando el encapsulamiento.

**Por qué se eligió esa solución:** La consigna pide adicionar días, por lo que se suma al plazo existente en lugar de reemplazarlo por el parámetro. No se repite el atributo de días en `Docente`, porque pertenece a `Socio`. Se utiliza el nombre singular del UML. El método realiza el aumento; quien lo invoque para premiar al docente debe comprobar previamente su responsabilidad.

**Caso comprobado y resultado:** En la prueba de escritorio, un plazo de cinco días con un aumento de tres da como resultado ocho días. Un segundo aumento de dos da como resultado diez días. Se comprueba así que los aumentos se acumulan. La actualización utiliza el getter público y el setter protegido que ya proporciona `Socio`.

## Comportamiento heredado

`puedePedir()` se hereda directamente de `Socio`, sin redefinirlo en `Docente`. La implementación de la superclase comprueba únicamente los préstamos actuales vencidos y no impone un límite de cantidad. Por eso un atraso antiguo ya resuelto no impide pedir otro libro, aunque sí puede hacer que `esResponsable()` devuelva `false`.