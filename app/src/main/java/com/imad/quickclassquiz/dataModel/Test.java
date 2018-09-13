package com.imad.quickclassquiz.dataModel;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

public class Test implements Parcelable {

    private String testId;
    private String testName;
    private String testDesc;
    private String testTimestamp;
    private String accessCode;
    private String masterCode;

    public Test(String testId, String testName, String testDesc, String testTimestamp) {
        this.testId = testId;
        this.testName = testName;
        this.testDesc = testDesc;
        this.testTimestamp = testTimestamp;
    }

    public Test() {

    }

    private Test(Parcel in) {
        this.testId = in.readString();
        this.testName = in.readString();
        this.testDesc = in.readString();
        this.testTimestamp = in.readString();
        this.accessCode = in.readString();
        this.masterCode = in.readString();
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(testId);
        dest.writeString(testName);
        dest.writeString(testDesc);
        dest.writeString(testTimestamp);
        dest.writeString(accessCode);
        dest.writeString(masterCode);
    }

    public static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {

        public Test createFromParcel(Parcel in) {
            return new Test(in);
        }

        public Test[] newArray(int size) {
            return new Test[size];
        }
    };

    public String getTestId() {
        return testId;
    }

    public void setTestId(String testId) {
        this.testId = testId;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getTestDesc() {
        return testDesc;
    }

    public void setTestDesc(String testDesc) {
        this.testDesc = testDesc;
    }

    public String getTestTimestamp() {
        return testTimestamp;
    }

    public void setTestTimestamp(String testTimestamp) {
        this.testTimestamp = testTimestamp;
    }

    public String getAccessCode() {
        return accessCode;
    }

    public void setAccessCode(String accessCode) {
        this.accessCode = accessCode;
    }

    public String getMasterCode() {
        return masterCode;
    }

    public void setMasterCode(String masterCode) {
        this.masterCode = masterCode;
    }

    @Override
    public String toString() {
        return "Test{" +
                "testId='" + testId + '\'' +
                ", testName='" + testName + '\'' +
                ", testDesc='" + testDesc + '\'' +
                ", testTimestamp='" + testTimestamp + '\'' +
                ", accessCode='" + accessCode + '\'' +
                ", masterCode='" + masterCode + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Test test = (Test) o;
        return Objects.equals(testId, test.testId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(testId);
    }

}
