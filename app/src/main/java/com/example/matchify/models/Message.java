package com.example.matchify.models;


import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Message")
public class Message extends ParseObject {

    public static final String SENDER = "spotifyUserSender";
    public static final String RECEIVER = "spotifyUserReceiver";
    public static final String BODY_KEY = "body";
    public static final String SONG_WIDGET = "songWidget";

    public static final int MessageOutgoing = 0;
    public static final int MessageIncoming = 1;
    public static final int IncomingMessageSongWidget = 2;
    public static final int OutgoingMessageSongWidget = 3;

    private int viewType;
    String text;
    public Message() {}
    // constructor for the normal message type
    public Message(int viewType, String text) {
        this.viewType = viewType;
        this.text = text;
    }

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

    public Song getSongWidget() {
        return (Song) getParseObject(SONG_WIDGET);
    }
    public void setSongWidget(Song songWidget) {
        put(SONG_WIDGET, songWidget);
    }

    // variables for the item of the song widget view type
    String albumCover;
    String songName;
    String artistName;

    public static final String SONG_NAME = "songName";
    public static final String ARTIST_NAME = "artistName";
    public static final String ALBUM_COVER = "albumCover";

    // constructor for song widget message
    public Message(int viewType, String albumCover, String songName, String artistName) {
        this.viewType = viewType;
        this.albumCover = albumCover;
        this.songName = songName;
        this.artistName = artistName;
    }

    public void setMSongName(String songName) {
        put(SONG_NAME, songName);
    }
    public void setMArtistName(String artistName) {
        put(ARTIST_NAME, artistName);
    }
    public void setMAlbumCover(String albumCover) {
        put(ALBUM_COVER, albumCover);
    }

    public String getMSongName() {
        return getString(SONG_NAME);
    }
    public String getMArtistName() {
        return getString(ARTIST_NAME);
    }
    public String getMAlbumCover() {
        return getString(ALBUM_COVER);
    }

    public int getViewType() { return viewType; }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }



}
