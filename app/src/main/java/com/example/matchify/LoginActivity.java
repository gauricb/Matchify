package com.example.matchify;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivity";
    private static final int REQUEST_CODE = 1337;
    private static final String REDIRECT_URI = "capstone-app-login://callback";
    private static final String CLIENT_ID = "33a4d498feb4475c902c47154d370dc2";
    public static final String AUTH_TOKEN = "AUTH_TOKEN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button buttonLogin = findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLoginWindow();
            }
        });
    }

    private void openLoginWindow() {
        AuthorizationRequest.Builder builder = new AuthorizationRequest.Builder(CLIENT_ID, AuthorizationResponse.Type.TOKEN, REDIRECT_URI);
        builder.setScopes(new String[]{"streaming", "user-read-email", "user-read-email", "user-read-private", "app-remote-control", "user-top-read", "user-library-read"} );
        AuthorizationRequest request = builder.build();

        AuthorizationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE) {
            AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);

            switch (response.getType()) {
                // Response was successful and contains a token
                case TOKEN:
                    Log.e(TAG, "Auth Token is: " + response.getAccessToken());
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra(AUTH_TOKEN, response.getAccessToken());
                    startActivity(intent);
                    finish();
                    break;
                case ERROR:
                    Log.e(TAG, "Auth flow returned an error: " + response.getError());
                    break;
                default:
                    Log.d(TAG, "Auth result: " + response.getType());

            }
        }
    }
}