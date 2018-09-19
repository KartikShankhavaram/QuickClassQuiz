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
    public int questionCount;

    public Test(String testId, String testName, String testDesc, String createdAt) {
        this.testId = testId;
        this.testName = testName;
        this.testDesc = testDesc;
        this.createdAt = createdAt;
        this.visible = false;
        this.questionCount = 0;
    }

    public Test() {

    }

    protected Test(Parcel in) {
        testId = in.readString();
        testName = in.readString();
        testDesc = in.readString();
        createdAt = in.readString();
        accessCode = in.readString();
        masterCode = in.readString();
        startedAt = in.readString();
        visible = in.readByte() != 0;
        questionCount = in.readInt();
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
        dest.writeByte((byte) (visible ? 1 : 0));
        dest.writeInt(questionCount);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Test> CREATOR = new Creator<Test>() {
        @Override
        public Test createFromParcel(Parcel in) {
            return new Test(in);
        }

        @Override
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

    public boolean getVisibility() {
        return visible;
    }

    public void setVisibility(boolean visible) {
        this.visible = visible;
    }

    public int getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(int questionCount) {
        this.questionCount = questionCount;
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
                questionCount == test.questionCount &&
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
                ", questionCount=" + questionCount +
                '}';
    }

}
