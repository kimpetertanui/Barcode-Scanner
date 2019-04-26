package com.mobitechstudio.wise.quotes.models.item;

import android.os.Parcel;
import android.os.Parcelable;

public class ItemModel implements Parcelable {
    String quoteText;
    Boolean isMarkerSet;

    public ItemModel(String quoteText, Boolean isMarkerSet) {
        this.quoteText = quoteText;
        this.isMarkerSet = isMarkerSet;
    }

    public String getQuoteText() {
        return quoteText;
    }

    public Boolean isMarkerSet() {
        return isMarkerSet;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(quoteText);
        dest.writeInt(isMarkerSet ? 1 : 0);
    }

    protected ItemModel(Parcel in) {
        quoteText = in.readString();
        isMarkerSet = in.readInt() != 0;
    }

    public static Creator<ItemModel> getCREATOR() {
        return CREATOR;
    }

    public static final Creator<ItemModel> CREATOR = new Creator<ItemModel>() {
        @Override
        public ItemModel createFromParcel(Parcel source) {
            return new ItemModel(source);
        }

        @Override
        public ItemModel[] newArray(int size) {
            return new ItemModel[size];
        }
    };
}