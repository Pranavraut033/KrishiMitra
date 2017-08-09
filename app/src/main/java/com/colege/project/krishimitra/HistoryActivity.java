package com.colege.project.krishimitra;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.preons.pranav.util.DBHelper3;

public class HistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        DBHelper3 dbHelper3 = new DBHelper3(this);
        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(new CustomAdapter2(this,dbHelper3.getEverything()));
    }
}
