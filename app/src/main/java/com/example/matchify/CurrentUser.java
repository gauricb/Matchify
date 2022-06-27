package com.example.matchify;

import static com.example.matchify.MainActivity.spotifyService;

import android.util.Log;

import com.example.matchify.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.UserPrivate;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import org.parceler.Parcel;

@Parcel
public class CurrentUser {

    private static String usr = "yoyo";
    // create fields for user properties
    //public String userName = "jOEY";
    public String userID;
    public String userProfileImage;


    public static final String TAG = "CurrentUser";

    //String usr;

    public CurrentUser() {}

    private static void setUserName(String name) {
       usr = name;
    }
    public String getUserName() {
        getUserNameFromSpotify();
        return usr;
    }

    public void getUserNameFromSpotify() {

        spotifyService.getMe(new Callback<UserPrivate>() {

            @Override
            public void success(UserPrivate userPrivate, Response response) {
                //usr = userPrivate.display_name;
                //setter method to change the global variable
                setUserName(userPrivate.display_name);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, error.toString());
            }
        });



    }

    public String getUserID() {
        return spotifyService.getMe().id;
    }

    public String getUserProfileImage() {
        return spotifyService.getMe().images.get(0).url;
    }

    /* TO DO: method to get recommendations here
    *
    * getTrackRecommendations(seed_artists, seed_genres, seed_tracks) {
    *
    *
    *
    *
    * }
    *
    *
    *
    *
    *
    *
    * song object: name, album, artist
    *
    * public static String[] getSeedArtists() {
    *   getTopArtists()
    * }
    * public static String[] getSeedGenres() {
    *   getSeedsGenres()
    * }
    * getSeedTracks() {
    *   getTopTracks()
    * }
    *
    *
    *
    *
    * */





}
