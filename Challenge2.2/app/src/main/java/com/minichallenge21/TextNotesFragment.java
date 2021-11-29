package com.minichallenge21;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class TextNotesFragment extends Fragment implements DBOperations.Callback, MainActivity.FragmentChanger {
    private TextView textView;
    private EditText editText;
    private AtomicReference<String> atomicNotes;
    private String noteDetails;
    private String title;
    private DBOperations dbOps;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_text_notes, container, false);

        setHasOptionsMenu(true);
        Toolbar toolbar = view.findViewById(R.id.toolbar2);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        Bundle bundle = getArguments();
        title = (String) bundle.getSerializable("title");

        textView = (TextView) view.findViewById(R.id.note_title);
        textView.setText(title);
        editText = (EditText) view.findViewById(R.id.note_text);

        // access db to read note details based on the title clicked
        dbOps = new DBOperations(getActivity());
        atomicNotes = dbOps.getNoteDetails(title);
        noteDetails = atomicNotes.get();
        onCompleteRead(noteDetails);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu2, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.back) {
            changeFragment();
            return true;
        } else if (item.getItemId() == R.id.save) {
            noteDetails = editText.getText().toString();
            // write note details to db
            Boolean checkEditNoteDetails = dbOps.editNoteDetails(title, noteDetails);
            Toast.makeText(getActivity(), "Notes Saved Successfully", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCompleteRead(String result) {
        editText.setText(result);
    }

    @Override
    public void changeFragment() {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ListNotesFragment listNotesFragment = new ListNotesFragment();

        transaction.replace(R.id.list_notes_fragment, listNotesFragment);
        transaction.remove(TextNotesFragment.this);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}