package com.axsvpn.android.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class BestLocationResponse {
    @SerializedName("data")
    @Expose
    private BestLocation data;

    @SerializedName("result")
    @Expose
    private String result;

    @SerializedName("message")
    @Expose
    private String message;

    public BestLocation getData() {
        return data;
    }

    public void setData(BestLocation data) {
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
