package com.example.matchify.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Message")
public class Message extends ParseObject {

    public static final String SENDER = "spotifyUserSender";
    public static final String RECEIVER = "spotifyUserReceiver";
    public static final String BODY_KEY = "body";



    public String getBody() {
        return getString(BODY_KEY);
    }
    public void setBody(String body) {
        put(BODY_KEY, body);
    }

    public SpotifyUser getSender() {
        return (SpotifyUser) getParseObject(SENDER);
    }
    public void setSender(SpotifyUser spotifyUser) {
        put(SENDER, spotifyUser);
    }

    public SpotifyUser getReceiver() {
        return (SpotifyUser) getParseObject(RECEIVER);
    }
    public void setReceiver(SpotifyUser spotifyUser) {
        put(RECEIVER, spotifyUser);
    }
}
