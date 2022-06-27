package com.example.matchify;

public class Song {

    public String songName;
    public String artistName;
    public String albumName;

    public static Song Song(String songName, String artistName, String albumName) {
        Song song = new Song();
        song.songName = songName;
        song.artistName = artistName;
        song.albumName = albumName;

        return song;
    }

}
