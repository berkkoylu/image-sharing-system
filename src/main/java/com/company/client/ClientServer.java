package com.company.client;

import com.company.client.model.UserCertificate;

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
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Scanner;

public class ClientServer  {

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        
        KeyPairGenerator keyPairGenerator =  KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        Key privateKey = keyPair.getPrivate();

        Socket socket = new Socket("localhost", 8080);
        try  {


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


            new ClientWriteThread(objectOutputStream,socket, scanner, line, privateKey, (PublicKey) serverPublicKeyClient).start();
            new ClientReadThread(objectInputStream,socket,scanner).start();



        }
        catch (IOException | ClassNotFoundException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }


    }




}
