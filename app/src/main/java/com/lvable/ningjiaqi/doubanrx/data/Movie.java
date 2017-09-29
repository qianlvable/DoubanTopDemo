package com.lvable.ningjiaqi.doubanrx.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by ningjiaqi on 16/4/22.
 */
public class Movie implements Parcelable {
    public float rating;
    public String title;
    public String orignTitle;
    public int year;
    public String id;
    public String imgUrl;
    public String directors;
    public List<Actor> actors;

    public Movie() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(this.rating);
        dest.writeString(this.title);
        dest.writeString(this.orignTitle);
        dest.writeInt(this.year);
        dest.writeString(this.id);
        dest.writeString(this.imgUrl);
        dest.writeString(this.directors);
        dest.writeTypedList(actors);
    }

    protected Movie(Parcel in) {
        this.rating = in.readFloat();
        this.title = in.readString();
        this.orignTitle = in.readString();
        this.year = in.readInt();
        this.id = in.readString();
        this.imgUrl = in.readString();
        this.directors = in.readString();
        this.actors = in.createTypedArrayList(Actor.CREATOR);
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
