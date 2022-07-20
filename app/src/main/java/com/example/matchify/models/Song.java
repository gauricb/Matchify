package com.example.matchify.models;

import android.os.Parcelable;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("LikedSong")

public class Song extends ParseObject implements Parcelable {

    public String songName;
    public String artistName;
    public String albumCoverUrl;
    public String songUri;
    public String songLink;
    public String songGenre;


    public static final String SONG_NAME = "songName";
    public static final String ARTIST_NAME = "artistName";
    public static final String ALBUM_COVER = "albumCover";
    public static final String SONG_USER = "songUser";
    public static final String SONG_URI = "uri";
    public static final String SONG_LINK = "link";
    public static final String SONG_GENRE = "genre";

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
    public void setSongUser(ParseUser songUser) {put(SONG_USER, songUser);}
    public void setSongUri(String songUri) {
        put(SONG_URI, songUri);
    }
    public void setSongLink(String songLink) {
        put(SONG_LINK, songLink);
    }
    public void setSongGenre(String songGenre) {
        put(SONG_GENRE, songGenre);
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
    public ParseUser getParseSongUser() {return getParseUser(SONG_USER);}
    public String getParseSongUri() {
        return getString(SONG_URI);
    }
    public String getParseSongLink() {
        return getString(SONG_LINK);
    }
    public String getParseSongGenre() {
        return getString(SONG_GENRE);
    }

    public Song(){}

    public Song (String songName, String artistName, String albumCoverUrl, String songUri, String songLink) {
        this.songName = songName;
        this.artistName = artistName;
        this.albumCoverUrl = albumCoverUrl;
        this.songUri = songUri;
        this.songLink = songLink;


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

    public String getSongUri() {
        return songUri;
    }
    public String getSongLink() {
        return songLink;
    }
    public String getSongGenre() {
        return songGenre;
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
