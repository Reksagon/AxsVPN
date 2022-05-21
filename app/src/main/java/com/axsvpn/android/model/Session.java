package com.axsvpn.android.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Session {
    @SerializedName("session_auth_hash")
    @Expose
    private String sessionAuthHash;

    @SerializedName("traffic_used")
    @Expose
    private long trafficUsed;

    @SerializedName("loc_hash")
    @Expose
    private String locHash;

    public String getSessionAuthHash() {
        return sessionAuthHash;
    }

    public void setSessionAuthHash(String sessionAuthHash) {
        this.sessionAuthHash = sessionAuthHash;
    }

    public long getTrafficUsed() {
        return trafficUsed;
    }

    public void setTrafficUsed(long trafficUsed) {
        this.trafficUsed = trafficUsed;
    }

    public String getLocHash() {
        return locHash;
    }

    public void setLocHash(String locHash) {
        this.locHash = locHash;
    }
}
