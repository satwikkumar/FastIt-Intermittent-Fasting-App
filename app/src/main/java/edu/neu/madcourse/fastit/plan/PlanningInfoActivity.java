package edu.neu.madcourse.fastit.plan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import edu.neu.madcourse.fastit.MainActivity;
import edu.neu.madcourse.fastit.R;

public class PlanningInfoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planning_info);
        String cycle = this.getIntent().getStringExtra("Plan");
        TextView title = findViewById(R.id.title);
        TextView duration = findViewById(R.id.duration);
        Date startDate=new Date();
        String currentTime = new SimpleDateFormat("h:mm a", Locale.getDefault()).format(startDate);
        double planHours = Double.parseDouble(cycle.split("-")[0]);
        Date endDate = new Date();
        endDate.setTime(System.currentTimeMillis()+(long)(60*60*planHours*1000));
        String endTime = new SimpleDateFormat("h:mm a", Locale.getDefault()).format(endDate);
        String endDay = "Today, ";
        if(startDate.getDate()!=endDate.getDate()){
            endDay = "Tomorrow, ";
        }
        title.setText(cycle);
        duration.setText(Html.fromHtml("<b>Start:</b>\t\t\t\tToday, " + currentTime + "<br><br>"
                + "<b>End (expected):</b>\t\t\t\t" + endDay + endTime));
    }

    public void dismissActivity(View view){
        finish();
    }

    public void startFasting(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
