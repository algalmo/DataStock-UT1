package es.iesisidra.datastock.ui;

import java.io.File;

import es.iesisidra.datastock.servicio.GestorArchivos;
import es.iesisidra.datastock.servicio.Inventario;
import es.iesisidra.datastock.util.ColoresConsola;
import es.iesisidra.datastock.util.LectorConsola;

/**
 * Presentación de menús y navegación de la primera fase de DataStock.
 * <p>
 * Los métodos marcados TODO son puntos de partida, NO soluciones de los
 * requisitos. El alumnado deberá delegar el acceso a ficheros, la validación
 * y el procesamiento en métodos o clases independientes.
 * </p>
 */

// GIT HUB

public class MenuPrincipal {
    private final Inventario inventario;
    private final GestorArchivos gestorArchivos;

    /** @param inventario colección común de productos de esta ejecución. */
    public MenuPrincipal(Inventario inventario) {
        this.inventario = inventario;
        this.gestorArchivos = new GestorArchivos();
    }

    /** Ejecuta el menú principal hasta que el usuario selecciona salir. */
    public void iniciar() {
        int opcion;
        do {
            mostrarCabecera();
            System.out.println("1. Explorar ficheros y directorios");
            System.out.println("2. Importar productos desde JSON");
            System.out.println("3. Importar productos desde XML");
            System.out.println("4. Consultar productos");
            System.out.println("5. Exportar productos");
            System.out.println("6. Guardar y recuperar inventario");

            System.out.println("0. Salir");
            opcion = LectorConsola.leerOpcion("Selecciona una opción: ", 0, 6);
            switch (opcion) {
                // Explorar ficheros y directorios
                case 1 -> {
                    String ruta = LectorConsola.leerTexto("Ruta a explorar: ");
                    String resultado = gestorArchivos.explorarRuta(ruta);
                    System.out.println(resultado);
                    LectorConsola.pausar();
                }

                // Importar JSON
                case 2 -> {
                    String ruta = LectorConsola.leerTexto("Ruta del fichero JSON: ");
                    String resultado = gestorArchivos.importarJSON(ruta, inventario);
                    System.out.println(resultado);
                    LectorConsola.pausar();
                }

                // Importar XML
                case 3 -> {
                    String ruta = LectorConsola.leerTexto("Ruta del fichero XML: ");
                    String resultado = gestorArchivos.importarXML(ruta, inventario);
                    System.out.println(resultado);
                    LectorConsola.pausar();
                }

                case 4 -> menuConsultas();
                case 5 -> menuExportaciones();
                case 6 -> menuSerializacion();
                case 0 -> System.out.println(ColoresConsola.pintar("Hasta pronto.", ColoresConsola.VERDE));
                default -> System.out.println("Opción no válida.");
            }
        } while (opcion != 0);
    }

    private void mostrarCabecera() {
        System.out.println(
                ColoresConsola.pintar("\n================================================", ColoresConsola.CIAN));
        System.out.println(ColoresConsola.pintar("         DATASTOCK - GESTIÓN DE INVENTARIO", ColoresConsola.NEGRITA));
        System.out.println(ColoresConsola.pintar("                 FASE 1 - UT1", ColoresConsola.CIAN));
        System.out.println(
                ColoresConsola.pintar("================================================", ColoresConsola.CIAN));
        System.out.println(ColoresConsola.pintar("Productos en memoria: " + inventario.getProductos().size(),
                ColoresConsola.GRIS));
    }

    private void menuConsultas() {
        int opcion;
        do {
            System.out.println("\n---------- CONSULTA DE PRODUCTOS ----------");
            System.out.println("1. Listar todos los productos");
            System.out.println("2. Buscar producto por código");
            System.out.println("3. Buscar productos por nombre");
            System.out.println("0. Volver al menú principal");
            opcion = LectorConsola.leerOpcion("Selecciona una opción: ", 0, 3);
            switch (opcion) {
                case 1 -> {
                    System.out.println(inventario.listarProductos());
                    LectorConsola.pausar();
                }
                case 2 -> {
                    String cod = LectorConsola.leerTexto("Introduce el código exacto: ");
                    System.out.println(inventario.buscarPorCodigo(cod));
                    LectorConsola.pausar();
                }
                case 3 -> {
                    String nombre = LectorConsola.leerTexto("Introduce el texto a buscar: ");
                    System.out.println(inventario.buscarPorNombre(nombre));
                    LectorConsola.pausar();
                }
                case 0 -> {
                }
                default -> System.out.println("Opción no válida.");
            }
        } while (opcion != 0);
    }

    private void menuExportaciones() {
        int opcion;
        do {
            System.out.println("\n---------- EXPORTAR PRODUCTOS ----------");
            System.out.println("1. Exportar productos a JSON");
            System.out.println("2. Exportar productos a XML");
            System.out.println("0. Volver al menú principal");
            opcion = LectorConsola.leerOpcion("Selecciona una opción: ", 0, 2);
            switch (opcion) {
                case 1 -> exportar("JSON");
                case 2 -> exportar("XML");
                case 0 -> {
                }
                default -> System.out.println("Opción no válida. Inténtalo de nuevo.");
            }
        } while (opcion != 0);
    }

    private void menuSerializacion() {
        int opcion;
        do {
            System.out.println("\n---------- GUARDAR Y RECUPERAR INVENTARIO ----------");
            System.out.println("1. Guardar inventario en fichero .dat");
            System.out.println("2. Recuperar inventario desde fichero .dat");
            System.out.println("0. Volver al menú principal");
            opcion = LectorConsola.leerOpcion("Selecciona una opción: ", 0, 2);
            switch (opcion) {
                case 1 -> {
                    System.out.println("\n===== GUARDAR INVENTARIO =====");
                    String ruta = LectorConsola.leerTexto("Ruta: ");
                    String resultado = gestorArchivos.guardarInventarioDAT(ruta, inventario);
                    System.out.println(resultado);
                    LectorConsola.pausar();
                }
                case 2 -> {
                    System.out.println("\n===== RECUPERAR INVENTARIO =====");
                    String ruta = LectorConsola.leerTexto("Ruta: ");

                    int cantidadActual = inventario.getProductos().size();
                    System.out.println("Actualmente hay " + cantidadActual + " productos en memoria.");
                    System.out.println("La recuperación sustituirá el inventario actual.");

                    String respuesta = LectorConsola.leerTexto("¿Continuar? (S/N): ");

                    if (respuesta != null && respuesta.trim().equalsIgnoreCase("S")) {
                        String resultado = gestorArchivos.recuperarInventarioDAT(ruta, inventario);
                        System.out.println(resultado);
                    } else {
                        System.out.println("Operación cancelada.");
                    }
                    LectorConsola.pausar();
                }
                case 0 -> {
                }
                default -> System.out.println("Opción no válida.");
            }
        } while (opcion != 0);
    }

    private void exportar(String formato) {
        if (inventario.estaVacio()) {
            System.out.println("Error: El inventario está vacío. No hay productos para exportar.");
        } else {
            String ruta = pedirRutaDestino();
            if (ruta != null) {
                if (formato.equals("JSON")) {
                    System.out.println(gestorArchivos.exportarJSON(ruta, inventario));
                } else {
                    System.out.println(gestorArchivos.exportarXML(ruta, inventario));
                }
            }
        }
        LectorConsola.pausar();
    }

    private String pedirRutaDestino() {
        String ruta = LectorConsola.leerTexto("Ruta: ");

        if (!ruta.trim().isEmpty() && new File(ruta.trim()).exists()) {
            if (!LectorConsola.confirmar("El fichero ya existe. ¿Sobrescribir?")) {
                System.out.println("Operación cancelada. El fichero original no se ha modificado.");
                ruta = null;
            }
        }

        return ruta;
    }

}
