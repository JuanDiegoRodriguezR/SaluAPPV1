package com.example.SaluAPP.Units;

import android.os.Parcel;
import android.os.Parcelable;

public class UnitRVModal implements Parcelable {
    private String unitName;
    private String unitDescription;
    private String unitImg;
    private String unitId;
    private String unitVid;
    private String unitRcmd;



    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    public UnitRVModal() {

    }

    protected UnitRVModal(Parcel in) {
        unitName = in.readString();
        unitId = in.readString();
        unitDescription = in.readString();
        unitImg = in.readString();
        unitRcmd = in.readString();
        unitVid = in.readString();
    }

    public static final Creator<UnitRVModal> CREATOR = new Creator<UnitRVModal>() {
        @Override
        public UnitRVModal createFromParcel(Parcel in) {
            return new UnitRVModal(in);
        }

        @Override
        public UnitRVModal[] newArray(int size) {
            return new UnitRVModal[size];
        }
    };

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getUnitDescription() {
        return unitDescription;
    }

    public void setUnitDescription(String unitDescription) {
        this.unitDescription = unitDescription;
    }

    public String getUnitImg() {
        return unitImg;
    }

    public void setUnitImg(String unitImg) {
        this.unitImg = unitImg;
    }

    public String getUnitRcmd() {
        return unitRcmd;
    }

    public void setUnitRcmd(String unitRcmd) {
        this.unitRcmd = unitRcmd;
    }

    public String getUnitVid() {
        return unitVid;
    }

    public void setUnitVid(String unitVid) {
        this.unitVid = unitVid;
    }
    public UnitRVModal(String unitId, String unitName, String unitDescription, String unitImg, String unitRcmd, String unitVid) {
        this.unitName = unitName;
        this.unitId = unitId;
        this.unitDescription = unitDescription;
        this.unitImg = unitImg;
        this.unitRcmd= unitRcmd;
        this.unitVid = unitVid;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(unitName);
        dest.writeString(unitId);
        dest.writeString(unitDescription);
        dest.writeString(unitImg);
        dest.writeString(unitRcmd);
        dest.writeString(unitVid);
    }



}
