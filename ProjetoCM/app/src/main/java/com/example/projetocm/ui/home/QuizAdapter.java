package com.example.projetocm.ui.home;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.example.projetocm.models.Quiz;
import com.example.projetocm.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.Viewholder> implements Filterable {

    private Context context;
    private ArrayList<Quiz> quizzes;
    private NavController navController;
    private FirebaseStorage storage;

    private ArrayList<Quiz> quizzesFull;

    public QuizAdapter(Context context, ArrayList<Quiz> quizzes, NavController navController) {
        this.context = context;
        this.quizzes = quizzes;
        this.navController=navController;
        this.storage = FirebaseStorage.getInstance();

        quizzesFull = new ArrayList<>(quizzes);
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_layout, parent, false);
        return new Viewholder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        Quiz quiz = quizzes.get(position);

        StorageReference imageRef = storage.getReferenceFromUrl(quiz.getImageRef());
        final long TEN_MB = 10*1024*1024;
        imageRef.getBytes(TEN_MB).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                holder.quizLogo.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        holder.quizName.setText(quiz.getName());
        holder.quizCategory.setText(quiz.getCategory().toString().toUpperCase());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("quiz",quiz);
                navController.navigate(R.id.action_nav_home_to_quizDetailsFragment, bundle);
            }
        });

    }

    @Override
    public int getItemCount() {
        return quizzes.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        private ImageView quizLogo;
        private TextView quizName, quizCategory;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            quizLogo = itemView.findViewById(R.id.quiz_logo);
            quizName = itemView.findViewById(R.id.quiz_name);
            quizCategory = itemView.findViewById(R.id.quiz_category);
        }
    }

    @Override
    public Filter getFilter() {
        return quizFilter;
    }

    private Filter quizFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<Quiz> filteredQuiz = new ArrayList<>();

            if (charSequence == null || charSequence.length() == 0) {
                filteredQuiz.addAll(quizzesFull);
            } else {
                String filteredPattern = charSequence.toString().toLowerCase().trim();

                for (Quiz item : quizzesFull) {
                    if (item.getName().toLowerCase().contains(filteredPattern)) {
                        filteredQuiz.add(item);
                        continue;
                    }
                    if (item.getCategory().toString().toLowerCase().contains(filteredPattern)) {
                        filteredQuiz.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredQuiz;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            quizzes.clear();
            quizzes.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };
}
