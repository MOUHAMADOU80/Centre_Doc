package sn.ugb.centredoc.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnexionBD {
    private static final Properties PROPS = new Properties();

    static {
        try (InputStream input = ConnexionBD.class.getClassLoader()
                .getResourceAsStream("db.properties")) {
            if (input == null) {
                throw new RuntimeException("Fichier db.properties introuvable dans resources");
            }
            PROPS.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Erreur de lecture de db.properties", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        String url = PROPS.getProperty("db.url");
        String user = PROPS.getProperty("db.user");
        String password = PROPS.getProperty("db.password");
        return DriverManager.getConnection(url, user, password);
    }
}