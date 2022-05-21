package com.axsvpn.android.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class BestLocation {
    @SerializedName("country_code")
    @Expose
    private String countryCode;

    @SerializedName("short_name")
    @Expose
    private String shortName;

    @SerializedName("location_name")
    @Expose
    private String locationName;

    @SerializedName("city_name")
    @Expose
    private String cityName;

    @SerializedName("dc_id")
    @Expose
    private int dcId;

    @SerializedName("server_id")
    @Expose
    private int serverId;

    @SerializedName("hostname")
    @Expose
    private String hostname;

    @SerializedName("ip")
    @Expose
    private String ip;

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getDcId() {
        return dcId;
    }

    public void setDcId(int dcId) {
        this.dcId = dcId;
    }

    public int getServerId() {
        return serverId;
    }

    public void setServer_id(int server_id) {
        this.serverId = server_id;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
