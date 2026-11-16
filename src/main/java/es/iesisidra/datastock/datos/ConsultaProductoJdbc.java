
package es.iesisidra.datastock.datos;

import es.iesisidra.datastock.config.ConexionBD;
import es.iesisidra.datastock.modelo.ProductoDetalleDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Componente reutilizable para consultar productos en MySQL mediante JDBC.
 */
public class ConsultaProductoJdbc {

    /**
     * Busca un producto por código utilizando una nueva conexión.
     *
     * @param codigo Código del producto.
     * @return Producto encontrado o Optional vacío si no existe.
     * @throws SQLException Si ocurre un error de acceso a la base de datos.
     */
    public Optional<ProductoDetalleDTO> buscarPorCodigo(String codigo)
            throws SQLException {

        try (Connection conexion = ConexionBD.obtenerConexion()) {
            return buscarPorCodigo(conexion, codigo);
        }
    }

    /**
     * Busca un producto utilizando una conexión existente.
     * No cierra la conexión recibida.
     *
     * @param conexion Conexión JDBC proporcionada por el llamador.
     * @param codigo Código del producto.
     * @return Producto encontrado o Optional vacío si no existe.
     * @throws SQLException Si ocurre un error SQL.
     */
    public Optional<ProductoDetalleDTO> buscarPorCodigo(
            Connection conexion, String codigo) throws SQLException {

        String sql = "SELECT p.codigo, p.nombre, p.precio, p.stock, "
                + "c.nombre AS cat_nombre, pr.nombre AS prov_nombre "
                + "FROM producto p "
                + "JOIN categoria c ON p.categoria_id = c.id "
                + "JOIN proveedor pr ON p.proveedor_id = pr.id "
                + "WHERE p.codigo = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, codigo);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    return Optional.empty();
                }

                return Optional.of(new ProductoDetalleDTO(
                        rs.getString("codigo"),
                        rs.getString("nombre"),
                        rs.getBigDecimal("precio"),
                        rs.getInt("stock"),
                        rs.getString("cat_nombre"),
                        rs.getString("prov_nombre")
                ));
            }
        }
    }
}

