package com.company;

import javax.crypto.*;
import java.io.*;
import java.net.Socket;
import java.security.*;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

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

        int index = 0 ;
        User user = new User() ;
        ObjectInputStream objectInputStream = null;
        ObjectOutputStream objectOutputStream = null;
        String welcomeMessage = "Welcome to Image System Please Write Username and Send Public Key";

        try{
            System.out.println("Waiting for the client request");
            objectInputStream  = new ObjectInputStream(socket.getInputStream());
            objectOutputStream  = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(welcomeMessage);

            Key userPublicKey = (Key) objectInputStream.readObject();
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

                String imageMessage = (String) objectInputStream.readObject();



                if(imageMessage.equals("break")){
                    break;
                }

            }



       } catch (IOException | ClassNotFoundException | NoSuchPaddingException | IllegalBlockSizeException | InvalidKeyException | NoSuchAlgorithmException  | BadPaddingException e) {
           e.printStackTrace();
       }
        }

//        PrintWriter out = null;
//        BufferedReader in = null;
//        try {
//
//            // get the outputstream of client
//            out = new PrintWriter(
//                    socket.getOutputStream(), true);
//
//            // get the inputstream of client
//            in = new BufferedReader(
//                    new InputStreamReader(
//                            socket.getInputStream()));
//
//            String line;
//            while ((line = in.readLine()) != null) {
//
//                // writing the received message from
//                // client
//                System.out.printf(
//                        " Sent from the client: %s\n",
//                        line);
//                out.println(line);
//            }
//        }
//        catch (IOException e) {
//            e.printStackTrace();
//        }
//        finally {
//            try {
//                if (out != null) {
//                    out.close();
//                }
//                if (in != null) {
//                    in.close();
//                    socket.close();
//                }
//            }
//            catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }






