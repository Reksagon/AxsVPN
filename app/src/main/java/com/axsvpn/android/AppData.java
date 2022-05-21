package com.axsvpn.android;

import com.axsvpn.android.model.BestLocation;
import com.axsvpn.android.model.Server;

import java.util.List;

public class AppData {
    public static final String baseURL = "https://axsvpn.com/api/v1.0/";
    public static final String loginURL = baseURL + "login";
    public static final String bestLocationURL = baseURL + "bestlocation";
    public static final String serverListURL = baseURL + "serverlist";
    public static final String sessionURL = baseURL + "session";
    public static final String serverCredentialsURL = baseURL + "servercredentials";
    public static final String serverConfigsURL = baseURL + "serverconfigs";
    public static final String ipLocationURL = baseURL + "ip";
    public static final String registerURL = baseURL + "register";

    public static final String CHANNEL_ID = "axsvpn_channel";
    public static final long MAX_FREE_TRAFFIC = 2 * 1024 * 1024 * 1024L;

    public static String email;
    public static String password;
    public static boolean isPremium;
    public static String productName;
    public static String sessionAuthHash;
    public static long trafficUsed;
    public static String locHash;
    public static BestLocation bestLocation;
    public static List<Server> servers;
    public static Server selectedServer;
    public static Server temporaryServer;
    public static String vpnUsername;
    public static String vpnPassword;
    public static String config;
}