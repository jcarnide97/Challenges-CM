package com.example.projetocm.ui.profile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.projetocm.models.History;
import com.example.projetocm.R;

import java.util.ArrayList;

public class TopFiveAdapter extends ArrayAdapter<History> {

    private Context mContext;
    private ArrayList<History> historyList = new ArrayList<>();

    public TopFiveAdapter(@NonNull Context context, ArrayList<History> list) {
            super(context, 0, list);
            this.mContext = context;
            this.historyList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View listItem = convertView;

            if (listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.list_view, parent, false);

            History currentHistory = historyList.get(position);

            TextView txtScore = listItem.findViewById(R.id.txtScore);
            TextView txtScore2 = listItem.findViewById(R.id.txtScore2);

            txtScore.setText(currentHistory.getQuizName());
            txtScore2.setText(String.valueOf(currentHistory.getScore()));

            return listItem;
            }
}
