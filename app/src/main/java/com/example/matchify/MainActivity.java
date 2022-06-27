package com.example.matchify;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import com.spotify.sdk.android.auth.AuthorizationClient;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.Recommendations;
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

    final FragmentManager fragmentManager = getSupportFragmentManager();
    private BottomNavigationView bottomNavigationView;

    private SpotifyAppRemote mSpotifyAppRemote;
    SpotifyApi api = new SpotifyApi();
    public static SpotifyService spotifyService;
    CurrentUser user = new CurrentUser();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AUTH_TOKEN = getIntent().getStringExtra(LoginActivity.AUTH_TOKEN);
        setServiceApi();

        bottomNavigationView = findViewById(R.id.bottomNavigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = new ProfileFragment();
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
                        connected();
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

    private void connected() {

        /* WHEN CONNECTED TO THE API, MAKE API CALLS HERE*/


        spotifyService.getMe(new Callback<UserPrivate>() {
            @Override
            public void success(UserPrivate userPrivate, Response response) {
                Log.d("User success ", userPrivate.display_name);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("User failure ", error.toString());
            }
        });

        spotifyService.getMySavedTracks(new Callback<Pager<SavedTrack>>() {
            @Override
            public void success(Pager<SavedTrack> savedTrackPager, Response response) {
                Log.d("my saved tracks ", "total number of saved tracks " + savedTrackPager.total);
                //Log.d(TAG, "full result of the request: " + savedTrackPager.href);
                List<SavedTrack> items = savedTrackPager.items;

                Log.d(TAG, "the requested content: " + items.get(0).track.name);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("error getting saved tracks ", error.toString());
            }
        });

        String[] artists = new String[5];
        spotifyService.getTopArtists(new Callback<Pager<Artist>>() {
            @Override
            public void success(Pager<Artist> artistPager, Response response) {

                for (int i = 0; i < 5; i++) {
                    //Log.d("user's top 5 artists ", artistPager.items.get(i).name);
                    //artists[i] = artistPager.items.get(i).name;
                    artists[i] = artistPager.items.get(i).id;
                }
                Log.d("user's top 5 artists ", Arrays.toString(artists));

            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("error getting top artist ", error.toString());
            }
        });

        String[] tracks = new String[5];
        spotifyService.getTopTracks(new Callback<Pager<Track>>() {
            @Override
            public void success(Pager<Track> trackPager, Response response) {

                for (int i = 0; i < 5; i++) {
                    //Log.d("user's top 5 artists ", artistPager.items.get(i).name);
                    tracks[i] = trackPager.items.get(i).id;
                }
                Log.d("user's top 5 tracks ", Arrays.toString(tracks));
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });

        String[] genres = new String[126];
        spotifyService.getSeedsGenres(new Callback<SeedsGenres>() {
            @Override
            public void success(SeedsGenres seedsGenres, Response response) {
                for (int i = 0; i < 126; i++) {
                    //Log.d("user's top 5 artists ", artistPager.items.get(i).name);
                    genres[i] = seedsGenres.genres.get(i);
                }
                Log.d("all the genres ", Arrays.toString(genres));

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
        String[] fiveGenres = new String[5];
        Map<String, Object> options = new HashMap<>(5);
        options.put("seed_genres", "hip hop");
        options.put("seed_artists", "5GnnSrwNCGyfAU4zuIytiS");
        options.put("seed_tracks", "2yGk5A4oipC4jvDLIVlQIJ");


//        options.put("seed_tracks", Arrays.toString(tracks));

        spotifyService.getRecommendations(options, new Callback<Recommendations>() {
            @Override
            public void success(Recommendations recommendations, Response response) {
                Log.d("recommended tracks: ", recommendations.tracks.get(0).name);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("cannot produce recommendations", error.toString());
            }
        });





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
        return super.onOptionsItemSelected(item);
    }

    private void onLogOutButton() {
        AuthorizationClient.clearCookies(MainActivity.this);
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
}