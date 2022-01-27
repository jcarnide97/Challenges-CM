package com.example.projetocm.ui.home;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.projetocm.HomepageActivityViewModel;
import com.example.projetocm.models.Quiz;

import com.example.projetocm.R;
import com.example.projetocm.Repository;
import com.example.projetocm.databinding.FabDialogBinding;

import java.io.FileNotFoundException;

public class DialogFab extends DialogFragment {

    private EditText newName;
    private CardView cardView;
    private ImageView imageView;
    private EditText newNumQuestions;
    private Spinner newCategory;
    private Spinner newDifficulty;
    private HomeViewModel homeViewModel;
    private Button save;
    private FabDialogBinding binding;
    private Quiz.ApiCategory category;
    private Quiz.ApiDifficulty difficulty;
    private Bitmap temp_img;
    private static final int REQUEST_CODE_SELECT_IMAGE = 2;
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    private Repository repository;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fab_dialog, null);

        repository = new ViewModelProvider(getActivity()).get(HomepageActivityViewModel.class).getRepository();
        builder.setView(view);

        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));

        cardView = view.findViewById(R.id.imageQuiz);
        temp_img=null;

        imageView = view.findViewById(R.id.imageQuiz2);

        newName = view.findViewById(R.id.new_quiz_name);
        newNumQuestions = view.findViewById(R.id.new_quiz_num_questions);
        newCategory = view.findViewById(R.id.spn_category);
        newDifficulty = view.findViewById(R.id.spn_difficulty);
        save = view.findViewById(R.id.btn_save);

        ArrayAdapter<Quiz.ApiCategory> arrayAdapter = new ArrayAdapter<Quiz.ApiCategory>(getContext(), android.R.layout.simple_spinner_dropdown_item, Quiz.ApiCategory.values());
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newCategory.setAdapter(arrayAdapter);

        ArrayAdapter<Quiz.ApiDifficulty> arrayAdapter2 = new ArrayAdapter<Quiz.ApiDifficulty>(getContext(), android.R.layout.simple_spinner_dropdown_item, Quiz.ApiDifficulty.values());
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newDifficulty.setAdapter(arrayAdapter2);

        int img = imageView.getContext().getResources().getIdentifier("ic_menu_camera", "drawable", imageView.getContext().getPackageName());
        imageView.setImageResource(img);

        newCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                category = (Quiz.ApiCategory) adapterView.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        newDifficulty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                difficulty =(Quiz.ApiDifficulty) adapterView.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION);
                }
                else{
                    selectImage();
                }
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(newNumQuestions.getText().toString().isEmpty()) && Integer.parseInt(newNumQuestions.getText().toString()) > 20) {
                    Toast.makeText(getActivity(), "Cannot create quiz with more than 20 questions", Toast.LENGTH_SHORT).show();
                } else if (newName.getText().toString().isEmpty() || newNumQuestions.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "Missing quiz necessary parameters", Toast.LENGTH_SHORT).show();
                } else {
                    Quiz quiz = new Quiz();
                    quiz.setName(newName.getText().toString());
                    quiz.setNumQuestions(Integer.parseInt(newNumQuestions.getText().toString()));
                    quiz.setCategory(category);
                    quiz.setDifficulty(difficulty);
                    if (temp_img == null) {
                        if (quiz.getCategory().toString().equals("bash")) {
                            quiz.setImageRef("gs://cmquiz-f0d08.appspot.com/quizImages/bash.png");
                        } else if (quiz.getCategory().toString().equals("devops")) {
                            quiz.setImageRef("gs://cmquiz-f0d08.appspot.com/quizImages/devops.png");
                        } else if (quiz.getCategory().toString().equals("docker")) {
                            quiz.setImageRef("gs://cmquiz-f0d08.appspot.com/quizImages/docker.png");
                        } else if (quiz.getCategory().toString().equals("html")) {
                            quiz.setImageRef("gs://cmquiz-f0d08.appspot.com/quizImages/html.png");
                        } else if (quiz.getCategory().toString().equals("javascript")) {
                            quiz.setImageRef("gs://cmquiz-f0d08.appspot.com/quizImages/javascript.png");
                        } else if (quiz.getCategory().toString().equals("kubernetes")) {
                            quiz.setImageRef("gs://cmquiz-f0d08.appspot.com/quizImages/kubernetes.png");
                        } else if (quiz.getCategory().toString().equals("laravel")) {
                            quiz.setImageRef("gs://cmquiz-f0d08.appspot.com/quizImages/laravel.png");
                        } else if (quiz.getCategory().toString().equals("linux")) {
                            quiz.setImageRef("gs://cmquiz-f0d08.appspot.com/quizImages/linux.png");
                        } else if (quiz.getCategory().toString().equals("mysql")) {
                            quiz.setImageRef("gs://cmquiz-f0d08.appspot.com/quizImages/mysql.png");
                        } else if (quiz.getCategory().toString().equals("php")) {
                            quiz.setImageRef("gs://cmquiz-f0d08.appspot.com/quizImages/php.png");
                        } else if (quiz.getCategory().toString().equals("wordpress")) {
                            quiz.setImageRef("gs://cmquiz-f0d08.appspot.com/quizImages/wordpress.png");
                        } else if (quiz.getCategory().toString().equals("all")) {
                            quiz.setImageRef("gs://cmquiz-f0d08.appspot.com/quizImages/all.png");
                        }
                        repository.checkQuizFormat(quiz, getContext());
                    } else {
                        repository.getImgReference(quiz, temp_img, getContext());
                    }
                    alertDialog.dismiss();
                }
            }
        });

        return alertDialog;
    }
    public void selectImage(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if(intent.resolveActivity(getActivity().getPackageManager()) != null){
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){

        if(requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.length > 0){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                selectImage();
            }
            else{
                Toast toast = Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent){

        if(resultCode == RESULT_OK){
            if(requestCode == REQUEST_CODE_SELECT_IMAGE){
                if(intent != null){
                    Uri selectedImageUri = intent.getData();
                    if(selectedImageUri != null){
                        try{
                            temp_img = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(selectedImageUri));
                            imageView.setImageBitmap(temp_img);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }

        }
    }
}
