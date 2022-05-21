package com.axsvpn.android.api;

import com.axsvpn.android.AppData;
import com.axsvpn.android.model.AccountResponse;
import com.axsvpn.android.model.BestLocationResponse;
import com.axsvpn.android.model.ConfigsResponse;
import com.axsvpn.android.model.CredentialsResponse;
import com.axsvpn.android.model.ServerResponse;
import com.axsvpn.android.model.SessionResponse;
import com.axsvpn.android.model.IpLocationResponse;
import com.axsvpn.android.model.RegisterResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    @POST(AppData.loginURL)
    @FormUrlEncoded
    Call<AccountResponse> login(@Field("email") String email,
                                @Field("password") String password);

    @POST(AppData.registerURL)
    @FormUrlEncoded
    Call<RegisterResponse> register(@Field("firstname") String firstname,
                                  @Field("lastname") String lastname,
                                  @Field("email") String email,
                                  @Field("password") String password);

    @GET(AppData.bestLocationURL)
    Call<BestLocationResponse> bestLocation(@Query("session_auth_hash") String sessionAuthHash, @Query("gps_lat") Float latitude, @Query("gps_long") Float longitude);

    @GET(AppData.serverListURL)
    Call<ServerResponse> serverList(@Query("loc_hash") String locHash);

    @GET(AppData.sessionURL)
    Call<SessionResponse> session(@Query("session_auth_hash") String sessionAuthHash);

    @GET(AppData.serverCredentialsURL)
    Call<CredentialsResponse> serverCredentials(@Query("session_auth_hash") String sessionAuthHash);

    @GET(AppData.serverConfigsURL)
    Call<ConfigsResponse> serverConfigs(@Query("session_auth_hash") String sessionAuthHash);

    @GET(AppData.ipLocationURL)
    Call<IpLocationResponse> ipLocation();
}
