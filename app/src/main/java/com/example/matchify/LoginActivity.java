package com.example.matchify;

import static com.example.matchify.MainActivity.spotifyService;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.matchify.models.SpotifyUser;
import com.parse.ParseUser;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import kaaes.spotify.webapi.android.models.UserPrivate;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivity";
    private static final int REQUEST_CODE = 1337;
    private static final String REDIRECT_URI = "capstone-app-login://callback";
    private static final String CLIENT_ID = "33a4d498feb4475c902c47154d370dc2";
    public static final String AUTH_TOKEN = "AUTH_TOKEN";
    public static boolean LOG_IN_SELECTED = false;
    public static boolean SIGN_UP_SELECTED = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (ParseUser.getCurrentUser() != null) {
//            Log.e(TAG, "huih");
//        }

        setContentView(R.layout.activity_login);
        Button buttonLogin = findViewById(R.id.btnLogin);
        Button buttonSignUp = findViewById(R.id.btnSignUp);
        buttonLogin.setBackgroundColor(getResources().getColor(R.color.brightBlue));
        buttonLogin.setTextColor(getResources().getColor(R.color.white));
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLoginWindow();
                LOG_IN_SELECTED = true;
            }
        });
        buttonSignUp.setBackgroundColor(getResources().getColor(R.color.pink));
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLoginWindow();
                SIGN_UP_SELECTED = true;
            }
        });
    }

    private void openLoginWindow() {
        AuthorizationRequest.Builder builder = new AuthorizationRequest.Builder(CLIENT_ID, AuthorizationResponse.Type.TOKEN, REDIRECT_URI);

        builder.setScopes(new String[]{"streaming", "user-read-email", "user-read-email", "user-read-private", "app-remote-control", "user-top-read", "user-library-read"} );
        builder.setShowDialog(true);
        AuthorizationRequest request = builder.build();
        //AuthorizationClient.openLoginActivity(this, REQUEST_CODE, request);

        AuthorizationClient.openLoginInBrowser(this, request);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Uri uri = intent.getData();
        if (uri != null) {
            AuthorizationResponse response = AuthorizationResponse.fromUri(uri);

            switch (response.getType()) {
                case TOKEN:
                    Log.e(TAG, "Auth Token is: " + response.getAccessToken());
                    Intent intent1 = new Intent(LoginActivity.this, MainActivity.class);
                    intent1.putExtra(AUTH_TOKEN, response.getAccessToken());
                    startActivity(intent1);
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