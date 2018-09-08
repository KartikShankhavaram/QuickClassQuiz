package com.imad.quickclassquiz.dataModel;

public class Test {

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
}
