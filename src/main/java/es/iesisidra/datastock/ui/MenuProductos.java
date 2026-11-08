package es.iesisidra.datastock.ui;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import es.iesisidra.datastock.config.ConexionBD;
import es.iesisidra.datastock.datos.CategoriaDAO;
import es.iesisidra.datastock.datos.ProductoDAO;
import es.iesisidra.datastock.datos.ProveedorDAO;
import es.iesisidra.datastock.modelo.ProductoDetalleDTO;
import es.iesisidra.datastock.util.ColoresConsola;
import es.iesisidra.datastock.util.LectorConsola;
import es.iesisidra.datastock.util.TraductorSql;
import es.iesisidra.datastock.validacion.Validador;

public class MenuProductos {
    public static void iniciar() {
        int opcion;
        do {
            System.out.println("\n--- MENÚ DE PRODUCTOS (CRUD) ---");
            System.out.println("1. Dar de alta un producto");
            System.out.println("2. Consultar producto por código");
            System.out.println("3. Listar productos");
            System.out.println("4. Modificar un producto");
            System.out.println("5. Eliminar un producto");
            System.out.println("0. Volver al menú MySQL");

            opcion = LectorConsola.leerOpcion("Selecciona una opción: ", 0, 5);
            switch (opcion) {
                case 1:
                    ejecutarAlta();
                    break;
                case 2:
                    ejecutarConsultaPorCodigo();
                    break;
                case 3:
                    ejecutarListado();
                    break;
                case 4:
                    ejecutarModificacion();
                    break;
                case 5:
                    ejecutarEliminacion();
                    break;
                case 0:
                    break;
                default:
                    ColoresConsola.error("Opción no válida.");
                    break;
            }
        } while (opcion != 0);
    }
    private static void ejecutarAlta() {
        try {
            String codigo = LectorConsola.leerTexto("Introduzca el código del producto (1-12 caracteres): ");
            String codigoValidado = Validador.validarCodigo(codigo);

            if (ProductoDAO.existeCodigo(codigoValidado)) {
                System.out.println(ColoresConsola.pintar("[ERROR] Ya existe un producto con ese código.", ColoresConsola.ROJO));
                return;
            }

            
            String nombre= LectorConsola.leerTexto("Introduzca el nombre del producto (3-100 caracteres): ");
            String nombreValidado = Validador.validarNombreProducto(nombre);

            BigDecimal precioValidado = null;
            boolean precioCorrecto = false;
            while (!precioCorrecto) {
                try {
                    String precio = LectorConsola.leerTexto("Introduzca el precio (ej: 49.90): ");
                    BigDecimal precioTemp = new BigDecimal(precio.trim());
                    precioValidado = Validador.validarPrecio(precioTemp);
                    precioCorrecto = true;
                } catch (NumberFormatException e) {
                    System.out.println(ColoresConsola.pintar("[ERROR] Formato de precio inválido. Introduzca un número decimal válido.", ColoresConsola.ROJO));
                } catch (IllegalArgumentException e) {
                    System.out.println(ColoresConsola.pintar("[ERROR] " + e.getMessage(), ColoresConsola.ROJO));
                }
            }

            int stock = LectorConsola.leerEntero("Introduzca el stock inicial (entero >= 0): ");
            int stockValidado = Validador.validarStock(stock);

            int catId= LectorConsola.leerEntero("Introduzca el ID de la categoría: ");
            int catIdValidado = Validador.validarId(catId);
            if (!CategoriaDAO.existeId(catIdValidado)) {
                System.out.println(ColoresConsola.pintar("[ERROR] No existe la categoría con ID " + catIdValidado, ColoresConsola.ROJO));
                return;
            }

            int provId = LectorConsola.leerEntero("Introduzca el ID del proveedor: ");
            int provIdValidado = Validador.validarId(provId);
            if (!ProveedorDAO.existeId(provIdValidado)) {
                System.out.println(ColoresConsola.pintar("[ERROR] No existe el proveedor con ID " + provIdValidado, ColoresConsola.ROJO));
                return;
            }

            ProductoDAO.insertar(codigoValidado, nombreValidado, precioValidado, stockValidado, catIdValidado, provIdValidado);
            System.out.println(ColoresConsola.pintar("[OK] Producto creado correctamente.", ColoresConsola.VERDE));

        } catch (IllegalArgumentException e) {
            System.out.println(ColoresConsola.pintar("[ERROR] " + e.getMessage(), ColoresConsola.ROJO));
        } catch (SQLException e) {
            System.out.println(ColoresConsola.pintar("[ERROR] " + TraductorSql.traducir(e), ColoresConsola.ROJO));
        }
    }

    private static void ejecutarConsultaPorCodigo() {
        try {
            String codigo = LectorConsola.leerTexto("Introduzca el código del producto a consultar: ");
            String codigoValidado = Validador.validarCodigo(codigo);

            ProductoDetalleDTO producto = ProductoDAO.buscarPorCodigo(codigoValidado);
            if (producto == null) {
                System.out.println("No existe ningún producto con el código especificado.");
            } else {
                System.out.println("\n--- DETALLES DEL PRODUCTO ---");
                System.out.println("Código: " + producto.getCodigo());
                System.out.println("Nombre: " + producto.getNombre());
                System.out.println("Precio: " + producto.getPrecio() + " €");
                System.out.println("Stock actual: " + producto.getStock());
                System.out.println("Categoría: " + producto.getNombreCategoria());
                System.out.println("Proveedor: " + producto.getNombreProveedor());
            }
        } catch (IllegalArgumentException e) {
            System.out.println(ColoresConsola.pintar("[ERROR] " + e.getMessage(), ColoresConsola.ROJO));
        } catch (SQLException e) {
            System.out.println(ColoresConsola.pintar("[ERROR] " + TraductorSql.traducir(e), ColoresConsola.ROJO));
        }
    }

    private static void ejecutarListado() {
        try {
            List<ProductoDetalleDTO> productos = ProductoDAO.listar();
            if (productos.isEmpty()) {
                System.out.println("No hay productos registrados.");
            } else {
                System.out.println("\n--- LISTADO GENERAL DE PRODUCTOS ---");
                for (ProductoDetalleDTO p : productos) {
                    System.out.println("[" + p.getCodigo() + "] " + p.getNombre() + 
                                       " | Precio: " + p.getPrecio() + " €" + 
                                       " | Stock: " + p.getStock() + 
                                       " | Cat: " + p.getNombreCategoria() + 
                                       " | Prov: " + p.getNombreProveedor());
                }
            }
        } catch (SQLException e) {
            System.out.println(ColoresConsola.pintar("[ERROR] " + TraductorSql.traducir(e), ColoresConsola.ROJO));
        }
    }

    private static void ejecutarModificacion() {
        try {
            String codigo = LectorConsola.leerTexto("Introduzca el código del producto a modificar: ");
            String codigoValidado = Validador.validarCodigo(codigo);

            ProductoDetalleDTO productoActual = ProductoDAO.buscarPorCodigo(codigoValidado);
            if (productoActual == null) {
                System.out.println(ColoresConsola.pintar("[ERROR] El producto especificado no existe.", ColoresConsola.ROJO));
            } else {
                System.out.println("Valores actuales -> Nombre: " + productoActual.getNombre() + 
                                   " | Precio: " + productoActual.getPrecio() + 
                                   " | Cat: " + productoActual.getNombreCategoria() + 
                                   " | Prov: " + productoActual.getNombreProveedor());

                String nuevoNombre = LectorConsola.leerTexto("Introduzca el nuevo nombre (Intro para conservar): ");
                String nombreFinal;
                if (nuevoNombre == null || nuevoNombre.trim().isEmpty()) {
                    nombreFinal = productoActual.getNombre();
                } else {
                    nombreFinal = Validador.validarNombreProducto(nuevoNombre);
                }

                BigDecimal precioFinal = productoActual.getPrecio();
                String nuevoPrecio = LectorConsola.leerTexto("Introduzca el nuevo precio (Intro para conservar): ");
                if (nuevoPrecio != null && !nuevoPrecio.trim().isEmpty()) {
                    BigDecimal precioTemp = new BigDecimal(nuevoPrecio.trim());
                    precioFinal = Validador.validarPrecio(precioTemp);
                }


                int catIdFinal = -1;
                String nuevaCat = LectorConsola.leerTexto("Introduzca el nuevo ID de categoría (Intro para conservar): ");
                if (nuevaCat == null || nuevaCat.trim().isEmpty()) {
                    catIdFinal = obtenerCategoriaIdPorProducto(codigoValidado);
                } else {
                    catIdFinal = Validador.validarId(Integer.parseInt(nuevaCat.trim()));
                    if (!CategoriaDAO.existeId(catIdFinal)) {
                        System.out.println(ColoresConsola.pintar("[ERROR] La categoría indicada no existe.", ColoresConsola.ROJO));
                        return;
                    }
                }

                int provIdFinal = -1;
                String nuevoProv = LectorConsola.leerTexto("Introduzca el nuevo ID de proveedor (Intro para conservar): ");
                if (nuevoProv == null || nuevoProv.trim().isEmpty()) {
                    provIdFinal = obtenerProveedorIdPorProducto(codigoValidado);
                } else {
                    provIdFinal = Validador.validarId(Integer.parseInt(nuevoProv.trim()));
                    if (!ProveedorDAO.existeId(provIdFinal)) {
                        System.out.println(ColoresConsola.pintar("[ERROR] El proveedor indicado no existe.", ColoresConsola.ROJO));
                        return;
                    }
                }

                String confirmacion = LectorConsola.leerTexto("¿Desea guardar los cambios? (S/N): ");
                if (confirmacion != null && confirmacion.trim().equalsIgnoreCase("S")) {
                    int filas = ProductoDAO.actualizar(codigoValidado, nombreFinal, precioFinal, catIdFinal, provIdFinal);
                    if (filas > 0) {
                        System.out.println(ColoresConsola.pintar("[OK] Producto modificado correctamente.", ColoresConsola.VERDE));
                    } else {
                        System.out.println(ColoresConsola.pintar("[ERROR] No se pudo modificar el producto.", ColoresConsola.ROJO));
                    }
                } else {
                    System.out.println("Operación cancelada por el usuario.");
                }
            }
        } catch (IllegalArgumentException e) {
            System.out.println(ColoresConsola.pintar("[ERROR] Datos introducidos no válidos: " + e.getMessage(), ColoresConsola.ROJO));
        } catch (SQLException e) {
            System.out.println(ColoresConsola.pintar("[ERROR] " + TraductorSql.traducir(e), ColoresConsola.ROJO));
        }
    }

    private static void ejecutarEliminacion() {
        try {
            String codigo = LectorConsola.leerTexto("Introduzca el código del producto a eliminar: ");
            String codigoValidado = Validador.validarCodigo(codigo);

            ProductoDetalleDTO producto = ProductoDAO.buscarPorCodigo(codigoValidado);
            if (producto == null) {
                System.out.println(ColoresConsola.pintar("[ERROR] El producto especificado no existe.", ColoresConsola.ROJO));
            } else {
                System.out.println("Datos -> Código: " + producto.getCodigo() + " | Nombre: " + producto.getNombre() + " | Stock: " + producto.getStock());

                String confirmacion = LectorConsola.leerTexto("¿Desea eliminar este producto? (S/N): ");
                if (confirmacion != null && confirmacion.trim().equalsIgnoreCase("S")) {
                    int filas = ProductoDAO.eliminar(codigoValidado);
                    if (filas > 0) {
                        System.out.println(ColoresConsola.pintar("[OK] Producto eliminado correctamente.", ColoresConsola.VERDE));
                    } else {
                        System.out.println(ColoresConsola.pintar("[ERROR] No se encontró el registro para eliminar.", ColoresConsola.ROJO));
                    }
                } else {
                    System.out.println("Operación cancelada por el usuario.");
                }
            }
        } catch (IllegalArgumentException e) {
            System.out.println(ColoresConsola.pintar("[ERROR] " + e.getMessage(), ColoresConsola.ROJO));
        } catch (SQLException e) {
            System.out.println(ColoresConsola.pintar("[ERROR] " + TraductorSql.traducir(e), ColoresConsola.ROJO));
        }
    }

    private static int obtenerCategoriaIdPorProducto(String codigo) throws SQLException {
        int id = 0;
        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement("SELECT categoria_id FROM producto WHERE codigo = ?")) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    id = rs.getInt(1);
                }
            }
        }
        return id;
    }

    private static int obtenerProveedorIdPorProducto(String codigo) throws SQLException {
        int id = 0;
        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement("SELECT proveedor_id FROM producto WHERE codigo = ?")) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    id = rs.getInt(1);
                }
            }
        }
        return id;
    }
}

