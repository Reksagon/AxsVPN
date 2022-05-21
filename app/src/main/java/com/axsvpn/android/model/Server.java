package com.axsvpn.android.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class Server {
    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("country_code")
    @Expose
    private String countryCode;

    @SerializedName("status")
    @Expose
    private int status;

    @SerializedName("premium_only")
    @Expose
    private int premiumOnly;

    @SerializedName("short_name")
    @Expose
    private String shortName;

    @SerializedName("nodes")
    @Expose
    private List<ServerNode> nodes;

    public Server(int id, String name, String countryCode, int status, int premiumOnly, String shortName, List<ServerNode> nodes) {
        this.id = id;
        this.name = name;
        this.countryCode = countryCode;
        this.status = status;
        this.premiumOnly = premiumOnly;
        this.shortName = shortName;
        this.nodes = nodes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getPremiumOnly() {
        return premiumOnly;
    }

    public void setPremiumOnly(int premiumOnly) {
        this.premiumOnly = premiumOnly;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public List<ServerNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<ServerNode> nodes) {
        this.nodes = nodes;
    }
}
