package com.mobitechstudio.wise.quotes.models.quote;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Category implements Parcelable {
    String title;
    String image;
    ArrayList<String> details = new ArrayList<>();

    public Category(String title, String image, ArrayList<String> details) {
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

    protected Category(Parcel in) {
        title = in.readString();
        image = in.readString();
        in.readList(details, Category.class.getClassLoader());
    }

    public static Creator<Category> getCREATOR() {
        return CREATOR;
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel source) {
            return new Category(source);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };
}