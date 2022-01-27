package com.example.projetocm.ui.home;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.projetocm.models.Quiz;
import com.example.projetocm.R;
import com.example.projetocm.databinding.FragmentQuizDetailsBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class QuizDetailsFragment extends Fragment {
    private NavController navController;

    private TextView quizName;
    private TextView quizCategory;
    private ImageView quizImage;
    private TextView quizNumQuestions;
    private TextView quizDifficulty;
    private Button startQuizz;

    private Quiz quiz;

    FragmentQuizDetailsBinding binding;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        Bundle bundle = getArguments();

        navController = Navigation.findNavController(getActivity(),R.id.nav_host_fragment_content_homepage);

        quiz = (Quiz) bundle.getSerializable("quiz");

        binding = FragmentQuizDetailsBinding.inflate(inflater,container,false);

        startQuizz = binding.btnStartQuiz;

        quizName = binding.quizName;
        quizCategory = binding.quizCategory;
        quizNumQuestions = binding.quizNumQuestions;
        quizImage = binding.quizLogo;
        quizDifficulty = binding.quizDifficulty;

        quizCategory.setFilters(new InputFilter[] { new InputFilter.AllCaps()});

        StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(quiz.getImageRef());
        final long TEN_MB = 10*1024*1024;
        imageRef.getBytes(TEN_MB).addOnSuccessListener(new OnSuccessListener<byte[]>() {
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

        quizName.setText(quiz.getName());
        quizCategory.setText(quiz.getCategory().toString());
        quizDifficulty.setText(quiz.getDifficulty().toString());
        quizNumQuestions.setText(String.valueOf(quiz.getNumQuestions()));


        View root = binding.getRoot();



        startQuizz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_quizDetailsFragment_to_quizAnswerFragment, bundle);
            }
        });
        return root;

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.details,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_chat) {
            Bundle bundle = new Bundle();
            bundle.putString("quizName", quiz.getName());
            navController.navigate(R.id.action_quizDetailsFragment_to_quizChatFragment, bundle);
        } else if (id == R.id.action_leaderboard) {
            Bundle bundle = new Bundle();
            bundle.putString("quizName", quiz.getName());
            navController.navigate(R.id.action_quizDetailsFragment_to_leaderboardQuizFragment, bundle);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
