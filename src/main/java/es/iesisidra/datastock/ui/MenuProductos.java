package es.iesisidra.datastock.ui;

import es.iesisidra.datastock.util.LectorConsola;

public class MenuProductos {
    public void iniciar() {
        int opcion;
        do {
            System.out.println("\n--- MENÚ DE PRODUCTOS (CRUD) ---");
            System.out.println("1. Dar de alta un producto");
            System.out.println("2. Consultar producto por código");
            System.out.println("3. Modificar un producto");
            System.out.println("4. Eliminar un producto");
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
