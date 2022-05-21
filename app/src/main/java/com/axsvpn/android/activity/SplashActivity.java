package com.axsvpn.android.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.axsvpn.android.AppData;
import com.axsvpn.android.R;
import com.axsvpn.android.api.HttpApi;
import com.axsvpn.android.model.AccountResponse;
import com.axsvpn.android.model.BestLocationResponse;
import com.axsvpn.android.model.IpLocationResponse;
import com.axsvpn.android.model.Server;
import com.axsvpn.android.model.ServerNode;
import com.axsvpn.android.model.ServerResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String email = sharedPreferences.getString("email", "");
        String password = sharedPreferences.getString("password", "");
        if (!email.equals("") && !password.equals("")) {
            login(email, password);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showLoginActivity();
                }
            }, 2000);
        }
    }

    private void login(String email, String password) {
        Call<AccountResponse> call = HttpApi.getApiService().login(email, password);
        call.enqueue(new Callback<AccountResponse>() {
            @Override
            public void onResponse(Call<AccountResponse> call, Response<AccountResponse> response) {
                if (response.isSuccessful()) {
                    AccountResponse accountResponse = response.body();
                    if (accountResponse.getResult().equals("success")) {
                        sharedPreferences.edit().putString("email", email).apply();
                        sharedPreferences.edit().putString("password", password).apply();
                        AppData.email = email;
                        AppData.password = password;
                        AppData.sessionAuthHash = accountResponse.getData().getSessionAuthHash();
                        AppData.isPremium = accountResponse.getData().isPremium();
                        AppData.productName = accountResponse.getData().getProductName();
                        AppData.locHash = accountResponse.getData().getLocHash();
                        AppData.trafficUsed = accountResponse.getData().getTrafficUsed();
                        getIpLocation();
                    } else {
                        showLoginActivity();
                    }
                } else {
                    showLoginActivity();
                }
            }

            @Override
            public void onFailure(Call<AccountResponse> call, Throwable t) {
                showLoginActivity();
            }
        });
    }

    private void getIpLocation() {
        Call<IpLocationResponse> call = HttpApi.getApiService().ipLocation();
        call.enqueue(new Callback<IpLocationResponse>() {
            @Override
            public void onResponse(Call<IpLocationResponse> call, Response<IpLocationResponse> response) {
                if (response.isSuccessful()) {
                    IpLocationResponse ipLocationResponse = response.body();
                    getBestLocation(ipLocationResponse.getLatitude(), ipLocationResponse.getLongitude());
                } else {
                    showLoginActivity();
                }
            }

            @Override
            public void onFailure(Call<IpLocationResponse> call, Throwable t) {
                showLoginActivity();
            }
        });
    }

    private void getBestLocation(Float latitude, Float longitude) {
        Call<BestLocationResponse> call = HttpApi.getApiService().bestLocation(AppData.sessionAuthHash, latitude, longitude);
        call.enqueue(new Callback<BestLocationResponse>() {
            @Override
            public void onResponse(Call<BestLocationResponse> call, Response<BestLocationResponse> response) {
                if (response.isSuccessful()) {
                    BestLocationResponse bestLocationResponse = response.body();
                    if (bestLocationResponse.getResult().equals("success")) {
                        AppData.bestLocation = bestLocationResponse.getData();
                        fetchServerList();
                    } else {
                        showLoginActivity();
                    }
                } else {
                    showLoginActivity();
                }
            }

            @Override
            public void onFailure(Call<BestLocationResponse> call, Throwable t) {
                showLoginActivity();
            }
        });
    }

    private void fetchServerList() {
        Call<ServerResponse> call = HttpApi.getApiService().serverList(AppData.locHash);
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if (response.isSuccessful()) {
                    ServerResponse serverResponse = response.body();
                    if (serverResponse.getResult().equals("success")) {
                        ServerNode serverNode = new ServerNode(AppData.bestLocation.getIp(), null, null, null);
                        ArrayList<ServerNode> serverNodes = new ArrayList<>();
                        serverNodes.add(serverNode);
                        Server bestServer = new Server(
                                AppData.bestLocation.getServerId(),
                                "Best Location",
                                AppData.bestLocation.getCountryCode(),
                                1,
                                0,
                                AppData.bestLocation.getShortName(),
                                serverNodes
                        );
                        AppData.servers = new ArrayList<>();
                        AppData.servers.add(bestServer);
                        List<Server> servers = serverResponse.getData();
                        for (Server server: servers) {
                            if (server.getStatus() == 1) {
                                AppData.servers.add(server);
                            }
                        }
                        AppData.selectedServer = bestServer;
                        showMainActivity();
                    } else {
                        showLoginActivity();
                    }
                } else {
                    showLoginActivity();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                showLoginActivity();
            }
        });
    }
    private void showLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void showMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
