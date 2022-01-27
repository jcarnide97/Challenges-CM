package com.example.projetocm.ui.leaderboard;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.projetocm.models.History;
import com.example.projetocm.HomepageActivityViewModel;
import com.example.projetocm.R;
import com.example.projetocm.Repository;
import com.example.projetocm.databinding.FragmentLeaderboardQuizBinding;

import java.util.ArrayList;
import java.util.List;


public class LeaderboardQuizFragment extends Fragment {

    private LeaderboardViewModel leaderboardViewModel;
    private FragmentLeaderboardQuizBinding binding;
    private Repository repository;

    private LeaderboardQuizAdapter leaderboardQuizAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        leaderboardViewModel = new ViewModelProvider(this).get(LeaderboardViewModel.class);

        binding = FragmentLeaderboardQuizBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Bundle bundle = getArguments();
        String quizName = bundle.getString("quizName");

        repository = new ViewModelProvider(getActivity()).get(HomepageActivityViewModel.class).getRepository();
        if (repository.getLeaderboardViewModel() == null) {
            LeaderboardViewModel leaderboardViewModel = new ViewModelProvider(this).get(LeaderboardViewModel.class);
            repository.setLeaderboardViewModel(leaderboardViewModel);
        }

        repository.getListHistories(quizName);

        repository.getLeaderboardViewModel().getHistoryList().observe(getViewLifecycleOwner(), new Observer<List<History>>() {
            @Override
            public void onChanged(List<History> histories) {
                setUpRecyclerView(histories);
            }
        });

        return root;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.leaderboard_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.leaderboard_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search by user name");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                leaderboardQuizAdapter.getFilter().filter(s);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void setUpRecyclerView(List<History> histories) {
        RecyclerView recyclerView = binding.cardLeaderboardQuiz;
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        leaderboardQuizAdapter = new LeaderboardQuizAdapter(getContext(), new ArrayList<>(histories), repository);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(leaderboardQuizAdapter);
    }
}