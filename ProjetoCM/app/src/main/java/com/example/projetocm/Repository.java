package com.example.projetocm;    // ViewModels
AnswerViewModel answerViewModel;
        HomepageActivityViewModel homepageActivityViewModel;
        HomeViewModel homeViewModel;
        RecentActivityViewModel recentActivityViewModel;
        SocialViewModel socialViewModel;
        ChatViewModel chatViewModel;
        LeaderboardViewModel leaderboardViewModel;
        ProfileViewModel profileViewModel;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.projetocm.models.ChatMessage;
import com.example.projetocm.models.History;
import com.example.projetocm.models.Quiz;
import com.example.projetocm.models.User;
import com.example.projetocm.ui.chat.ChatViewModel;
import com.example.projetocm.ui.home.HomeViewModel;
import com.example.projetocm.ui.leaderboard.LeaderboardViewModel;
import com.example.projetocm.ui.profile.ProfileViewModel;
import com.example.projetocm.ui.quizAnswers.AnswerViewModel;
import com.example.projetocm.ui.recent.RecentActivityViewModel;
import com.example.projetocm.ui.social.SocialViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Repository {
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseDatabase realtimedb;
    private final String TAG = "REPOSITORY";
    private final String URL = "https://quizapi.io/api/v1/questions";
    private final String API_KEY = "4Ja1rLIi8sxL4uYBx0atzV081xRHqvcwpxnBENxD";
    private boolean checked;


    public interface repositoryInterface{
        public void onUpdated(String updated_msg);
        public void onUpdatedHistory(ArrayList<History> history, int flag);
    }

    public Repository(){
        this.db = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
        this.storage = FirebaseStorage.getInstance();
        this.realtimedb = FirebaseDatabase.getInstance();
    }

    public Repository(HomepageActivityViewModel homepageActivityViewModel) {
        this();
        this.homepageActivityViewModel = homepageActivityViewModel;
    }

    public void updateCurrentUserData() {
        if (this.auth.getCurrentUser() == null) {
            Log.e(TAG, "user is null!");
            return;
        }

        db.collection("users").document(auth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.i(TAG, "Retrieval is successful!");
                                User retrievedProfile = document.toObject(User.class);
                                homepageActivityViewModel.setCurrentUser(retrievedProfile);
                            } else {
                                // There is no profile, so create it with current user data
                                FirebaseUser currentUser = auth.getCurrentUser();
                                String userId = currentUser.getUid();
                                String userName = currentUser.getDisplayName();
                                String userEmail = currentUser.getEmail();
                                String userBiography = "Hello! I'm using QuizAPP";
                                User newProfile = new User(userId, userName, userEmail, userBiography, null);

                                db.collection("users").document(currentUser.getUid()).set(newProfile);
                                homepageActivityViewModel.setCurrentUser(newProfile);
                            }
                        }
                        else {
                            Log.e(TAG, "error retrieving current user data!");
                        }
                    }
                });

    }


    public void changesUser(String username, String description, Bitmap photo, repositoryInterface callback) {
        User old_user = homepageActivityViewModel.getCurrentUser().getValue();


        StorageReference storageRef = storage.getReferenceFromUrl("gs://cmquiz-f0d08.appspot.com/userPhotos");
        StorageReference photoRef = storageRef.child(auth.getCurrentUser().getUid() + ".jpg");


        if(photo != null){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = photoRef.putBytes(data);
            uploadTask
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            db.collection("users").document(auth.getCurrentUser().getUid()).update("name", username, "biography", description, "photoRef", photoRef.toString())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "User successfully updated!");
                                            User new_user = new User();
                                            new_user.setName(username);
                                            new_user.setBiography(description);
                                            new_user.setEmail(old_user.getEmail());
                                            new_user.setPhotoRef(photoRef.toString());
                                            new_user.setTotalScore(old_user.getTotalScore());
                                            homepageActivityViewModel.setCurrentUser(new_user);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error updating User", e);
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error updating Photo", e);
                        }
                    });
        }
        else{
            db.collection("users").document(auth.getCurrentUser().getUid()).update("name", username, "biography", description)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "User successfully updated!");
                            User new_user = new User();
                            new_user.setName(username);
                            new_user.setBiography(description);
                            new_user.setEmail(old_user.getEmail());
                            new_user.setPhotoRef(old_user.getPhotoRef());
                            new_user.setTotalScore(old_user.getTotalScore());
                            homepageActivityViewModel.setCurrentUser(new_user);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error updating User", e);
                        }
                    });
        }

        callback.onUpdated("Saved");

    }

    public void checkQuizFormat(Quiz quiz, Context context){
        RequestQueue queue = Volley.newRequestQueue(context);
        String uri = String.format("%s?apiKey=%s", URL, API_KEY);

        String difficulty = quiz.getDifficulty().toString();
        String num_questions = String.valueOf(quiz.getNumQuestions());
        if (quiz.getCategory().toString().compareTo("all") == 0)
            uri = String.format("%s&difficulty=%s&limit=%s", uri, difficulty, num_questions);
        else {
            String category = quiz.getCategory().toString();
            uri = String.format("%s&tags=%s&difficulty=%s&limit=%s", uri, category, difficulty, num_questions);
        }
        System.out.println(uri);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, uri, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                ArrayList<Quiz> quizzes = (ArrayList<Quiz>) homeViewModel.getQuizes().getValue();
                for (Quiz q : quizzes) {
                    if (q.getName().equals(quiz.getName())) {
                        Toast.makeText(context.getApplicationContext(), "Name already in use", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (q.getCategory().toString().equals(quiz.getCategory().toString()) && q.getNumQuestions() == quiz.getNumQuestions() && q.getDifficulty().toString().equals(quiz.getDifficulty().toString())) {
                        Toast.makeText(context, "Quiz with these parameters already exists", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
               
                quiz.setCreatedBy(auth.getCurrentUser().getUid());;
                db.collection("quiz").document().set(quiz)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        db.collection("quiz")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            if (document.toObject(Quiz.class).getName().equals(quiz.getName())) {
                                                quiz.setId(document.getId());
                                                quizzes.add(quiz);
                                                homeViewModel.setQuizes(quizzes);
                                            }
                                        }
                                    }
                                });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "FAILED");
                    }
                });

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context.getApplicationContext(), "Cannot create quiz with given parameters", Toast.LENGTH_SHORT).show();
                System.out.println(error.toString());
            }
        });
        queue.add(jsonArrayRequest);
    }


    public void getImgReference(Quiz quiz, Bitmap photo, Context context) {
        StorageReference storageRef = storage.getReferenceFromUrl("gs://cmquiz-f0d08.appspot.com/quizImages");

        String photoName = quiz.getName().replaceAll("\\s+", "");
        StorageReference photoRef = storageRef.child(photoName + ".png");

        if (photo != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = photoRef.putBytes(data);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    quiz.setImageRef(photoRef.toString());
                    checkQuizFormat(quiz, context.getApplicationContext());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Error updating User", e);
                }
            });
        }
    }

    public void updateQuizes() {

        db.collection("quiz")
                .orderBy("category", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            ArrayList<Quiz> quizzes = new ArrayList<>();
                            if(task.getResult().isEmpty()){
                                Quiz quiz = new Quiz("Penguin Quiz", Quiz.ApiCategory.LINUX, "gs://cmquiz-f0d08.appspot.com/quizImages/linux.png",5, Quiz.ApiDifficulty.EASY,auth.getUid());
                                Quiz quiz2 = new Quiz("Quiz ReMix",Quiz.ApiCategory.ALL, "gs://cmquiz-f0d08.appspot.com/quizImages/all.png", 10, Quiz.ApiDifficulty.MEDIUM,auth.getUid());
                                Quiz quiz3 = new Quiz("Whale Quiz",Quiz.ApiCategory.DOCKER, "gs://cmquiz-f0d08.appspot.com/quizImages/docker.png", 5, Quiz.ApiDifficulty.EASY,auth.getUid());
                                Quiz quiz4 = new Quiz("Bash your head",Quiz.ApiCategory.BASH, "gs://cmquiz-f0d08.appspot.com/quizImages/bash.png", 4, Quiz.ApiDifficulty.EASY,auth.getUid());
                                quizzes.add(quiz2);
                                quizzes.add(quiz);
                                quizzes.add(quiz3);
                                quizzes.add(quiz4);
                                db.collection("quiz").document().set(quiz);
                                db.collection("quiz").document().set(quiz2);
                                db.collection("quiz").document().set(quiz3);
                                db.collection("quiz").document().set(quiz4);
                            }
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Quiz quiz = document.toObject(Quiz.class);
                                quiz.setId(document.getId());

                                quizzes.add(quiz);

                            }

                            homeViewModel.setQuizes(quizzes);
                        } else{
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public void updateHistory(History history){
        history.setUserId(auth.getCurrentUser().getUid());
        history.setUserName(auth.getCurrentUser().getDisplayName());
        db.collection("history")
                .add(history)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        Log.w(TAG, "SUCCESS");
                    }
                });

    }


    public void getOtherHistories(String userId) {
        db.collection("history")
                .orderBy("score", Query.Direction.DESCENDING)
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            ArrayList<History> histories = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                histories.add(document.toObject(History.class));
                            }
                            socialViewModel.setHistories(histories);
                        }else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
      }

    public void getUserHistory(Repository.repositoryInterface callback){
        db.collection("history").whereEqualTo("userId", auth.getCurrentUser().getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        ArrayList<History> history = new ArrayList<History>();
                        List<DocumentSnapshot> result = queryDocumentSnapshots.getDocuments();
                        for(DocumentSnapshot r : result){
                            history.add(r.toObject(History.class));
                        }
                        int flag;
                        if(!history.isEmpty()){
                            flag = 1;
                        }
                        else{
                            flag = -1;
                        }
                        callback.onUpdatedHistory(history, flag);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating Photo", e);
                    }
                });
    }

    public void updateHistoriesList() {
        db.collection("history")
                .orderBy("takenOn", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<History> histories = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                History history = document.toObject(History.class);

                                histories.add(history);
                            }
                            recentActivityViewModel.setHistoryList(histories);
                        } else {
                            Log.e(TAG, "Couldn't retrieve history data!");
                        }
                    }
                });

    }

    public void updateUsers(){
        db.collection("users")
                .orderBy("name", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<User> users = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                User user = document.toObject(User.class);
                                if(user.getEmail().equals(auth.getCurrentUser().getEmail())){
                                    continue;
                                }
                                if(user.getPhotoRef()==null){
                                    user.setPhotoRef("gs://cmquiz-f0d08.appspot.com/userPhotos/avatar.png");
                                }
                                users.add(user);

                            }
                            socialViewModel.setUsers(users);
                        }
                    }
                });
    }

    public void sendGlobalChatMessage(String text) {
        ChatMessage chatMessage = new ChatMessage(text, auth.getCurrentUser().getDisplayName());

        realtimedb.getReference()
                .child("globalchat")
                .push()
                .setValue(chatMessage);
    }

    public void sendQuizChatMessage(String quizName, String text) {
        ChatMessage chatMessage = new ChatMessage(text, auth.getCurrentUser().getDisplayName());

        realtimedb.getReference()
                .child(quizName)
                .push()
                .setValue(chatMessage);
    }
  
    public void updateUserScore(int quizScore) {
        int currentScore = homepageActivityViewModel.getCurrentUser().getValue().getTotalScore() + quizScore;

        db.collection("users")
                .document(auth.getCurrentUser().getUid())
                .update("totalScore", currentScore)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        homepageActivityViewModel.getCurrentUser().getValue().setTotalScore(currentScore);
                    }
                });
    }

    public void getListUsers() {
        db.collection("users")
                .orderBy("totalScore", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<User> users = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                User user = document.toObject(User.class);
                                if(user.getPhotoRef()==null){
                                    user.setPhotoRef("gs://cmquiz-f0d08.appspot.com/userPhotos/avatar.png");
                                }
                                users.add(user);

                            }
                            leaderboardViewModel.setUserList(users);
                        }
                    }
                });
    }

    public void getListHistories(String quizName) {
        db.collection("history")
                .whereEqualTo("quizName", quizName)
                .orderBy("score", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<History> histories = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                History history = document.toObject(History.class);
                                histories.add(history);
                            }
                            leaderboardViewModel.setHistoryList(histories);
                        }
                    }
                });
    }

    public void getUserPhotoRef(String userId, int position, TextView userName, ImageView photo) {
        db.collection("users")
                .document(userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String username = document.getString("name");
                                String diplay = position + ". " + username;
                                userName.setText(diplay);
                                String photoRef = document.getString("photoRef");
                                if (photoRef == null) {
                                    photoRef = "gs://cmquiz-f0d08.appspot.com/userPhotos/avatar.png";
                                }
                                StorageReference imageRef = storage.getReferenceFromUrl(photoRef);
                                final long TEN_MB = 10 * 1024*1024;
                                imageRef.getBytes(TEN_MB).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                    @Override
                                    public void onSuccess(byte[] bytes) {
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                        photo.setImageBitmap(bitmap);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
        });

    }
            
  
    public void getQuizPhotoRef(String quizId, ImageView photo) {
        db.collection("quiz")
                .document(quizId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String photoRef = document.getString("imageRef");

                                StorageReference imageRef = storage.getReferenceFromUrl(photoRef);
                                final long TEN_MB = 10 * 1024*1024;
                                imageRef.getBytes(TEN_MB).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                    @Override
                                    public void onSuccess(byte[] bytes) {
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                        photo.setImageBitmap(bitmap);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });
    }


    public AnswerViewModel getAnswerViewModel() {
        return answerViewModel;
    }

    public void setAnswerViewModel(AnswerViewModel answerViewModel) {
        this.answerViewModel = answerViewModel;
    }

    public HomepageActivityViewModel getHomepageActivityViewModel() {
        return homepageActivityViewModel;
    }

    public void setHomepageActivityViewModel(HomepageActivityViewModel homepageActivityViewModel) {
        this.homepageActivityViewModel = homepageActivityViewModel;
    }

    public HomeViewModel getHomeViewModel() {
        return homeViewModel;
    }

    public void setHomeViewModel(HomeViewModel homeViewModel) {
        this.homeViewModel = homeViewModel;
    }

    public SocialViewModel getSocialViewModel() {
        return socialViewModel;
    }

    public void setSocialViewModel(SocialViewModel socialViewModel) {
        this.socialViewModel = socialViewModel;
    }

    public RecentActivityViewModel getRecentActivityViewModel() {
        return recentActivityViewModel;
    }

    public void setRecentActivityViewModel(RecentActivityViewModel recentActivityViewModel) {
        this.recentActivityViewModel = recentActivityViewModel;
    }

    public ChatViewModel getChatViewModel() {
        return chatViewModel;
    }

    public void setChatViewModel(ChatViewModel chatViewModel) {
        this.chatViewModel = chatViewModel;
    }

    public LeaderboardViewModel getLeaderboardViewModel() {
        return leaderboardViewModel;
    }

    public void setLeaderboardViewModel(LeaderboardViewModel leaderboardViewModel) {
        this.leaderboardViewModel = leaderboardViewModel;
    }

    public ProfileViewModel getProfileViewModel() {
        return profileViewModel;
    }

    public void setProfileViewModel(ProfileViewModel profileViewModel) {
        this.profileViewModel = profileViewModel;
    }
}
