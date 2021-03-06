package edu.neu.madcourse.fastit;


import android.app.AlertDialog;
import android.content.DialogInterface;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import android.os.CountDownTimer;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import edu.neu.madcourse.fastit.plan.FastingCycle;
import edu.neu.madcourse.fastit.plan.Helpers;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class FastingFragment extends Fragment {

    private MaterialProgressBar progressBar;
    private CountDownTimer countDownTimer;
    private boolean TimerRunning;
    private TextView timerText;
    private SharedPreferenceManager sharedPreferenceManager;
    private  Button endFastingButton;
    private  Button startFastingButton;
    private TextView status;
    private TextView end_time;
    private String NOTIFICATION_ID = "";

    private long timeRemainingInMillis = 0;
    private AlarmManager alarmManager;

    public FastingFragment() {
        // Required empty public constructor
    }

    public static FastingFragment newInstance(String param1, String param2) {
        FastingFragment fragment = new FastingFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferenceManager = new SharedPreferenceManager(getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_fasting, container, false);
        timerText = view.findViewById(R.id.timer_text);
        progressBar = view.findViewById(R.id.circular_progress_bar);
        TextView fastingCycleTextView = view.findViewById(R.id.current_fasting_cycle_text);
        FastingCycle cycle = Helpers.getFastingCycleForNum(
                sharedPreferenceManager.getIntPref(Constants.SP_CURRENT_FASTING_CYCLE));
        String cycleText = Helpers.getStringForFastingCycle(cycle);
        fastingCycleTextView.setText("Current Plan: " + cycleText);

        status = view.findViewById(R.id.status);
        end_time = view.findViewById(R.id.end_time);
        endFastingButton = view.findViewById(R.id.end_fasting);
        endFastingButton.setEnabled(sharedPreferenceManager.getLongPref(
                Constants.SP_CURRENT_FASTING_START_TIME) != -1);
        endFastingButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                showAlert();
            }
        });

        startFastingButton = view.findViewById(R.id.start_fasting);
        startFastingButton.setEnabled(sharedPreferenceManager.getLongPref(
                Constants.SP_CURRENT_FASTING_START_TIME) == -1);
        startFastingButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                FastingCycle cycle = Helpers.getFastingCycleForNum(
                        sharedPreferenceManager.getIntPref(Constants.SP_CURRENT_FASTING_CYCLE));
                if (cycle != FastingCycle.INVALID_CYCLE){
                    endFastingButton.setEnabled(true);
                    startFastingButton.setEnabled(false);
                    status.setText("You are fasting!");
                    status.setTextColor(Color.parseColor("#00ff00"));
                    long currentTime = System.currentTimeMillis();
                    sharedPreferenceManager.setLongPref(Constants.SP_CURRENT_FASTING_START_TIME,
                            currentTime);
                    sharedPreferenceManager.setLongPref(Constants.SP_ESTIMATED_FASTING_END_TIME,
                            (long) Helpers.getEndTimeFromStartTime(currentTime, cycle));
                    Date endDate = new Date((long) Helpers.getEndTimeFromStartTime(currentTime, cycle));
                    Date startDate = new Date();
                    String endTime = new SimpleDateFormat("h:mm a", Locale.getDefault()).format(endDate);
                    String endDay = "Today, ";
                    if(startDate.getDate()!=endDate.getDate()){
                        endDay = "Tomorrow, ";
                    }
                    end_time.setText(Html.fromHtml("End Time<br><b>"+endDay+" "+endTime+"</b>"));
                    end_time.setVisibility(View.VISIBLE);
                    queueNotifications("Half way to end fasting! You can do it",
                            Helpers.getTimeFromPercentage(System.currentTimeMillis(),
                                    endDate.getTime(), 50.0));
                    queueNotifications("Almost there to end fasting! Keep going",
                            Helpers.getTimeFromPercentage(System.currentTimeMillis(),
                                    endDate.getTime(), 75.0));
                    queueNotifications("Completed fasting!",
                            Helpers.getTimeFromPercentage(System.currentTimeMillis(),
                                    endDate.getTime(), 100.0));
                    startTimer();
                }else  {
                    ((MainActivity)getActivity()).changeNavigationTab(R.id.action_plan);
                }
            }
        });
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setShowProgressBackground(true);
        progressBar.setProgress(0);
        progressBar.setMax(100);

        startTimer();

        if(sharedPreferenceManager.getLongPref(Constants.SP_CURRENT_FASTING_START_TIME) != -1 && sharedPreferenceManager.getLongPref(Constants.SP_ESTIMATED_FASTING_END_TIME) != -1){
            Long end1 = (long) Helpers.getEndTimeFromStartTime(sharedPreferenceManager.getLongPref(Constants.SP_CURRENT_FASTING_START_TIME),cycle);
            Long end2 = sharedPreferenceManager.getLongPref(Constants.SP_ESTIMATED_FASTING_END_TIME);
            Log.e("Timing",end1.equals(end2)+"");
            Log.e("SavedTiming",sharedPreferenceManager.getLongPref(Constants.SP_ESTIMATED_FASTING_END_TIME)+"");
            if(!end1.equals(end2)){
                status.setText("You are not fasting!");
                status.setTextColor(Color.parseColor("#ffa500"));
                end_time.setVisibility(View.INVISIBLE);
                resetTimer();
                if(startFastingButton != null){
                    startFastingButton.setEnabled(true);
                }
                endFastingButton.setEnabled(false);
                end_time.setVisibility(View.INVISIBLE);
            } else {
                status.setText("You are fasting!");
                status.setTextColor(Color.parseColor("#00ff00"));
            }
        }

        alarmManager = (AlarmManager) getActivity().getSystemService(Context. ALARM_SERVICE ) ;
        return view;
    }

    private void showAlert(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(false);
        builder.setTitle("Are you sure you want to end the current fasting session?");
        long currentStartTime = sharedPreferenceManager.getLongPref(Constants.SP_CURRENT_FASTING_START_TIME);
        long approxEndTime = sharedPreferenceManager.getLongPref(Constants.SP_ESTIMATED_FASTING_END_TIME);
        long diff = (new Date().getTime() - currentStartTime)*100/(approxEndTime - currentStartTime);
        String text = "You have completed " + diff + "% of the session. ";
        if(100-diff<51){
            text+="Only ";
        }
        text+=(100-diff) + "% is remaining!";
        builder.setMessage(text);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sharedPreferenceManager.setLongPref(Constants.SP_CURRENT_FASTING_END_TIME, System.currentTimeMillis());
                endCurrentCycle();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        sharedPreferenceManager.setStringPref(Constants.SP_SHOW_NOTIFICATION, "No");
        sharedPreferenceManager.setIntPref(Constants.SP_CURRENT_STREAK, 0);
        builder.show();
    }

    private void endCurrentCycle(){
        if(startFastingButton != null){
            startFastingButton.setEnabled(true);
        }
        endFastingButton.setEnabled(false);
        loadAdditionalActivity();
        status.setText("You are not fasting!");
        status.setTextColor(Color.parseColor("#ffa500"));
        end_time.setVisibility(View.INVISIBLE);
        resetTimer();
    }

    private void showCompleted(){
        long end = sharedPreferenceManager.getLongPref(Constants.SP_ESTIMATED_FASTING_END_TIME);
        sharedPreferenceManager.setLongPref(Constants.SP_CURRENT_FASTING_END_TIME, end);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(false);
        builder.setTitle("You have completed the fasting cycle!");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { updateCurrentStreak();
              endCurrentCycle();
              dialog.dismiss();
            }
        });
        builder.show();
    }

    private void startTimer() {
        final long startTime = sharedPreferenceManager.getLongPref(Constants.SP_CURRENT_FASTING_START_TIME);
        if (startTime > 0) {
            final long currentTime = System.currentTimeMillis();
            final long approxEndTime = sharedPreferenceManager.getLongPref(Constants.SP_ESTIMATED_FASTING_END_TIME);

            countDownTimer = new CountDownTimer(approxEndTime - currentTime, 1000) {
                double numberOfSeconds = (approxEndTime - startTime) / 1000.0; // Ex : 20000/1000 = 20
                double factor = 100 / numberOfSeconds; // 100/20 = 5, for each second multiply this, for sec 1 progressPercentage = 1x5 =5, for sec 5 progressPercentage = 5x5 = 25, for sec 20 progressPercentage = 20x5 =100

                @Override
                public void onTick(long millisUntilFinished) {
                    timeRemainingInMillis = millisUntilFinished;
                    updateCountDownText();
                    double secondsRemaining = millisUntilFinished / 1000.0;
                    double progressPercentage = (numberOfSeconds - secondsRemaining) * factor;
                    progressBar.setProgress((int) progressPercentage);
                }

                @Override
                public void onFinish() {
                    if (TimerRunning){
                        showCompleted();
                    }
                    TimerRunning = false;
                    progressBar.setProgress(100);
                }
            }.start();
        }
        else {
            timeRemainingInMillis = 0;
            updateCountDownText();
        }

        TimerRunning = true;
    }

    private void resetTimer() {
        timeRemainingInMillis = 0;
        updateCountDownText();

        progressBar.setProgress(0);
        countDownTimer.cancel();

    }

    private void updateCountDownText() {
        int[] hms = Helpers.getHMSFromMillis(timeRemainingInMillis);

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hms[0], hms[1], hms[2]);
        timerText.setText(timeLeftFormatted);

    }

    private void loadAdditionalActivity(){

        Intent intent = new Intent(getActivity(), AdditionalInfoActivity.class);
        startActivity(intent);
    }

    private void updateCurrentStreak(){
        int currentStreak = sharedPreferenceManager.getIntPref(Constants.SP_CURRENT_STREAK);
        if (currentStreak == -1){
            currentStreak = 1;
        } else {
            AppDatabase db = Room.databaseBuilder(getActivity().getApplicationContext(),
                    AppDatabase.class, "fastit-database").allowMainThreadQueries().build();
            FastingSessionDao fastingSessionDao = db.fastingSessionDao();
            List<FastingSession> sessionList = fastingSessionDao.getAllSessions();
            if(sessionList.size()>0) {
                FastingSession lastKnownSession = sessionList.get(sessionList.size() - 1);
                long currentStartTime = sharedPreferenceManager.getLongPref(Constants.SP_CURRENT_FASTING_START_TIME);
                if (currentStartTime - lastKnownSession.endTime <= TimeUnit.HOURS.toMillis(24)) {
                    currentStreak++;
                } else {
                    currentStreak = 1;
                }
            }
        }
        sharedPreferenceManager.setIntPref(Constants.SP_CURRENT_STREAK, currentStreak);
        int longestStreak = sharedPreferenceManager.getIntPref(Constants.SP_LONGEST_STREAK);
        if (longestStreak < currentStreak){
            sharedPreferenceManager.setIntPref(Constants.SP_LONGEST_STREAK, currentStreak);
        }
        String userToken = sharedPreferenceManager.getStringPref(Constants.SP_LOGGED_IN_USER_TOKEN);
        if(userToken.length() > 0){
          String userName = sharedPreferenceManager.getStringPref(Constants.SP_LOGGED_IN_USER_NAME);
          updateCurrentStreakInFirebase(currentStreak, userToken, userName);
        }
    }

    private void updateCurrentStreakInFirebase(int currentStreak, String userID, String name){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("users/"+userID);
        FbFriend friend = new FbFriend(name, currentStreak, userID);
        myRef.setValue(friend);
    }

    private void queueNotifications(String text, long estimatedEndTime){
            sharedPreferenceManager.setStringPref(Constants.SP_SHOW_NOTIFICATION, "Yes");
            CharSequence name = getActivity().getString(R.string.app_name);
            String description = getActivity().getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(estimatedEndTime+"", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);

            Notification notification1 = getNotification("Reminder", text, estimatedEndTime);

            Intent notificationIntent = new Intent( getActivity(), NotificationPublisher. class ) ;
            notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID , estimatedEndTime) ;
            notificationIntent.putExtra(NotificationPublisher.NOTIFICATION , notification1) ;
            PendingIntent pendingIntent = PendingIntent. getBroadcast ( getActivity(), (int)estimatedEndTime , notificationIntent , PendingIntent.FLAG_IMMUTABLE ) ;


            assert alarmManager != null;
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MILLISECOND, (int)estimatedEndTime);
            alarmManager.set(AlarmManager.RTC_WAKEUP ,  calendar.getTimeInMillis() , pendingIntent) ;
    }

    private Notification getNotification(String title, String text, long time){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MILLISECOND, (int)time);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getActivity(), time+"")
                .setSmallIcon(R.drawable.ic_fasting)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setWhen(calendar.getTimeInMillis());


        return notificationBuilder.build();
    }
}