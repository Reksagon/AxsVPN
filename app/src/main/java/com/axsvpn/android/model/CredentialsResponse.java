package com.axsvpn.android.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class CredentialsResponse {
    @SerializedName("data")
    @Expose
    private Credentials data;

    @SerializedName("result")
    @Expose
    private String result;

    @SerializedName("message")
    @Expose
    private String message;

    public Credentials getData() {
        return data;
    }

    public void setData(Credentials data) {
        this.data = data;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
