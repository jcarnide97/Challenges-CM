package com.example.projetocm.ui.profile;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.projetocm.models.History;
import com.example.projetocm.HomepageActivityViewModel;
import com.example.projetocm.models.Quiz;
import com.example.projetocm.R;
import com.example.projetocm.Repository;
import com.example.projetocm.models.User;
import com.example.projetocm.databinding.FragmentProfileBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ProfileFragment extends Fragment implements Repository.repositoryInterface{
    private Repository rep;
    private FragmentProfileBinding binding;

    private TextView textViewTotalScore;
    private ProgressBar correctnessBar;
    private TextView txtBar;
    private ListView listViewTopScores;

    private final String TAG = "PROFILE FRAGMENT";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

            rep = new ViewModelProvider(getActivity()).get(HomepageActivityViewModel.class).getRepository();
            if(rep.getProfileViewModel() == null){
                ProfileViewModel profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
                rep.setProfileViewModel(profileViewModel);
            }
            if(rep.getHomepageActivityViewModel() == null){
                HomepageActivityViewModel homepageActivityViewModel = new ViewModelProvider(getActivity()).get(HomepageActivityViewModel.class);
                rep.setHomepageActivityViewModel(homepageActivityViewModel);
            }

            binding = FragmentProfileBinding.inflate(inflater, container, false);
            View root = binding.getRoot();

            textViewTotalScore = root.findViewById(R.id.scoreProfile);
            correctnessBar = root.findViewById(R.id.progress_bar2);
            txtBar = root.findViewById(R.id.txtProgressBar);
            listViewTopScores = root.findViewById(R.id.listTopScores);

            rep.getHomepageActivityViewModel().getCurrentUser().observe(getViewLifecycleOwner(), new Observer<User>() {
                @Override
                public void onChanged(User user) {
                    ImageView imageView = root.findViewById(R.id.imageProfile2);

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
                        int img = getContext().getResources().getIdentifier("ic_menu_camera", "drawable", imageView.getContext().getPackageName());
                        imageView.setImageResource(img);
                    }

                    //Profile Page info
                    TextView textViewName = root.findViewById(R.id.nameProfile);
                    textViewName.setText(user.getName());

                    TextView textViewDescription = root.findViewById(R.id.descriptionProfile);
                    textViewDescription.setText(user.getBiography());

                    textViewTotalScore = root.findViewById(R.id.scoreProfile);
                    textViewTotalScore.setText(String.valueOf(user.getTotalScore()));

                    updateHistory();
                }
            });

            return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.profile, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_history){
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_homepage);;
            navController.navigate(R.id.action_nav_profile_to_historyFragment);
        }

        else if(id == R.id.action_edit){
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_homepage);;
            navController.navigate(R.id.action_nav_profile_to_editFragment);
        }
        return false;
    }

    public void updateHistory(){
        rep.getUserHistory(this);
    }

    @Override
    public void onUpdated(String updated_msg) {

    }

    @Override
    public void onUpdatedHistory(ArrayList<History> history, int flag) {
        if(flag == -1){
            Toast toast = Toast.makeText(getContext(), "No stats available!", Toast.LENGTH_SHORT);
            toast.show();
        }
        else{
            rep.getProfileViewModel().setHistory(history);

            List<Quiz> quizzes = rep.getHomeViewModel().getQuizes().getValue();
            double percentage = getPercentage(quizzes,history);



            correctnessBar.setProgress((int) percentage);
            String s = ((int)percentage) + " % Correct Answers";
            txtBar.setText(s);

            history.sort(Comparator.comparing(History::getScore).reversed());
            TopFiveAdapter top = new TopFiveAdapter(getContext(), history);
            listViewTopScores.setAdapter(top);
        }
    }

    public double getPercentage(List<Quiz> quizzes, ArrayList<History> history){
        int sum_total = 1;
        int sum_correct = 0;
        int num_questions = 0;
        double percentage = 0;
        for(int i=0; i<history.size(); i++){
            sum_correct += history.get(i).getNumCorrectQuestions();
            for(Quiz quiz : quizzes){
                if(quiz.getId().equals(history.get(i).getQuizId())){
                    num_questions = quiz.getNumQuestions();
                    continue;
                }
            }
            sum_total += num_questions ;
        }

        percentage = (((double)sum_correct) / ((double)sum_total)) * 100;
        return percentage;
    }
}