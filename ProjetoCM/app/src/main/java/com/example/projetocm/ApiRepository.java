package com.example.projetocm;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import com.android.volley.toolbox.Volley;
import com.example.projetocm.databinding.FragmentQuizAnswerBinding;
import com.example.projetocm.models.Question;
import com.example.projetocm.models.Quiz;
import com.example.projetocm.ui.quizAnswers.AnswerViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;


public class ApiRepository implements Serializable {

    private ArrayList<Question> questions;
    private FragmentQuizAnswerBinding binding;
    private Quiz quizParameters;


    private int currentScore;

    private final String URL = "https://quizapi.io/api/v1/questions";
    private final String API_KEY = "4Ja1rLIi8sxL4uYBx0atzV081xRHqvcwpxnBENxD";

    AnswerViewModel answerViewModel;


    public ApiRepository() {
        this.questions = new ArrayList<>();
    }

    public void getQuestions(){
        if (this.questions.isEmpty()){
            RequestQueue queue = Volley.newRequestQueue(binding.getRoot().getContext());

            String uri = String.format("%s?apiKey=%s", URL, API_KEY);

            String difficulty = quizParameters.getDifficulty().toString();
            String num_questions = String.valueOf(quizParameters.getNumQuestions());
            if (quizParameters.getCategory().toString().compareTo("all") == 0)
                uri = String.format("%s&difficulty=%s&limit=%s", uri, difficulty, num_questions);
            else {
                String category = quizParameters.getCategory().toString();
                uri = String.format("%s&tags=%s&difficulty=%s&limit=%s", uri, category, difficulty, num_questions);
            }

            System.out.println(uri);

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, uri, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject object = response.getJSONObject(i);
                            Question question = parseJSONToQuestion(object);
                            questions.add(question);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    answerViewModel.setCurrentQuestion(questions.get(answerViewModel.getCounter()));
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println(error.toString());
                }
            });

            queue.add(jsonArrayRequest);
        }
    }

    private Question parseJSONToQuestion(JSONObject object) throws JSONException {
        Question question = new Question();
        question.setQuestion(object.getString("question"));
        question.setDescription(object.getString("description") == null ? object.getString("description") : null);

        ArrayList<String> answers = new ArrayList<>();
        JSONObject jsonArray = object.getJSONObject("answers");
        Iterator<String> iterator = jsonArray.keys();
        while (iterator.hasNext()) {
            answers.add(jsonArray.getString(iterator.next()));
        }
        question.setAnswers(answers);
        question.setMultiple_correct_answers(object.getBoolean("multiple_correct_answers"));

        ArrayList<Boolean> correctAnswers = new ArrayList<>();
        jsonArray = object.getJSONObject("correct_answers");
        iterator = jsonArray.keys();
        while (iterator.hasNext()) {
            correctAnswers.add(jsonArray.getBoolean(iterator.next()));
        }
        question.setCorrect_answers(correctAnswers);

        question.setExplanation(object.getString("explanation") == null ? object.getString("explanation") : null);
        question.setCategory(object.getString("category"));
        question.setDifficulty(object.getString("difficulty"));

        return question;
    }

    public void nextQuestion() {
        answerViewModel.increaseCounter();
        answerViewModel.setCurrentQuestion(questions.get(answerViewModel.getCounter()));
    }

    public void currentScore(int score, int seconds, String difficulty){
        currentScore=0;
        if (difficulty.toLowerCase().equals("easy")) {
            currentScore += 100;  // correct answer on easy quiz
        } else if (difficulty.toLowerCase().equals("medium")) {
            currentScore += 200;  // correct answer on medium quiz
        } else if (difficulty.toLowerCase().equals("hard")) {
            currentScore += 300;  // correct answer on hard quiz
        }
        if (seconds >= 0 && seconds <= 5){
            currentScore += 100;
        } else if(seconds > 5 && seconds <= 10){
            currentScore += 50;
        } else if(seconds > 10 && seconds <= 25){
            currentScore += 25;
        }
        answerViewModel.increaseScore(currentScore);
    }

    public int getCurrentScore() {
        return answerViewModel.getCurrentScore();
    }

    public int getCurrentTimeSpent() {
        return answerViewModel.getCurrentTime();
    }

    public void setCurrentTimeSpent(int seconds) {
        answerViewModel.increaseTime(seconds);
    }

    public int getCurrentCorrectAnswers() {
        return answerViewModel.getCurrentCorrect();
    }

    public void setCurrentScore(int score){
        answerViewModel.setCurrentScore(score);
    }
    public void setCurrentCorrectAnswers() {
        answerViewModel.increaseCorrect();
    }

    public void setQuestions(ArrayList<Question> questions) {
        this.questions = questions;
    }

    public FragmentQuizAnswerBinding getBinding() {
        return binding;
    }

    public void setBinding(FragmentQuizAnswerBinding binding) {
        this.binding = binding;
    }

    public Quiz getQuizParameters() {
        return quizParameters;
    }

    public void setQuizParameters(Quiz quizParameters) {
        this.quizParameters = quizParameters;
    }

    public AnswerViewModel getAnswerViewModel() {
        return answerViewModel;
    }

    public void setAnswerViewModel(AnswerViewModel answerViewModel) {
        this.answerViewModel = answerViewModel;
    }


}
