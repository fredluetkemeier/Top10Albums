package com.fred.topalbums;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Album implements Parcelable {
    private String artistName;
    private String releaseDate;
    private String albumName;
    private String copyright;
    private String artworkURL;
    private ArrayList<String> albumGenres;

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Album createFromParcel(Parcel in) {
            return new Album(in);
        }

        @Override
        public Album[] newArray(int size) {
            return new Album[size];
        }
    };

    public Album(String artistName, String releaseDate, String albumName, String copyright, String artworkURL, ArrayList<String> albumGenres) {
        this.artistName = artistName;
        this.releaseDate = releaseDate;
        this.albumName = albumName;
        this.copyright = copyright;
        this.artworkURL = artworkURL;
        this.albumGenres = albumGenres;
    }

    public Album(Parcel in){
        this.artistName = in.readString();
        this.releaseDate = in.readString();
        this.albumName = in.readString();
        this.copyright = in.readString();
        this.artworkURL = in.readString();
        this.albumGenres = in.readArrayList(Album.class.getClassLoader());
    }

    public String getArtistName(){ return artistName; }
    public String getReleaseDate() { return releaseDate; }
    public String getAlbumName() { return albumName; }
    public String getCopyright() { return copyright; }
    public String getArtworkURL() { return artworkURL; }
    public String getArtworkURL(int size) {
        String temp = artworkURL.replaceFirst("200x200bb.png", size + "x" + size + "bb.png");
        return temp;
    }
    public ArrayList<String> getAlbumGenres() { return albumGenres; }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.artistName);
        dest.writeString(this.releaseDate);
        dest.writeString(this.albumName);
        dest.writeString(this.copyright);
        dest.writeString(this.artworkURL);
        dest.writeList(this.albumGenres);
    }
}
