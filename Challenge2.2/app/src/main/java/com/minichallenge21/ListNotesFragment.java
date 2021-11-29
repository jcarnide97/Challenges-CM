package com.minichallenge21;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;


public class ListNotesFragment extends Fragment implements DBOperations.Callback, MainActivity.FragmentChanger {
    private ListView listView;
    private ArrayList<String> titleList;
    private String newTitle;
    private String removeNote;
    private ArrayAdapter arrayAdapter;
    private DBOperations dbOps;
    private String title, noteDetails;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_notes, container, false);

        setHasOptionsMenu(true);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        listView = (ListView) view.findViewById(R.id.list_view);

        dbOps = new DBOperations(getActivity());
        readData();

        arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, titleList);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                title = (String) listView.getItemAtPosition(i);
                changeFragment();
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int pos, long l) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setTitle("Note Options");
                dialog.setPositiveButton("Edit Title", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AlertDialog.Builder dialog2 = new AlertDialog.Builder(getActivity());
                        dialog2.setTitle("New Note Title");

                        final EditText noteTitle = new EditText(getActivity());
                        noteTitle.setInputType(InputType.TYPE_CLASS_TEXT);
                        dialog2.setView(noteTitle);

                        dialog2.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String oldNoteTitle = (String) listView.getItemAtPosition(pos);
                                String nTitle = noteTitle.getText().toString();
                                if ((!titleList.contains(nTitle))) {
                                    editNote(oldNoteTitle, nTitle);
                                    arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, titleList);
                                    listView.setAdapter(arrayAdapter);
                                    listView.requestLayout();
                                } else {
                                    Toast.makeText(getActivity(), "Title Already Exists", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        dialog2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                        dialog2.show();
                    }
                });
                dialog.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        removeNote = (String) listView.getItemAtPosition(pos);
                        deleteNote(removeNote);
                        arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, titleList);
                        listView.setAdapter(arrayAdapter);
                        listView.requestLayout();
                    }
                });
                dialog.show();
                return true;
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_activity, menu);

        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Type here to search");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                arrayAdapter.getFilter().filter(s);
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add_title) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setTitle("New Note Title");

            final EditText noteTitle = new EditText(getActivity());
            noteTitle.setInputType(InputType.TYPE_CLASS_TEXT);
            dialog.setView(noteTitle);

            dialog.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    newTitle = noteTitle.getText().toString();
                    if (!(titleList.contains(newTitle))) {
                        addTitle(newTitle);
                        listView.requestLayout();
                    } else {
                        Toast.makeText(getActivity(), "Title Already Exists", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            dialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void readData() {
        if (null == titleList) {
            titleList = new ArrayList<String>();
        }
        // access db to read data
        titleList = dbOps.getTitles();
    }

    public void addTitle(String newTitle) {
        if (null == titleList) {
            titleList = new ArrayList<String>();
        }
        titleList.add(newTitle);
        // access db to insert new note with title and empty details
        String details = "";
        Boolean checkInsertData = dbOps.insertNoteData(newTitle, details);
        Toast.makeText(getActivity(), "New Note Created", Toast.LENGTH_SHORT).show();
    }

    public void deleteNote(String noteTitle) {
        titleList.remove(noteTitle);
        // access db to delete note (title and details)
        Boolean checkDeleteData = dbOps.deleteNoteData(noteTitle);
        Toast.makeText(getActivity(), "Note Successfully Deleted", Toast.LENGTH_SHORT).show();
    }

    public void editNote(String oldTitle, String newTitle) {
        for (int i = 0; i < titleList.size(); i++) {
            if (titleList.get(i).equals(oldTitle)) {
                titleList.set(i, newTitle);
            }
        }
        // access db to change name of title (maintaining details the same)
        Boolean checkEditNote = dbOps.editNoteTitle(oldTitle, newTitle);
        Toast.makeText(getActivity(), "Note Title Changed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCompleteRead(String result) {
        noteDetails = result;
    }

    @Override
    public void changeFragment() {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        TextNotesFragment textNotesFragment = new TextNotesFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable("title", title);
        textNotesFragment.setArguments(bundle);

        transaction.replace(R.id.text_notes_fragment, textNotesFragment);
        transaction.remove(ListNotesFragment.this);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}