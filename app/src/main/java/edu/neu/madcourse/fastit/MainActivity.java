package edu.neu.madcourse.fastit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView textFasting;
    private TextView textPlan;
    private TextView textLeaderboard;
    private TextView textUserProfile;

    static final int REQUEST_PICTURE_CAPTURE = 1;
    private String pictureFilePath;

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

    /*
    * Picture taking and saving logic
    * */
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
        pictureFilePath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PICTURE_CAPTURE && resultCode == RESULT_OK) {
            showSnackBar("Photo created and saved succesfully");
        }
    }

    private void showSnackBar(String message) {
        Snackbar.make(findViewById(android.R.id.content), message,
                Snackbar.LENGTH_SHORT)
                .show();
    }


}