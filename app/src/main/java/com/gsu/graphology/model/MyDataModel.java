package com.gsu.graphology.model;


import com.google.gson.annotations.SerializedName;

public class MyDataModel {

    @SerializedName("Email Address")
    private String email;

    @SerializedName("UniqueID")
    private String uniqueID;


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
    }
}