package com.imad.quickclassquiz.datamodel;

import java.util.HashMap;

import androidx.annotation.Keep;

@Keep
public class ScoreModel {
    private String userName;
    private String userRollNumber;
    private String userScore;
    private String submissionTime;
    private HashMap<String, Object> attemptedAnswers;

    public ScoreModel() {}

    public ScoreModel(String userName, String userRollNumber, String userScore, String submissionTime, HashMap<String, Object> attemptedAnswers){
        this.userName = userName;
        this.userRollNumber = userRollNumber;
        this.userScore = userScore;
        this.submissionTime = submissionTime;
        this.attemptedAnswers = attemptedAnswers;
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

    public HashMap<String, Object> getAttemptedAnswers() {
        return attemptedAnswers;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserRollNumber(String userRollNumber) {
        this.userRollNumber = userRollNumber;
    }

    public void setUserScore(String userScore) {
        this.userScore = userScore;
    }

    public void setSubmissionTime(String submissionTime) {
        this.submissionTime = submissionTime;
    }

    public void setAttemptedAnswers(HashMap<String, Object> attemptedAnswers) {
        this.attemptedAnswers = attemptedAnswers;
    }
}
