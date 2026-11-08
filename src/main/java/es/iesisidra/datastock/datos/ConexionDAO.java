package es.iesisidra.datastock.datos;

import es.iesisidra.datastock.config.ConexionBD;
import es.iesisidra.datastock.modelo.InfoBaseDatos;
import es.iesisidra.datastock.util.TraductorSql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexionDAO {

    public static InfoBaseDatos obtenerInfoConexion() {
        InfoBaseDatos info = null;

        try (Connection conexion = ConexionBD.obtenerConexion()) {
            
            DatabaseMetaData metaData = conexion.getMetaData();
            String nombreGestor = metaData.getDatabaseProductName();
            String versionGestor = metaData.getDatabaseProductVersion();
            String nombreBD = conexion.getCatalog();

            int totalProductos = 0;
            String sql = "SELECT COUNT(*) FROM producto";
            
            try (Statement stmt = conexion.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                
                if (rs.next()) {
                    totalProductos = rs.getInt(1);
                }
            }

            info = new InfoBaseDatos(nombreBD, nombreGestor, versionGestor, totalProductos);

        } catch (SQLException e) {
            String mensajeAmigable = TraductorSql.traducir(e);
            throw new RuntimeException(mensajeAmigable);
        }

        return info;
    }
}
