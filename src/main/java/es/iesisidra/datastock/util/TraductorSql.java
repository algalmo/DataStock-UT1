package es.iesisidra.datastock.util;

import java.sql.SQLException;

public class TraductorSql {
    public static String traducir(SQLException e) {
        int codigo = e.getErrorCode();
        String estado = e.getSQLState();
        String mensaje;

        if (codigo == 1045) {
            mensaje = "Error de autenticación: Credenciales de MySQL incorrectas.";
        } else if (codigo == 1049) {
            mensaje = "La base de datos o esquema especificado no existe en el servidor.";
        } else if ("08S01".equals(estado) || codigo == 2003 || codigo == 0) {
            mensaje = "No se pudo conectar con el servidor MySQL. Comprueba que esté encendido.";
        } else {
            mensaje = "Error de base de datos (Código " + codigo + "): " + e.getMessage();
        }

        return mensaje;
    }
}
