package com.example.matchify.models;

import android.os.Parcelable;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("LikedSong")

public class Song extends ParseObject implements Parcelable {

    public String songName;
    public String artistName;
    public String albumCoverUrl;

    public static final String SONG_NAME = "songName";
    public static final String ARTIST_NAME = "artistName";
    public static final String ALBUM_COVER = "albumCover";

    // getters and setters for parse
    public void setSongName(String songName) {
        put(SONG_NAME, songName);
    }
    public void setArtistName(String artistName) {
        put(ARTIST_NAME, artistName);
    }
    public void setAlbumCover(String albumCover) {
        put(ALBUM_COVER, albumCover);
    }
    public String getParseSongName() {
        return getString(SONG_NAME);
    }
    public String getParseArtistName() {
        return getString(ARTIST_NAME);
    }
    public String getParseAlbumCover() {
        return getString(ALBUM_COVER);
    }

    public Song(){}

    public Song (String songName, String artistName, String albumCoverUrl) {
        this.songName = songName;
        this.artistName = artistName;
        this.albumCoverUrl = albumCoverUrl;
    }

    // for Parcelable
    protected Song(android.os.Parcel in) {
        songName = in.readString();
        artistName = in.readString();
        albumCoverUrl = in.readString();
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(android.os.Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    public String getSongName() {
        return songName;
     }

    public String getArtistName() {
        return artistName;
    }

    public String getAlbumCoverUrl() {
        return albumCoverUrl;
     }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeString(songName);
        dest.writeString(artistName);
        dest.writeString(albumCoverUrl);
    }

}
