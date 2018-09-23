package com.imad.quickclassquiz.datamodel;

public class AttemptedQuestionsMessage {

    private boolean questionAttempted;

    public AttemptedQuestionsMessage(boolean questionAttempted) {
        this.questionAttempted = questionAttempted;
    }

    // When returns false, means attempted question's response was cleared
    public boolean wasQuestionAttempted() {
        return questionAttempted;
    }
}
