package com.jasongoodisondevelopment.microhiit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private static final String KEY_REPEAT = "KEY_REPEAT";
    public static final String CHANNEL_ID = "10001";
    private Button button;
    private int desiredExercises = 3;

    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.nextlesson);
        button.setOnClickListener(v -> launchNextLesson());

        //setUpRecurringAlarm();
        buildNotification();
    }

    public void launchNextLesson() {
        Intent intent = new Intent(this, Lesson.class);
        startActivity(intent);
    }

    public void setUpRecurringAlarm() {
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);

        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(),
                1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Set the alarm to start midnight
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);

        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY, pi);
    }

    public int setUpAndGetFinishedValue() {
        sharedPref = getApplicationContext().getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        if (!sharedPref.contains(getString(R.string.finished_exercises_key))){
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(getString(R.string.finished_exercises_key), 0);
            editor.apply();
        }
        return sharedPref.getInt(getString(R.string.finished_exercises_key), 0);
    }

    public void updateRemainingExercises(int finished) {
        int left = Math.max(desiredExercises - finished, 0);
        TextView tv1 = findViewById(R.id.exercisesLeft);
        TextView topHalf = findViewById(R.id.whatsleft1);
        TextView bottomHalf = findViewById(R.id.whatsleft2);
        TextView done = findViewById(R.id.done);
        TextView done2 = findViewById(R.id.done2);

        if (left == 0) {
            tv1.setVisibility(View.INVISIBLE);
            topHalf.setVisibility(View.INVISIBLE);
            bottomHalf.setVisibility(View.INVISIBLE);
            done.setVisibility(View.VISIBLE);
            done2.setVisibility(View.VISIBLE);
            button.setText("Bonus Exercise");
        }
        else {
            tv1.setText(String.valueOf(left));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        int finished = setUpAndGetFinishedValue();
        updateRemainingExercises(finished);
    }

    private void createNotificationChannel() {
        System.out.println("Create notification channel");
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        System.out.println("Created notification channel");
    }

    public void buildNotification() {
        createNotificationChannel();
        System.out.println("Setting up a notification");
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);

        Intent intent = new Intent(getApplicationContext(), NotificationReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(),
                2, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        // Set the alarm to start midnight
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        calendar.set(Calendar.HOUR_OF_DAY, 10);
        calendar.set(Calendar.MINUTE, 0);
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);

        calendar.set(Calendar.HOUR_OF_DAY, 13);
        calendar.set(Calendar.MINUTE, 0);
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);

        calendar.set(Calendar.HOUR_OF_DAY, 16);
        calendar.set(Calendar.MINUTE, 0);
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);
    }
}
