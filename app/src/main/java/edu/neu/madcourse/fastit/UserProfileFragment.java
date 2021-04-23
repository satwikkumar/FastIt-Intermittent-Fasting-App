package edu.neu.madcourse.fastit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

import edu.neu.madcourse.fastit.plan.Helpers;

public class UserProfileFragment extends Fragment {

    private LineChart chart;
    private List<FastingSession> sessionList;
    private SharedPreferenceManager preferenceManager;

    public UserProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fetchData();
    }

    private void loadCardData(View view){
        if(sessionList.size()==0) return;
        FastingSession session = sessionList.get(sessionList.size()-1);
        CardView cardView = view.findViewById(R.id.history_cardView);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DetailedHistoryActivity.class);
                startActivity(intent);
            }
        });
        TextView startDate = cardView.findViewById(R.id.start_time);
        startDate.setText(Helpers.getFormattedDate(session.startTime));
        TextView endDate = cardView.findViewById(R.id.end_time);
        endDate.setText(Helpers.getFormattedDate(session.endTime));
        TextView sessionCompleted = cardView.findViewById(R.id.completed_status);
        sessionCompleted.setText(session.hasCompletedSession ? "Yes" : "No");
        TextView weight = cardView.findViewById(R.id.weight_value);
        weight.setText(session.weight+" KG");
        ImageView imageView = cardView.findViewById(R.id.session_thumbnail);
        Bitmap bitmap = BitmapFactory.decodeFile(session.progressImagePath);
        imageView.setImageBitmap(bitmap);
    }

    private void fetchData(){
        AppDatabase db = Room.databaseBuilder(getActivity().getApplicationContext(),
                AppDatabase.class, "fastit-database").allowMainThreadQueries().build();
        FastingSessionDao fastingSessionDao = db.fastingSessionDao();
        sessionList = fastingSessionDao.getAllSessions();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        chart = view.findViewById(R.id.weight_chart);
        chart.setViewPortOffsets(0, 0, 0, 0);
        chart.setBackgroundColor(Color.rgb(66, 103, 178));

        // no description text
        chart.getDescription().setEnabled(false);

        // enable touch gestures
        chart.setTouchEnabled(true);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(false);

        chart.setDrawGridBackground(false);
        chart.setMaxHighlightDistance(300);

        XAxis x = chart.getXAxis();
        x.setEnabled(false);

        YAxis y = chart.getAxisLeft();
        y.setLabelCount(6, false);
        y.setTextColor(Color.WHITE);
        y.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        y.setDrawGridLines(false);
        y.setAxisLineColor(Color.WHITE);

        chart.getAxisRight().setEnabled(false);

        chart.getLegend().setEnabled(false);

        chart.animateXY(2000, 2000);

        // don't forget to refresh the drawing
        chart.invalidate();

        setData();

        loadCardData(view);

        preferenceManager = new SharedPreferenceManager(getActivity());
        StringBuilder longestStreakText = new StringBuilder();
        longestStreakText.append("Longest Streak: ");
        int longestStreak = preferenceManager.getIntPref(Constants.SP_LONGEST_STREAK);
        longestStreakText.append(longestStreak > -1 ? longestStreak : 0);
        TextView longestStreakTextView = view.findViewById(R.id.longest_streak);
        longestStreakTextView.setText(longestStreakText.toString());

        return view;
    }

    private void setData() {

        ArrayList<Entry> values = getWeightsForGraph();

        LineDataSet set1;

        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(values, "DataSet 1");

            set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            set1.setCubicIntensity(0.2f);
            set1.setDrawFilled(true);
            set1.setDrawCircles(false);
            set1.setLineWidth(1.8f);
            set1.setCircleRadius(4f);
            set1.setCircleColor(Color.WHITE);
            set1.setHighLightColor(Color.rgb(244, 117, 117));
            set1.setColor(Color.WHITE);
            set1.setFillColor(Color.WHITE);
            set1.setFillAlpha(100);
            set1.setDrawHorizontalHighlightIndicator(false);
            set1.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return chart.getAxisLeft().getAxisMinimum();
                }
            });

            // create a data object with the data sets
            LineData data = new LineData(set1);
            data.setValueTextSize(9f);
            data.setDrawValues(false);

            // set data
            chart.setData(data);
        }
    }

    private ArrayList<Entry> getWeightsForGraph(){

        ArrayList<Entry> weights = new ArrayList<>();
        float lastKnownWeight = 0f;
        if(sessionList.size()==0 || sessionList.get(0).weight < 0){
            weights.add(new Entry(0, 0));
        } else {
            lastKnownWeight = sessionList.get(0).weight;
            weights.add(new Entry(0,lastKnownWeight));
        }

        for(int i=1;i<sessionList.size();i++){
            float currentWeight = sessionList.get(i).weight;
            if(currentWeight <= 0){
                weights.add(new Entry(i, lastKnownWeight));
            } else {
                weights.add(new Entry(i,currentWeight));
                lastKnownWeight = currentWeight;
            }
        }
        return weights;
    }
}