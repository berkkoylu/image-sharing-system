package com.company.client.model;

import javax.crypto.spec.IvParameterSpec;
import java.io.Serializable;

public class ImageDto implements Serializable {

    private String imageName;
    private byte [] encryptedImage;
    private byte [] digitalSignature ;
    private byte [] encryptedAESKey;
    private  byte [] ivParameterSpec;

    public ImageDto(String imageName,byte[] encryptedImage, byte[] digitalSignature, byte[] encryptedAESKey,  byte [] ivParameterSpec) {
       this.imageName = imageName;
        this.encryptedImage = encryptedImage;
        this.digitalSignature = digitalSignature;
        this.encryptedAESKey = encryptedAESKey;
        this.ivParameterSpec = ivParameterSpec;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public byte[] getEncryptedImage() {
        return encryptedImage;
    }

    public void setEncryptedImage(byte[] encryptedImage) {
        this.encryptedImage = encryptedImage;
    }

    public byte[] getDigitalSignature() {
        return digitalSignature;
    }

    public void setDigitalSignature(byte[] digitalSignature) {
        this.digitalSignature = digitalSignature;
    }

    public byte[] getEncryptedAESKey() {
        return encryptedAESKey;
    }

    public void setEncryptedAESKey(byte[] encryptedAESKey) {
        this.encryptedAESKey = encryptedAESKey;
    }

    public  byte [] getIvParameterSpec() {
        return ivParameterSpec;
    }

    public void setIvParameterSpec( byte [] ivParameterSpec) {
        this.ivParameterSpec = ivParameterSpec;
    }
}
