package com.jasongoodisondevelopment.microhiit;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

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
    private TextView banner, descriptionBanner;
    private FrameLayout videoFrame;
    private static int excerciseAmountMillis = 40 * 1000;
    private static int lessonBeginsInMillis = 10 * 1000;
    private static int restForInMillis = 20 * 1000;
    private TextView countDown;
    private SharedPreferences sharedPref;
    private static int sets = 3;
    private int finishedSets = 0;
    private Exercise exercise;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson);

        countDown = (TextView)findViewById(R.id.countdown);
        startButton = (Button) findViewById(R.id.ready);
        progressBar = (ProgressBar) findViewById((R.id.progressBar));
        videoView = (VideoView) findViewById(R.id.videoview);
        banner = (TextView) findViewById(R.id.exercisesLeftBanner);
        descriptionBanner = findViewById(R.id.exercisesInDescription);
        videoFrame = findViewById(R.id.video_view_container);
        exercise = new Exercise();

        descriptionBanner.setText(exercise.toString());

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startButton.setVisibility(TextView.INVISIBLE);
                countDown.setVisibility(TextView.VISIBLE);
                descriptionBanner.setVisibility(View.INVISIBLE);
                videoFrame.setBackground(
                        ResourcesCompat.getDrawable(getResources(),
                                R.drawable.rounded_video_background,
                                null));
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

        if (finishedSets >= (sets * 2)) {
            setBanner("Done!");
            lessonCompleted();
            return 0;
        }
        else if (finishedSets == 0) {
            setBanner("Get Ready For");
            time = lessonBeginsInMillis;
            message = "Start in ";
        }
        else if (finishedSets % 2 == 0) {
            setBanner("Up Next");
            time = restForInMillis;
            message = "Break for ";
        } else {
            setBanner("GO!");
        }

        startVideo(finishedSets);
        finishedSets++;
        startTimer(time, callback, message);
        return 0;
    }

    private void startVideo(int finishedSets) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            videoFrame.setClipToOutline(true);
        }

        VideoView videoView = findViewById(R.id.videoview);

        videoView.setVideoURI(exercise.getExerciseUri(finishedSets));
        videoView.setVisibility(View.VISIBLE);
        videoView.start();
    }

    private void setBanner(String message) {
        banner.setVisibility(View.VISIBLE);
        banner.setText(message);
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
