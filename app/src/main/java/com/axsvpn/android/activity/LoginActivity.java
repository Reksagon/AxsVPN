package com.axsvpn.android.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.axsvpn.android.AppData;
import com.axsvpn.android.R;
import com.axsvpn.android.api.HttpApi;
import com.axsvpn.android.model.AccountResponse;
import com.axsvpn.android.model.BestLocationResponse;
import com.axsvpn.android.model.Server;
import com.axsvpn.android.model.ServerNode;
import com.axsvpn.android.model.ServerResponse;
import com.axsvpn.android.model.IpLocationResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    EditText emailEdit;
    EditText passwordEdit;
    Button loginButton;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEdit = findViewById(R.id.email_edit);
        passwordEdit = findViewById(R.id.password_edit);
        loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoginButtonClick();
            }
        });
        Button registerButton = findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRegisterActivity();
            }
        });

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        emailEdit.setText(sharedPreferences.getString("email", ""));
        passwordEdit.setText(sharedPreferences.getString("password", ""));
    }

    private void onLoginButtonClick() {
        String email = emailEdit.getText().toString();
        String password = passwordEdit.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(LoginActivity.this, "Please input email", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(LoginActivity.this, "Please input password", Toast.LENGTH_LONG).show();
            return;
        }

        login(email, password);
    }

    private void login(String email, String password) {
        showProgress(true);
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
                    } else if (accountResponse.getResult().equals("error")) {
                        showProgress(false);
                        Toast.makeText(LoginActivity.this, accountResponse.getMessage(), Toast.LENGTH_LONG).show();
                    } else {
                        showProgress(false);
                        Toast.makeText(LoginActivity.this, "Unknown response", Toast.LENGTH_LONG).show();
                    }
                } else {
                    showProgress(false);
                    Toast.makeText(LoginActivity.this, "Http response failed", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<AccountResponse> call, Throwable t) {
                showProgress(false);
                Toast.makeText(LoginActivity.this, "Network connection failed", Toast.LENGTH_LONG).show();
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
                    showProgress(false);
                    Toast.makeText(LoginActivity.this, "Http response failed", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<IpLocationResponse> call, Throwable t) {
                showProgress(false);
                Toast.makeText(LoginActivity.this, "Network connection failed", Toast.LENGTH_LONG).show();
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
                    } else if (bestLocationResponse.getResult().equals("error")) {
                        showProgress(false);
                        Toast.makeText(LoginActivity.this, bestLocationResponse.getMessage(), Toast.LENGTH_LONG).show();
                    } else {
                        showProgress(false);
                        Toast.makeText(LoginActivity.this, "Unknown response", Toast.LENGTH_LONG).show();
                    }
                } else {
                    showProgress(false);
                    Toast.makeText(LoginActivity.this, "Http response failed", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<BestLocationResponse> call, Throwable t) {
                showProgress(false);
                Toast.makeText(LoginActivity.this, "Network connection failed", Toast.LENGTH_LONG).show();
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
                    } else if (serverResponse.getResult().equals("error")) {
                        showProgress(false);
                        Toast.makeText(LoginActivity.this, serverResponse.getMessage(), Toast.LENGTH_LONG).show();
                    } else {
                        showProgress(false);
                        Toast.makeText(LoginActivity.this, "Unknown response", Toast.LENGTH_LONG).show();
                    }
                } else {
                    showProgress(false);
                    Toast.makeText(LoginActivity.this, "Http response failed", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                showProgress(false);
                Toast.makeText(LoginActivity.this, "Network connection failed", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showProgress(boolean show) {
        if (show) {
            emailEdit.setEnabled(false);
            passwordEdit.setEnabled(false);
            loginButton.setEnabled(false);
            loginButton.setText("Logging In...");
        } else {
            emailEdit.setEnabled(true);
            passwordEdit.setEnabled(true);
            loginButton.setEnabled(true);
            loginButton.setText("Log In");
        }
    }

    private void showMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void showRegisterActivity() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}
