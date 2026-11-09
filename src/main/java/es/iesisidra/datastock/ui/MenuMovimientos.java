package es.iesisidra.datastock.ui;

import java.sql.SQLException;

import es.iesisidra.datastock.datos.MovimientoDAO;
import es.iesisidra.datastock.datos.ProductoDAO;
import es.iesisidra.datastock.modelo.MovimientoStockDTO;
import es.iesisidra.datastock.modelo.ProductoDetalleDTO;
import es.iesisidra.datastock.util.ColoresConsola;
import es.iesisidra.datastock.util.LectorConsola;
import es.iesisidra.datastock.validacion.Validador;

public class MenuMovimientos {
    public static void iniciar() {
        int opcion;
        do {
            System.out.println("\n--- MENÚ DE MOVIMIENTOS DE STOCK ---");
            System.out.println("1. Registrar una ENTRADA de stock");
            System.out.println("2. Registrar una SALIDA de stock");
            System.out.println("0. Volver al menú MySQL");

            opcion = LectorConsola.leerOpcion("Selecciona una opción: ", 0, 2);
            switch (opcion) {
                case 1:
                    ejecutarMovimiento("ENTRADA");
                    break;
                case 2:
                    ejecutarMovimiento("SALIDA");
                    break;
                case 0:
                    break;
                default:
                    ColoresConsola.error("Opción no válida.");
                    break;
            }
        } while (opcion != 0);
    }

    private static void ejecutarMovimiento(String tipo) {
        try {
            String codigo = LectorConsola.leerTexto("Introduzca el código del producto: ");
            String codigoValidado = Validador.validarCodigo(codigo);

            ProductoDetalleDTO productoActual = ProductoDAO.buscarPorCodigo(codigoValidado);
            
            if (productoActual == null) {
                System.out.println(ColoresConsola.pintar("[ERROR] No existe ningún producto con el código especificado.", ColoresConsola.ROJO));
            } else {
                System.out.println("Producto: " + productoActual.getNombre() + " | Stock actual: " + productoActual.getStock());
                
                int cantidad = LectorConsola.leerEntero("Introduzca la cantidad (" + tipo + ") estrictamente positiva: ");
                
                if (cantidad <= 0) {
                    System.out.println(ColoresConsola.pintar("[ERROR] La cantidad debe ser mayor que 0.", ColoresConsola.ROJO));
                } else {
                    String confirmacion = LectorConsola.leerTexto("¿Confirmar operación de " + tipo + " de " + cantidad + " unidades? (S/N): ");
                    
                    if (confirmacion != null && confirmacion.trim().equalsIgnoreCase("S")) {
                        try {
                            MovimientoDAO.registrarMovimiento(codigoValidado, tipo, cantidad);
                            
                            ProductoDetalleDTO productoActualizado = ProductoDAO.buscarPorCodigo(codigoValidado);
                            MovimientoStockDTO ultimoMovimiento = MovimientoDAO.obtenerUltimoMovimiento(codigoValidado);
                            
                            if (productoActualizado != null && ultimoMovimiento != null) {
                                int stockPosterior = productoActualizado.getStock();
                                int stockAnterior = 0;
                                
                                if (tipo.equals("ENTRADA")) {
                                    stockAnterior = stockPosterior - cantidad;
                                } else {
                                    stockAnterior = stockPosterior + cantidad;
                                }
                                
                                System.out.println(ColoresConsola.pintar("[OK] Movimiento registrado correctamente en MySQL.", ColoresConsola.VERDE));
                                System.out.println("-> Stock anterior: " + stockAnterior + " | Operación: " + tipo + " de " + ultimoMovimiento.getCantidad() + " | Stock posterior: " + stockPosterior);
                            }
                        } catch (SQLException e) {
                            System.out.println(ColoresConsola.pintar("[ERROR MySQL] " + e.getMessage(), ColoresConsola.ROJO));
                        }
                    } else {
                        System.out.println("Operación cancelada por el usuario. No se ha modificado el stock.");
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            System.out.println(ColoresConsola.pintar("[ERROR] " + e.getMessage(), ColoresConsola.ROJO));
        } catch (SQLException e) {
            System.out.println(ColoresConsola.pintar("[ERROR JDBC] " + e.getMessage(), ColoresConsola.ROJO));
        }
    }
}
