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
    public boolean visible;

    public Test(String testId, String testName, String testDesc, String createdAt) {
        this.testId = testId;
        this.testName = testName;
        this.testDesc = testDesc;
        this.createdAt = createdAt;
        this.visible = false;
    }

    public Test() {

    }

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

    public boolean getVisibility() {
        return visible;
    }

    public void setVisibility(boolean visible) {
        this.visible = visible;
    }

    @Override
    public int hashCode() {
        return Objects.hash(testId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Test test = (Test) o;
        return visible == test.visible &&
                Objects.equals(testId, test.testId) &&
                Objects.equals(testName, test.testName) &&
                Objects.equals(testDesc, test.testDesc) &&
                Objects.equals(createdAt, test.createdAt) &&
                Objects.equals(accessCode, test.accessCode) &&
                Objects.equals(masterCode, test.masterCode) &&
                Objects.equals(startedAt, test.startedAt);
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
                ", visible=" + visible +
                '}';
    }


    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.testId);
        dest.writeString(this.testName);
        dest.writeString(this.testDesc);
        dest.writeString(this.createdAt);
        dest.writeString(this.accessCode);
        dest.writeString(this.masterCode);
        dest.writeString(this.startedAt);
        dest.writeByte(this.visible ? (byte) 1 : (byte) 0);
    }

    protected Test(Parcel in) {
        this.testId = in.readString();
        this.testName = in.readString();
        this.testDesc = in.readString();
        this.createdAt = in.readString();
        this.accessCode = in.readString();
        this.masterCode = in.readString();
        this.startedAt = in.readString();
        this.visible = in.readByte() != 0;
    }

    public static final Creator<Test> CREATOR = new Creator<Test>() {
        @Override
        public Test createFromParcel(Parcel source) {
            return new Test(source);
        }

        @Override
        public Test[] newArray(int size) {
            return new Test[size];
        }
    };
}
