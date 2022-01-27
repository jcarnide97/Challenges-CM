package com.example.projetocm.ui.quizAnswers;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ListView;
import android.widget.TextView;

import com.example.projetocm.ApiRepository;
import com.example.projetocm.HomepageActivityViewModel;
import com.example.projetocm.models.Question;
import com.example.projetocm.models.Quiz;
import com.example.projetocm.R;
import com.example.projetocm.databinding.FragmentQuizAnswerBinding;

import java.util.ArrayList;
import java.util.List;

public class QuizAnswerFragment extends Fragment {

    private FragmentQuizAnswerBinding binding;
    private ApiRepository apiRepository;

    private ArrayList<String> answers;
    private boolean correct = false;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<Integer> correctIndex;
    private Bundle bundle;

    private int scored;
    private ListView lstanswers;
    private Chronometer chronometer;
    private TextView questionNum;
    private TextView questionWord;
    private TextView description;

    private Quiz quiz;
    private Question q;

    private NavController navController;

    private boolean running;
    private long pauseOffset;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bundle = new Bundle();
        navController = Navigation.findNavController(getActivity(),R.id.nav_host_fragment_content_homepage);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        binding = FragmentQuizAnswerBinding.inflate(inflater,container,false);
        Bundle bundle = getArguments();


        quiz = (Quiz) bundle.getSerializable("quiz");

        apiRepository = new ViewModelProvider(getActivity()).get(HomepageActivityViewModel.class).getApiRepository();
        if (apiRepository.getAnswerViewModel() == null) {
            AnswerViewModel answerViewModel = new ViewModelProvider(this).get(AnswerViewModel.class);
            apiRepository.setAnswerViewModel(answerViewModel);
        }

        if (apiRepository.getQuizParameters() == null || apiRepository.getQuizParameters() != quiz)
            apiRepository.setQuizParameters(quiz);

        apiRepository.setBinding(binding);


        questionNum = binding.pergunta;
        description = binding.description;
        questionWord = binding.question;
        chronometer = binding.chronometer;
        lstanswers = binding.lstButton;


        apiRepository.getQuestions();

        apiRepository.getAnswerViewModel().getCurrentQuestion().observe(getViewLifecycleOwner(), new Observer<Question>() {
            @Override
            public void onChanged(Question question) {
                q = question;
                correctIndex = new ArrayList<>();
                for(int i=0; i< question.getCorrect_answers().size();i++){
                    if(question.getCorrect_answers().get(i))
                        correctIndex.add(i);
                }
                String numQuestionsStr = "Question " + (apiRepository.getAnswerViewModel().getCounter()+1) + "/" + quiz.getNumQuestions();
                questionNum.setText(numQuestionsStr);
                description.setText(question.getDescription());
                questionWord.setText(question.getQuestion());

                chronometer.setFormat("Time: %s");
                chronometer.setBase(SystemClock.elapsedRealtime());
                resetChronometer();
                startChronometer();

                answers = new ArrayList<>();
                answers = (ArrayList<String>) question.getAnswers();
                while(answers.contains("null")){
                    answers.remove("null");
                }
                lstanswers.setAdapter(new ListAnswersAdapter(getContext(), R.layout.lst_view_buttons, answers));
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

    private class ListAnswersAdapter extends ArrayAdapter<String> {

        private int layout;

        public ListAnswersAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
            super(context, resource, objects);
            layout = resource;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);

                Button answerBtn = (Button) convertView.findViewById(R.id.btn_answer);
                answerBtn.setText(answers.get(position));
                answerBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        scored = apiRepository.getCurrentScore();
                        pauseChronometer();
                        int seconds = (int) pauseOffset / 1000;
                        apiRepository.setCurrentTimeSpent(seconds);
                        bundle.putInt("timeLeft", seconds);

                        bundle.putInt("choice",position);
                        if (correctIndex.contains(position)){
                            correct = true;
                        } else {
                            correct = false;
                        }
                        if(correct){
                            apiRepository.currentScore(scored, seconds, quiz.getDifficulty().toString());
                            apiRepository.setCurrentCorrectAnswers();
                        }
                        bundle.putBoolean("verdict",correct);
                        bundle.putIntegerArrayList("correctAnswers", correctIndex);
                        bundle.putInt("numQuestions",quiz.getNumQuestions());
                        bundle.putSerializable("question",q);
                        bundle.putInt("counter", apiRepository.getAnswerViewModel().getCounter());

                        navController.navigate(R.id.action_quizAnswerFragment_to_quizVerdictFragment, bundle);
                    }
                });
            }
            return convertView;
        }
    }

    public void startChronometer() {
        if (!running) {
            chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            chronometer.start();
            running = true;
        }
    }

    public void pauseChronometer() {
        if (running) {
            chronometer.stop();
            pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
            running = false;
        }
    }

    public void resetChronometer(){
        chronometer.setBase(SystemClock.elapsedRealtime());
        pauseOffset = 0;
    }


}