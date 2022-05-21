package com.axsvpn.android.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class SessionResponse {
    @SerializedName("data")
    @Expose
    private Session data;

    @SerializedName("result")
    @Expose
    private String result;

    @SerializedName("message")
    @Expose
    private String message;

    public Session getData() {
        return data;
    }

    public void setData(Session data) {
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
