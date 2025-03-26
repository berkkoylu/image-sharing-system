package com.company.server;

import com.company.client.model.ImageDto;
import com.company.client.model.User;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    public static final Set<User> userSet = new HashSet<>();
    public static final Map<String, ImageDto> imageHashMap = new HashMap<>();

    public static PublicKey publicKeyServer;
    public static PrivateKey privateKeyServer;


    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {

        KeyPairGenerator keyPairGenerator =  KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        publicKeyServer = keyPair.getPublic();
        privateKeyServer = keyPair.getPrivate();

        System.out.println("Server is running");
        ExecutorService pool = Executors.newFixedThreadPool(500);
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            while (true) {
                pool.execute(new ServerHandler(serverSocket.accept()));
            }

        }
    }
}
