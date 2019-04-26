package com.mobitechstudio.wise.quotes.models.marker;

public class MarkerModel {
    int id;
    String details;

    public MarkerModel(int id, String details) {
        this.id = id;
        this.details = details;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}