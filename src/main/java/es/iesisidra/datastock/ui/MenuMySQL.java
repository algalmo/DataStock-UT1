package es.iesisidra.datastock.ui;

import es.iesisidra.datastock.datos.ConexionDAO;
import es.iesisidra.datastock.modelo.InfoBaseDatos;
import es.iesisidra.datastock.servicio.Inventario;
import es.iesisidra.datastock.util.ColoresConsola;
import es.iesisidra.datastock.util.LectorConsola;

public class MenuMySQL {
    private final Inventario inventario;

    public MenuMySQL(Inventario inventario) {
        this.inventario = inventario;
    }

    public void iniciar() {
        int opcion;

        do {
            System.out.println("\n==========================");
            System.out.println("    GESTIÓN MYSQL (UT2)     ");
            System.out.println("============================");
            System.out.println("1. Comprobar conexión y mostrar información de la BD");
            System.out.println("2. Gestionar categorías");
            System.out.println("3. Gestionar proveedores");
            System.out.println("4. Gestionar productos (CRUD)");
            System.out.println("5. Consultar y buscar productos");
            System.out.println("6. Registrar entrada o salida de stock");
            System.out.println("7. Consultar historial de movimientos");
            System.out.println("8. Importar productos de memoria a MySQL");
            System.out.println("9. Demostración de transacción con commit/rollback");

            System.out.println("0. Volver al menú principal");

            opcion = LectorConsola.leerOpcion("Selecciona una opción: ", 0, 9);

            switch (opcion) {
                case 1:
                    try {
                        InfoBaseDatos info = ConexionDAO.obtenerInfoConexion();

                        System.out.println(ColoresConsola.pintar("[OK] Conexión establecida correctamente.",
                                ColoresConsola.VERDE));
                        System.out.println("- Base de datos: " + info.getNombreBaseDatos());
                        System.out.println(
                                "- Gestor: " + info.getNombreGestor() + " (Versión: " + info.getVersionGestor() + ")");
                        System.out.println("- Total de productos en la tabla: " + info.getTotalProductos());

                    } catch (RuntimeException e) {
                        System.out.println(ColoresConsola.pintar("[ERROR] " + e.getMessage(), ColoresConsola.ROJO));
                    }
                    break;

                case 2:
                    MenuCategorias.iniciar();
                    break;

                case 3:
                    MenuProveedores.iniciar();
                    break;

                case 4:
                    MenuProductos.iniciar();
                    break;

                case 5:
                    MenuConsultas.iniciar();
                    break;

                case 6:
                    MenuMovimientos.iniciar();
                    break;

                case 7:
                    MenuHistorial.iniciar();
                    break;

                case 8:

                    break;

                case 9:

                    break;

                case 0:

                    break;

                default:
                    ColoresConsola.error("Opción no válida. Introduce un número entre 0 y 9");
                    break;
            }

        } while (opcion != 0);
    }
}
