package org.acme;

import java.util.Base64;

public class HandImage {

    private String image;

    public HandImage() {
    }

    public HandImage(String image) {
        this.image = image;
    }

    public byte[] getImage() {
        return Base64.getDecoder().decode(image);
    }
}