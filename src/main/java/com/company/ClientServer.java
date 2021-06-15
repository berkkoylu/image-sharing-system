package com.company;

import java.io.*;
import java.net.Socket;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class ClientServer {

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {

        KeyPairGenerator keyPairGenerator =  KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(1024);
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

            while(true){


                if(line.equals("exit")){
                    break;
                }

            }

            System.out.println("Bye");
            // closing the scanner object
            sc.close();
        }
        catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


}
