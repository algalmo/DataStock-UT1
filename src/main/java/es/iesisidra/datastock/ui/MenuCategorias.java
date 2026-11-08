package es.iesisidra.datastock.ui;

import java.sql.SQLException;
import java.util.List;

import es.iesisidra.datastock.datos.CategoriaDAO;
import es.iesisidra.datastock.modelo.Categoria;
import es.iesisidra.datastock.util.ColoresConsola;
import es.iesisidra.datastock.util.LectorConsola;
import es.iesisidra.datastock.util.TraductorSql;
import es.iesisidra.datastock.validacion.Validador;

public class MenuCategorias {
    public static void iniciar() {
        int opcion;
        do {
            System.out.println("\n=== MENÚ DE CATEGORÍAS ===");
            System.out.println("1. Dar de alta una categoría");
            System.out.println("2. Listar categorías");
            System.out.println("3. Modificar el nombre de una categoría");
            System.out.println("4. Eliminar una categoría");
            System.out.println("0. Volver al menú MySQL");

            opcion = LectorConsola.leerOpcion("Selecciona una opción: ", 0, 4);
            switch (opcion) {
                case 1:
                    ejecutarAlta();
                    break;

                case 2:
                    ejecutarListado();
                    break;

                case 3:
                    ejecutarModificacion();
                    break;

                case 4:
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
            int id = LectorConsola.leerEntero("Introduzca el ID de la categoría (entero positivo): ");
            int idValidado = Validador.validarId(id);

            if (CategoriaDAO.existeId(idValidado)) {
                System.out.println(ColoresConsola.pintar("[ERROR] Ya existe una categoría con ese ID.", ColoresConsola.ROJO));
            } else {
                String nombre = LectorConsola.leerTexto("Introduzca el nombre de la categoría (1-60 caracteres): ");
                String nombreValidado = Validador.validarNombreCategoria(nombre);

                if (CategoriaDAO.existeNombre(nombreValidado)) {
                    System.out.println(ColoresConsola.pintar("[ERROR] Ya existe otra categoría con el mismo nombre.", ColoresConsola.ROJO));
                } else {
                    CategoriaDAO.insertar(idValidado, nombreValidado);
                    System.out.println(ColoresConsola.pintar("[OK] Categoría creada correctamente.", ColoresConsola.VERDE));
                }
            }
        } catch (IllegalArgumentException e) {
            System.out.println(ColoresConsola.pintar("[ERROR] " + e.getMessage(), ColoresConsola.ROJO));
        } catch (SQLException e) {
            System.out.println(ColoresConsola.pintar("[ERROR] " + TraductorSql.traducir(e), ColoresConsola.ROJO));
        }
    }

    private static void ejecutarListado() {
        try {
            List<Categoria> categorias = CategoriaDAO.listar();
            if (categorias.isEmpty()) {
                System.out.println("No hay categorías registradas.");
            } else {
                System.out.println("\n--- LISTADO DE CATEGORÍAS ---");
                for (Categoria c : categorias) {
                    System.out.println("ID: " + c.getId() + " | Nombre: " + c.getNombre());
                }
            }
        } catch (SQLException e) {
            System.out.println(ColoresConsola.pintar("[ERROR] " + TraductorSql.traducir(e), ColoresConsola.ROJO));
        }
    }

    private static void ejecutarModificacion() {
        try {
            int id = LectorConsola.leerEntero("Introduzca el ID de la categoría a modificar: ");
            int idValido = Validador.validarId(id);

            Categoria categoriaActual = CategoriaDAO.buscarPorId(idValido);
            if (categoriaActual == null) {
                System.out.println(ColoresConsola.pintar("[ERROR] El ID especificado no existe.", ColoresConsola.ROJO));
            } else {
                System.out.println("Nombre actual: " + categoriaActual.getNombre());

                String nuevoNombre = LectorConsola.leerTexto("Introduzca el nuevo nombre: ");
                String nuevoNombreValidado = Validador.validarNombreCategoria(nuevoNombre);

                if (CategoriaDAO.existeNombreExcluyendoId(idValido, nuevoNombreValidado)) {
                    System.out.println(ColoresConsola.pintar("[ERROR] El nombre ya está en uso por otra categoría.", ColoresConsola.ROJO));
                } else {
                    String confirmacion = LectorConsola.leerTexto("¿Desea guardar los cambios? (S/N): ");
                    if (confirmacion != null && confirmacion.trim().equalsIgnoreCase("S")) {
                        int filas = CategoriaDAO.actualizarNombre(idValido, nuevoNombreValidado);
                        if (filas > 0) {
                            System.out.println(ColoresConsola.pintar("[OK] Categoría modificada correctamente.", ColoresConsola.VERDE));
                        } else {
                            System.out.println(ColoresConsola.pintar("[ERROR] No se pudo modificar el registro.", ColoresConsola.ROJO));
                        }
                    } else {
                        System.out.println("Operación cancelada por el usuario.");
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            System.out.println(ColoresConsola.pintar("[ERROR] " + e.getMessage(), ColoresConsola.ROJO));
        } catch (SQLException e) {
            System.out.println(ColoresConsola.pintar("[ERROR] " + TraductorSql.traducir(e), ColoresConsola.ROJO));
        }
    }

    private static void ejecutarEliminacion() {
        try {
            int id = LectorConsola.leerEntero("Introduzca el ID de la categoría a eliminar: ");
            int idValido = Validador.validarId(id);

            Categoria categoria = CategoriaDAO.buscarPorId(idValido);
            if (categoria == null) {
                System.out.println(ColoresConsola.pintar("[ERROR] El ID especificado no existe.", ColoresConsola.ROJO));
            } else {
                System.out.println("Datos de la categoría -> ID: " + categoria.getId() + " | Nombre: " + categoria.getNombre());

                String confirmacion = LectorConsola.leerTexto("¿Desea eliminar esta categoría? (S/N): ");
                if (confirmacion != null && confirmacion.trim().equalsIgnoreCase("S")) {
                    int filas = CategoriaDAO.eliminar(idValido);
                    if (filas > 0) {
                        System.out.println(ColoresConsola.pintar("[OK] Categoría eliminada correctamente.", ColoresConsola.VERDE));
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
}

