package es.iesisidra.datastock.ui;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import es.iesisidra.datastock.datos.MovimientoDAO;
import es.iesisidra.datastock.datos.ProductoDAO;
import es.iesisidra.datastock.modelo.MovimientoStock;
import es.iesisidra.datastock.util.ColoresConsola;
import es.iesisidra.datastock.util.LectorConsola;
import es.iesisidra.datastock.util.TraductorSql;
import es.iesisidra.datastock.validacion.Validador;

public class MenuHistorial {
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    public static void iniciar() {
        int opcion;
        do {
            System.out.println("\n--- MENÚ DE HISTORIAL ---");
            System.out.println("1. Consultar todos los movimientos");
            System.out.println("2. Consultar movimientos de un producto");
            System.out.println("0. Volver al menú MySQL");

            opcion = LectorConsola.leerOpcion("Selecciona una opción: ", 0, 2);
            switch (opcion) {
                case 1:
                    ejecutarHistorialCompleto();
                    break;
                case 2:
                    ejecutarHistorialPorProducto();
                    break;
                case 0:
                    break;
                default:
                    ColoresConsola.error("Opción no válida.");
                    break;
            }
        } while (opcion != 0);
    }

    private static void ejecutarHistorialCompleto() {
        try {
            List<MovimientoStock> lista = MovimientoDAO.obtenerHistorialCompleto();
            if (lista.isEmpty()) {
                System.out.println("No hay ningún movimiento registrado en el sistema.");
            } else {
                imprimirListaMovimientos(lista, "HISTORIAL COMPLETO");
            }
        } catch (SQLException e) {
            System.out.println(ColoresConsola.pintar("[ERROR] " + TraductorSql.traducir(e), ColoresConsola.ROJO));
        }
    }

    private static void ejecutarHistorialPorProducto() {
        try {
            String codigo = LectorConsola.leerTexto("Introduzca el código del producto: ");
            String codigoValidado = Validador.validarCodigo(codigo);

            if (!ProductoDAO.existeCodigo(codigoValidado)) {
                System.out.println(ColoresConsola.pintar("[ERROR] El producto con código '" + codigoValidado + "' no existe en la base de datos.", ColoresConsola.ROJO));
            } else {
                List<MovimientoStock> lista = MovimientoDAO.obtenerHistorialPorProducto(codigoValidado);
                
                if (lista.isEmpty()) {
                    System.out.println("Sin movimientos registrados.");
                } else {
                    imprimirListaMovimientos(lista, "HISTORIAL DEL PRODUCTO: " + codigoValidado);
                }
            }
        } catch (IllegalArgumentException e) {
            System.out.println(ColoresConsola.pintar("[ERROR] " + e.getMessage(), ColoresConsola.ROJO));
        } catch (SQLException e) {
            System.out.println(ColoresConsola.pintar("[ERROR] " + TraductorSql.traducir(e), ColoresConsola.ROJO));
        }
    }

    private static void imprimirListaMovimientos(List<MovimientoStock> lista, String titulo) {
        System.out.println("\n--- " + titulo + " ---");
        System.out.printf("%-5s | %-20s | %-12s | %-17s | %-8s | %-14s | %-14s%n", 
                "ID", "FECHA Y HORA", "PRODUCTO", "TIPO", "CANTIDAD", "STOCK ANTERIOR", "STOCK POSTERIOR");
        System.out.println("--------------------------------------------------------------------------------------------------");
        
        for (MovimientoStock m : lista) {
            String tipoColor = "";

        if (m.getTipo() == MovimientoStock.TipoMovimiento.ENTRADA) {
            tipoColor = ColoresConsola.pintar(m.getTipo().name(), ColoresConsola.VERDE);
        } else {
            tipoColor = ColoresConsola.pintar(m.getTipo().name(), ColoresConsola.AMARILLO);
}

            System.out.printf("%-5d | %-20s | %-12s | %-26s | %8d | %14d | %14d%n",
                    m.getId(),
                    m.getFecha().format(FORMATO_FECHA),
                    m.getProductoCodigo(),
                    tipoColor,
                    m.getCantidad(),
                    m.getStockAnterior(),
                    m.getStockPosterior());
        }
        System.out.println("--------------------------------------------------------------------------------------------------");
        System.out.println("Total de movimientos: " + lista.size());
    }
}
