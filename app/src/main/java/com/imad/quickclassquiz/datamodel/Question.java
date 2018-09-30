package com.imad.quickclassquiz.datamodel;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.Keep;

import java.util.Objects;

import static androidx.room.ForeignKey.CASCADE;

@Keep
@Entity(tableName = "questions", foreignKeys = @ForeignKey(onDelete = CASCADE, entity = Test.class, parentColumns = "test_id", childColumns = "test_id"))
public class Question implements Parcelable {

    public static final Parcelable.Creator<Question> CREATOR = new Parcelable.Creator<Question>() {

        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        public Question[] newArray(int size) {
            return new Question[size];
        }
    };
    @PrimaryKey
    @ColumnInfo(name = "question_id")
    public String questionId;
    @ColumnInfo(name = "test_id")
    public String testId;
    @ColumnInfo(name = "question")
    public String question;
    @ColumnInfo(name = "option_1")
    public String option1;
    @ColumnInfo(name = "option_2")
    public String option2;
    @ColumnInfo(name = "option_3")
    public String option3;
    @ColumnInfo(name = "option_4")
    public String option4;
    @ColumnInfo(name = "correct_option")
    public String correctOption;

    public Question(String testId, String questionId, String question, String option1, String option2, String option3, String option4, String correctOption) {
        this.testId = testId;
        this.questionId = questionId;
        this.question = question;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.option4 = option4;
        this.correctOption = correctOption;
    }

    public Question() {
    }

    public Question(Parcel in) {
        this.testId = in.readString();
        this.questionId = in.readString();
        this.question = in.readString();
        this.option1 = in.readString();
        this.option2 = in.readString();
        this.option3 = in.readString();
        this.option4 = in.readString();
        this.correctOption = in.readString();
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(testId);
        dest.writeString(questionId);
        dest.writeString(question);
        dest.writeString(option1);
        dest.writeString(option2);
        dest.writeString(option3);
        dest.writeString(option4);
        dest.writeString(correctOption);
    }

    public String getTestId() {
        return testId;
    }

    public void setTestId(String testId) {
        this.testId = testId;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getOption1() {
        return option1;
    }

    public void setOption1(String option1) {
        this.option1 = option1;
    }

    public String getOption2() {
        return option2;
    }

    public void setOption2(String option2) {
        this.option2 = option2;
    }

    public String getOption3() {
        return option3;
    }

    public void setOption3(String option3) {
        this.option3 = option3;
    }

    public String getOption4() {
        return option4;
    }

    public void setOption4(String option4) {
        this.option4 = option4;
    }

    public String getCorrectOption() {
        return correctOption;
    }

    public void setCorrectOption(String correctOption) {
        this.correctOption = correctOption;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Question question1 = (Question) o;
        return Objects.equals(testId, question1.testId) &&
                Objects.equals(questionId, question1.questionId) &&
                Objects.equals(question, question1.question) &&
                Objects.equals(option1, question1.option1) &&
                Objects.equals(option2, question1.option2) &&
                Objects.equals(option3, question1.option3) &&
                Objects.equals(option4, question1.option4) &&
                Objects.equals(correctOption, question1.correctOption);
    }

    @Override
    public int hashCode() {
        return Objects.hash(testId, questionId);
    }

    @Override
    public String toString() {
        return "Question{" +
                "questionId='" + questionId + '\'' +
                ", testId='" + testId + '\'' +
                ", question='" + question + '\'' +
                ", option1='" + option1 + '\'' +
                ", option2='" + option2 + '\'' +
                ", option3='" + option3 + '\'' +
                ", option4='" + option4 + '\'' +
                ", correctOption='" + correctOption + '\'' +
                '}';
    }
}
