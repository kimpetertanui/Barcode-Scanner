package com.mobitechstudio.wise.quotes.models.quote;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Author implements Parcelable {
    String title;
    String image;
    ArrayList<String> details = new ArrayList<>();

    public Author(String title, String image, ArrayList<String> details) {
        this.title = title;
        this.image = image;
        this.details = details;
    }

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public ArrayList<String> getDetails() {
        return details;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(image);
        dest.writeList(details);
    }

    protected Author(Parcel in) {
        title = in.readString();
        image = in.readString();
        in.readList(details, Author.class.getClassLoader());
    }

    public static Creator<Author> getCREATOR() {
        return CREATOR;
    }

    public static final Creator<Author> CREATOR = new Creator<Author>() {
        @Override
        public Author createFromParcel(Parcel source) {
            return new Author(source);
        }

        @Override
        public Author[] newArray(int size) {
            return new Author[size];
        }
    };
}