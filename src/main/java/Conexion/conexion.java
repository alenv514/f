package Conexion;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class conexion {

    private static final Properties CONFIGURACION = cargarConfiguracion();

    private conexion() {
    }

    private static Properties cargarConfiguracion() {
        Properties propiedades = new Properties();
        try (InputStream entrada = conexion.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (entrada != null) {
                propiedades.load(entrada);
            }
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer el archivo db.properties", e);
        }
        return propiedades;
    }

    private static String valor(String variableEntorno, String clave, String porDefecto) {
        String desdeEntorno = System.getenv(variableEntorno);
        if (desdeEntorno != null && !desdeEntorno.isEmpty()) {
            return desdeEntorno;
        }
        return CONFIGURACION.getProperty(clave, porDefecto);
    }

    public static Connection obtener() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("No se encontró el controlador de MySQL", e);
        }
        String url = valor("BODEGA_DB_URL", "db.url", "jdbc:mysql://localhost:3306/bodega_db");
        String usuario = valor("BODEGA_DB_USUARIO", "db.usuario", "root");
        String clave = valor("BODEGA_DB_CLAVE", "db.clave", "");
        return DriverManager.getConnection(url, usuario, clave);
    }
}
