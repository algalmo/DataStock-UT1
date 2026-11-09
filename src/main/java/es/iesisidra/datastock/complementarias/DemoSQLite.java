package es.iesisidra.datastock.complementarias;

import es.iesisidra.datastock.util.ColoresConsola;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DemoSQLite {

    public static void main(String[] args) {
        String rutaDir = "datos/sqlite";
        File carpeta = new File(rutaDir);
        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }

        String url = "jdbc:sqlite:" + rutaDir + "/productos_demo.db";
        String sqlTabla = "CREATE TABLE IF NOT EXISTS producto_demo (" +
                          "codigo TEXT PRIMARY KEY NOT NULL, " +
                          "nombre TEXT NOT NULL" +
                          ");";
        String sqlInsert = "INSERT OR IGNORE INTO producto_demo (codigo, nombre) VALUES (?, ?)";
        String sqlSelect = "SELECT codigo, nombre FROM producto_demo ORDER BY codigo ASC";

        int insertados = 0;
        int yaExistentes = 0;

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {

            // 1. Crear tabla si no existe
            stmt.execute(sqlTabla);

            // 2. Insertar registros de demostración
            String[][] datos = {
                {"SQL-001", "Producto demo A"},
                {"SQL-002", "Producto demo B"}
            };

            try (PreparedStatement ps = conn.prepareStatement(sqlInsert)) {
                for (String[] fila : datos) {
                    ps.setString(1, fila[0]);
                    ps.setString(2, fila[1]);
                    int filasAfectadas = ps.executeUpdate();
                    if (filasAfectadas > 0) {
                        insertados++;
                    } else {
                        yaExistentes++;
                    }
                }
            }

            System.out.println(ColoresConsola.VERDE + "[OK] Insertados: " + insertados + " | Ya existentes: " + yaExistentes + ColoresConsola.RESET);

            //Consultar y mostrar el contenido
            System.out.println("\n--- CONTENIDO DE LA TABLA producto_demo ---");
            try (PreparedStatement psSelect = conn.prepareStatement(sqlSelect);
                 ResultSet rs = psSelect.executeQuery()) {

                while (rs.next()) {
                    String codigo = rs.getString("codigo");
                    String nombre = rs.getString("nombre");
                    System.out.println("Código: " + codigo + " | Nombre: " + nombre);
                }
            }

        } catch (SQLException e) {
            System.out.println(ColoresConsola.ROJO + "[ERROR] Fallo en la base de datos SQLite: " + e.getMessage() + ColoresConsola.RESET);
        }
    }
}
