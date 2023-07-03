/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

/**
 * Clase que porporciona el hasch code de una cadena con metodo sha512, en nuestro caso la contraseña
 * @author antoniodavid
 */
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;


public class SHA {

    private static String bytesToHex(byte[] bytes){
            StringBuilder sb = new StringBuilder();
            
            for (byte b : bytes) 
                sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
            
            return sb.toString();
    }

    

    public static String generate512(String passwordToHash){
        String generatedPassword = null;

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");

            //md.update(salt);

            byte[] byteOfTextToHash = passwordToHash.getBytes(StandardCharsets.UTF_8);
            byte[] hashedByteArray  = md.digest(byteOfTextToHash);

            generatedPassword = bytesToHex(hashedByteArray);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }

    

    private static byte[] getSalt() {

        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        
        return salt;

    }
    
    public static void main(String[] args) {
        System.out.println(generate512("nano"));
    }
}

