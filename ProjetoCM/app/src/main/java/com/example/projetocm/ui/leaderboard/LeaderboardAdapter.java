package com.example.projetocm.ui.leaderboard;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projetocm.R;
import com.example.projetocm.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.Viewholder> implements Filterable {
    private Context context;
    private ArrayList<User> users;
    private FirebaseStorage storage;

    private ArrayList<User> usersFull;

    public LeaderboardAdapter(Context context, ArrayList<User> users) {
        this.context = context;
        this.users = users;
        this.storage = FirebaseStorage.getInstance();

        usersFull = new ArrayList<>(users);
    }

    @NonNull
    @Override
    public LeaderboardAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_leaderboard_layout, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardAdapter.Viewholder holder, int position) {
        User user = users.get(position);

        StorageReference imageRef = storage.getReferenceFromUrl(user.getPhotoRef());
        final long TEN_MB = 10*1024*1024;
        imageRef.getBytes(TEN_MB).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                holder.userPhoto.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        String rankAndName = (position + 1) + ". " + user.getName();
        holder.userName.setText(rankAndName);
        String score = "Total Score: " + user.getTotalScore();
        holder.userTotalScore.setText(score);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    @Override
    public Filter getFilter() {
        return userFilter;
    }

    private Filter userFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<User> filteredUser = new ArrayList<>();
            if (charSequence == null || charSequence.length() == 0) {
                filteredUser.addAll(usersFull);
            } else {
                String filteredPattern = charSequence.toString().toLowerCase().trim();

                for (User item : usersFull) {
                    if (item.getName().toLowerCase().contains(filteredPattern)) {
                        filteredUser.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredUser;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            users.clear();
            users.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };


    public class Viewholder extends RecyclerView.ViewHolder {
        private ImageView userPhoto;
        private TextView userName, userTotalScore;


        public Viewholder(@NonNull View itemView) {
            super(itemView);
            userPhoto = itemView.findViewById(R.id.user_picture);
            userName = itemView.findViewById(R.id.user_name_leader);
            userTotalScore = itemView.findViewById(R.id.user_total_score);
        }
    }
}
