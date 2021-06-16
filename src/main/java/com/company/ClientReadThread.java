package com.company;

import java.io.IOException;
import java.io.ObjectInputStream;

public class ClientReadThread  implements Runnable{

    private final ObjectInputStream objectInputStream ;

    public ClientReadThread(ObjectInputStream objectInputStream) {

        this.objectInputStream = objectInputStream;
    }

    @Override
    public void run() {

        while (true){

            String serverMessage = null;

            try {
                serverMessage = (String) objectInputStream.readObject();
                System.out.println(serverMessage);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }


        }




    }
}
