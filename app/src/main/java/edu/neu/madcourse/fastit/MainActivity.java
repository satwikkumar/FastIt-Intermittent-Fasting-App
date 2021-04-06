package edu.neu.madcourse.fastit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Fragment planFragment;
    private Fragment fastingFragment;
    private Fragment leaderBoardFragment;
    private Fragment userProfileFragment;
    private Fragment activeFragment;

    BottomNavigationView bottomNavigationView;
    static final int REQUEST_PICTURE_CAPTURE = 1;
    private String pictureFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
        } else {
            initializeApp();
        }




        /*
          AppDatabase db = Room.databaseBuilder(getApplicationContext(),
           AppDatabase.class, "fastit-database").allowMainThreadQueries().build();

          FastingSessionDao fastingSessionDao = db.fastingSessionDao();
          for(int i=0;i<3;i++){
              FastingSession fastingSession = new FastingSession();
              fastingSession.startTime = System.currentTimeMillis();
              fastingSession.endTime = System.currentTimeMillis() + 10;
              fastingSession.fastCycle = FastingCycle.SIXTEEN_HOUR_CYCLE.getId();
              fastingSession.progressImagePath = "/path/to/image/file";
              fastingSession.weight = 100 + i;
              fastingSessionDao.insertAll(fastingSession);
          }

           List<FastingSession> fastingSessions = fastingSessionDao.getAllSessions();
            showSnackBar(fastingSessions.get(0).progressImagePath);
        */
        /*
         * Using database, example code
         * AppDatabase db = Room.databaseBuilder(getApplicationContext(),
         *  AppDatabase.class, "fastit-database").build();
         *
         * FastingSessionDao fastingSessionDao = db.fastingSessionDao();
         *  List<FastingSession> fastingSessions = fastingSessionDao.getAllSessions();
         *
         * */
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                initializeApp();
            }
            else
            {
                Toast.makeText(this, "File access permission denied!", Toast.LENGTH_LONG).show();
                finishAndRemoveTask();
            }
        }
    }
    private void initializeApp(){
        planFragment = new PlanFragment();
        fastingFragment = new FastingFragment();
        leaderBoardFragment = new LeaderboardFragment();
        userProfileFragment = new UserProfileFragment();
        activeFragment = fastingFragment;
        final FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frameLayout, activeFragment).commit();

        bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        switch (item.getItemId()) {
                            case R.id.action_fasting:
                                activeFragment = fastingFragment;
                                break;
                            case R.id.action_leaderboard:
                                activeFragment = leaderBoardFragment;
                                break;
                            case R.id.action_plan:
                                activeFragment = planFragment;
                                break;
                            case R.id.action_user_profile:
                                activeFragment = userProfileFragment;
                                break;
                        }
                        if (activeFragment != null){
                            fragmentTransaction.replace(R.id.frameLayout, activeFragment).commit();
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

    public void changeNavigationTab(int itemId){
        View view = bottomNavigationView.findViewById(itemId);
        view.performClick();
    }


}