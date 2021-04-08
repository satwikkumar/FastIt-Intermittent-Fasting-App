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

import edu.neu.madcourse.fastit.plan.FastingCycle;
import edu.neu.madcourse.fastit.plan.Helpers;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class FastingFragment extends Fragment {

    private MaterialProgressBar progressBar;
    private CountDownTimer countDownTimer;
    private boolean TimerRunning;
    private TextView timerText;
    private SharedPreferenceManager sharedPreferenceManager;

    private long timeRemainingInMillis = 0;

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

        TextView fastingCycleTextView = view.findViewById(R.id.current_fasting_cycle_text);
        FastingCycle cycle = Helpers.getFastingCycleForNum(
                sharedPreferenceManager.getIntPref(Constants.SP_CURRENT_FASTING_CYCLE));
        String cycleText = Helpers.getStringForFastingCycle(cycle);
        fastingCycleTextView.setText(cycleText);

        final Button endFastingButton = view.findViewById(R.id.end_fasting);
        endFastingButton.setEnabled(sharedPreferenceManager.getLongPref(
                Constants.SP_CURRENT_FASTING_START_TIME) != -1);
        endFastingButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                sharedPreferenceManager.setLongPref(Constants.SP_CURRENT_FASTING_END_TIME, System.currentTimeMillis());
                loadAdditionalActivity();
                resetTimer();
            }
        });

        final Button startFastingButton = view.findViewById(R.id.start_fasting);
        startFastingButton.setEnabled(sharedPreferenceManager.getLongPref(
                Constants.SP_CURRENT_FASTING_START_TIME) == -1);
        startFastingButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                endFastingButton.setEnabled(true);
                startFastingButton.setEnabled(false);
                long currentTime = System.currentTimeMillis();
                sharedPreferenceManager.setLongPref(Constants.SP_CURRENT_FASTING_START_TIME,
                        currentTime);
                FastingCycle cycle = Helpers.getFastingCycleForNum(
                        sharedPreferenceManager.getIntPref(Constants.SP_CURRENT_FASTING_CYCLE));
                sharedPreferenceManager.setLongPref(Constants.SP_ESTIMATED_FASTING_END_TIME,
                        (long) Helpers.getEndTimeFromStartTime(currentTime, cycle));
                startTimer();
            }
        });

        progressBar = view.findViewById(R.id.circular_progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);
        progressBar.setMax(100);

        timerText = view.findViewById(R.id.timer_text);

        startTimer();


        return view;
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
        // do some validations
        Intent intent = new Intent(getActivity(), AdditionalInfoActivity.class);
        startActivity(intent);
    }
}