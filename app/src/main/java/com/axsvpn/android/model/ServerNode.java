package com.axsvpn.android.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class ServerNode {
    @SerializedName("ip")
    @Expose
    private String ip;

    @SerializedName("ip2")
    @Expose
    private String ip2;

    @SerializedName("ip3")
    @Expose
    private String ip3;

    @SerializedName("hostname")
    @Expose
    private String hostname;

    public ServerNode(String ip, String ip2, String ip3, String hostname) {
        this.ip = ip;
        this.ip2 = ip2;
        this.ip3 = ip3;
        this.hostname = hostname;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getIp2() {
        return ip2;
    }

    public void setIp2(String ip2) {
        this.ip2 = ip2;
    }

    public String getIp3() {
        return ip3;
    }

    public void setIp3(String ip3) {
        this.ip3 = ip3;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }
}
