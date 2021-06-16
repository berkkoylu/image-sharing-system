package com.company;

import com.google.gson.Gson;

import javax.crypto.Cipher;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;

public class Test {
    /**
     Generate key file using openssl
     1、key pair
     openssl genrsa -out private_key.pem 2048
     2、public key
     openssl rsa -in private_key.pem -pubout -outform DER -out tst_public.der
     3、private key
     openssl pkcs8 -topk8 -inform PEM -outform DER -in private_key.pem -out private_key.der -nocrypt
     */
    public static byte[] toByteArray(Object obj) throws IOException {
        byte[] bytes = null;
        ByteArrayOutputStream bos = null;
        ObjectOutputStream oos = null;
        try {
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            bytes = bos.toByteArray();
        } finally {
            if (oos != null) {
                oos.close();
            }
            if (bos != null) {
                bos.close();
            }
        }
        return bytes;
    }

    public static Object toObject(byte[] bytes) throws IOException, ClassNotFoundException {
        Object obj = null;
        ByteArrayInputStream bis = null;
        ObjectInputStream ois = null;
        try {
            bis = new ByteArrayInputStream(bytes);
            ois = new ObjectInputStream(bis);
            obj = ois.readObject();
        } finally {
            if (bis != null) {
                bis.close();
            }
            if (ois != null) {
                ois.close();
            }
        }
        return obj;
    }

    public static void main(String[] args) throws Exception {
        try {


            MessageDigest md = MessageDigest.getInstance("SHA-256");

            KeyPairGenerator keyPairGeneratorw = KeyPairGenerator.getInstance("RSA");
            keyPairGeneratorw.initialize(512);
            KeyPair keyPair2 = keyPairGeneratorw.generateKeyPair();
            Key publicas = keyPair2.getPublic();

            KeyPairGenerator keyPairGenerator =  KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            Key publicKey = keyPair.getPublic();
            Key privateKey = keyPair.getPrivate();

            User user =  new User();
            user.setUserName("berk");
            user.setPublicKey(publicas);

            User userw =  new User();
            user.setUserName("berk");
            byte [] deneme2 = md.digest(toByteArray(userw));



            byte [] deneme = md.digest(toByteArray(user));

            Cipher privateEncryptCipher = Cipher
                    .getInstance("RSA/ECB/PKCS1Padding");
            privateEncryptCipher.init(Cipher.ENCRYPT_MODE, privateKey);

            byte[] encryptedFirstString = privateEncryptCipher.doFinal(deneme);


            Cipher publicDecryptCipher = Cipher
                    .getInstance("RSA/ECB/PKCS1Padding");
            publicDecryptCipher.init(Cipher.DECRYPT_MODE, publicKey);
            byte[] decryptedFirstStringByte = publicDecryptCipher
                    .doFinal(encryptedFirstString);


            System.out.println(Arrays.equals(deneme2, decryptedFirstStringByte));



//            // Let's encrypt with public and decrypt with private
//            // Encrypt with public key
//            String secondString = "Ekagra";
//
//            Cipher publicEncryptCipher = Cipher.getInstance("RSA");
//            publicEncryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
//            byte[] encryptedSecondString = publicEncryptCipher
//                    .doFinal(secondString.getBytes());
//            String encodedEncryptedSecondString = Base64.getEncoder()
//                    .encodeToString(encryptedSecondString);
//            System.out.println("Encoded encrypted String for Ekagra: "
//                    + encodedEncryptedSecondString);
//
//            // Decrypt with private key
//            byte[] decodedEncryptedSecondString = Base64.getDecoder().decode(
//                    encodedEncryptedSecondString.getBytes());
//            Cipher privateDecryptCipher = Cipher.getInstance("RSA");
//            privateDecryptCipher.init(Cipher.DECRYPT_MODE, privateKey);
//            byte[] decryptedSecondStringByte = privateDecryptCipher
//                    .doFinal(decodedEncryptedSecondString);
//            System.out.println("Decrypted String for Ekagra: "
//                    + new String(decryptedSecondStringByte));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
