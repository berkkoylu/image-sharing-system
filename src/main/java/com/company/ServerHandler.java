package com.company;

import javax.crypto.*;
import java.io.*;
import java.net.Socket;
import java.security.*;
import java.util.Scanner;
import org.bouncycastle.operator.OperatorCreationException;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import static com.company.Server.*;

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
            user.setPublicKey(userPublicKey);
            String userName = (String) objectInputStream.readObject();
            user.setUserName(userName);
            UserCertificate userCertificate = new UserCertificate(userPublicKey,userName );
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte [] userByteArray = messageDigest.digest(CertificateCreateUtil.toByteArray(userCertificate));
            Cipher privateEncryptCipher = Cipher
                    .getInstance("RSA");
            privateEncryptCipher.init(Cipher.ENCRYPT_MODE, privateKeyServer);
            byte[] encryptedFirstString = privateEncryptCipher.doFinal(userByteArray);
            objectOutputStream.writeObject(encryptedFirstString);
            objectOutputStream.writeObject(publicKeyServer);



            while (true){

                System.out.println("deneme :" );
                Scanner scanner = new Scanner(System.in);
                String fileName = scanner.nextLine();
                objectOutputStream.writeObject(fileName);


                
            }



       } catch (IOException | ClassNotFoundException | NoSuchPaddingException | IllegalBlockSizeException | InvalidKeyException | NoSuchAlgorithmException  | BadPaddingException e) {
           e.printStackTrace();
       }
        }

}








