package es.iesisidra.datastock.datos;

import es.iesisidra.datastock.config.ConexionBD;
import es.iesisidra.datastock.modelo.Categoria;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {

    public static List<Categoria> listar() throws SQLException {
        List<Categoria> lista = null;
        try (Connection conexion = ConexionBD.obtenerConexion()) {
            lista = listar(conexion);
        }
        return lista;
    }

    public static List<Categoria> listar(Connection conexion) throws SQLException {
        List<Categoria> lista = new ArrayList<>();
        String sql = "SELECT id, nombre FROM categoria ORDER BY id ASC";
        
        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            boolean hayDatos = rs.next();
            while (hayDatos) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                lista.add(new Categoria(id, nombre));
                hayDatos = rs.next();
            }
        }
        return lista;
    }

    public static Categoria buscarPorId(int id) throws SQLException {
        Categoria categoria = null;
        try (Connection conexion = ConexionBD.obtenerConexion()) {
            categoria = buscarPorId(conexion, id);
        }
        return categoria;
    }

    public static Categoria buscarPorId(Connection conexion, int id) throws SQLException {
        Categoria categoria = null;
        String sql = "SELECT id, nombre FROM categoria WHERE id = ?";
        
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    categoria = new Categoria(rs.getInt("id"), rs.getString("nombre"));
                }
            }
        }
        return categoria;
    }

    public static boolean existeId(int id) throws SQLException {
        boolean existe = false;
        try (Connection conexion = ConexionBD.obtenerConexion()) {
            existe = existeId(conexion, id);
        }
        return existe;
    }

    public static boolean existeId(Connection conexion, int id) throws SQLException {
        boolean existe = false;
        String sql = "SELECT 1 FROM categoria WHERE id = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                existe = rs.next();
            }
        }
        return existe;
    }

    public static boolean existeNombre(String nombre) throws SQLException {
        boolean existe = false;
        try (Connection conexion = ConexionBD.obtenerConexion()) {
            existe = existeNombre(conexion, nombre);
        }
        return existe;
    }

    public static boolean existeNombre(Connection conexion, String nombre) throws SQLException {
        boolean existe = false;
        String sql = "SELECT 1 FROM categoria WHERE nombre = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                existe = rs.next();
            }
        }
        return existe;
    }

    public static boolean existeNombreExcluyendoId(int id, String nombre) throws SQLException {
        boolean existe = false;
        try (Connection conexion = ConexionBD.obtenerConexion()) {
            existe = existeNombreExcluyendoId(conexion, id, nombre);
        }
        return existe;
    }

    public static boolean existeNombreExcluyendoId(Connection conexion, int id, String nombre) throws SQLException {
        boolean existe = false;
        String sql = "SELECT 1 FROM categoria WHERE nombre = ? AND id <> ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setInt(2, id);
            try (ResultSet rs = ps.executeQuery()) {
                existe = rs.next();
            }
        }
        return existe;
    }

    public static void insertar(int id, String nombre) throws SQLException {
        try (Connection conexion = ConexionBD.obtenerConexion()) {
            insertar(conexion, id, nombre);
        }
    }

    public static void insertar(Connection conexion, int id, String nombre) throws SQLException {
        String sql = "INSERT INTO categoria (id, nombre) VALUES (?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setString(2, nombre);
            ps.executeUpdate();
        }
    }

    public static int actualizarNombre(int id, String nuevoNombre) throws SQLException {
        int filas = 0;
        try (Connection conexion = ConexionBD.obtenerConexion()) {
            filas = actualizarNombre(conexion, id, nuevoNombre);
        }
        return filas;
    }

    public static int actualizarNombre(Connection conexion, int id, String nuevoNombre) throws SQLException {
        int filas = 0;
        String sql = "UPDATE categoria SET nombre = ? WHERE id = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, nuevoNombre);
            ps.setInt(2, id);
            filas = ps.executeUpdate();
        }
        return filas;
    }

    public static int eliminar(int id) throws SQLException {
        int filas = 0;
        try (Connection conexion = ConexionBD.obtenerConexion()) {
            filas = eliminar(conexion, id);
        }
        return filas;
    }

    public static int eliminar(Connection conexion, int id) throws SQLException {
        int filas = 0;
        String sql = "DELETE FROM categoria WHERE id = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            filas = ps.executeUpdate();
        }
        return filas;
    }
}
