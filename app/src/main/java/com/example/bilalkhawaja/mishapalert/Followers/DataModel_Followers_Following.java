package com.example.bilalkhawaja.mishapalert.Followers;

/**
 * Created by Hasnain Ali on 8/2/2017.
 */

public class DataModel_Followers_Following {

    String id, name,picture;

    public DataModel_Followers_Following() {
    }

    public DataModel_Followers_Following(String id, String name, String picture) {
        this.id = id;
        this.name = name;
        this.picture = picture;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
