package com.example.matchify;

public class Song {

    public String songName;
    public String artistName;
    public String albumCoverUrl;

    public Song (String songName, String artistName, String albumCoverUrl) {
        this.songName = songName;
        this.artistName = artistName;
        this.albumCoverUrl = albumCoverUrl;
    }
     public String getSongName() {
        return songName;
     }

     public String getArtistName() {
        return artistName;
    }


}
