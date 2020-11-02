package com.jasongoodisondevelopment.microhiit;

import android.net.Uri;
import android.util.Pair;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Exercise {
    private List<Pair<Integer, String>> exerciseList;
//    public int breakLengthMillis = 2 * 1000;
//    public int exerciseLengthMillis = 3 * 1000;

    public Exercise() {

        exerciseList = new ArrayList<Pair<Integer, String>>();
        // Temporary exercise uses the same video
        AddExercise(R.raw.intro, "Intro");
        AddExercise(R.raw.intro, "Intro");
        AddExercise(R.raw.intro, "Intro");
    }

    public void AddExercise(Integer resource, String name) {
        // One for explanation, one for doing
        exerciseList.add(new Pair<>(resource, name));
        exerciseList.add(new Pair<>(resource, name));
    }

    public List<Integer> getExercises() {
        // Would use streams but but API minimum is stopping me
        List<Integer> onlyInts = new ArrayList<>();
        for (int i = 0; i < exerciseList.size(); i++) {
            onlyInts.add(exerciseList.get(i).first);
        }
        return onlyInts;
    }

    public String getExerciseName(int finishedSets) {
        return exerciseList.get(finishedSets).second;
    }

    public Uri getExerciseUri(int finishedSets) {
        String videoPath = "android.resource://com.jasongoodisondevelopment.microhiit/" ;
        videoPath += exerciseList.get(finishedSets).first;
        return Uri.parse(videoPath);
    }

    @NonNull
    @Override
    // This can be used for printing exercise to users
    public String toString() {
        StringBuilder str = new StringBuilder();
        int counter = 0;
        for (Pair<Integer, String> exercise : exerciseList) {
            if (counter == 0) {
                counter += 1;
                continue;
            }
            else if (counter % 2 == 0) {
                str.append(String.format("%-8s %12s%n", "Rest", "(20 sec)"));
            } else {
                str.append(String.format("%-8s %12s%n", exercise.second, "(40 sec)"));
            }
            counter += 1;
        }
        str.append("-----------------------------\n");
        str.append(String.format("Total       %8s", "(3 mins)"));
        return str.toString();
    }
}
