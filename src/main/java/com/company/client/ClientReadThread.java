package com.company.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class ClientReadThread extends Thread{

    Socket socket;
    Scanner scanner;
    ObjectInputStream objectInputStream ;



    public ClientReadThread( ObjectInputStream objectInputStream,Socket socket, Scanner scanner){
        this.scanner = scanner;
        this.socket = socket;
        this.objectInputStream = objectInputStream;

    }

    public void run() {

        String text;

        do {
            try {
                text = (String) objectInputStream.readObject();
                System.out.println();
                System.out.println(text);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        } while (true);


    }








}
