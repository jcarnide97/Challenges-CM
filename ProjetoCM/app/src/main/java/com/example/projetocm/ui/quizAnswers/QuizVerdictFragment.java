package com.example.projetocm.ui.quizAnswers;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.projetocm.ApiRepository;
import com.example.projetocm.HomepageActivityViewModel;
import com.example.projetocm.models.Question;
import com.example.projetocm.R;
import com.example.projetocm.databinding.FragmentQuizVeredictBinding;


import java.util.ArrayList;


public class QuizVerdictFragment extends Fragment {

    private ApiRepository apiRepository;
    private FragmentQuizVeredictBinding binding;
    private Question question;
    private Boolean correct_answer;
    private ArrayAdapter<String> arrayAdapter;
    private AnswerViewModel answerViewModel;
    private ArrayList<Integer> correctAnswers;
    private ArrayList<String> correctAnswerWord;

    private ListView lstanswers;
    private Chronometer timer;
    private TextView questionNum;
    private TextView questionWord;
    private TextView description;
    private TextView choice;
    private Button yourChoice;
    private TextView explanation;
    private TextView correctChoices;
    private TextView score;
    private Button btnNext;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentQuizVeredictBinding.inflate(inflater,container,false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        NavController navController = Navigation.findNavController(getActivity(),R.id.nav_host_fragment_content_homepage);

        // answerViewModel = new ViewModelProvider(this).get(AnswerViewModel.class);
        apiRepository = new ViewModelProvider(getActivity()).get(HomepageActivityViewModel.class).getApiRepository();
        answerViewModel = apiRepository.getAnswerViewModel();

        Bundle bundle = getArguments();
        question = (Question) bundle.getSerializable("question");
        correct_answer = bundle.getBoolean("verdict");
        correctAnswers = bundle.getIntegerArrayList("correctAnswers");
        correctAnswerWord = new ArrayList<>();
        int answer = bundle.getInt("choice");
        int numQuestions = bundle.getInt("numQuestions");
        int counter = bundle.getInt("counter");
        int secondsLeft = bundle.getInt("timeLeft");

        questionNum = binding.pergunta;
        description = binding.description;
        questionWord = binding.question;
        timer = binding.timer;
        explanation = binding.explanation;
        lstanswers = binding.lstButton;
        choice = binding.yourChoice;
        yourChoice = binding.chosenAnswer;
        correctChoices = binding.correctAnswers;
        score = binding.score;
        btnNext = binding.btnNext;


        String numQuestionsText = "Question " + (counter+1) + "/" + numQuestions;
        questionNum.setText(numQuestionsText);
        description.setText(question.getDescription());
        questionWord.setText(question.getQuestion());
        explanation.setText(question.getExplanation());

        String secondsSpent = "Answered time: " + secondsLeft + "s";
        timer.setText(secondsSpent);

        yourChoice.setBackgroundTintList(getContext().getResources().getColorStateList(android.R.color.holo_red_light));
        if (correct_answer) {
            yourChoice.setBackgroundTintList(getContext().getResources().getColorStateList(android.R.color.holo_green_light));
        }
        if (answer == -1) {
            String notAnswered = "Not Answered";
            yourChoice.setText(notAnswered);
        } else {
            yourChoice.setText(question.getAnswers().get(answer));
        }

        for(int i = 0; i < correctAnswers.size(); i++) {
            correctAnswerWord.add(question.getAnswers().get(correctAnswers.get(i)));
        }
        arrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.lst_view_buttons, correctAnswerWord);
        lstanswers.setAdapter(arrayAdapter);

        if (question.getExplanation() == null) {
            String expText = "No further explanations";
            explanation.setText(expText);
        } else {
            explanation.setText(question.getExplanation());
        }

        if(correct_answer){
            score.setText("Scored: "+ apiRepository.getCurrentScore());
            correct_answer=false;
        }else{
            score.setText("Scored: " + apiRepository.getCurrentScore());
        }

        boolean finalAnswer = false;
        if (counter + 1 == numQuestions) {
            String viewResults = "View Results";
            btnNext.setText(viewResults);
            finalAnswer = true;
        }

        boolean finalAnswer1 = finalAnswer;
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (finalAnswer1) {
                    navController.navigate(R.id.action_quizVerdictFragment_to_quizFinalResultFragment);
                }
                else {
                    apiRepository.nextQuestion();
                    navController.navigateUp();
                }
            }
        });


        View root = binding.getRoot();

        root.setFocusableInTouchMode(true);
        root.requestFocus();
        root.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    if (i == KeyEvent.KEYCODE_BACK) {

                        return true;
                    }
                }
                return false;
            }
        });

        return root;
    }

}
