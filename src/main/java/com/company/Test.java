package com.company;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

import javax.crypto.Cipher;

import com.google.gson.Gson;

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

            KeyPairGenerator keyPairGeneratorw = KeyPairGenerator.getInstance("RSA");
            keyPairGeneratorw.initialize(512);
            KeyPair keyPair2 = keyPairGeneratorw.generateKeyPair();
            Key publicas = keyPair2.getPublic();

            KeyPairGenerator keyPairGenerator =  KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(4096);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            Key publicKey = keyPair.getPublic();
            Key privateKey = keyPair.getPrivate();


            User user =  new User();



            Gson gson =  new Gson();

            String deneme = gson.toJson(user);

            Cipher privateEncryptCipher = Cipher
                    .getInstance("RSA/ECB/PKCS1Padding");
            privateEncryptCipher.init(Cipher.ENCRYPT_MODE, privateKey);

            byte[] encryptedFirstString = privateEncryptCipher.doFinal(deneme.getBytes(StandardCharsets.UTF_8));


            Cipher publicDecryptCipher = Cipher
                    .getInstance("RSA/ECB/PKCS1Padding");
            publicDecryptCipher.init(Cipher.DECRYPT_MODE, publicKey);
            byte[] decryptedFirstStringByte = publicDecryptCipher
                    .doFinal(encryptedFirstString);

            User car = gson.fromJson(decryptedFirstStringByte.toString(),User.class);


            System.out.println(car.getUserName());



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
