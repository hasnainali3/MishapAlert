package com.example.bilalkhawaja.mishapalert;

/**
 * Created by Zahid on 10/26/17.
 */

public class NotificationModel {
    private String id;
    private String description;
    private String title;

    public NotificationModel() {
    }



    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
