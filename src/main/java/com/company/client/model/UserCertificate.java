package com.company.client.model;

import java.io.Serializable;
import java.security.Key;

public class UserCertificate implements Serializable {

    Key userPublicKey ;
    String name ;

    public UserCertificate(Key userPublicKey, String name) {
        this.userPublicKey = userPublicKey;
        this.name = name;
    }

    public Key getUserPublicKey() {
        return userPublicKey;
    }

    public void setUserPublicKey(Key userPublicKey) {
        this.userPublicKey = userPublicKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
