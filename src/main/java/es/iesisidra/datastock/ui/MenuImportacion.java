package es.iesisidra.datastock.ui;

import java.util.List; // El de memoria

import es.iesisidra.datastock.modelo.Producto;
import es.iesisidra.datastock.modelo.ResultadoImportacion;
import es.iesisidra.datastock.servicio.ImportadorMySQL; // Ajusta a tu clase de UT1
import es.iesisidra.datastock.servicio.Inventario;
import es.iesisidra.datastock.util.ColoresConsola;
import es.iesisidra.datastock.util.LectorConsola;

public class MenuImportacion {

    public static void ejecutar() {
        System.out.println("\n=== IMPORTACIÓN DE INVENTARIO (JAVA -> MySQL) ===");

        // Obtener la lista de memoria
        List<Producto> productosMemoria = new Inventario().getProductos();
        if (productosMemoria == null || productosMemoria.isEmpty()) {
            System.out.println("No hay productos en el inventario de memoria para importar.");
        } else {
            System.out.println("Se han detectado " + productosMemoria.size() + " productos en memoria.");
            
            String confirmacion = LectorConsola.leerTexto("¿Desea iniciar la validación e importación? (S/N): ");
            
            if (confirmacion != null && confirmacion.trim().equalsIgnoreCase("S")) {
                System.out.println("Procesando lote, por favor espere...\n");
                
                ResultadoImportacion resultado = ImportadorMySQL.importar(productosMemoria);
                
                for (String incidencia : resultado.getIncidencias()) {
                    if (incidencia.contains("[OMITIDO]")) {
                        System.out.println(ColoresConsola.pintar(incidencia, ColoresConsola.AMARILLO));
                    } else {
                        System.out.println(ColoresConsola.pintar(incidencia, ColoresConsola.ROJO));
                    }
                }

                // Resumen Final
                System.out.println("\n--- RESUMEN DE LA IMPORTACIÓN ---");
                System.out.println("Total analizados: " + resultado.getTotalAnalizados());
                
                if (resultado.isRollbackEjecutado()) {
                    System.out.println(ColoresConsola.pintar("Insertados: 0", ColoresConsola.ROJO));
                    System.out.println(ColoresConsola.pintar("[ATENCIÓN] Se produjo un error SQL crítico. Se ha ejecutado ROLLBACK. No se ha confirmado ninguna inserción del lote.", ColoresConsola.ROJO));
                } else {
                    System.out.println(ColoresConsola.pintar("Insertados con éxito: " + resultado.getInsertados(), ColoresConsola.VERDE));
                }
                
                System.out.println("Omitidos por duplicado: " + resultado.getOmitidosDuplicados());
                System.out.println("Rechazados por otras causas: " + resultado.getRechazados());
            } else {
                System.out.println("Operación de importación cancelada por el usuario.");
            }
        }
    }
}