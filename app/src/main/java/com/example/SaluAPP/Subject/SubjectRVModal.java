package com.example.SaluAPP.Subject;

import android.os.Parcel;
import android.os.Parcelable;

public class SubjectRVModal implements Parcelable {
    // creating variables for our different fields.
    private String subjectName;
    private String subjectDescription;
    private String subjectImg;
    private String subjectId;


    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public SubjectRVModal(){

    }
    protected SubjectRVModal(Parcel in) {
        subjectName = in.readString();
        subjectId = in.readString();
        subjectDescription = in.readString();
        subjectImg = in.readString();
    }

    public static final Creator<SubjectRVModal> CREATOR = new Creator<SubjectRVModal>() {
        @Override
        public SubjectRVModal createFromParcel(Parcel in) {
            return new SubjectRVModal(in);
        }

        @Override
        public SubjectRVModal[] newArray(int size) {
            return new SubjectRVModal[size];
        }
    };

    // creating getter and setter methods.
    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getSubjectDescription() {
        return subjectDescription;
    }

    public void setSubjectDescription(String subjectDescription) {
        this.subjectDescription = subjectDescription;
    }



    public String getSubjectImg() {
        return subjectImg;
    }

    public void setSubjectImg(String subjectImg) {
        this.subjectImg = subjectImg;
    }


    public SubjectRVModal(String subjectId, String subjectName, String subjectDescription, String subjectImg) {
        this.subjectName = subjectName;
        this.subjectId = subjectId;
        this.subjectDescription = subjectDescription;
        this.subjectImg = subjectImg;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(subjectName);
        dest.writeString(subjectId);
        dest.writeString(subjectDescription);
        dest.writeString(subjectImg);
    }
}