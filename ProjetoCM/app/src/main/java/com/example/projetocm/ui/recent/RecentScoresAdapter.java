package com.example.projetocm.ui.recent;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.projetocm.models.History;
import com.example.projetocm.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RecentScoresAdapter extends ArrayAdapter<History> {

    private Context mContext;
    private List<History> historyList = new ArrayList<>();

    public RecentScoresAdapter(@NonNull Context context, ArrayList<History> list) {
        super(context, 0, list);
        this.mContext = context;
        this.historyList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;

        if (listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.list_view_recent_item, parent, false);

        History currentHistory = historyList.get(position);

        TextView txtRecentBody = listItem.findViewById(R.id.txtRecentBody);
        TextView txtRecentTime = listItem.findViewById(R.id.txtRecentTime);

        String body = String.format("<span style=\"color:red\">%s</span> just took the <span style=\"color:green\">%s</span> and scored %s points",
                currentHistory.getUserName(),
                currentHistory.getQuizName(),
                String.valueOf(currentHistory.getScore()));

        Spanned spanned = Html.fromHtml(body, Html.FROM_HTML_MODE_LEGACY);

        txtRecentBody.setText(spanned);

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date past = currentHistory.getTakenOn();
        Date now = new Date();

        if (TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime()) < 60) {
            txtRecentTime.setText(TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime()) + " sec ago");
        }
        else if (TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime()) < 60) {
            txtRecentTime.setText(TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime()) + " min ago");
        }
        else if (TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime()) < 24) {

            if (TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime()) > 60 &&
                TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime()) < 120)
                txtRecentTime.setText(TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime()) + " hour ago");
            else
                txtRecentTime.setText(TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime()) + " hours ago");
        }
        else {
            if (TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime()) == 1)
                txtRecentTime.setText(TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime()) + " day ago");
            else
                txtRecentTime.setText(TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime()) + " days ago");
        }
        return listItem;
    }
}
