package com.example.bilalkhawaja.mishapalert.Registration;

import com.example.bilalkhawaja.mishapalert.NotificationModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Bilal Khawaja on 4/22/2017.
 */
public class User {

    private String key;
    private String FCM_TOKEN;
   // private List<NotificationModel> Notification;
    private String Password;
    private String city;
    private String email;
    private String id;
    private String latitude;
    private String longitude;
    private String name;
    private ArrayList<PostModel> posts;
    private String radius;
    private String uri;
    private String username;


    public User() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getFCM_TOKEN() {
        return FCM_TOKEN;
    }


  /*  public List<NotificationModel> getNotification() {
        return Notification;
    }


    public void setNotification(List<NotificationModel> notification) {
        Notification = notification;
    }*/

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<PostModel> getPosts() {
        return posts;
    }

    public void setPosts(ArrayList<PostModel> posts) {
        this.posts = posts;
    }

    public String getRadius() {
        return radius;
    }

    public void setRadius(String radius) {
        this.radius = radius;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
