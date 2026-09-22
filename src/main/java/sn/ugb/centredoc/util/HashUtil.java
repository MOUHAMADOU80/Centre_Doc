package sn.ugb.centredoc.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class HashUtil {

    public static String hacher(String motDePasse) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(motDePasse.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Algorithme de hachage indisponible", e);
        }
    }

    public static boolean verifier(String motDePasseClair, String motDePasseHache) {
        String hashCalcule = hacher(motDePasseClair);
        return hashCalcule.equals(motDePasseHache);
    }
}