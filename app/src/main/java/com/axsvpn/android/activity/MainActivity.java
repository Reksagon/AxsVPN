package com.axsvpn.android.activity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.axsvpn.android.AppData;
import com.axsvpn.android.R;
import com.axsvpn.android.api.HttpApi;
import com.axsvpn.android.databinding.ActivityMainBinding;
import com.axsvpn.android.model.ConfigsResponse;
import com.axsvpn.android.model.CredentialsResponse;
import com.axsvpn.android.model.Server;
import com.axsvpn.android.model.SessionResponse;
import com.google.common.collect.ImmutableList;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import de.blinkt.openvpn.LaunchVPN;
import de.blinkt.openvpn.VpnProfile;
import de.blinkt.openvpn.core.ConfigParser;
import de.blinkt.openvpn.core.ConnectionStatus;
import de.blinkt.openvpn.core.IOpenVPNServiceInternal;
import de.blinkt.openvpn.core.OpenVPNService;
import de.blinkt.openvpn.core.ProfileManager;
import de.blinkt.openvpn.core.VpnStatus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements VpnStatus.StateListener {

    static int SERVER_SELECT = 10001;
    ImageButton connectButton;
    LinearLayout openLogLayout;
    TextView statusText;
    TextView description1Text;
    TextView description2Text;
    LinearLayout connectedLayout;
    TextView serverText;
    TextView ipText;
    TextView productText;



    private SharedPreferences sharedPreferences;

    private IOpenVPNServiceInternal mService;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            mService = IOpenVPNServiceInternal.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mService = null;
        }
    };


    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        AppData.servers.get(0).setName(getResources().getString(R.string.best_location));


        connectButton = findViewById(R.id.connect_button);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onConnectButtonClick();
            }
        });

        openLogLayout = findViewById(R.id.open_log_layout);
        openLogLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOpenLogLayoutClick();
            }
        });

        LinearLayout logoutLayout = findViewById(R.id.logout_layout);
        logoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLogoutLayoutClick();
            }
        });

        statusText = findViewById(R.id.status_text);
        description1Text = findViewById(R.id.descrption1_text);
        description2Text = findViewById(R.id.descrption2_text);
        connectedLayout = findViewById(R.id.connected_layout);

        RelativeLayout upgradeLayout = findViewById(R.id.product_layout);
        upgradeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://axsvpn.com/clients/cart.php")));
                Intent intent = new Intent(MainActivity.this, PremiumActivity.class);
                startActivity(intent);
            }
        });

        RelativeLayout serverLayout = findViewById(R.id.server_layout);
        serverLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showServerListActivity();
            }
        });
        serverText = findViewById(R.id.server_text);
        ipText = findViewById(R.id.ip_text);
        productText = findViewById(R.id.product_text);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        Intent intent = new Intent(this, OpenVPNService.class);
        intent.setAction(OpenVPNService.START_SERVICE);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        VpnStatus.addStateListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        serverText.setText(AppData.selectedServer.getName());
        showProductLayout();
        refreshTrafficUI();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SERVER_SELECT) {
            if (AppData.temporaryServer.getId() != AppData.selectedServer.getId()) {
                if (OpenVPNService.mState.equals("NOPROCESS") || OpenVPNService.mState.equals("EXITING")) {
                    AppData.selectedServer = AppData.temporaryServer;
                    serverText.setText(AppData.selectedServer.getName());
                    showDownloadingLayout();
                    getServerCredentials();
                } else {
                    showServerChangedDialog(AppData.temporaryServer);
                }
            }
        }
    }

    private void showServerChangedDialog(Server server) {
        new AlertDialog.Builder(this).setTitle(R.string.server_change_title)
                .setMessage(R.string.server_change_description)
                .setPositiveButton(R.string.server_change_continue, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AppData.selectedServer = server;
                        serverText.setText(AppData.selectedServer.getName());
                        showDisconnectingLayout();
                        stopOpenVPN();
                        TimerTask timerTask = new TimerTask() {
                            @Override
                            public void run() {
                                showConnectingLayout();
                                startOpenVPN();
                            }
                        };
                        new Timer().schedule(timerTask, 1000);
                    }
                })
                .setNeutralButton(de.blinkt.openvpn.R.string.cancel, null)
                .show();
    }

    @Override
    public void updateState(String state, String logmessage, int localizedResId, ConnectionStatus level) {
        switch (state) {
            case "USER_VPN_PERMISSION":
                showConnectingLayout();
                break;
            case "VPN_GENERATE_CONFIG":
                showConnectingLayout();
                break;
            case "NONETWORK":
                showConnectingLayout();
                break;
            case "TCP_CONNECT":
                showConnectingLayout();
                break;
            case "WAIT":
                showConnectingLayout();
                break;
            case "AUTH":
                showConnectingLayout();
                break;
            case "GET_CONFIG":
                showConnectingLayout();
                break;
            case "ASSIGN_IP":
                showConnectingLayout();
                break;
            case "ADD_ROUTES":
                showConnectingLayout();
                break;
            case "CONNECTED":
                showConnectedLayout();
                break;
            case "EXITING":
                showDisconnectedLayout();
                break;
            case "NOPROCESS":
                showDisconnectedLayout();
                break;
            default:
                break;
        }
    }

    @Override
    public void setConnectedVPN(String uuid) {

    }

    private void showDisconnectedLayout() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                connectButton.setBackground(getDrawable(R.drawable.disabled));
                connectButton.setEnabled(true);
                statusText.setTextColor(getResources().getColor(R.color.colorDisconnected));
                statusText.setText(getResources().getString(R.string.disabled));
                description1Text.setText(getResources().getString(R.string.press));
                description2Text.setVisibility(View.VISIBLE);
                connectedLayout.setVisibility(View.GONE);
            }
        });
    }

    private void showConnectingLayout() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                connectButton.setBackground(getDrawable(R.drawable.disabled));
                connectButton.setEnabled(true);
                statusText.setTextColor(getResources().getColor(R.color.colorConnecting));
                statusText.setText(getResources().getString(R.string.connecting));
                description1Text.setText(getResources().getString(R.string.press_to_cancel));
                description2Text.setVisibility(View.VISIBLE);
                connectedLayout.setVisibility(View.GONE);
            }
        });
    }

    private void showConnectedLayout() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                connectButton.setBackground(getDrawable(R.drawable.enabled));
                connectButton.setEnabled(true);
                statusText.setTextColor(getResources().getColor(R.color.colorConnected));
                statusText.setText(getResources().getString(R.string.connected));
                description1Text.setText(getResources().getString(R.string.press_to_disconnect));
                description2Text.setVisibility(View.GONE);
                connectedLayout.setVisibility(View.VISIBLE);
                ipText.setText(AppData.selectedServer.getNodes().get(0).getIp());
            }
        });
    }

    private void showDisconnectingLayout() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                connectButton.setBackground(getDrawable(R.drawable.disabled));
                connectButton.setEnabled(false);
                statusText.setTextColor(getResources().getColor(R.color.colorDisconnecting));
                statusText.setText(getResources().getString(R.string.disconencting));
                description1Text.setText(getResources().getString(R.string.press_to_disconnect));
                description2Text.setVisibility(View.VISIBLE);
                connectedLayout.setVisibility(View.GONE);
            }
        });
    }

    private void showDownloadingLayout() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                connectButton.setBackground(getDrawable(R.drawable.disabled));
                connectButton.setEnabled(false);
                statusText.setTextColor(getResources().getColor(R.color.colorConnecting));
                statusText.setText(getResources().getString(R.string.connecting));
                description1Text.setText(getResources().getString(R.string.press_to_cancel));
                description2Text.setVisibility(View.VISIBLE);
                connectedLayout.setVisibility(View.GONE);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void showProductLayout() {
        if (AppData.isPremium) {
            productText.setText(getResources().getString(R.string.premium) + AppData.productName);
            binding.upgradeText.setText(getResources().getString(R.string.extend));
            connectButton.setEnabled(true);
        } else {
            double trafficLeft = (AppData.MAX_FREE_TRAFFIC - AppData.trafficUsed) / (1024 * 1024 * 1024f);
            productText.setText(getResources().getString(R.string.free) + new DecimalFormat("##.##").format(trafficLeft) +
                    getResources().getString(R.string.left));
            binding.upgradeText.setText(getResources().getString(R.string.upgrade));
            if (AppData.trafficUsed >= AppData.MAX_FREE_TRAFFIC) {
                connectButton.setEnabled(false);
                if (OpenVPNService.mState.equals("NOPROCESS") == false || OpenVPNService.mState.equals("EXITING") == false) {
                    showDisconnectingLayout();
                    stopOpenVPN();
                }
            } else {
                connectButton.setEnabled(true);
                if (AppData.trafficUsed > AppData.MAX_FREE_TRAFFIC * 0.8) {
                    productText.setTextColor(getResources().getColor(R.color.colorDisconnecting));
                } else {
                    productText.setTextColor(getResources().getColor(R.color.colorPrimary));
                }
            }
        }
    }

    private void refreshTrafficUI() {
        Call<SessionResponse> call = HttpApi.getApiService().session(AppData.sessionAuthHash);
        call.enqueue(new Callback<SessionResponse>() {
            @Override
            public void onResponse(Call<SessionResponse> call, Response<SessionResponse> response) {
                if (response.isSuccessful()) {
                    SessionResponse sessionResponse = response.body();
                    if (sessionResponse.getResult().equals("success")) {
                        AppData.trafficUsed = sessionResponse.getData().getTrafficUsed();
                        showProductLayout();
                    } else if (sessionResponse.getResult().equals("error")) {
                        Toast.makeText(MainActivity.this, sessionResponse.getMessage(), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Unknown response", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Http response failed", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<SessionResponse> call, Throwable t) {
                Log.d("MainActivity","Network connection failed");
            }
        });
    }

    private void onConnectButtonClick() {
        if (OpenVPNService.mState.equals("NOPROCESS") || OpenVPNService.mState.equals("EXITING")) {
            showDownloadingLayout();
            getServerCredentials();
        } else {
            showDisconnectingLayout();
            stopOpenVPN();
        }
    }

    private void getServerCredentials() {
        Call<CredentialsResponse> call = HttpApi.getApiService().serverCredentials(AppData.sessionAuthHash);
        call.enqueue(new Callback<CredentialsResponse>() {
            @Override
            public void onResponse(Call<CredentialsResponse> call, Response<CredentialsResponse> response) {
                if (response.isSuccessful()) {
                    CredentialsResponse credentialsResponse = response.body();
                    if (credentialsResponse.getResult().equals("success")) {
                        AppData.vpnUsername = new String(Base64.decode(credentialsResponse.getData().getUsername(), Base64.DEFAULT));
                        AppData.vpnPassword = new String(Base64.decode(credentialsResponse.getData().getPassword(), Base64.DEFAULT));
                        getServerConfigs();
                    } else if (credentialsResponse.getResult().equals("error")) {
                        showDisconnectedLayout();
                        Toast.makeText(MainActivity.this, credentialsResponse.getMessage(), Toast.LENGTH_LONG).show();
                    } else {
                        showDisconnectedLayout();
                        Toast.makeText(MainActivity.this, "Unknown response", Toast.LENGTH_LONG).show();
                    }
                } else {
                    showDisconnectedLayout();
                    Toast.makeText(MainActivity.this, "Http response failed", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<CredentialsResponse> call, Throwable t) {
                showDisconnectedLayout();
                Toast.makeText(MainActivity.this, "Network connection failed", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getServerConfigs() {
        Call<ConfigsResponse> call = HttpApi.getApiService().serverConfigs(AppData.sessionAuthHash);
        call.enqueue(new Callback<ConfigsResponse>() {
            @Override
            public void onResponse(Call<ConfigsResponse> call, Response<ConfigsResponse> response) {
                if (response.isSuccessful()) {
                    ConfigsResponse configsResponse = response.body();
                    if (configsResponse.getResult().equals("success")) {
                        AppData.config = new String(Base64.decode(configsResponse.getData(), Base64.DEFAULT));
                        showConnectingLayout();
                        startOpenVPN();
                    } else if (configsResponse.getResult().equals("error")) {
                        showDisconnectedLayout();
                        Toast.makeText(MainActivity.this, configsResponse.getMessage(), Toast.LENGTH_LONG).show();
                    } else {
                        showDisconnectedLayout();
                        Toast.makeText(MainActivity.this, "Unknown response", Toast.LENGTH_LONG).show();
                    }
                } else {
                    showDisconnectedLayout();
                    Toast.makeText(MainActivity.this, "Http response failed", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ConfigsResponse> call, Throwable t) {
                showDisconnectedLayout();
                Toast.makeText(MainActivity.this, "Network connection failed", Toast.LENGTH_LONG).show();
            }
        });
    }

    private ProfileManager getPM() {
        return ProfileManager.getInstance(this);
    }

    private void startOpenVPN() {
        try {
            String config = AppData.config + "\r\nremote " + AppData.selectedServer.getNodes().get(0).getIp() + " 1194";
            InputStreamReader isr = new InputStreamReader(new ByteArrayInputStream(config.getBytes()));
            ConfigParser cp = new ConfigParser();
            cp.parseConfig(isr);

            VpnProfile vpnProfile = cp.convertProfile();
            vpnProfile.mUsername = AppData.vpnUsername;
            vpnProfile.mPassword = AppData.vpnPassword;
            vpnProfile.mName = "AxsVPN";

            vpnProfile.mCustomConfigOptions = "ifconfig-ipv6 fd15:53b6:dead::2/64 fd15:53b6:dead::1\n" +
                    "redirect-gateway ipv6\n" +
                    "block-ipv6";

            getPM().addProfile(vpnProfile);

            Intent intent = new Intent(MainActivity.this, LaunchVPN.class);
            intent.putExtra(LaunchVPN.EXTRA_KEY, vpnProfile.getUUID().toString());
            intent.putExtra(LaunchVPN.EXTRA_HIDELOG, true);
            intent.setAction(Intent.ACTION_MAIN);
            startActivity(intent);
        } catch (ConfigParser.ConfigParseError | IOException e) {
            e.printStackTrace();
        }
    }

    private void stopOpenVPN() {
//        ProfileManager.setConntectedVpnProfileDisconnected(this);
        if (mService != null) {
            try {
                mService.stopVPN(false);
            } catch (RemoteException e) {
                VpnStatus.logException(e);
            }
        }
    }

    private void onOpenLogLayoutClick() {
        Intent intent = new Intent(this, LogActivity.class);
        startActivity(intent);
    }

    private void onLogoutLayoutClick() {
        sharedPreferences.edit().putBoolean("traffic_warning", false).apply();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void showServerListActivity() {
        Intent intent = new Intent(this, ServerListActivity.class);
        startActivityForResult(intent, SERVER_SELECT);
    }
}
