package com.company;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.net.Socket;
import java.security.*;
import java.util.Arrays;
import java.util.Scanner;

public class ClientServer  {

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        
        KeyPairGenerator keyPairGenerator =  KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        Key publicKey = keyPair.getPublic();
        Key privateKey = keyPair.getPrivate();


        try (Socket socket = new Socket("localhost", 8080)) {

            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            Scanner sc = new Scanner(System.in);
            String welcomeMessage = (String) objectInputStream.readObject();
            System.out.println(welcomeMessage);
            System.out.println("Write Username: ");
            objectOutputStream.writeObject(publicKey);
            String line = sc.nextLine();
            objectOutputStream.writeObject(line);

            UserCertificate userCertificate =  new UserCertificate(publicKey,"adad");
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte [] userByteArray = messageDigest.digest(CertificateCreateUtil.toByteArray(userCertificate));



            byte[] encryptedFirstString = (byte[]) objectInputStream.readObject();
            Key serverPublicKeyClient = (Key) objectInputStream.readObject();


                    Cipher publicDecryptCipher = Cipher
                    .getInstance("RSA");

            publicDecryptCipher.init(Cipher.DECRYPT_MODE, serverPublicKeyClient );
            byte[] decryptedFirstStringByte = publicDecryptCipher
                    .doFinal(encryptedFirstString);




            System.out.println(Arrays.equals(decryptedFirstStringByte,userByteArray));

            while(true){


                if(line.equals("exit")){
                    break;
                }

            }

            System.out.println("Bye");
            // closing the scanner object
            sc.close();
        }
        catch (IOException | ClassNotFoundException | NoSuchPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
    }


}
