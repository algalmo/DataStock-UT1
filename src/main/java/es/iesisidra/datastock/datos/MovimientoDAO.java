package es.iesisidra.datastock.datos;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import es.iesisidra.datastock.config.ConexionBD;
import es.iesisidra.datastock.modelo.MovimientoStock;
import es.iesisidra.datastock.modelo.MovimientoStockDTO;

public class MovimientoDAO {

    //Invoca el procedimiento almacenado que gestiona su propia transacción.
     
    public static void registrarMovimiento(String codigoProducto, String tipo, int cantidad) throws SQLException {
        String sql = "{call registrar_movimiento(?, ?, ?)}";
        
        try (Connection conexion = ConexionBD.obtenerConexion();
             CallableStatement cs = conexion.prepareCall(sql)) {
            
            cs.setString(1, codigoProducto);
            cs.setString(2, tipo);
            cs.setInt(3, cantidad);
            
            cs.execute();
        }
    }

    // Consulta el último movimiento de un producto
     
    public static MovimientoStockDTO obtenerUltimoMovimiento(String codigoProducto) throws SQLException {
        MovimientoStockDTO movimiento = null;
        // Ajusta historial
        String sql = "SELECT producto_codigo, tipo, cantidad, fecha FROM movimiento_stock WHERE producto_codigo = ? ORDER BY id DESC LIMIT 1";
        
        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            
            ps.setString(1, codigoProducto);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    movimiento = new MovimientoStockDTO(
                        rs.getString("producto_codigo"),
                        rs.getString("tipo"),
                        rs.getInt("cantidad"),
                        rs.getTimestamp("fecha")
                    );
                }
            }
        }
        return movimiento;
    }

    // Consulta el historial completo ordenado por fecha y luego por ID.
     
    public static List<MovimientoStock> obtenerHistorialCompleto() throws SQLException {
        List<MovimientoStock> lista = new ArrayList<>();
        String sql = "SELECT id, fecha, producto_codigo, tipo, cantidad, stock_anterior, stock_posterior " +
                     "FROM movimiento_stock " +
                     "ORDER BY fecha ASC, id ASC";
        
        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                lista.add(new MovimientoStock(
                    rs.getLong("id"),
                    rs.getString("producto_codigo"),
                    MovimientoStock.TipoMovimiento.valueOf(rs.getString("tipo")),
                    rs.getInt("cantidad"),
                    rs.getTimestamp("fecha").toLocalDateTime(),
                    rs.getInt("stock_anterior"),
                    rs.getInt("stock_posterior")
                ));
            }
        }
        return lista;
    }

    // Consulta el historial filtrado por un producto específico.
     
    public static List<MovimientoStock> obtenerHistorialPorProducto(String codigoProducto) throws SQLException {
        List<MovimientoStock> lista = new ArrayList<>();
        String sql = "SELECT id, fecha, producto_codigo, tipo, cantidad, stock_anterior, stock_posterior " +
                     "FROM movimiento_stock " +
                     "WHERE producto_codigo = ? " +
                     "ORDER BY fecha ASC, id ASC";
        
        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            
            ps.setString(1, codigoProducto);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new MovimientoStock(
                        rs.getLong("id"),
                        rs.getString("producto_codigo"),
                        MovimientoStock.TipoMovimiento.valueOf(rs.getString("tipo")),
                        rs.getInt("cantidad"),
                        rs.getTimestamp("fecha").toLocalDateTime(),
                        rs.getInt("stock_anterior"),
                        rs.getInt("stock_posterior")
                    ));
                }
            }
        }
        return lista;
    }
}