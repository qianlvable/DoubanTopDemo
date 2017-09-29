package com.lvable.ningjiaqi.doubanrx.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ningjiaqi on 16/5/3.
 */
public class Actor implements Parcelable {
    public String name;
    public String avatarUrl;

    public Actor( String name,String avatarUrl) {
        this.avatarUrl = avatarUrl;
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.avatarUrl);
    }

    public Actor() {
    }

    protected Actor(Parcel in) {
        this.name = in.readString();
        this.avatarUrl = in.readString();
    }

    public static final Parcelable.Creator<Actor> CREATOR = new Parcelable.Creator<Actor>() {
        @Override
        public Actor createFromParcel(Parcel source) {
            return new Actor(source);
        }

        @Override
        public Actor[] newArray(int size) {
            return new Actor[size];
        }
    };
}
