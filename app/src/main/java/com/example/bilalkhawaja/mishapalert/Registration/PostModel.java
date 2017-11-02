package com.example.bilalkhawaja.mishapalert.Registration;

/**
 * Created by Zahid on 10/26/17.
 */

public class PostModel {

    private String dateTime;
    private String description;
    private String lat;
    private String lon;
    private String metadata;
    private String posturi;
    private String severity;
    private String type;

    public PostModel() {
    }


    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public String getPosturi() {
        return posturi;
    }

    public void setPosturi(String posturi) {
        this.posturi = posturi;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
