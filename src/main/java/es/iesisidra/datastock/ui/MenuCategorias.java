package es.iesisidra.datastock.ui;

import es.iesisidra.datastock.util.LectorConsola;

public class MenuCategorias {
    public void iniciar() {
        int opcion;
        do {
            System.out.println("\n--- MENÚ DE CATEGORÍAS ---");
            System.out.println("1. Dar de alta una categoría");
            System.out.println("2. Listar categorías");
            System.out.println("3. Modificar el nombre de una categoría");
            System.out.println("4. Eliminar una categoría");
            System.out.println("0. Volver al menú MySQL");

            opcion = LectorConsola.leerOpcion("Selecciona una opción: ", 0, 4);
            switch (opcion) {
                case 1:

                    break;

                case 2:

                    break;

                case 3:

                    break;

                case 4:

                    break;

                case 0:
                    break;

            }
        } while (opcion != 0);
    }
}
