package es.iesisidra.datastock.ui;

import es.iesisidra.datastock.util.LectorConsola;

public class MenuMovimientos {
    public void iniciar() {
        int opcion;
        do {
            System.out.println("\n--- MENÚ DE MOVIMIENTOS DE STOCK ---");
            System.out.println("1. Registrar una ENTRADA de stock");
            System.out.println("2. Registrar una SALIDA de stock");
            System.out.println("0. Volver al menú MySQL");

            opcion = LectorConsola.leerOpcion("Selecciona una opción: ", 0, 2);
            switch (opcion) {
                case 1:
                    break;
                case 2:
                    break;
                case 0:
                    break;
            }
        } while (opcion != 0);
    }
}
