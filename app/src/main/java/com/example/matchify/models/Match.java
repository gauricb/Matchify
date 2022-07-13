package com.example.matchify.models;


import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Match")
public class Match extends SpotifyUser {



    @Override
    public String getUserName() {
        return super.getUserName();
    }

    @Override
    public void setUserName(String userSpotId) {
        super.setUserName(userSpotId);
    }

    @Override
    public String getUserImage() {
        return super.getUserImage();
    }

    @Override
    public void setUserImage(String userImage) {
        super.setUserImage(userImage);
    }

    @Override
    public void setCurrentUser(ParseUser songUser) {
        super.setCurrentUser(songUser);
    }

    @Override
    public ParseUser getCurrentUser() {
        return super.getCurrentUser();
    }
}
