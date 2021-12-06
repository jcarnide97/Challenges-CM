package com.minichallenge21;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private ListNotesFragment listNotesFragment;
    private final String userId = generateUserId();

    public interface FragmentChanger {
        void changeFragment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listNotesFragment = new ListNotesFragment();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.list_notes_fragment, listNotesFragment);
        fragmentTransaction.commit();
    }

    public String getUserId() {
        return userId;
    }

    public String generateUserId() {
        int leftLimit = 48; // caracter 0
        int rightLimit = 122;  // caracter z
        int passwordLength = 10;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(passwordLength);
        for (int i = 0; i < passwordLength; i++) {
            int randomLimitedInt = leftLimit + (int)(random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        String passwordGenerated = buffer.toString();
        return passwordGenerated;
    }
}