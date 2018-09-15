package com.imad.quickclassquiz.dataModel;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Keep;

import java.util.Objects;

@Keep
public class Test implements Parcelable {

    public String testId;
    public String testName;
    public String testDesc;
    public String createdAt;
    public String accessCode;
    public String masterCode;
    public String startedAt;

    public Test(String testId, String testName, String testDesc, String createdAt) {
        this.testId = testId;
        this.testName = testName;
        this.testDesc = testDesc;
        this.createdAt = createdAt;
    }

    public Test() {

    }

    public Test(Parcel in) {
        this.testId = in.readString();
        this.testName = in.readString();
        this.testDesc = in.readString();
        this.createdAt = in.readString();
        this.accessCode = in.readString();
        this.masterCode = in.readString();
        this.startedAt = in.readString();
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
        dest.writeString(createdAt);
        dest.writeString(accessCode);
        dest.writeString(masterCode);
        dest.writeString(startedAt);
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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
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

    public String getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(String startedAt) {
        this.startedAt = startedAt;
    }

    @Override
    public String toString() {
        return "Test{" +
                "testId='" + testId + '\'' +
                ", testName='" + testName + '\'' +
                ", testDesc='" + testDesc + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", accessCode='" + accessCode + '\'' +
                ", masterCode='" + masterCode + '\'' +
                ", startedAt='" + startedAt + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Test test = (Test) o;
        return Objects.equals(testId, test.testId) &&
                Objects.equals(testName, test.testName) &&
                Objects.equals(testDesc, test.testDesc) &&
                Objects.equals(createdAt, test.createdAt) &&
                Objects.equals(accessCode, test.accessCode) &&
                Objects.equals(masterCode, test.masterCode) &&
                Objects.equals(startedAt, test.startedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(testId);
    }

}
