package es.iesisidra.datastock.ui;

import java.sql.SQLException;
import java.util.List;

import es.iesisidra.datastock.datos.ProductoDAO;
import es.iesisidra.datastock.modelo.ProductoDetalleDTO;
import es.iesisidra.datastock.util.ColoresConsola;
import es.iesisidra.datastock.util.LectorConsola;
import es.iesisidra.datastock.util.TraductorSql;
import es.iesisidra.datastock.validacion.Validador;

public class MenuConsultas {
    public static void iniciar() {
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
                    ejecutarListadoCompleto();
                    break;
                case 2:
                    ejecutarBusquedaPorCodigo();
                    break;
                case 3:
                    ejecutarBusquedaPorNombre();
                    break;
                case 4:
                    ejecutarBusquedaPorStock();
                    break;
                case 0:
                    break;
                default:
                    ColoresConsola.error("Opción no válida.");
                    break;
            }
        } while (opcion != 0);
    }
    private static void ejecutarListadoCompleto() {
        try {
            List<ProductoDetalleDTO> lista = ProductoDAO.listar();
            if (lista.isEmpty()) {
                System.out.println("No hay productos registrados en el sistema.");
            } else {
                System.out.println("\n-------------------------------------------------------------------------------------------------");
                System.out.printf("%-12s | %-25s | %-10s | %-6s | %-15s | %-15s%n", "CÓDIGO", "NOMBRE", "PRECIO", "STOCK", "CATEGORÍA", "PROVEEDOR");
                System.out.println("-------------------------------------------------------------------------------------------------");
                for (ProductoDetalleDTO p : lista) {
                    System.out.printf("%-12s | %-25s | %8.2f € | %6d | %-15s | %-15s%n",
                            p.getCodigo(), p.getNombre(), p.getPrecio(), p.getStock(), p.getNombreCategoria(), p.getNombreProveedor());
                }
                System.out.println("-------------------------------------------------------------------------------------------------");
                System.out.println("Total de productos encontrados: " + lista.size());
            }
        } catch (SQLException e) {
            System.out.println(ColoresConsola.pintar("[ERROR] " + TraductorSql.traducir(e), ColoresConsola.ROJO));
        }
    }

    private static void ejecutarBusquedaPorCodigo() {
        try {
            String codigo = LectorConsola.leerTexto("Introduzca el código exacto a buscar: ");
            String codigoValidado = Validador.validarCodigo(codigo);

            ProductoDetalleDTO p = ProductoDAO.buscarPorCodigo(codigoValidado);
            if (p == null) {
                System.out.println("No existe ningún producto con el código especificado.");
            } else {
                System.out.println("\n--- RESULTADO DE LA BÚSQUEDA ---");
                System.out.println("Código:    " + p.getCodigo());
                System.out.println("Nombre:    " + p.getNombre());
                System.out.println("Precio:    " + p.getPrecio() + " €");
                System.out.println("Stock:     " + p.getStock());
                System.out.println("Categoría: " + p.getNombreCategoria());
                System.out.println("Proveedor: " + p.getNombreProveedor());
            }
        } catch (IllegalArgumentException e) {
            System.out.println(ColoresConsola.pintar("[ERROR] " + e.getMessage(), ColoresConsola.ROJO));
        } catch (SQLException e) {
            System.out.println(ColoresConsola.pintar("[ERROR] " + TraductorSql.traducir(e), ColoresConsola.ROJO));
        }
    }

    private static void ejecutarBusquedaPorNombre() {
        try {
            String fragmento = LectorConsola.leerTexto("Introduzca el fragmento de nombre a buscar: ");
            if (fragmento == null || fragmento.trim().isEmpty()) {
                System.out.println(ColoresConsola.pintar("[ERROR] El fragmento de búsqueda no puede estar vacío.", ColoresConsola.ROJO));
                return;
            }

            List<ProductoDetalleDTO> lista = ProductoDAO.buscarPorNombre(fragmento.trim());
            if (lista.isEmpty()) {
                System.out.println("No se encontraron productos que coincidan con ese fragmento.");
            } else {
                System.out.println("\n--- RESULTADOS DE BÚSQUEDA POR NOMBRE (" + lista.size() + ") ---");
                for (ProductoDetalleDTO p : lista) {
                    System.out.printf("[%s] %s | %.2f € | Stock: %d | Cat: %s | Prov: %s%n",
                            p.getCodigo(), p.getNombre(), p.getPrecio(), p.getStock(), p.getNombreCategoria(), p.getNombreProveedor());
                }
                System.out.println("Total de coincidencias: " + lista.size());
            }
        } catch (SQLException e) {
            System.out.println(ColoresConsola.pintar("[ERROR] " + TraductorSql.traducir(e), ColoresConsola.ROJO));
        }
    }

    private static void ejecutarBusquedaPorStock() {
        try {
            int umbral = LectorConsola.leerEntero("Introduzca el umbral de stock (entero >= 0): ");
            if (umbral < 0) {
                System.out.println(ColoresConsola.pintar("[ERROR] El umbral no puede ser negativo.", ColoresConsola.ROJO));
                return;
            }

            List<ProductoDetalleDTO> lista = ProductoDAO.buscarPorStockMenorQue(umbral);
            if (lista.isEmpty()) {
                System.out.println("No hay productos con stock estrictamente menor que " + umbral + ".");
            } else {
                System.out.println("\n--- PRODUCTOS CON STOCK < " + umbral + " (" + lista.size() + ") ---");
                for (ProductoDetalleDTO p : lista) {
                    System.out.printf("Stock: %3d | [%s] %s | %.2f € | Cat: %s | Prov: %s%n",
                            p.getStock(), p.getCodigo(), p.getNombre(), p.getPrecio(), p.getNombreCategoria(), p.getNombreProveedor());
                }
                System.out.println("Total de productos listados: " + lista.size());
            }
        } catch (SQLException e) {
            System.out.println(ColoresConsola.pintar("[ERROR] " + TraductorSql.traducir(e), ColoresConsola.ROJO));
        }
    }
}

