package com.example.projetocm.ui.leaderboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projetocm.models.History;
import com.example.projetocm.R;
import com.example.projetocm.Repository;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardQuizAdapter extends RecyclerView.Adapter<LeaderboardQuizAdapter.Viewholder> implements Filterable {
    private Context context;
    private ArrayList<History> histories;
    private FirebaseStorage storage;

    private Repository repository;
    private ArrayList<History> historiesFull;

    public LeaderboardQuizAdapter(Context context, ArrayList<History> histories, Repository repository) {
        this.context = context;
        this.histories = histories;
        this.repository = repository;
        this.storage = FirebaseStorage.getInstance();

        historiesFull = new ArrayList<>(histories);
    }

    @NonNull
    @Override
    public LeaderboardQuizAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_leaderboard_layout, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardQuizAdapter.Viewholder holder, int position) {
        History history = histories.get(position);

        repository.getUserPhotoRef(history.getUserId(), position+1, holder.userName, holder.userPhoto);

        String score = "Quiz Score: " + history.getScore();
        holder.userQuizScore.setText(score);
    }

    @Override
    public int getItemCount() {
        return histories.size();
    }

    @Override
    public Filter getFilter() {
        return historyFilter;
    }

    private Filter historyFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<History> filteredHistory = new ArrayList<>();

            if (charSequence == null || charSequence.length() == 0) {
                filteredHistory.addAll(historiesFull);
            } else {
                String filteredPattern = charSequence.toString().toLowerCase().trim();

                for (History item : historiesFull) {
                    if (item.getUserName().toLowerCase().contains(filteredPattern)) {
                        filteredHistory.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredHistory;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            histories.clear();
            histories.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };


    public class Viewholder extends RecyclerView.ViewHolder {
        private ImageView userPhoto;
        private TextView userName, userQuizScore;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            userPhoto = itemView.findViewById(R.id.user_picture);
            userName = itemView.findViewById(R.id.user_name_leader);
            userQuizScore = itemView.findViewById(R.id.user_total_score);
        }
    }
}
