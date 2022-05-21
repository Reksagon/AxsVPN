package com.axsvpn.android.activity;

import androidx.appcompat.app.AppCompatActivity;

import com.axsvpn.android.R;
import com.axsvpn.android.api.HttpApi;
import com.axsvpn.android.model.RegisterResponse;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    EditText firstNameEdit;
    EditText lastNameEdit;
    EditText emailEdit;
    EditText passwordEdit;
    EditText confirmPasswordEdit;
    Button registerButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firstNameEdit = findViewById(R.id.firstname_edit);
        lastNameEdit = findViewById(R.id.lastname_edit);
        emailEdit = findViewById(R.id.email_edit);
        passwordEdit = findViewById(R.id.password_edit);
        confirmPasswordEdit = findViewById(R.id.confirm_password_edit);

        registerButton = findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onRegisterButtonClick();
            }
        });

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void onRegisterButtonClick() {
        String firstName = firstNameEdit.getText().toString();
        String lastName = lastNameEdit.getText().toString();
        String email = emailEdit.getText().toString();
        String password = passwordEdit.getText().toString();
        String password2 = confirmPasswordEdit.getText().toString();

        if (TextUtils.isEmpty(firstName)) {
            Toast.makeText(RegisterActivity.this, "Please input first name", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(lastName)) {
            Toast.makeText(RegisterActivity.this, "Please input last name", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(RegisterActivity.this, "Please input email", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(RegisterActivity.this, "Please input password", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(password2)) {
            Toast.makeText(RegisterActivity.this, "Please input confirm password", Toast.LENGTH_LONG).show();
            return;
        }

        if (!password2.equals(password)) {
            Toast.makeText(RegisterActivity.this, "Please input same password", Toast.LENGTH_LONG).show();
            return;
        }

        registerAccount(firstName, lastName, email, password);
    }

    private void registerAccount(String firstName, String lastName, String email, String password) {
        showProgress(true);
        Call<RegisterResponse> call = HttpApi.getApiService().register(firstName, lastName, email, password);
        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                if (response.isSuccessful()) {
                    RegisterResponse registerResponse = response.body();

                    if (registerResponse.getResult().equals("success")) {
                        showProgress(false);
                        Toast.makeText(RegisterActivity.this, "You are registered now!", Toast.LENGTH_LONG).show();
                        finish();
                    } else if (registerResponse.getResult().equals("error")) {
                        showProgress(false);
                        Toast.makeText(RegisterActivity.this, registerResponse.getMessage(), Toast.LENGTH_LONG).show();
                    } else {
                        showProgress(false);
                        Toast.makeText(RegisterActivity.this, "Unknown response", Toast.LENGTH_LONG).show();
                    }
                } else {
                    showProgress(false);
                    Toast.makeText(RegisterActivity.this, "Http response failed", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                showProgress(false);
                Toast.makeText(RegisterActivity.this, "Network connection failed", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showProgress(boolean show) {
        if (show) {
            firstNameEdit.setEnabled(false);
            lastNameEdit.setEnabled(false);
            emailEdit.setEnabled(false);
            passwordEdit.setEnabled(false);
            confirmPasswordEdit.setEnabled(false);
            registerButton.setEnabled(false);
            registerButton.setText("Registering...");
        } else {
            firstNameEdit.setEnabled(true);
            lastNameEdit.setEnabled(true);
            emailEdit.setEnabled(true);
            passwordEdit.setEnabled(true);
            confirmPasswordEdit.setEnabled(true);
            registerButton.setEnabled(true);
            registerButton.setText("Register");
        }
    }
}