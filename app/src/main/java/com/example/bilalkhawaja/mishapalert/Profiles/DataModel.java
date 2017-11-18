package com.example.bilalkhawaja.mishapalert.Profiles;

/**
 * Created by Bilal Khawaja on 22/06/2017.
 */

public class DataModel {

    String id, name, time, description, profileImage, postImage, lat, lon, severity, postid, metadata;
    Double radius;

    public DataModel() {

    }

    public DataModel(String id, String name, String time, String description, String profileImage, String postImage, String lon, String lat, String severity, String postid, String metadata, Double radius) {
        this.id = id;
        this.name = name;
        this.time = time;
        this.description = description;
        this.profileImage = profileImage;
        this.postImage = postImage;
        this.lon = lon;
        this.lat = lat;
        this.severity = severity;
        this.postid = postid;
        this.metadata = metadata;
        this.radius = radius;
    }


    public String getMetadata() {
        return metadata;
    }

    public String getId() {
        return id;
    }

    public Double getRadius() {
        return radius;
    }

    public void setRadius(Double radius) {
        this.radius = radius;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {

        this.description = description;
    }

    public void setProfileImage(String profileImage) {

        this.profileImage = profileImage;
    }

    public String getProfileImage() {

        return profileImage;
    }

    public void setPostImage(String postImage) {

        this.postImage = postImage;
    }

    public String getPostImage() {

        return postImage;
    }

    public void setLat(String lat) {

        this.lat = lat;
    }

    public String getLat() {

        return lat;
    }

    public void setLon(String lon) {

        this.lon = lon;
    }

    public String getLon() {

        return lon;
    }

    public void setSeverity(String severity) {

        this.severity = severity;
    }

    public String getSeverity() {

        return severity;
    }

    public String getPostid() {
        return postid;
    }
}

