package com.company.server;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.Socket;
import java.security.*;
import java.util.Map;
import java.util.Scanner;

import com.company.client.CertificateCreateUtil;
import com.company.client.model.ImageDto;
import com.company.client.model.User;
import com.company.client.model.UserCertificate;
import com.company.crypto.AES;

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
            System.out.println("User " + user.getUserName() + " registered successfully");


            while (true){

                String requestHeader = (String ) objectInputStream.readObject();

                if(requestHeader.equals("post_image")){

                    ImageDto imageDto = (ImageDto) objectInputStream.readObject();
                    imageDto.setPublicKey(userPublicKey);
                    imageHashMap.put(userName, imageDto);

                    System.out.println("Image " + imageDto.getImageName() + " posted by " + user.getUserName());
                    if(userSet.size() > 1){
                        broadcastNewImageMessage(user.getUserName(),imageDto.getImageName());
                    }

                }else if (requestHeader.equals("down_image")){

                    String imageName = (String) objectInputStream.readObject();


                    for (Map.Entry<String,ImageDto> entry : imageHashMap.entrySet()) {
                        if(entry.getValue().getImageName().equals(imageName)){

                            ImageDto imageDtoSend = new ImageDto();
                            ImageDto imageDto = entry.getValue();


                            Cipher cipher = null;
                            SecretKey key = null;
                            try{

                                cipher = Cipher.getInstance("RSA");
                                cipher.init(Cipher.DECRYPT_MODE, privateKeyServer );
                                key = new SecretKeySpec(cipher.doFinal(imageDto.getEncryptedAESKey()),"AES");
                                byte [] encryptedAesUserKey = encryptAesKey(key, userPublicKey);
                                imageDtoSend.setDigitalSignature(imageDto.getDigitalSignature());
                                imageDtoSend.setEncryptedAESKey(encryptedAesUserKey);
                                imageDtoSend.setEncryptedImage(imageDto.getEncryptedImage());
                                imageDtoSend.setImageName(imageDto.getImageName());
                                imageDtoSend.setIvParameterSpec(imageDto.getIvParameterSpec());
                                imageDtoSend.setPublicKey(imageDto.getPublicKey());
                                System.out.println("Image " + imageDto.getImageName() + " downloaded by " + user.getUserName());


                            }catch(Exception e){
                                e.printStackTrace();
                            }

                            objectOutputStream.writeObject(imageDtoSend);
                        }

                    }




                }





            }


       } catch (IOException | ClassNotFoundException | NoSuchPaddingException | IllegalBlockSizeException | InvalidKeyException | NoSuchAlgorithmException  | BadPaddingException e) {
           e.printStackTrace();
       }
        }
        
        private void broadcastNewImageMessage(String username, String imageName) throws IOException {

        String message = "NEW_IMAGE " + imageName + " " + username;
        System.out.println("Posted image " + imageName + " notification broadcast online users");
        for (User userM :
                    userSet) {
               if(!userM.getUserName().equals(username)){
                   ObjectOutputStream objectOutputStream1 = userM.getObjectOutputStream();
                   objectOutputStream1.writeObject(message);
               }
            }
        
        
        
        
        
        
        }

    public static byte[]  encryptAesKey(SecretKey secretKey, Key publicKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        Cipher pipher = Cipher.getInstance("RSA");
        pipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] kryptoKey = pipher.doFinal(secretKey.getEncoded());

        return kryptoKey;
    }

}








