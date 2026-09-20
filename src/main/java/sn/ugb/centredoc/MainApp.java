package sn.ugb.centredoc;

import sn.ugb.centredoc.dao.ConnexionBD;
import java.sql.Connection;

public class MainApp {
    public static void main(String[] args) {
        try {
            Connection conn = ConnexionBD.getConnection();
            System.out.println("Connexion reussie a la base centre_doc !");
            conn.close();
        } catch (Exception e) {
            System.out.println("Erreur de connexion : " + e.getMessage());
        }
    }
}