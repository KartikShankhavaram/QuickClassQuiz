package com.imad.quickclassquiz.datamodel;

public class AttemptedQuestionsMessage {

    private int attemptCode;
    private String questionId;
    private String attemptedAnswer;

    public final static int ATTEMPT_ADDED  = 0;
    public final static int ATTEMPT_REMOVED  = 1;
    public final static int ATTEMPT_REPLACED = 2;

    public AttemptedQuestionsMessage(int attemptCode, String questionId, String attemptedAnswer) {
        this.attemptCode = attemptCode;
        this.questionId = questionId;
        this.attemptedAnswer = attemptedAnswer;
    }

    public AttemptedQuestionsMessage(int attemptCode, String questionId) {
        this.attemptCode = attemptCode;
        this.questionId = questionId;
        attemptedAnswer = null;
    }

    // When returns false, means attempted question's response was cleared
    public int getAttemptCode() {
        return attemptCode;
    }

    public String getQuestionId() {
        return questionId;
    }

    public String getAttemptedAnswer() {
        return attemptedAnswer;
    }
}
