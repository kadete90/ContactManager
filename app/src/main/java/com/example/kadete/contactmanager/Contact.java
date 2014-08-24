package com.example.kadete.contactmanager;

import android.net.Uri;

/**
 * Created by Kadete on 20/08/2014.
 */
public class Contact {

    private final Uri imgUri;
    private String name, phone, email, address;
    private int id;

    public Contact(int id, String name, String phone, String email, String address, Uri imgUri) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.imgUri = imgUri;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public Uri getImgUri() {
        return imgUri;
    }

    public int getId() {
        return id;
    }
}
