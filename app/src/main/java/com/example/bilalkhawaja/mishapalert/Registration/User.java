package com.example.bilalkhawaja.mishapalert.Registration;

import android.net.Uri;

/**
 * Created by Bilal Khawaja on 4/22/2017.
 */
public class User {

String id , name , username , email, city;

    public User(){}

    public User(String id , String name , String email , String username, String city){
        this.id = id ;
        this.name = name;
        this.email = email;
        this.username = username;
        this.city = city;

    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getCity() {return city;}

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCity(String city) {this.city = city; }
}
