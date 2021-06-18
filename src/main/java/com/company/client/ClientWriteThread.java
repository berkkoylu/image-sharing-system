package com.company.client;

import com.company.client.model.ImageDto;
import com.company.crypto.AES;
import com.company.server.Server;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.Socket;
import java.security.*;
import java.util.Scanner;

public class ClientWriteThread extends Thread {

   private Socket socket;
   private Scanner scanner;
   private ObjectOutputStream objectOutputStream ;
   private String userName;
   private Key privateKey;
   public  PublicKey publicKeyServer;





    public ClientWriteThread(ObjectOutputStream objectOutputStream, Socket socket, Scanner scanner, String userName , Key privateKey, PublicKey publicKeyServer){
        this.scanner = scanner;
        this.socket = socket;
        this.objectOutputStream = objectOutputStream;
        this.userName = userName;
        this.privateKey = privateKey;
        this.publicKeyServer = publicKeyServer;

    }

    public void run() {
        AES aes =  new AES();
        IvParameterSpec ivParameterSpec = aes.generateIv();
        SecureRandom rand = new SecureRandom();
        KeyGenerator aesKey = null;
        try {
            aesKey = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        aesKey.init(256, rand);
        SecretKey secretKey = aesKey.generateKey();



        System.out.println("For posting image enter post_image");
        System.out.println("For downloading image enter down_image");
        System.out.print("Enter enter your command: ");


        String text;

        do {

            text = scanner.nextLine();
            String[] subStringArray = text.split(" ");
            text = subStringArray[0];
            String imageName = subStringArray[1];

            byte[] digitalSignature =null;
            byte[] encryptedAESKey = null;

            if(text.equals("post_image")){


                byte [] imageFile = getFile(imageName, "image/" + userName +"-image-folder/");
                byte [] encryptedImage = aes.encryptPdfFile(secretKey,imageFile,ivParameterSpec);
                MessageDigest  messageDigest = null;
                Cipher cipher = null;

            try {
                messageDigest = MessageDigest.getInstance("SHA-256");
                byte[] imageDigest = messageDigest.digest(imageFile);
                cipher = Cipher.getInstance("RSA");
                cipher.init(Cipher.ENCRYPT_MODE, privateKey);
                digitalSignature = cipher.doFinal(imageDigest);


                encryptedAESKey = encryptAesKey(secretKey, publicKeyServer);


            } catch (NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | BadPaddingException | InvalidKeyException e) {
                e.printStackTrace();
            }

                ImageDto imageDto = null;


                imageDto = new ImageDto(imageName,encryptedImage,digitalSignature,encryptedAESKey, ivParameterSpec.getIV());


                try {
                    objectOutputStream.writeObject("post_image");
                    objectOutputStream.writeObject(imageDto);
                    System.out.println("Image " + imageName + " posted to server.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else if (text.equals("down_image")){

                try {
                    objectOutputStream.writeObject("down_image");
                    objectOutputStream.writeObject(imageName);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                continue;
            }


            System.out.print("Enter enter your command: ");


        } while (!text.equals("bye"));


    }

    public static byte[] getFile(String fileName, String projectPath) {

        projectPath = projectPath + fileName;

        File f = new File(projectPath);
        InputStream is = null;
        try {
            is = new FileInputStream(f);
        } catch (FileNotFoundException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }
        byte[] content = null;
        try {
            content = new byte[is.available()];
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            is.read(content);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return content;
    }

    private SecretKey decryptAESKey(byte[] data , PrivateKey privKey)
    {
        SecretKey key = null;
        Cipher cipher = null;
        try
        {
            cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privKey );


            key = new SecretKeySpec( cipher.doFinal(data), "AES" );
        }
        catch(Exception e)
        {
            System.out.println ( "exception decrypting the aes key: "
                    + e.getMessage() );
            return null;
        }

        return key;
    }

    public static byte[]  encryptAesKey(SecretKey secretKey, Key publicKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        Cipher pipher = Cipher.getInstance("RSA");
        pipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] kryptoKey = pipher.doFinal(secretKey.getEncoded());

        return kryptoKey;
    }
}
