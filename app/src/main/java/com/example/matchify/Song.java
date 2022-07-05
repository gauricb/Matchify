package com.example.matchify;

import android.os.Parcelable;

import org.parceler.Parcel;

@Parcel
public class Song implements Parcelable {

    public String songName;
    public String artistName;
    public String albumCoverUrl;

    public Song() {

    }

    public Song (String songName, String artistName, String albumCoverUrl) {
        this.songName = songName;
        this.artistName = artistName;
        this.albumCoverUrl = albumCoverUrl;
    }

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
