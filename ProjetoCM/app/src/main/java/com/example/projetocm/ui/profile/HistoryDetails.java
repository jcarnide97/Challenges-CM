package com.example.projetocm.ui.profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.projetocm.models.History;
import com.example.projetocm.HomepageActivityViewModel;
import com.example.projetocm.models.Quiz;
import com.example.projetocm.R;
import com.example.projetocm.Repository;
import com.example.projetocm.databinding.FragmentHistoryDetailsBinding;
import com.example.projetocm.ui.home.HomeViewModel;

import java.util.List;


public class HistoryDetails extends Fragment {
    private FragmentHistoryDetailsBinding binding;
    private Repository rep;
    private History history;

    private ImageView photoQuiz;
    private TextView nameQuiz;
    private TextView categoryQuiz;
    private TextView numRightAnswers;
    private TextView timeQuiz;
    private TextView scoreQuiz;



    public HistoryDetails() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHistoryDetailsBinding.inflate(inflater,container,false);
        View root = binding.getRoot();

        rep = new ViewModelProvider(getActivity()).get(HomepageActivityViewModel.class).getRepository();
        if (rep.getHomeViewModel() == null) {
            HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
            rep.setHomeViewModel(homeViewModel);
        }

        Bundle bundle = getArguments();

        history = (History) bundle.getSerializable("history");

        photoQuiz = root.findViewById(R.id.logo_quiz);
        nameQuiz = root.findViewById(R.id.quiz_name);
        categoryQuiz = root.findViewById(R.id.quiz_category);
        numRightAnswers = root.findViewById(R.id.quiz_right_answers);
        timeQuiz = root.findViewById(R.id.quiz_time);
        scoreQuiz = root.findViewById(R.id.quiz_score);

        Quiz quiz = getQuiz(history.getQuizId());
        rep.getQuizPhotoRef(history.getQuizId(), photoQuiz);
        nameQuiz.setText(history.getQuizName());
        categoryQuiz.setText(quiz.getCategory().toString().toUpperCase());
        String right_answers = history.getNumCorrectQuestions() + "/" + quiz.getNumQuestions();
        numRightAnswers.setText(right_answers);
        String time_spent = history.getTimeTaken() + " seconds";
        timeQuiz.setText(time_spent);
        String score = String.valueOf(history.getScore());
        scoreQuiz.setText(score);


        return root;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding=null;
    }


    public Quiz getQuiz(String quizId){
        Quiz quiz = new Quiz();
        List<Quiz> quizes = rep.getHomeViewModel().getQuizes().getValue();
        for(int i = 0; i < quizes.size(); i++){
            if(quizes.get(i).getId().equals(quizId)){
                return quizes.get(i);
            }
        }
        return quiz;
    }
}