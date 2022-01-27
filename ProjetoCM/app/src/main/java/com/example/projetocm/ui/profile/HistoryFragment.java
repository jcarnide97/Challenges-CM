package com.example.projetocm.ui.profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
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
import com.example.projetocm.databinding.FragmentHistoryBinding;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class HistoryFragment extends Fragment {
    private Repository rep;
    private FragmentHistoryBinding binding;

    private HistoryAdapter historyAdapter;

    private final String TAG = "HISTORY FRAGMENT";

    public HistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        rep = new ViewModelProvider(getActivity()).get(HomepageActivityViewModel.class).getRepository();
        if(rep.getProfileViewModel() == null){
            ProfileViewModel profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
            rep.setProfileViewModel(profileViewModel);
        }

        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        rep.getProfileViewModel().getHistory().observe(getViewLifecycleOwner(), new Observer<List<History>>() {
            @Override
            public void onChanged(List<History> history) {
                setUpRecyclerView(history);
            }
        });



        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater){
        menu.clear();
        inflater.inflate(R.menu.history_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.history_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search by quiz name");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                historyAdapter.getFilter().filter(s);
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    private void setUpRecyclerView(List<History> history) {
        RecyclerView recyclerView = binding.cardHistory;
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        history.sort(Comparator.comparing(History::getTakenOn).reversed());
        historyAdapter = new HistoryAdapter(getContext(), new ArrayList<>(history), rep, Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_homepage));
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(historyAdapter);
    }
}