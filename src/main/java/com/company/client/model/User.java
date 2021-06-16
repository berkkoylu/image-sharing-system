package com.company.client;

import java.io.*;
import java.security.Key;


public class User implements Serializable {

    private String userName;
    private Key publicKey;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private UserCertificate userCertificate;

    public User() {

    }

    public User(String userName, Key publicKey, ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream, UserCertificate userCertificate) {
        this.userName = userName;
        this.publicKey = publicKey;
        this.objectInputStream = objectInputStream;
        this.objectOutputStream = objectOutputStream;
        this.userCertificate = userCertificate;
    }

    public UserCertificate getUserCertificate() {
        return userCertificate;
    }

    public void setUserCertificate(UserCertificate userCertificate) {
        this.userCertificate = userCertificate;
    }



    public ObjectInputStream getObjectInputStream() {
        return objectInputStream;
    }

    public void setObjectInputStream(ObjectInputStream objectInputStream) {
        this.objectInputStream = objectInputStream;
    }

    public ObjectOutputStream getObjectOutputStream() {
        return objectOutputStream;
    }

    public void setObjectOutputStream(ObjectOutputStream objectOutputStream) {
        this.objectOutputStream = objectOutputStream;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Key getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(Key publicKey) {
        this.publicKey = publicKey;
    }


    @Override
    public boolean equals(Object other) {
        if (!(other instanceof User)) {
            return false;
        }
        return userName.equals(((User)other).userName);
    }

    @Override
    public int hashCode() {
        return userName.hashCode();
    }

}
