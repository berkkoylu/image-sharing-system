package com.company;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
            Scanner scanner = new Scanner(System.in);
            String welcomeMessage = (String) objectInputStream.readObject();
            System.out.println(welcomeMessage);
            System.out.print("Write Username to Register Server: ");
            objectOutputStream.writeObject(publicKey);
            String line = scanner.nextLine();
            objectOutputStream.writeObject(line);

            UserCertificate userCertificate =  new UserCertificate(publicKey,line);
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte [] userByteArray = messageDigest.digest(CertificateCreateUtil.toByteArray(userCertificate));

            byte[] encryptedFirstString = (byte[]) objectInputStream.readObject();
            Key serverPublicKeyClient = (Key) objectInputStream.readObject();

            Cipher publicDecryptCipher = Cipher
                    .getInstance("RSA");


            publicDecryptCipher.init(Cipher.DECRYPT_MODE, serverPublicKeyClient);
            byte[] decryptedFirstStringByte = publicDecryptCipher
                    .doFinal(encryptedFirstString);
            String projectPath = null;
            if( Arrays.equals( decryptedFirstStringByte,userByteArray ) ){
                System.out.println("Certificate Verified. Registered Successfully");
                projectPath = "/Users/berkkoylu/IdeaProjects/image-sharing-system/image/";
                projectPath += line + "-image-folder";
                Path path = Paths.get(projectPath);
                Files.createDirectories(path);
            }else{
                System.out.println("Certificate Denied");
            }

            new ClientReadThread( objectInputStream );



            while(true){

                System.out.print("Enter the name of the image file to Upload: ");
                String fileName = scanner.nextLine();
                byte [] imageByteArray = getFile(fileName,projectPath);












                if(line.equals("exit")){
                    break;
                }

            }

            System.out.println("Bye");
            // closing the scanner object
            scanner.close();
        }
        catch (IOException | ClassNotFoundException | NoSuchPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
    }

    public static byte[] getFile(String fileName, String projectPath) {

        fileName = fileName + projectPath;

        File f = new File(fileName);
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


}
