package com.example.projetocm.ui.profile;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.projetocm.models.History;
import com.example.projetocm.HomepageActivityViewModel;
import com.example.projetocm.R;
import com.example.projetocm.Repository;
import com.example.projetocm.models.User;
import com.example.projetocm.databinding.FragmentProfileBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class EditFragment extends Fragment implements Repository.repositoryInterface{
    private HomepageActivityViewModel homepageActivityViewModel;
    private FragmentProfileBinding binding;

    private CardView cardView;
    private ImageView imageView;
    private EditText editName;
    private EditText editBiography;

    private Bitmap temp_img;

    private final String TAG = "EDIT FRAGMENT";
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    private static final int REQUEST_CODE_SELECT_IMAGE = 2;


    public EditFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        homepageActivityViewModel = new ViewModelProvider(getActivity()).get(HomepageActivityViewModel.class);

        cardView = view.findViewById(R.id.imageProfile);
        temp_img = null;


        homepageActivityViewModel.getCurrentUser().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {

                imageView = view.findViewById(R.id.imageProfile2);
                if(user.getPhotoRef() != null && !user.getPhotoRef().isEmpty()){

                    StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(user.getPhotoRef());
                    final long TEN_MB = 10 * 1024 * 1024;
                    imageRef.getBytes(TEN_MB).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            imageView.setImageBitmap(bitmap);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error loading Photo", e);
                        }
                    });


                }
                else{
                    int img = imageView.getContext().getResources().getIdentifier("ic_menu_camera", "drawable", imageView.getContext().getPackageName());
                    imageView.setImageResource(img);
                }

                editName = view.findViewById(R.id.nameProfile);
                editName.setText(user.getName());

                editBiography = view.findViewById(R.id.descriptionProfile);
                editBiography.setText(user.getBiography());


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


        return view;

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.edit_profile, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_save){
            Repository rep = new ViewModelProvider(getActivity()).get(HomepageActivityViewModel.class).getRepository();
            rep.changesUser(editName.getText().toString(), editBiography.getText().toString(), temp_img, this);
        }
        return false;
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




    @Override
    public void onUpdated(String updated_msg){
        if(!updated_msg.equals("")){
            Toast toast = Toast.makeText(getContext(), updated_msg, Toast.LENGTH_SHORT);
            toast.show();
        }
        else{
            Toast toast = Toast.makeText(getContext(), "Nothing to Update", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    public void onUpdatedHistory(ArrayList<History> history, int flag) {

    }

}