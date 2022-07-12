package com.example.matchify;

import static com.example.matchify.LoginActivity.LOG_IN_SELECTED;
import static com.example.matchify.LoginActivity.SIGN_UP_SELECTED;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.matchify.fragments.DiscoverFragment;
import com.example.matchify.fragments.MatchesFragment;
import com.example.matchify.fragments.ProfileFragment;
import com.example.matchify.models.Song;
import com.example.matchify.models.SpotifyUser;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import com.spotify.sdk.android.auth.AuthorizationClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.SavedTrack;
import kaaes.spotify.webapi.android.models.SeedsGenres;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.UserPrivate;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity {

    private static final String CLIENT_ID = "33a4d498feb4475c902c47154d370dc2";
    private static final String REDIRECT_URI = "capstone-app-login://callback";
    public String AUTH_TOKEN;

    public static final String TAG = "MainActivity";
    public static final String USER_PASSWORD = "xyz";
    private final int REQUEST_CODE = 22;

    final FragmentManager fragmentManager = getSupportFragmentManager();
    private BottomNavigationView bottomNavigationView;

    private SpotifyAppRemote mSpotifyAppRemote;
    public static SpotifyService spotifyService;


    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AUTH_TOKEN = getIntent().getStringExtra(LoginActivity.AUTH_TOKEN);

        setServiceApi();

        bottomNavigationView = findViewById(R.id.bottomNavigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = new DiscoverFragment();
                switch (item.getItemId()) {
                    case R.id.action_discover:
                        fragment = new DiscoverFragment();
                        break;
                    case R.id.action_match:
                        fragment = new MatchesFragment();
                        break;
                    case R.id.action_profile:
                        fragment = new ProfileFragment();
                        break;
                    default:
                        break;
                }

                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.action_profile);
    }

    @Override
    protected void onStart() {
        super.onStart();

        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        SpotifyAppRemote.connect(this, connectionParams,
                new Connector.ConnectionListener() {

                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        Log.d("MainActivity", "Connected! Yay!");
                        // Now you can start interacting with App Remote
                        try {
                            connected();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                    public void onFailure(Throwable throwable) {
                        Log.e("MyActivity", throwable.getMessage(), throwable);
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }

    private void connected() throws ParseException {

        Log.d(TAG, "login was selected? " + LOG_IN_SELECTED);

        if (LOG_IN_SELECTED == 1) {
            spotifyService.getMe(new Callback<UserPrivate>() {
                @Override
                public void success(UserPrivate userPrivate, Response response) {
                    login(userPrivate.display_name, USER_PASSWORD);

                }

                @Override
                public void failure(RetrofitError error) {

                }
            });

        }
        else if (SIGN_UP_SELECTED == 1) {
            signUpUser();
        }



    }
    public boolean userExists(SpotifyUser user, String userID) {
        return true;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "menu inflated");
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.buttonLogout) {
            Toast.makeText(MainActivity.this, "logout selected!", Toast.LENGTH_SHORT).show();
            onLogOutButton();
            finish();
        }
        if (item.getItemId() == R.id.chatButton) {
            Toast.makeText(MainActivity.this, "chat selected!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, ChatActivity.class);
            startActivityForResult(intent, REQUEST_CODE);

        }

        return super.onOptionsItemSelected(item);
    }

    private void onLogOutButton() {
        AuthorizationClient.clearCookies(MainActivity.this);
        ParseUser.getCurrentUser().logOut();
        // navigate back to the login screen
        Intent i = new Intent(MainActivity.this, LoginActivity.class);
        //make sure the back button won't work
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // this makes sure the Back button won't work
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // same as above
        startActivity(i);
    }

    private void setServiceApi() {
        SpotifyApi api = new SpotifyApi();
        api.setAccessToken(AUTH_TOKEN);
        spotifyService = api.getService();
    }

    void login(String username, String password) {

        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "cannot login this user");
                    return;
                }
                Log.d(TAG, "logging in this user");
            }
        });
    }
    private void signUpUser() {

        spotifyService.getMe(new Callback<UserPrivate>() {
            @Override
            public void success(UserPrivate userPrivate, Response response) {
                SpotifyUser spotifyUser = new SpotifyUser();
                ParseUser user = new ParseUser();

                String username = userPrivate.display_name;
                user.setUsername(username);
                user.setPassword(USER_PASSWORD);

                spotifyUser.setUserImage(userPrivate.images.get(0).url);
                spotifyUser.setUserName(username);
                spotifyService.getTopTracks(new Callback<Pager<Track>>() {
                    @Override
                    public void success(Pager<Track> trackPager, Response response) {
                        String[] tracks = new String[20];
                        for (int i = 0; i < 20; i++) {
                            tracks[i] = trackPager.items.get(i).name;
                        }
                        spotifyUser.setTopTracks(tracks);
                        spotifyUser.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                Log.d(TAG, "user's top tracks saved");
                            }
                        });

                    }
                    @Override
                    public void failure(RetrofitError error) {
                        Log.e(TAG, error.toString());
                    }

                });
                spotifyService.getTopArtists(new Callback<Pager<Artist>>() {
                    @Override
                    public void success(Pager<Artist> artistPager, Response response) {
                        String[] artists = new String[20];
                        for (int i = 0; i < 20; i++) {
                            artists[i] = artistPager.items.get(i).name;
                        }
                        spotifyUser.setTopArtists(artists);
                        spotifyUser.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                Log.d(TAG, "user's top artists saved");
                            }
                        });
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e(TAG, error.toString());
                    }
                });

                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            spotifyUser.setCurrentUser(ParseUser.getCurrentUser());
                            spotifyUser.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        Log.d(TAG, "user details saved");
                                    }
                                    else {Log.e(TAG, "error saving user details" + e.toString());}
                                }
                            });
                            login(username, USER_PASSWORD);
                        }
                        else {
                            // Sign up didn't succeed. Look at the ParseException
                            // to figure out what went wrong
                            Log.e(TAG, "Sign up Error. Username: " + username + " Password: " + USER_PASSWORD, e);
                        }
                    }
                });



            }

            @Override
            public void failure(RetrofitError error) {

            }
        });



    }


}