package com.jasongoodisondevelopment.microhiit;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.concurrent.Callable;

public class Lesson extends AppCompatActivity {
    private Button startButton;
    private ProgressBar progressBar;
    private VideoView videoView;
    private TextView banner;
    private TextView bannerNum;
    private FrameLayout videoFrame;
    private static int excerciseAmountMillis = 30 * 1000;
    private static int lessonBeginsInMillis = 5 * 1000;
    private TextView countDown;
    private SharedPreferences sharedPref;
    private static int sets = 3;
    private int finishedSets = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson);

        countDown = (TextView)findViewById(R.id.countdown);
        startButton = (Button) findViewById(R.id.ready);
        progressBar = (ProgressBar) findViewById((R.id.progressBar));
        videoView = (VideoView) findViewById(R.id.videoview);
        banner = (TextView) findViewById(R.id.exercisesLeftBanner);
        bannerNum = findViewById(R.id.numExercisesLeft);
        videoFrame = findViewById(R.id.video_view_container);


        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startButton.setVisibility(TextView.INVISIBLE);
                countDown.setVisibility(TextView.VISIBLE);
                beginLesson();
            }
        });
    }

    private int beginLesson() {
        Callable<Integer> callback = () -> {
            beginLesson();
            return null;
        };
        int time = excerciseAmountMillis;
        String message = "";

        System.out.println("finished" + finishedSets);

        if (finishedSets >= sets * 2) {
           callback = () -> {
             lessonCompleted();
             return null;
           };
        }
        else if (finishedSets % 2 == 0) {
            time = lessonBeginsInMillis;
            message = "Break for ";
        }

        finishedSets++;
        startVideo(finishedSets);
        setBanner(finishedSets);
        startTimer(time, callback, message);
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void startVideo(int finishedSets) {
        // Use finishedSets to determine which video to play
        videoFrame.setClipToOutline(true);

        VideoView videoView = findViewById(R.id.videoview);
        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.test;
        Uri uri = Uri.parse(videoPath);
        System.out.println("URI IS " + uri.getPath());
        videoView.setVideoURI(uri);
        videoFrame.setVisibility(View.VISIBLE);
        videoView.start();
    }

    private void setBanner(int finishedSets) {
        banner.setVisibility(View.VISIBLE);
        bannerNum.setVisibility(View.VISIBLE);
        bannerNum.setText("" + (sets - (finishedSets/2)));
    }

    private void startTimer(int millis, Callable<Integer> callback, String message) {
        progressBar.setProgress(0);
        new CountDownTimer(millis, 50) {

            public void onTick(long millisUntilFinished) {
                long secondsLeft = (millisUntilFinished + 1000) / 1000;
                int progress = (int)(( (millis - millisUntilFinished) / (double)millis) * 1000);
                countDown.setText(message + secondsLeft);
                progressBar.setProgress(progress);
            }

            public void onFinish() {
                try {
                    callback.call();
                } catch (Exception e) {
                    System.out.println("Callback thew an exception");
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void lessonCompleted() {
        countDown.setText("Exercise Finished!");
        progressBar.setProgress(1000);
        sharedPref = getApplicationContext().getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        int curFinished = sharedPref.getInt(getString(R.string.finished_exercises_key), 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getString(R.string.finished_exercises_key), curFinished + 1);
        editor.apply();
    }
}
