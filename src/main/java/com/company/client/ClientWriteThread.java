package com.company.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class ClientReadThread extends Thread {

    Socket socket;
    Scanner scanner;
    ObjectOutputStream objectOutputStream ;



    public ClientReadThread(   ObjectOutputStream objectOutputStream,Socket socket, Scanner scanner){
        this.scanner = scanner;
        this.socket = socket;
        this.objectOutputStream = objectOutputStream;

    }

    public void run() {

        System.out.print("\nEnter your name: ");

        String text;

        do {
            text = scanner.nextLine();

            try {
                objectOutputStream.writeObject(text);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } while (!text.equals("bye"));


    }




}
