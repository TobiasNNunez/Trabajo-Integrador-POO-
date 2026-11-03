# Implementación de Estudiante

**Método:** `puedePedir()`

**Qué hace:** Determina si el estudiante puede solicitar otro libro. Debe estar habilitado por la condición común de los socios y tener menos de tres libros en su poder.

**Cómo se implementó y qué conceptos de POO se aplicaron:** Se redefinió el método heredado de `Socio`, utilizando `@Override`. Primero se consulta `super.puedePedir()` para aprovechar la comprobación de préstamos vencidos. Esa condición se combina mediante `&&` con `this.cantLibrosPrestados() < 3`. Se aplican herencia y reutilización del comportamiento de la superclase.

**Por qué se eligió esa solución:** La comprobación de vencimientos ya corresponde a `Socio`, por lo que no hace falta repetirla en `Estudiante`. La subclase agrega únicamente su condición particular de cantidad. Se tomó como criterio un máximo de tres préstamos simultáneos: si ya tiene tres libros, no puede recibir un cuarto. La redacción del enunciado permite otra lectura, por lo que este criterio debe confirmarse al integrar el trabajo.

**Caso comprobado y resultado:** En la prueba de escritorio, con dos libros y `super.puedePedir()` igual a `true`, se obtiene `true`. Con tres libros se obtiene `false`. Si la comprobación heredada devuelve `false` por un préstamo actual vencido, el resultado también es `false`, aunque tenga menos de tres libros. Los préstamos ya devueltos no aumentan la cantidad de libros actuales ni bloquean el resultado de `Socio.puedePedir()`.