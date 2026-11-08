package es.iesisidra.datastock.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Crea conexiones JDBC con MySQL.
 * <p>Los datos de conexión no están en el código: se leen primero de las
 * variables de entorno JDBC_URL, JDBC_USER y JDBC_PASSWORD y, si no existen,
 * del fichero local config/db.properties (excluido de Git).</p>
 * <p>Cada llamada devuelve una conexión NUEVA que quien la pide debe cerrar
 * (con try-with-resources). No se guarda ninguna conexión abierta.</p>
 */

//UT2

public final class ConexionBD {

    private static final String FICHERO_CONFIG = "config/db.properties";

    private ConexionBD() { }

    /** Abre una conexión nueva. El que la llama es responsable de cerrarla. */
    public static Connection obtenerConexion() throws SQLException {
        Properties config = leerFicheroConfiguracion();

        String url = leerDato("JDBC_URL", "jdbc.url", config);
        String usuario = leerDato("JDBC_USER", "jdbc.user", config);
        String clave = leerDato("JDBC_PASSWORD", "jdbc.password", config);

        if (url == null || usuario == null || clave == null) {
            throw new SQLException("Faltan datos de conexión. Define JDBC_URL, JDBC_USER y JDBC_PASSWORD "
                    + "o rellena el fichero " + FICHERO_CONFIG + ".");
        }

        return DriverManager.getConnection(url, usuario, clave);
    }

    /** Busca un dato primero en la variable de entorno y después en el fichero. */
    private static String leerDato(String variableEntorno, String claveFichero, Properties config) {
        String valor = System.getenv(variableEntorno);
        if (valor == null || valor.isBlank()) {
            valor = config.getProperty(claveFichero);
        }
        return valor;
    }

    /** Lee config/db.properties; si el fichero no existe devuelve unas propiedades vacías. */
    private static Properties leerFicheroConfiguracion() throws SQLException {
        Properties config = new Properties();
        Path ruta = Path.of(FICHERO_CONFIG);

        if (Files.isRegularFile(ruta)) {
            try (BufferedReader br = Files.newBufferedReader(ruta, StandardCharsets.UTF_8)) {
                config.load(br);
            } catch (IOException e) {
                throw new SQLException("No se pudo leer el fichero " + FICHERO_CONFIG + ".", e);
            }
        }
        return config;
    }

    /**
     * Traduce una excepción de conexión a un mensaje para el usuario.
     * Nunca incluye la contraseña ni la traza técnica.
     */
    public static String describirError(SQLException e) {
        String mensaje;

        if (e.getErrorCode() == 1045) {
            mensaje = "Usuario o contraseña de MySQL incorrectos.";
        } else if (e.getErrorCode() == 1049) {
            mensaje = "La base de datos 'datastock' no existe. Ejecuta el script DataStock_BD_MySQL.sql.";
        } else if ("08S01".equals(e.getSQLState()) || "08001".equals(e.getSQLState())) {
            mensaje = "No se puede conectar con el servidor MySQL. Comprueba que está encendido.";
        } else if (e.getMessage() != null && e.getMessage().startsWith("Faltan datos")) {
            mensaje = e.getMessage();
        } else {
            mensaje = "Error de base de datos (código " + e.getErrorCode() + ").";
        }
        return mensaje;
    }
}
