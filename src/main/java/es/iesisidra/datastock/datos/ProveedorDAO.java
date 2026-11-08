package es.iesisidra.datastock.datos;

import es.iesisidra.datastock.config.ConexionBD;
import es.iesisidra.datastock.modelo.Proveedor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProveedorDAO {

    public static List<Proveedor> listar() throws SQLException {
        List<Proveedor> lista = null;
        try (Connection conexion = ConexionBD.obtenerConexion()) {
            lista = listar(conexion);
        }
        return lista;
    }

    public static List<Proveedor> listar(Connection conexion) throws SQLException {
        List<Proveedor> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, email FROM proveedor ORDER BY id ASC";
        
        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            boolean hayDatos = rs.next();
            while (hayDatos) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String email = rs.getString("email");
                lista.add(new Proveedor(id, nombre, email));
                hayDatos = rs.next();
            }
        }
        return lista;
    }

    public static Proveedor buscarPorId(int id) throws SQLException {
        Proveedor proveedor = null;
        try (Connection conexion = ConexionBD.obtenerConexion()) {
            proveedor = buscarPorId(conexion, id);
        }
        return proveedor;
    }

    public static Proveedor buscarPorId(Connection conexion, int id) throws SQLException {
        Proveedor proveedor = null;
        String sql = "SELECT id, nombre, email FROM proveedor WHERE id = ?";
        
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    proveedor = new Proveedor(rs.getInt("id"), rs.getString("nombre"), rs.getString("email"));
                }
            }
        }
        return proveedor;
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
        String sql = "SELECT 1 FROM proveedor WHERE id = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                existe = rs.next();
            }
        }
        return existe;
    }

    public static boolean existeEmail(String email) throws SQLException {
        boolean existe = false;
        try (Connection conexion = ConexionBD.obtenerConexion()) {
            existe = existeEmail(conexion, email);
        }
        return existe;
    }

    public static boolean existeEmail(Connection conexion, String email) throws SQLException {
        boolean existe = false;
        if (email != null && !email.trim().isEmpty()) {
            String sql = "SELECT 1 FROM proveedor WHERE email = ?";
            try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                ps.setString(1, email);
                try (ResultSet rs = ps.executeQuery()) {
                    existe = rs.next();
                }
            }
        }
        return existe;
    }

    public static boolean existeEmailExcluyendoId(int id, String email) throws SQLException {
        boolean existe = false;
        try (Connection conexion = ConexionBD.obtenerConexion()) {
            existe = existeEmailExcluyendoId(conexion, id, email);
        }
        return existe;
    }

    public static boolean existeEmailExcluyendoId(Connection conexion, int id, String email) throws SQLException {
        boolean existe = false;
        if (email != null && !email.trim().isEmpty()) {
            String sql = "SELECT 1 FROM proveedor WHERE email = ? AND id <> ?";
            try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                ps.setString(1, email);
                ps.setInt(2, id);
                try (ResultSet rs = ps.executeQuery()) {
                    existe = rs.next();
                }
            }
        }
        return existe;
    }

    public static void insertar(int id, String nombre, String email) throws SQLException {
        try (Connection conexion = ConexionBD.obtenerConexion()) {
            insertar(conexion, id, nombre, email);
        }
    }

    public static void insertar(Connection conexion, int id, String nombre, String email) throws SQLException {
        String sql = "INSERT INTO proveedor (id, nombre, email) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setString(2, nombre);
            if (email == null || email.trim().isEmpty()) {
                ps.setNull(3, Types.VARCHAR);
            } else {
                ps.setString(3, email);
            }
            ps.executeUpdate();
        }
    }

    public static int actualizar(int id, String nuevoNombre, String nuevoEmail) throws SQLException {
        int filas = 0;
        try (Connection conexion = ConexionBD.obtenerConexion()) {
            filas = actualizar(conexion, id, nuevoNombre, nuevoEmail);
        }
        return filas;
    }

    public static int actualizar(Connection conexion, int id, String nuevoNombre, String nuevoEmail) throws SQLException {
        int filas = 0;
        String sql = "UPDATE proveedor SET nombre = ?, email = ? WHERE id = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, nuevoNombre);
            if (nuevoEmail == null || nuevoEmail.trim().isEmpty()) {
                ps.setNull(2, Types.VARCHAR);
            } else {
                ps.setString(2, nuevoEmail);
            }
            ps.setInt(3, id);
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
        String sql = "DELETE FROM proveedor WHERE id = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            filas = ps.executeUpdate();
        }
        return filas;
    }
}
