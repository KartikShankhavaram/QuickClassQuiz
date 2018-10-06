package com.imad.quickclassquiz.datamodel;

public class ScoreModel {
    private String userName;
    private String userRollNumber;
    private String userScore;
    private String submissionTime;

    public ScoreModel(String userName, String userRollNumber, String userScore, String submissionTime){
        this.userName = userName;
        this.userRollNumber = userRollNumber;
        this.userScore = userScore;
        this.submissionTime = submissionTime;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserRollNumber() {
        return userRollNumber;
    }

    public String getUserScore() {
        return userScore;
    }

    public String getSubmissionTime() {
        return submissionTime;
    }
}
