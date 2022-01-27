package com.example.projetocm.ui.chat;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.projetocm.models.ChatMessage;
import com.example.projetocm.HomepageActivityViewModel;
import com.example.projetocm.R;
import com.example.projetocm.Repository;
import com.example.projetocm.databinding.FragmentQuizChatBinding;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class QuizChatFragment extends Fragment {
    private FragmentQuizChatBinding binding;
    private Repository repository;

    private FirebaseListAdapter<ChatMessage> adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        binding = FragmentQuizChatBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        repository = new ViewModelProvider(getActivity()).get(HomepageActivityViewModel.class).getRepository();
        if (repository.getChatViewModel() == null) {
            ChatViewModel chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);
            repository.setChatViewModel(chatViewModel);
        }

        Bundle bundle = getArguments();
        String chatName = bundle.getString("quizName");


        Query query = FirebaseDatabase.getInstance().getReference().child(chatName);
        FirebaseListOptions<ChatMessage> options = new FirebaseListOptions.Builder<ChatMessage>()
                .setQuery(query, ChatMessage.class)
                .setLayout(R.layout.chat_list_view_item)
                .build();


        adapter = new FirebaseListAdapter<ChatMessage>(options) {
            @Override
            protected void populateView(@NonNull View v, @NonNull ChatMessage model, int position) {
                TextView messageText = (TextView) v.findViewById(R.id.message_text);
                TextView messageUser = (TextView) v.findViewById(R.id.message_user);
                TextView messageTime = (TextView) v.findViewById(R.id.message_time);

                messageText.setText(model.getMessageText());
                messageUser.setText(model.getMessageUser());

                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                        model.getMessageTime()));
            }
        };
        adapter.startListening();

        binding.lstQuizChat.setAdapter(adapter);

        final TextView input = binding.quizInput;

        binding.fabQuizSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (input.getText().toString().isEmpty())
                    return;
                repository.sendQuizChatMessage(chatName, input.getText().toString());
                input.setText("");
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        adapter.stopListening();
        binding = null;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();

        super.onCreateOptionsMenu(menu, inflater);
    }
}
