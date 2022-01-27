package com.example.projetocm.ui.recent;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.projetocm.models.History;
import com.example.projetocm.HomepageActivityViewModel;
import com.example.projetocm.Repository;
import com.example.projetocm.databinding.FragmentRecentBinding;

import java.util.ArrayList;
import java.util.List;

public class RecentActivityFragment extends Fragment {

    private RecentActivityViewModel recentActivityViewModel;
    private FragmentRecentBinding binding;
    private Repository repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        recentActivityViewModel = new ViewModelProvider(this).get(RecentActivityViewModel.class);

        binding = FragmentRecentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final ListView lstRecent = binding.lstRecent;

        repository = new ViewModelProvider(getActivity()).get(HomepageActivityViewModel.class).getRepository();
        if (repository.getRecentActivityViewModel() == null) {
            RecentActivityViewModel recentActivityViewModel = new ViewModelProvider(this).get(RecentActivityViewModel.class);
            repository.setRecentActivityViewModel(recentActivityViewModel);
        }

        repository.updateHistoriesList();

        repository.getRecentActivityViewModel().getHistoryList().observe(getViewLifecycleOwner(), new Observer<List<History>>() {
            @Override
            public void onChanged(List<History> histories) {
                RecentScoresAdapter adapter = new RecentScoresAdapter(getContext(), new ArrayList<>(histories));
                lstRecent.setAdapter(adapter);
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
        binding = null;
    }
}
