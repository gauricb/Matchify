package com.example.matchify;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;

public class CurrentUser {

    // create fields for user properties
    public String userName;
    public String userID;
    public String userProfileImageProperties;
    //public JSONArray userTopItems;
    public static final String TAG = "CurrentUser";

    SpotifyApi api = new SpotifyApi();
    public static SpotifyService spotifyService;



    // empty constructor needed by the parceler library
    public CurrentUser() {

    }

    public static CurrentUser fromJson(JSONObject jsonObject) throws JSONException {
        CurrentUser user = new CurrentUser();
        String display_name = spotifyService.getMe().display_name;
        user.userName = jsonObject.getString(spotifyService.getMe().display_name);
        user.userID = jsonObject.getString(spotifyService.getMe().id);
        user.userProfileImageProperties = jsonObject.getString(spotifyService.getMe().images.get(0).url);

        return user;

    }
    //method to get recommendations here



}
