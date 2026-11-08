package es.iesisidra.datastock.ui;

import es.iesisidra.datastock.util.LectorConsola;

public class MenuConsultas {
    public void iniciar() {
        int opcion;
        do {
            System.out.println("\n--- MENÚ DE CONSULTAS ---");
            System.out.println("1. Listar todos los productos");
            System.out.println("2. Buscar producto por código");
            System.out.println("3. Buscar productos por nombre");
            System.out.println("4. Consultar productos con stock inferior a un umbral");
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
