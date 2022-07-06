package com.example.matchify.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("SpotifyUser")
public class SpotifyUser extends ParseObject {

    public static final String USER_DISPLAY_NAME = "display_name";
    public static final String USER_SPOT_ID = "username";
    public static final String USER_IMAGE = "profileImage";


    public String getUserDisplayName() {
        return getString(USER_DISPLAY_NAME);
    }
    public String getUserSpotId() {
        return getString(USER_SPOT_ID);
    }
    public void setUserDisplayName(String userDisplayName) {
        put(USER_DISPLAY_NAME, userDisplayName);
    }
    public void setUserSpotId(String userSpotId) {
        put(USER_SPOT_ID, userSpotId);
    }
    public String getUserImage() {return getString(USER_IMAGE);}
    public void setUserImage(String userImage) {put(USER_IMAGE, userImage);}
}
