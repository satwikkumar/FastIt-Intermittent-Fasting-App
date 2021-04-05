package edu.neu.madcourse.fastit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class AdditionalInfoActivity extends Activity {

    private SharedPreferenceManager sharedPreferenceManager;
    private float weight = 0.0f;
    private String filePath;
    private TextView weightTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additional_info);
        sharedPreferenceManager = new SharedPreferenceManager(this);
        weightTextView = findViewById(R.id.weight_text);
        weightTextView.setText(weight+" kg");
    }

    public void dismissActivity(View view){
        saveData();
        finish();
    }

    private void saveData(){
        sharedPreferenceManager.setFloatPref(Constants.SP_CURRENT_WEIGHT, weight);
        sharedPreferenceManager.setStringPref(Constants.SP_CURRENT_IMAGE_PATH, filePath);
    }

    public void addNewUser(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter current weight");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final TextView dotText = new TextView(this);
        dotText.setText("dot");
        dotText.setGravity(Gravity.CENTER);

        final TextView kgText = new TextView(this);
        kgText.setText("KG");
        dotText.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        layout.setOrientation(LinearLayout.HORIZONTAL);

        final NumberPicker numberPicker = new NumberPicker(this);
        numberPicker.setMaxValue(200);
        numberPicker.setMinValue(1);
        numberPicker.setWrapSelectorWheel(true);
        numberPicker.setGravity(Gravity.CENTER);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) { }
        });

        final NumberPicker decimalPicker = new NumberPicker(this);
        decimalPicker.setMaxValue(9);
        decimalPicker.setMinValue(0);
        decimalPicker.setWrapSelectorWheel(true);
        decimalPicker.setGravity(Gravity.CENTER);
        decimalPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) { }
        });
        layout.addView(numberPicker);
        layout.addView(dotText);
        layout.addView(decimalPicker);
        layout.addView(kgText);


        builder.setView(layout);


        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                weight = numberPicker.getValue() +  (float)(decimalPicker.getValue()/10.0);
                weightTextView.setText(weight+" kg");
                dialog.cancel();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void enterWeightPressed(View view){
        addNewUser();
    }
}