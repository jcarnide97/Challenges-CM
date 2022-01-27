package com.example.projetocm.ui.profile;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projetocm.models.History;
import com.example.projetocm.R;
import com.example.projetocm.Repository;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.Viewholder> implements Filterable {
    private Context context;
    private ArrayList<History> history;
    private FirebaseStorage storage;
    private Repository rep;

    NavController navController;

    private ArrayList<History> historyFull;

    public HistoryAdapter(Context context, ArrayList<History> history, Repository rep, NavController navController) {
        this.context = context;
        this.history = history;
        this.storage = FirebaseStorage.getInstance();
        this.rep = rep;

        this.navController = navController;

        historyFull = new ArrayList<>(history);
    }

    @NonNull
    @Override
    public HistoryAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_history_layout, parent, false);
        return new HistoryAdapter.Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.Viewholder holder, int position) {
        History h = history.get(position);
        rep.getQuizPhotoRef(h.getQuizId(), holder.quizPhoto);

        holder.quizName.setText(h.getQuizName());
        String score2 = "Final Score: " + h.getScore() + " pts";
        holder.historyScore.setText(score2);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("history", h);
                navController.navigate(R.id.action_historyFragment_to_history_details, bundle);
            }
        });

    }

    @Override
    public int getItemCount() {
        return history.size();
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
                filteredHistory.addAll(historyFull);
            } else {
                String filteredPattern = charSequence.toString().toLowerCase().trim();

                for (History item : historyFull) {
                    if (item.getQuizName().toLowerCase().contains(filteredPattern)) {
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
            history.clear();
            history.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };


    public class Viewholder extends RecyclerView.ViewHolder {
        private ImageView quizPhoto;
        private TextView quizName, historyScore;


        public Viewholder(@NonNull View itemView) {
            super(itemView);
            quizPhoto = itemView.findViewById(R.id.quiz_picture);
            quizName = itemView.findViewById(R.id.name_quiz);
            historyScore = itemView.findViewById(R.id.history_score);
        }
    }
}
