package es.iesisidra.datastock.datos;

import es.iesisidra.datastock.config.ConexionBD;
import es.iesisidra.datastock.modelo.ProductoDetalleDTO;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    public static List<ProductoDetalleDTO> listar() throws SQLException {
        List<ProductoDetalleDTO> lista = null;
        try (Connection conexion = ConexionBD.obtenerConexion()) {
            lista = listar(conexion);
        }
        return lista;
    }

    public static List<ProductoDetalleDTO> listar(Connection conexion) throws SQLException {
        List<ProductoDetalleDTO> lista = new ArrayList<>();
        String sql = "SELECT p.codigo, p.nombre, p.precio, p.stock, c.nombre AS cat_nombre, pr.nombre AS prov_nombre " +
                     "FROM producto p " +
                     "JOIN categoria c ON p.categoria_id = c.id " +
                     "JOIN proveedor pr ON p.proveedor_id = pr.id " +
                     "ORDER BY p.codigo ASC";
        
        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            boolean hayDatos = rs.next();
            while (hayDatos) {
                String codigo = rs.getString("codigo");
                String nombre = rs.getString("nombre");
                BigDecimal precio = rs.getBigDecimal("precio");
                int stock = rs.getInt("stock");
                String catNombre = rs.getString("cat_nombre");
                String provNombre = rs.getString("prov_nombre");
                
                lista.add(new ProductoDetalleDTO(codigo, nombre, precio, stock, catNombre, provNombre));
                hayDatos = rs.next();
            }
        }
        return lista;
    }

    public static ProductoDetalleDTO buscarPorCodigo(String codigo) throws SQLException {
        ProductoDetalleDTO producto = null;
        try (Connection conexion = ConexionBD.obtenerConexion()) {
            producto = buscarPorCodigo(conexion, codigo);
        }
        return producto;
    }

    public static ProductoDetalleDTO buscarPorCodigo(Connection conexion, String codigo) throws SQLException {
        ProductoDetalleDTO producto = null;
        String sql = "SELECT p.codigo, p.nombre, p.precio, p.stock, c.nombre AS cat_nombre, pr.nombre AS prov_nombre " +
                     "FROM producto p " +
                     "JOIN categoria c ON p.categoria_id = c.id " +
                     "JOIN proveedor pr ON p.proveedor_id = pr.id " +
                     "WHERE p.codigo = ?";
        
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    producto = new ProductoDetalleDTO(
                        rs.getString("codigo"),
                        rs.getString("nombre"),
                        rs.getBigDecimal("precio"),
                        rs.getInt("stock"),
                        rs.getString("cat_nombre"),
                        rs.getString("prov_nombre")
                    );
                }
            }
        }
        return producto;
    }

    public static boolean existeCodigo(String codigo) throws SQLException {
        boolean existe = false;
        try (Connection conexion = ConexionBD.obtenerConexion()) {
            existe = existeCodigo(conexion, codigo);
        }
        return existe;
    }

    public static boolean existeCodigo(Connection conexion, String codigo) throws SQLException {
        boolean existe = false;
        String sql = "SELECT 1 FROM producto WHERE codigo = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                existe = rs.next();
            }
        }
        return existe;
    }

    public static void insertar(String codigo, String nombre, BigDecimal precio, int stock, int categoriaId, int proveedorId) throws SQLException {
        try (Connection conexion = ConexionBD.obtenerConexion()) {
            insertar(conexion, codigo, nombre, precio, stock, categoriaId, proveedorId);
        }
    }

    public static void insertar(Connection conexion, String codigo, String nombre, BigDecimal precio, int stock, int categoriaId, int proveedorId) throws SQLException {
        String sql = "INSERT INTO producto (codigo, nombre, precio, stock, categoria_id, proveedor_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, codigo);
            ps.setString(2, nombre);
            ps.setBigDecimal(3, precio);
            ps.setInt(4, stock);
            ps.setInt(5, categoriaId);
            ps.setInt(6, proveedorId);
            ps.executeUpdate();
        }
    }

    public static int actualizar(String codigo, String nuevoNombre, BigDecimal nuevoPrecio, int nuevaCategoriaId, int nuevoProveedorId) throws SQLException {
        int filas = 0;
        try (Connection conexion = ConexionBD.obtenerConexion()) {
            filas = actualizar(conexion, codigo, nuevoNombre, nuevoPrecio, nuevaCategoriaId, nuevoProveedorId);
        }
        return filas;
    }

    public static int actualizar(Connection conexion, String codigo, String nuevoNombre, BigDecimal nuevoPrecio, int nuevaCategoriaId, int nuevoProveedorId) throws SQLException {
        int filas = 0;
        String sql = "UPDATE producto SET nombre = ?, precio = ?, categoria_id = ?, proveedor_id = ? WHERE codigo = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, nuevoNombre);
            ps.setBigDecimal(2, nuevoPrecio);
            ps.setInt(3, nuevaCategoriaId);
            ps.setInt(4, nuevoProveedorId);
            ps.setString(5, codigo);
            filas = ps.executeUpdate();
        }
        return filas;
    }

    public static int eliminar(String codigo) throws SQLException {
        int filas = 0;
        try (Connection conexion = ConexionBD.obtenerConexion()) {
            filas = eliminar(conexion, codigo);
        }
        return filas;
    }

    public static int eliminar(Connection conexion, String codigo) throws SQLException {
        int filas = 0;
        String sql = "DELETE FROM producto WHERE codigo = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, codigo);
            filas = ps.executeUpdate();
        }
        return filas;
    }
}
