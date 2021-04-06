package edu.neu.madcourse.fastit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AdditionalInfoActivity extends Activity {


    static final int REQUEST_PICTURE_CAPTURE = 1;
    private SharedPreferenceManager sharedPreferenceManager;
    private float weight = 0.0f;
    private String filePath;
    private TextView weightTextView;
    private ImageView previewImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additional_info);
        sharedPreferenceManager = new SharedPreferenceManager(this);
        sharedPreferenceManager.setLongPref(Constants.SP_CURRENT_FASTING_END_TIME, System.currentTimeMillis());
        weightTextView = findViewById(R.id.weight_text);
        weightTextView.setText(weight+" kg");
        previewImage = findViewById(R.id.image_preview);
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

    public void launchCamera(View view){
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 10);
        } else {
            takeImage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 10)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                takeImage();
            }
            else
            {
                Toast.makeText(this, "Camera permission denied!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void takeImage(){
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            File pictureFile = null;
            try {
                pictureFile = getImageFile();
            } catch (IOException ex) {
                showSnackBar("Photo file can't be created, please try again");
                return;
            }
            if (pictureFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.fastit.android.fileprovider",
                        pictureFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(cameraIntent, REQUEST_PICTURE_CAPTURE);
            }
        }
    }

    private File getImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US).format(new Date());
        String pictureFile = "FASTIT_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(pictureFile,  ".jpg", storageDir);
        filePath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PICTURE_CAPTURE && resultCode == RESULT_OK) {
            Uri photoURI = Uri.parse(filePath);
            Bitmap bitmap = BitmapFactory.decodeFile(filePath);
            previewImage.setImageBitmap(bitmap);
        }
    }

    private void showSnackBar(String message) {
        Snackbar.make(findViewById(android.R.id.content), message,
                Snackbar.LENGTH_SHORT)
                .show();
    }
}