package com.imad.quickclassquiz.datamodel;

public class ScoreModel {
    private String userName;
    private String userRollNumber;
    private String userScore;

    public ScoreModel(String userName, String userRollNumber, String userScore){
        this.userName = userName;
        this.userRollNumber = userRollNumber;
        this.userScore = userScore;
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
}
