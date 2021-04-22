package edu.neu.madcourse.fastit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import java.util.List;

public class DetailedHistoryActivity extends AppCompatActivity {

    List<FastingSession> sessionList;
    RecyclerView recyclerView;
    HistoryAdapter historyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_history);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "fastit-database").allowMainThreadQueries().build();
        FastingSessionDao fastingSessionDao = db.fastingSessionDao();
        sessionList = fastingSessionDao.getAllSessions();

        recyclerView = findViewById(R.id.history_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        historyAdapter = new HistoryAdapter(sessionList,this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(historyAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void enlargeImage(Drawable image){
        final AlertDialog.Builder imageDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.fullimage_dialog, (ViewGroup)findViewById(R.id.layout_root));
        ImageView enlargedImage = (ImageView)layout.findViewById(R.id.fullimage);
        Button backButton = layout.findViewById(R.id.back_button);
        enlargedImage.setBackground(image);
        imageDialog.setView(layout);
        imageDialog.create();
        final AlertDialog d = imageDialog.show();
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                d.dismiss();
            }
        });
//        imageDialog.setPositiveButton("Return", new DialogInterface.OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                // TODO Auto-generated method stub
//                dialog.dismiss();
//            }
//        });

    }
}