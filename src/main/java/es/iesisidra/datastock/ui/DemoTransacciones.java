package es.iesisidra.datastock.ui;

import es.iesisidra.datastock.config.ConexionBD;
import es.iesisidra.datastock.util.ColoresConsola;
import es.iesisidra.datastock.util.LectorConsola;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DemoTransacciones {

    // IDS ALTOS, para no mezclar con categorías
    private static final int ID_A1 = 991;
    private static final int ID_A2 = 992;
    private static final int ID_B1 = 993;

    public static void ejecutar() {
        System.out.println("\n=== DEMOSTRACIÓN DE TRANSACCIONES JDBC ===");
        System.out.println("Se van a utilizar los IDs de categoría de prueba: " + ID_A1 + ", " + ID_A2 + " y " + ID_B1 + ".");
        System.out.println("No afecta a los datos del inventario.");

        String confirmacion = LectorConsola.leerTexto("¿Desea comenzar la demostración? (S/N): ");

        if (confirmacion != null && confirmacion.trim().equalsIgnoreCase("S")) {
            Connection conexion = null;
            boolean autoCommitOriginal = true;

            try {
                conexion = ConexionBD.obtenerConexion();
                autoCommitOriginal = conexion.getAutoCommit();

                
                limpiarDatosPrueba(conexion);

                
                System.out.println("\n--- ESCENARIO A: COMMIT ---");
                System.out.println("1. Desactivando AutoCommit...");
                conexion.setAutoCommit(false); 

                System.out.println("2. Insertando categorías " + ID_A1 + " y " + ID_A2 + " en memoria...");
                insertarCategoria(conexion, ID_A1, "DEMO_A1_OK");
                insertarCategoria(conexion, ID_A2, "DEMO_A2_OK");

                System.out.println("3. Ejecutando COMMIT para confirmar los datos de forma definitiva.");
                conexion.commit();

                if (existeCategoria(conexion, ID_A1) && existeCategoria(conexion, ID_A2)) {
                    System.out.println(ColoresConsola.pintar("[OK] Las categorías " + ID_A1 + " y " + ID_A2 + " existen correctamente en MySQL.", ColoresConsola.VERDE));
                } else {
                    System.out.println(ColoresConsola.pintar("[ERROR] No se encuentran las categorías del Escenario A.", ColoresConsola.ROJO));
                }

                System.out.println("\n--- ESCENARIO B: ROLLBACK ---");
                System.out.println("1. Iniciando nueva transacción (AutoCommit sigue en false).");
                
                try {
                    System.out.println("2. Insertando primera categoría del lote (ID " + ID_B1 + ")...");
                    insertarCategoria(conexion, ID_B1, "DEMO_B1_ROLLBACK");

                    System.out.println("3. Provocando un error intencionado (insertar mismo ID " + ID_B1 + ")...");
                    insertarCategoria(conexion, ID_B1, "DEMO_DUPLICADO"); 
                    conexion.commit(); 
                } catch (SQLException e) {
                    System.out.println(ColoresConsola.pintar("[ERROR ESPERADO CAPTURADO] " + e.getMessage(), ColoresConsola.AMARILLO));
                    System.out.println("4. Error detectado. Ejecutando ROLLBACK para anular todo el lote B...");
                    conexion.rollback();
                    System.out.println("El ROLLBACK ha funcionado: la transacción se ha descartado al completo.");
                }

                if (!existeCategoria(conexion, ID_B1)) {
                    System.out.println(ColoresConsola.pintar("[OK] La primera categoría (" + ID_B1 + ") no existe. El rollback protegió la integridad de la base de datos.", ColoresConsola.VERDE));
                } else {
                    System.out.println(ColoresConsola.pintar("[ERROR] La categoría " + ID_B1 + " existe. El rollback ha fallado.", ColoresConsola.ROJO));
                }

            } catch (SQLException e) {
                System.out.println(ColoresConsola.pintar("[FATAL] Error de conexión en la demostración: " + e.getMessage(), ColoresConsola.ROJO));
            } finally {
                if (conexion != null) {
                    try {
                        conexion.setAutoCommit(autoCommitOriginal);
                        conexion.close();
                        System.out.println("\n[INFO] AutoCommit restaurado y recursos cerrados limpiamente.");
                    } catch (SQLException e) {
                        System.out.println(ColoresConsola.pintar("[ERROR] Fallo al cerrar los recursos: " + e.getMessage(), ColoresConsola.ROJO));
                    }
                }
            }
        } else {
            System.out.println("Demostración cancelada por el usuario.");
        }
    }


    private static void insertarCategoria(Connection conexion, int id, String nombre) throws SQLException {
        String sql = "INSERT INTO categoria (id, nombre) VALUES (?, ?)";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setString(2, nombre);
            pstmt.executeUpdate();
        }
    }

    private static boolean existeCategoria(Connection conexion, int id) throws SQLException {
        boolean existe = false;
        String sql = "SELECT id FROM categoria WHERE id = ?";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    existe = true;
                }
            }
        }
        return existe;
    }

    private static void limpiarDatosPrueba(Connection conexion) throws SQLException {
        String sql = "DELETE FROM categoria WHERE id IN (?, ?, ?)";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setInt(1, ID_A1);
            pstmt.setInt(2, ID_A2);
            pstmt.setInt(3, ID_B1);
            pstmt.executeUpdate();
        }
    }
}
