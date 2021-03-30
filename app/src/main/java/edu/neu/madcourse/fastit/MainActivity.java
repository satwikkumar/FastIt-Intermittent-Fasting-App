package edu.neu.madcourse.fastit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private TextView textFasting;
    private TextView textPlan;
    private TextView textLeaderboard;
    private TextView textUserProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textFasting = (TextView) findViewById(R.id.text_fasting);
        textLeaderboard = (TextView) findViewById(R.id.text_leaderboard);
        textPlan = (TextView) findViewById(R.id.text_plan);
        textUserProfile = (TextView) findViewById(R.id.text_user_profile);

        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        textFasting.setVisibility(View.GONE);
                        textLeaderboard.setVisibility(View.GONE);
                        textPlan.setVisibility(View.GONE);
                        textUserProfile.setVisibility(View.GONE);
                        switch (item.getItemId()) {
                            case R.id.action_fasting:
                                textFasting.setVisibility(View.VISIBLE);
                                break;
                            case R.id.action_leaderboard:
                                textLeaderboard.setVisibility(View.VISIBLE);
                                break;
                            case R.id.action_plan:
                                textPlan.setVisibility(View.VISIBLE);
                                break;
                            case R.id.action_user_profile:
                                textUserProfile.setVisibility(View.VISIBLE);
                                break;
                        }
                        return false;
                    }
                });
    }


}