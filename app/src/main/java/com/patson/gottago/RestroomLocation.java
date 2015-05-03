package com.patson.gottago;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Copyright 2015 Patrick Son
 */
public class RestroomLocation implements Parcelable {

    private String mName;
    private String mNotes;
    private String mType;
    private int mCondition;
    private int mHandicap;
    private double mLatitude;
    private double mLongitude;

    public RestroomLocation() {}

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getNotes() {
        return mNotes;
    }

    public void setNotes(String notes) {
        mNotes = notes;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public int getCondition() {
        return mCondition;
    }

    public void setCondition(int condition) {
        mCondition = condition;
    }

    public int getHandicap() {
        return mHandicap;
    }

    public void setHandicap(int handicap) {
        mHandicap = handicap;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeString(mNotes);
        dest.writeString(mType);
        dest.writeInt(mCondition);
        dest.writeInt(mHandicap);
        dest.writeDouble(mLatitude);
        dest.writeDouble(mLongitude);
    }

    private RestroomLocation(Parcel in) {
        mName = in.readString();
        mNotes = in.readString();
        mType = in.readString();
        mCondition = in.readInt();
        mHandicap = in.readInt();
        mLatitude = in.readDouble();
        mLongitude = in.readDouble();
    }

    public static final Creator<RestroomLocation> CREATOR = new Creator<RestroomLocation>() {
        @Override
        public RestroomLocation createFromParcel(Parcel source) {
            return new RestroomLocation(source);
        }

        @Override
        public RestroomLocation[] newArray(int size) {
            return new RestroomLocation[size];
        }
    };
}
