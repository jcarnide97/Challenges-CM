package com.example.projetocm.models;

import java.io.Serializable;
import java.util.List;

public class Question implements Serializable {

    private String question;
    private String description;
    private List<String> answers;
    private boolean multiple_correct_answers;
    private List<Boolean> correct_answers;
    private String explanation;
    private String category;
    private String difficulty;

    public Question(String question, String description, List<String> answers, boolean multiple_correct_answers, List<Boolean> correct_answers, String explanation, String category, String difficulty) {
        this.question = question;
        this.description = description;
        this.answers = answers;
        this.multiple_correct_answers = multiple_correct_answers;
        this.correct_answers = correct_answers;
        this.explanation = explanation;
        this.category = category;
        this.difficulty = difficulty;
    }

    public Question() {

    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }

    public boolean isMultiple_correct_answers() {
        return multiple_correct_answers;
    }

    public void setMultiple_correct_answers(boolean multiple_correct_answers) {
        this.multiple_correct_answers = multiple_correct_answers;
    }

    public List<Boolean> getCorrect_answers() {
        return correct_answers;
    }

    public void setCorrect_answers(List<Boolean> correct_answers) {
        this.correct_answers = correct_answers;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }
}
