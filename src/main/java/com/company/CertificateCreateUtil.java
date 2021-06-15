package com.company;

import javax.crypto.*;
import java.io.IOException;
import java.io.Serializable;
import java.security.*;
import java.util.Base64;

public class CertificateCreateUtil {


    public static SealedObject encryptObject(Serializable object,
                                       PrivateKey privateKey) throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidKeyException, IOException, IllegalBlockSizeException, SignatureException {

        Cipher encryptCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        encryptCipher.init(Cipher.ENCRYPT_MODE, privateKey);
        SealedObject sealedObject = new SealedObject(object, encryptCipher);
        return sealedObject;

    }
    public static Serializable decryptObject( SealedObject sealedObject,
                                             PublicKey publicKey) throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException,
            ClassNotFoundException, BadPaddingException, IllegalBlockSizeException,
            IOException {

        Cipher encryptCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        encryptCipher.init(Cipher.DECRYPT_MODE, publicKey);
        Serializable unsealObject = (Serializable) sealedObject.getObject(encryptCipher);
        return unsealObject;
    }


}
