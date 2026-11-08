package es.iesisidra.datastock.ui;

import java.sql.SQLException;
import java.util.List;

import es.iesisidra.datastock.datos.ProveedorDAO;
import es.iesisidra.datastock.modelo.Proveedor;
import es.iesisidra.datastock.util.ColoresConsola;
import es.iesisidra.datastock.util.LectorConsola;
import es.iesisidra.datastock.util.TraductorSql;
import es.iesisidra.datastock.validacion.Validador;

public class MenuProveedores {
    public static void iniciar() {
        int opcion;
        do {
            System.out.println("\n--- MENÚ DE PROVEEDORES ---");
            System.out.println("1. Dar de alta un proveedor");
            System.out.println("2. Listar proveedores");
            System.out.println("3. Modificar un proveedor");
            System.out.println("4. Eliminar un proveedor");
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
            int id = LectorConsola.leerEntero("Introduzca el ID del proveedor (entero positivo): ");
            int idValidado = Validador.validarId(id);

            if (ProveedorDAO.existeId(idValidado)) {
                System.out.println(ColoresConsola.pintar("[ERROR] Ya existe un proveedor con ese ID.", ColoresConsola.ROJO));
            } else {
                String nombre = LectorConsola.leerTexto("Introduzca el nombre del proveedor (máx. 100 caracteres): ");
                String nombreValidado = Validador.validarNombreProveedor(nombre);

                String email = LectorConsola.leerTexto("Introduzca el correo electrónico (opcional, Pulse Intro para omitir): ");
                String emailValidado = null;

                if (email != null && !email.trim().isEmpty()) {
                    emailValidado = Validador.validarEmail(email);
                    if (ProveedorDAO.existeEmail(emailValidado)) {
                        System.out.println(ColoresConsola.pintar("[ERROR] Ya existe otro proveedor registrado con ese correo.", ColoresConsola.ROJO));
                        return;
                    }
                }

                ProveedorDAO.insertar(idValidado, nombreValidado, emailValidado);
                System.out.println(ColoresConsola.pintar("[OK] Proveedor creado correctamente.", ColoresConsola.VERDE));
            }
        } catch (IllegalArgumentException e) {
            System.out.println(ColoresConsola.pintar("[ERROR] " + e.getMessage(), ColoresConsola.ROJO));
        } catch (SQLException e) {
            System.out.println(ColoresConsola.pintar("[ERROR] " + TraductorSql.traducir(e), ColoresConsola.ROJO));
        }
    }

    private static void ejecutarListado() {
        try {
            List<Proveedor> proveedores = ProveedorDAO.listar();
            if (proveedores.isEmpty()) {
                System.out.println("No hay proveedores registrados.");
            } else {
                System.out.println("\n--- LISTADO DE PROVEEDORES ---");
                for (Proveedor p : proveedores) {
                    String emailMostrar = (p.getEmail() == null || p.getEmail().trim().isEmpty()) ? "—" : p.getEmail();
                    System.out.println("ID: " + p.getId() + " | Nombre: " + p.getNombre() + " | Email: " + emailMostrar);
                }
            }
        } catch (SQLException e) {
            System.out.println(ColoresConsola.pintar("[ERROR] " + TraductorSql.traducir(e), ColoresConsola.ROJO));
        }
    }

    private static void ejecutarModificacion() {
        try {
            int id = LectorConsola.leerEntero("Introduzca el ID del proveedor a modificar: ");
            int idValido = Validador.validarId(id);

            Proveedor proveedorActual = ProveedorDAO.buscarPorId(idValido);
            if (proveedorActual == null) {
                System.out.println(ColoresConsola.pintar("[ERROR] El ID especificado no existe.", ColoresConsola.ROJO));
            } else {
                System.out.println("Nombre actual: " + proveedorActual.getNombre());
                System.out.println("Email actual: " + (proveedorActual.getEmail() == null ? "—" : proveedorActual.getEmail()));

                String nuevoNombreCrudo = LectorConsola.leerTexto("Introduzca el nuevo nombre (Intro para conservar actual): ");
                String nombreFinal;
                if (nuevoNombreCrudo == null || nuevoNombreCrudo.trim().isEmpty()) {
                    nombreFinal = proveedorActual.getNombre();
                } else {
                    nombreFinal = Validador.validarNombreProveedor(nuevoNombreCrudo);
                }

                String nuevoEmail = LectorConsola.leerTexto("Introduzca el nuevo email (Intro para conservar, '-' para eliminar): ");
                String emailFinal;
                if (nuevoEmail == null || nuevoEmail.trim().isEmpty()) {
                    emailFinal = proveedorActual.getEmail();
                } else if (nuevoEmail.trim().equals("-")) {
                    emailFinal = null;
                } else {
                    emailFinal = Validador.validarEmail(nuevoEmail);
                    if (ProveedorDAO.existeEmailExcluyendoId(idValido, emailFinal)) {
                        System.out.println(ColoresConsola.pintar("[ERROR] El correo electrónico ya está en uso por otro proveedor.", ColoresConsola.ROJO));
                        return;
                    }
                }

                String confirmacion = LectorConsola.leerTexto("¿Desea guardar los cambios? (S/N): ");
                if (confirmacion != null && confirmacion.trim().equalsIgnoreCase("S")) {
                    int filas = ProveedorDAO.actualizar(idValido, nombreFinal, emailFinal);
                    if (filas > 0) {
                        System.out.println(ColoresConsola.pintar("[OK] Proveedor modificado correctamente.", ColoresConsola.VERDE));
                    } else {
                        System.out.println(ColoresConsola.pintar("[ERROR] No se pudo modificar el registro.", ColoresConsola.ROJO));
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

    private static void ejecutarEliminacion() {
        try {
            int id = LectorConsola.leerEntero("Introduzca el ID del proveedor a eliminar: ");
            int idValido = Validador.validarId(id);

            Proveedor proveedor = ProveedorDAO.buscarPorId(idValido);
            if (proveedor == null) {
                System.out.println(ColoresConsola.pintar("[ERROR] El ID especificado no existe.", ColoresConsola.ROJO));
            } else {
                String emailMostrar = (proveedor.getEmail() == null ? "—" : proveedor.getEmail());
                System.out.println("Datos del proveedor -> ID: " + proveedor.getId() + " | Nombre: " + proveedor.getNombre() + " | Email: " + emailMostrar);

                String confirmacion = LectorConsola.leerTexto("¿Desea eliminar este proveedor? (S/N): ");
                if (confirmacion != null && confirmacion.trim().equalsIgnoreCase("S")) {
                    int filas = ProveedorDAO.eliminar(idValido);
                    if (filas > 0) {
                        System.out.println(ColoresConsola.pintar("[OK] Proveedor eliminado correctamente.", ColoresConsola.VERDE));
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

