TP7 - Gestión de Biblioteca
Ejecución desde BlueJ

1. Abrí BlueJ con Java 21. Desde Proyecto > Abrir proyecto, elegí la
   carpeta TP7, que contiene el archivo package.bluej.
2. Para configurar SQLite, agregá en Preferencias > Librerías el archivo
   biblioteca/persistencia/sqlite-jdbc-3.54.1.0.jar y reiniciá BlueJ.
   También podés copiar el JAR a una carpeta +libs en TP7 y volver a
   abrir el proyecto.
3. Entrá en el paquete biblioteca y pulsá Compilar.
4. Elegí uno de los dos ejecutables del proyecto. Cada uno tiene su propio
   método main. Hacé clic derecho en la clase, seleccioná main(String[] args)
   y aceptá el arreglo vacío {} cuando BlueJ pida los argumentos.

   - GestionBiblioteca abre el menú de consola en la terminal de BlueJ.
     Usá la opción 0 para salir.
   - Interfaz abre el menú gráfico con las pestañas Libros, Socios y Préstamos.
     Para salir, cerrá la ventana.

Ambos menús usan biblioteca/persistencia/biblioteca.db. Cerrá uno antes de
abrir el otro para que cada ejecución cargue los datos más recientes.
