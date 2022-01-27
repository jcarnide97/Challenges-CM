package com.example.projetocm.ui.social;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projetocm.R;
import com.example.projetocm.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.Viewholder> implements Filterable {
    private Context context;
    private ArrayList<User> users;
    private NavController navController;
    private FirebaseStorage storage;

    private ArrayList<User> usersFull;

    public UserAdapter(Context context, ArrayList<User> users, NavController navController) {
        this.context = context;
        this.users = users;
        this.navController = navController;
        this.storage = FirebaseStorage.getInstance();

        usersFull = new ArrayList<>(users);
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_people_layout, parent,false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        User user = users.get(position);

        StorageReference imageRef = storage.getReferenceFromUrl(user.getPhotoRef());
        final long TEN_MB = 10*1024*1024;
        imageRef.getBytes(TEN_MB).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                holder.userPhoto.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        holder.userName.setText(user.getName());
        holder.userDesc.setText(user.getBiography());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("user",user);
                navController.navigate(R.id.action_nav_social_to_otherUserProfile,bundle);
            }
        });
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

            if(charSequence == null || charSequence.length()==0){
                filteredUser.addAll(usersFull);
            }else{
                String filteredPattern = charSequence.toString().toLowerCase().trim();

                for(User item : usersFull){
                    if(item.getName().toLowerCase().contains(filteredPattern)){
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
        private TextView userName, userDesc;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            userPhoto = itemView.findViewById(R.id.user_profile_pic);
            userName = itemView.findViewById(R.id.user_name);
            userDesc = itemView.findViewById(R.id.user_description);
        }
    }

}
