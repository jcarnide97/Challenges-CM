package com.example.projetocm.ui.social;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projetocm.HomepageActivityViewModel;
import com.example.projetocm.R;
import com.example.projetocm.Repository;
import com.example.projetocm.models.User;
import com.example.projetocm.databinding.FragmentSocialBinding;

import java.util.ArrayList;
import java.util.List;

public class SocialFragment extends Fragment {

    private SocialViewModel socialViewModel;
    private FragmentSocialBinding binding;
    private UserAdapter userAdapter;
    private Repository repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        socialViewModel = new ViewModelProvider(this).get(SocialViewModel.class);

        repository = new ViewModelProvider(getActivity()).get(HomepageActivityViewModel.class).getRepository();
        binding = FragmentSocialBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        if (repository.getSocialViewModel() == null) {
            SocialViewModel socialViewModel = new ViewModelProvider(this).get(SocialViewModel.class);
            repository.setSocialViewModel(socialViewModel);
        }

        repository.updateUsers();

        repository.getSocialViewModel().getUsers().observe(getViewLifecycleOwner(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                setUpRecyclerView(users);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.social_menu,menu);

        MenuItem menuItem = menu.findItem(R.id.social_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search by user name");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                userAdapter.getFilter().filter(s);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void setUpRecyclerView(List<User> users){
        RecyclerView recyclerView = binding.CardsPeople;
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        userAdapter = new UserAdapter(getContext(),new ArrayList<>(users), Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_homepage));
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(userAdapter);

    }
}
