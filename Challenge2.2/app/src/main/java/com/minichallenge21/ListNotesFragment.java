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

import android.os.Handler;
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

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

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
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;


public class ListNotesFragment extends Fragment implements DBOperations.Callback, MainActivity.FragmentChanger {
    private ListView listView;
    private ArrayList<String> titleList = new ArrayList<>();
    private String newTitle;
    private String removeNote;
    private ArrayAdapter arrayAdapter;
    private DBOperations dbOps;
    private String title, noteDetails;
    private ArrayList<String> myTopicsList = new ArrayList<>();

    private MQTTHelper mqttHelper;
    private final DBOperations.Callback callback = this;
    private String userId;

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

        MainActivity activity = (MainActivity) getActivity();
        userId = activity.getUserId();
        startMqtt();

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
        } else if (item.getItemId() == R.id.subscribe) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setTitle("Subscribe to Topic");

            final EditText topicName = new EditText(getActivity());
            topicName.setInputType(InputType.TYPE_CLASS_TEXT);
            dialog.setView(topicName);

            dialog.setPositiveButton("Subscribe", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String subTopic = topicName.getText().toString().toLowerCase();
                    if (!(myTopicsList.contains(subTopic))) {
                        myTopicsList.add(subTopic);
                        Boolean checkSubTopic = dbOps.insertTopic(subTopic);
                        // add subscription to topic in mqtt
                        mqttHelper.subscribeToTopic(subTopic);
                        Toast.makeText(getActivity(), "Subscribed to " + subTopic, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Already Subscribed to " + subTopic, Toast.LENGTH_SHORT).show();
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
        } else if (item.getItemId() == R.id.unsubscribe) {
            if (myTopicsList.isEmpty()) {
                Toast.makeText(getActivity(), "You're not subscribed to any topic", Toast.LENGTH_SHORT).show();
            } else {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setTitle("Select a Topic to Unsubscribe");

                dialog.setItems(myTopicsList.toArray(new String[myTopicsList.size()]), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String unsubTopic = myTopicsList.get(i);
                        // UNSUBSCRIBE TO TOPIC
                        Toast.makeText(getActivity(), "Unsubscribed to " + unsubTopic, Toast.LENGTH_SHORT).show();
                        mqttHelper.unsubscribeToTopic(unsubTopic);
                        myTopicsList.remove(unsubTopic);
                        Boolean checkUnsubTopic = dbOps.deleteTopic(unsubTopic);
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
        } else if (item.getItemId() == R.id.publish) {
            if (titleList.isEmpty()) {
                Toast.makeText(getActivity(), "You don't have any notes", Toast.LENGTH_SHORT).show();
            } else {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setTitle("Select a Note to Publish");

                dialog.setItems(titleList.toArray(new String[titleList.size()]), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int iNote) {
                        AlertDialog.Builder dialog2 = new AlertDialog.Builder(getActivity());
                        dialog2.setTitle("Publish to Which Topic");

                        final EditText topicName = new EditText(getActivity());
                        topicName.setInputType(InputType.TYPE_CLASS_TEXT);
                        dialog2.setView(topicName);

                        dialog2.setPositiveButton("Publish", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String pubTopic = topicName.getText().toString().toLowerCase();
                                String pubTitle = titleList.get(iNote);
                                AtomicReference<String> atomicNotes = dbOps.getNoteDetails(pubTitle, callback);
                                String pubDetails = atomicNotes.get();
                                // PUBLISH NOTE TO TOPIC
                                mqttHelper.publishNote(pubTopic, mqttHelper.getName(), pubTitle, pubDetails);
                                Toast.makeText(getActivity(), titleList.get(iNote) + " Published to " + pubTopic, Toast.LENGTH_SHORT).show();
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
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                dialog.show();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void readData() {
        // access db to read titles
        if (null == titleList) {
            titleList = new ArrayList<String>();
        }
        titleList = dbOps.getTitles();
        // access db to read topics
        if (null == myTopicsList) {
            myTopicsList = new ArrayList<String>();
        }
        myTopicsList = dbOps.getTopics();
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

    public void startMqtt() {
        mqttHelper = new MQTTHelper(getActivity().getApplicationContext(), userId);
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                if (myTopicsList != null) {
                    for (String topic : myTopicsList) {
                        mqttHelper.subscribeToTopic(topic);
                    }
                }
            }

            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                if (myTopicsList.contains(topic)) {
                    String msgReceived = new String(message.getPayload());
                    String[] note = msgReceived.split("###");
                    String name = note[0];
                    String noteTitle = note[1];
                    String noteDetails = note[2];
                    if (name.equals(mqttHelper.getName())) {
                        // User does not receive its own messages back
                        System.out.println("Don't receive my own messages");
                    } else {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                        dialog.setTitle("New Note " + noteTitle + " Received! Want to add it?");
                        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (!(titleList.contains(noteTitle))) {
                                    titleList.add(noteTitle);
                                    dbOps.insertNoteData(noteTitle, noteDetails);
                                    listView.requestLayout();
                                    Toast.makeText(getActivity(), "New Note Added", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getActivity(), "You already have a note with this title", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                        dialog.show();
                    }
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }
}