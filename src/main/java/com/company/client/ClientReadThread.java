package com.company.client;

import com.company.client.model.ImageDto;
import com.company.crypto.AES;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.security.*;
import java.util.Arrays;
import java.util.Scanner;

public class ClientReadThread extends Thread{

   private Socket socket;
   private Scanner scanner;
   private ObjectInputStream objectInputStream ;
   private String userName;
   private Key privateKey;

    public ClientReadThread( ObjectInputStream objectInputStream,Socket socket, Scanner scanner, String userName, Key privateKey){
        this.scanner = scanner;
        this.socket = socket;
        this.objectInputStream = objectInputStream;
        this.userName = userName;
        this.privateKey = privateKey;
    }

    public void run() {

        String text;

        do {
            try {
                Object object = objectInputStream.readObject();

                if(object instanceof String){
                    System.out.println();
                    System.out.println((String)object);

                    System.out.print("\nEnter enter your command: ");


                }else if (object instanceof ImageDto){

                    ImageDto imageDto = (ImageDto) object;
                    SecretKey secretKey = decryptAESKey(imageDto.getEncryptedAESKey(),(PrivateKey) privateKey );
                    AES aes = new AES();
                    byte [] decryptedImage = aes.decryptPdfFile(secretKey, imageDto.getEncryptedImage(), imageDto.getIvParameterSpec());


                    Cipher cipher = Cipher.getInstance("RSA");
                    cipher.init(Cipher.DECRYPT_MODE, imageDto.getPublicKey());
                    byte [] decSignature = cipher.doFinal(imageDto.getDigitalSignature());

                    MessageDigest messageDigest =  MessageDigest.getInstance("SHA-256");
                    byte [] imageDigest = messageDigest.digest(decryptedImage);

                    if(Arrays.equals(decSignature, imageDigest)){
                        saveFile(decryptedImage,imageDto.getImageName(), userName);
                        System.out.println("Image " +  (imageDto.getImageName() + " downloaded from server."));
                        System.out.print("\nEnter enter your command: ");

                    }

                }

            } catch (IOException | ClassNotFoundException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            }
        } while (true);


    }

    private SecretKey decryptAESKey(byte[] data , PrivateKey privKey)
    {
        SecretKey key = null;
        Cipher cipher = null;
        try
        {
            cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privKey );


            key = new SecretKeySpec( cipher.doFinal(data), "AES" );
        }
        catch(Exception e)
        {
            System.out.println ( "exception decrypting the aes key: "
                    + e.getMessage() );
            return null;
        }

        return key;
    }

    public static void saveFile(byte[] bytes, String nameOfTheImage, String userName) throws IOException {

        String location = "image/"+userName + "-image-folder/" + nameOfTheImage ;
        FileOutputStream fileOutputStream = new FileOutputStream(location);
        fileOutputStream.write(bytes);
        fileOutputStream.close();
    }






}
