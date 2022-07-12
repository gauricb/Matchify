package com.example.matchify.models;

import static com.example.matchify.MainActivity.spotifyService;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;

import java.util.Arrays;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.Track;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

@ParseClassName("SpotifyUser")
public class SpotifyUser extends ParseObject {

    public static final String TAG = "SpotifyUser";
    public static final String USER_NAME = "username";
    public static final String USER_IMAGE = "profileImage";
    public static final String CURRENT_USER = "songUser";
    public static final String TOP_TRACKS = "topTracks";
    public static final String TOP_ARTISTS = "topArtists";
    public static final String LIKED_SONGS = "likedSongs";

    public String getUserName() {
        return getString(USER_NAME);
    }
    public void setUserName(String userSpotId) {
        put(USER_NAME, userSpotId);
    }

    public String getUserImage() {return getString(USER_IMAGE);}
    public void setUserImage(String userImage) {put(USER_IMAGE, userImage);}

    public void setCurrentUser(ParseUser songUser) {put(CURRENT_USER, songUser);}
    public ParseUser getCurrentUser() {return getParseUser(CURRENT_USER);}

    public void setTopTracks(String[] topTracks) {
        put(TOP_TRACKS, Arrays.asList(topTracks));
    }

    public void setTopArtists(String[] topArtists) {
        put(TOP_ARTISTS, Arrays.asList(topArtists));
    }

    public JSONArray getTopArtists() {
        return getJSONArray(TOP_ARTISTS);
    }

    public JSONArray getTopTracks() {
        return getJSONArray(TOP_TRACKS);
    }

    public void setLikedSongs(String[] likedSong) {put(LIKED_SONGS, Arrays.asList(likedSong));}
    public JSONArray getLikedSongs() {
        return getJSONArray(LIKED_SONGS);
    }




}
