package com.example.bilalkhawaja.mishapalert.Notification;

/**
 * Created by Hasnain Ali on 8/14/2017.
 */

public class Notification_datamodel {
    String userid, postid, title, description, image, type;

    public Notification_datamodel() {
    }

    public Notification_datamodel(String userid, String postid, String title, String description, String image, String type) {
        this.userid = userid;
        this.postid = postid;
        this.title = title;
        this.description = description;
        this.image = image;
        this.type = type;
    }

    public String getUserid() {
        return userid;
    }

    public String getPostid() {
        return postid;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() { return image; }

    public String getType() {
        return type;
    }
}
