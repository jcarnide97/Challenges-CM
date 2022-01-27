package com.example.projetocm.ui.social;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.projetocm.models.History;
import com.example.projetocm.HomepageActivityViewModel;
import com.example.projetocm.R;
import com.example.projetocm.Repository;
import com.example.projetocm.models.User;
import com.example.projetocm.databinding.FragmentOtherUserProfileBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.util.ArrayList;
import java.util.List;

public class OtherUserProfile extends Fragment {
    private SocialViewModel socialViewModel;
    private Repository repository;
    private FragmentOtherUserProfileBinding binding;

    private final String TAG = "OTHER PROFILE FRAGMENT";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Bundle bundle = getArguments();

        User user = (User) bundle.getSerializable("user");

        binding = FragmentOtherUserProfileBinding.inflate(inflater,container,false);

        repository = new ViewModelProvider(getActivity()).get(HomepageActivityViewModel.class).getRepository();
        View root = binding.getRoot();
        ImageView imageView = binding.otheUserImgProfile2;
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

        TextView username = binding.otherUsernameProfile;
        username.setText(user.getName());
        TextView userDescription = binding.otherDescriptionProfile;
        userDescription.setText(user.getBiography());

        TextView userScore = binding.otherScoreProfile;
        userScore.setText(String.valueOf(user.getTotalScore()));

        repository.getOtherHistories(user.getUserId());
        ListView listViewTopScores = binding.otherListTopScores;
        repository.getSocialViewModel().getHistories().observe(getViewLifecycleOwner(), new Observer<List<History>>() {
            @Override
            public void onChanged(List<History> histories) {
                ListTopScoresAdapter top = new ListTopScoresAdapter(getContext(), histories);
                listViewTopScores.setAdapter(top);
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
    public void onDestroyView() {
        super.onDestroyView();
        binding=null;
    }

    public class ListTopScoresAdapter extends ArrayAdapter<History> {
        private Context context;
        private List<History> histories = new ArrayList<>();


        public ListTopScoresAdapter(@NonNull Context context, List<History> list) {
            super(context, 0, list);
            this.context = context;
            this.histories = list;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View listItem = convertView;
            if (listItem == null) {
                listItem = LayoutInflater.from(context).inflate(R.layout.list_view, parent, false);
            }

            History currentHistory = histories.get(position);
            TextView textScore = listItem.findViewById(R.id.txtScore);
            TextView textScore2 = listItem.findViewById(R.id.txtScore2);

            textScore.setText(currentHistory.getQuizName());
            textScore2.setText(String.valueOf(currentHistory.getScore()));

            return listItem;
        }
    }
}
