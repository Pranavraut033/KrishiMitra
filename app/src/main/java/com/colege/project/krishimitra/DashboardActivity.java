package com.colege.project.krishimitra;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.text.MessageFormat;

import static com.preons.pranav.util.Constants.FULL_NAME;
import static com.preons.pranav.util.Constants.preferences;

public class DashboardActivity extends AppCompatActivity {
    int[] ints = new int[]{
            R.id.browse, R.id.your, R.id.createAd
    };
    TextView[] textViews = new TextView[ints.length];
    TextView textView;

    View.OnClickListener[] listeners = new View.OnClickListener[]{
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), BrowseActivity.class));
                }
            },
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), HistoryActivity.class));

                }
            }, new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(getApplicationContext(), CreateActivity.class));
        }
    }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        textView = (TextView) findViewById(R.id.full_name);
        for (int i = 0; i < ints.length; i++) {
            textViews[i] = (TextView) findViewById(ints[i]);
            textViews[i].setOnClickListener(listeners[i]);
        }
        if(preferences != null)
            textView.setText(MessageFormat.format("Welcome, {0}", preferences.getString(FULL_NAME, "User")));
    }
}
