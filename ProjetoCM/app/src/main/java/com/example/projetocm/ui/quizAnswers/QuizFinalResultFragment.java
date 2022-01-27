package com.example.projetocm.ui.quizAnswers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.projetocm.ApiRepository;
import com.example.projetocm.models.History;
import com.example.projetocm.HomepageActivityViewModel;
import com.example.projetocm.models.Quiz;
import com.example.projetocm.R;
import com.example.projetocm.Repository;
import com.example.projetocm.databinding.FragmentFinalResultsBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;

public class QuizFinalResultFragment extends Fragment {
    private TextView quizName;
    private TextView quizCategory;
    private ImageView quizImage;
    private TextView quizNumRightAnswers;
    private TextView quizTotalTime;
    private TextView quizTotalScore;
    private History history;
    private AnswerViewModel answerViewModel;
    private Repository repository;
    private ApiRepository apiRepository;

    private Quiz quiz;

    FragmentFinalResultsBinding binding;

    NavController navController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();

        navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_homepage);

        binding = FragmentFinalResultsBinding.inflate(inflater, container, false);

        quizName = binding.quizFinalName;
        quizCategory = binding.quizFinalCategory;
        quizImage = binding.quizLogo;
        quizNumRightAnswers = binding.quizNumRightAnswers;
        quizTotalTime = binding.quizTotalTime;
        quizTotalScore = binding.quizScore;

        View root = binding.getRoot();

        repository = new ViewModelProvider(getActivity()).get(HomepageActivityViewModel.class).getRepository();
        apiRepository = new ViewModelProvider(getActivity()).get(HomepageActivityViewModel.class).getApiRepository();

        answerViewModel = apiRepository.getAnswerViewModel();
        quiz = apiRepository.getQuizParameters();

        quizName.setText(quiz.getName());
        quizCategory.setText(quiz.getCategory().toString().toUpperCase());
        StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(quiz.getImageRef());
        final long ONE_MB = 1024*1024;
        imageRef.getBytes(ONE_MB).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                quizImage.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        String numCorrectAnswers = apiRepository.getCurrentCorrectAnswers() + "/" + quiz.getNumQuestions();
        quizNumRightAnswers.setText(numCorrectAnswers);

        String timeSpent = apiRepository.getCurrentTimeSpent() + " seconds";
        quizTotalTime.setText(timeSpent);

        String finalScore = String.valueOf(apiRepository.getCurrentScore());
        quizTotalScore.setText(finalScore);
        history = new History();

        Date date = new Date();

        history.setTakenOn(date);
        history.setScore(apiRepository.getCurrentScore());
        history.setNumCorrectQuestions(apiRepository.getCurrentCorrectAnswers());
        history.setTimeTaken(apiRepository.getCurrentTimeSpent());
        history.setQuizId(quiz.getId());
        history.setQuizName(quiz.getName());

        repository.updateHistory(history);
        repository.updateHistoriesList();
        repository.updateUserScore(apiRepository.getCurrentScore());

        apiRepository.getAnswerViewModel().setCounter(0);
        apiRepository.getAnswerViewModel().setCurrentCorrect(0);
        apiRepository.setCurrentScore(0);
        apiRepository.getAnswerViewModel().setCurrentTime(0);
        apiRepository.setQuestions(new ArrayList<>());

        root.setFocusableInTouchMode(true);
        root.requestFocus();
        root.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    if (i == KeyEvent.KEYCODE_BACK) {
                        navController.navigate(R.id.action_quizFinalResultFragment_to_nav_home);
                        return true;
                    }
                }
                return false;
            }
        });

        return root;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            navController.navigate(R.id.action_quizFinalResultFragment_to_nav_home);

        }
        return super.onOptionsItemSelected(item);
    }
}
