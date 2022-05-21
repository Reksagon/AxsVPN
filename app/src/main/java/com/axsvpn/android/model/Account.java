package com.axsvpn.android.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Account {
    @SerializedName("session_auth_hash")
    @Expose
    private String sessionAuthHash;

    @SerializedName("is_premium")
    @Expose
    private boolean isPremium;

    @SerializedName("product_name")
    @Expose
    private String productName;

    @SerializedName("loc_hash")
    @Expose
    private String locHash;

    @SerializedName("traffic_used")
    @Expose
    private long trafficUsed;

    public String getSessionAuthHash() {
        return sessionAuthHash;
    }

    public void setSessionAuthHash(String sessionAuthHash) {
        this.sessionAuthHash = sessionAuthHash;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public void setPremium(boolean premium) {
        isPremium = premium;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getLocHash() {
        return locHash;
    }

    public void setLocHash(String locHash) {
        this.locHash = locHash;
    }

    public long getTrafficUsed() {
        return trafficUsed;
    }

    public void setTrafficUsed(long trafficUsed) {
        this.trafficUsed = trafficUsed;
    }
}
