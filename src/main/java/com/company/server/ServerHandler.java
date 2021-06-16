package com.company.server;

import javax.crypto.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.security.*;
import java.util.Scanner;

import com.company.client.CertificateCreateUtil;
import com.company.client.model.ImageDto;
import com.company.client.model.User;
import com.company.client.model.UserCertificate;

import static com.company.server.Server.*;

public class ServerHandler implements Runnable{


    private String name;
    private Socket socket;
    private Scanner in;
    private PrintWriter out;


    public ServerHandler(Socket socket) {
        this.socket = socket;
    }

    public void run() {

        User user = new User() ;
        ObjectInputStream objectInputStream = null;
        ObjectOutputStream objectOutputStream = null;
        String welcomeMessage = "Welcome to Image System Please Write Username and Send Public Key";

        try{
            System.out.println("Waiting for the client request");
            objectInputStream  = new ObjectInputStream(socket.getInputStream());
            objectOutputStream  = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(welcomeMessage);

            PublicKey userPublicKey = (PublicKey) objectInputStream.readObject();
            String userName = (String) objectInputStream.readObject();

            UserCertificate userCertificate = new UserCertificate(userPublicKey,userName );
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte [] userByteArray = messageDigest.digest(CertificateCreateUtil.toByteArray(userCertificate));
            Cipher privateEncryptCipher = Cipher
                    .getInstance("RSA");
            privateEncryptCipher.init(Cipher.ENCRYPT_MODE, privateKeyServer);
            byte[] encryptedFirstString = privateEncryptCipher.doFinal(userByteArray);

            objectOutputStream.writeObject(encryptedFirstString);
            objectOutputStream.writeObject(publicKeyServer);


            user.setPublicKey(userPublicKey);
            user.setUserName(userName);
            user.setObjectInputStream(objectInputStream);
            user.setObjectOutputStream(objectOutputStream);
            user.setUserCertificate(encryptedFirstString);
            userSet.add(user);


            while (true){
                    ImageDto imageDto = (ImageDto) objectInputStream.readObject();
                    imageHashSet.put(userName, imageDto);

                for (User userM :
                        userSet) {
                    ObjectOutputStream objectOutputStream1 = userM.getObjectOutputStream();
                    objectOutputStream1.writeObject("Mesaj geldi");
                }
            }


       } catch (IOException | ClassNotFoundException | NoSuchPaddingException | IllegalBlockSizeException | InvalidKeyException | NoSuchAlgorithmException  | BadPaddingException e) {
           e.printStackTrace();
       }
        }

}








