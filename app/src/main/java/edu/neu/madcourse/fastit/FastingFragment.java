package edu.neu.madcourse.fastit;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class FastingFragment extends Fragment {

    private static final long START_TIME_IN_MILLIS = 40000;
    private Button startFastingButton;
    private Button endFastingButton;
    private MaterialProgressBar progressBar;
    private CountDownTimer countDownTimer;
    private boolean TimerRunning;
    private TextView timerText;

    private long TimeLeftInMillis = START_TIME_IN_MILLIS;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_fasting, container, false);
        endFastingButton = view.findViewById(R.id.end_fasting);
        endFastingButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });

        startFastingButton = view.findViewById(R.id.start_fasting);
        startFastingButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                startTimer();
            }
        });

        progressBar = view.findViewById(R.id.circular_progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);
        progressBar.setMax(100);

        timerText = view.findViewById(R.id.timer_text);


        return view;
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(START_TIME_IN_MILLIS, 1000) {
            double numberOfSeconds = START_TIME_IN_MILLIS/1000; // Ex : 20000/1000 = 20
            double  factor = 100/numberOfSeconds; // 100/20 = 5, for each second multiply this, for sec 1 progressPercentage = 1x5 =5, for sec 5 progressPercentage = 5x5 = 25, for sec 20 progressPercentage = 20x5 =100
            @Override
            public void onTick(long millisUntilFinished) {
                TimeLeftInMillis = millisUntilFinished;
                updateCountDownText(); //  Updating CountDown_Tv
                double secondsRemaining = millisUntilFinished / 1000;
                double progressPercentage = (numberOfSeconds-secondsRemaining) * factor ;
                progressBar.setProgress((int)progressPercentage);
            }

            @Override
            public void onFinish() {
                TimerRunning = false;
                timerText.setText("00:00");
                progressBar.setProgress(100);
            }
        }.start();

        TimerRunning = true;


    }

    private void pauseTimer() {
        countDownTimer.cancel();
        TimerRunning = false;
        progressBar.clearAnimation();
    }

    private void resetTimer() {
        TimeLeftInMillis = START_TIME_IN_MILLIS;
        updateCountDownText();

        progressBar.setProgress(0);
        countDownTimer.cancel();

    }

    private void updateCountDownText() {
        int minutes = (int) (TimeLeftInMillis / 1000) / 60;
        int seconds = (int) (TimeLeftInMillis / 1000) % 60;


        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        //        String newtime = hours + ":" + minutes + ":" + seconds;

        timerText.setText(timeLeftFormatted);

    }

    private void loadAdditionalActivity(){
        // do some validations
        Intent intent = new Intent(getActivity(), AdditionalInfoActivity.class);
        startActivity(intent);
    }
}