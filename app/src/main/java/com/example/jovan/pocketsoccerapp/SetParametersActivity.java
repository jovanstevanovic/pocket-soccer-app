package com.example.jovan.pocketsoccerapp;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class SetParametersActivity extends AppCompatActivity {

    public static final int NUM_TERRAINS = 4;

    private int numGoalsChosen;
    private int numMinutesChosen;
    private int speedChosen;

    private ImageView terrainImageView;
    private int[] terrainImageResourceNum;
    private int currentImage;

    private Button oneGoalButton;
    private Button threeGoalButton;
    private Button sevenGoalButton;

    private Button twoMinuteButton;
    private Button fiveMinuteButton;
    private Button tenMinuteButton;

    private Button twoSpeedButton;
    private Button fiveSpeedButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_parameters);

        SharedPreferences sharedPreferences = getSharedPreferences("parameters_select", MODE_PRIVATE);

        numGoalsChosen = sharedPreferences.getInt(MainActivity.MAX_GOALS, MainActivity.MaxGoals.ONE_GOAL.getValue());
        numMinutesChosen = sharedPreferences.getInt(MainActivity.MAX_MINUTES, MainActivity.MaxMinutes.TWO_MIN.getValue());
        speedChosen = sharedPreferences.getInt(MainActivity.SPEED, MainActivity.Speed.TWO_SPEED.getValue());
        currentImage = sharedPreferences.getInt(MainActivity.TERRAIN, 0);

        oneGoalButton = findViewById(R.id.oneGoalButton);
        threeGoalButton = findViewById(R.id.threeGoalButton);
        sevenGoalButton = findViewById(R.id.sevenGoalButton);

        switch (numGoalsChosen) {
            case 1:
                oneGoalButton.setTextColor(Color.WHITE);
                threeGoalButton.setTextColor(Color.BLACK);
                sevenGoalButton.setTextColor(Color.BLACK);
                break;
            case 3:
                oneGoalButton.setTextColor(Color.BLACK);
                threeGoalButton.setTextColor(Color.WHITE);
                sevenGoalButton.setTextColor(Color.BLACK);
                break;
            case 7:
                oneGoalButton.setTextColor(Color.BLACK);
                threeGoalButton.setTextColor(Color.BLACK);
                sevenGoalButton.setTextColor(Color.WHITE);
                break;
        }

        twoMinuteButton = findViewById(R.id.twoMinuteButton);
        fiveMinuteButton = findViewById(R.id.fiveMinuteButton);
        tenMinuteButton = findViewById(R.id.tenMinuteButton);

        switch (numMinutesChosen) {
            case 2:
                twoMinuteButton.setTextColor(Color.WHITE);
                fiveMinuteButton.setTextColor(Color.BLACK);
                tenMinuteButton.setTextColor(Color.BLACK);
                break;
            case 5:
                twoMinuteButton.setTextColor(Color.BLACK);
                fiveMinuteButton.setTextColor(Color.WHITE);
                tenMinuteButton.setTextColor(Color.BLACK);
                break;
            case 10:
                twoMinuteButton.setTextColor(Color.BLACK);
                fiveMinuteButton.setTextColor(Color.BLACK);
                tenMinuteButton.setTextColor(Color.WHITE);
                break;
        }

        twoSpeedButton = findViewById(R.id.twoSpeedSelect);
        fiveSpeedButton = findViewById(R.id.fiveSpeedSelect);

        switch (speedChosen) {
            case 2:
                twoSpeedButton.setTextColor(Color.WHITE);
                fiveSpeedButton.setTextColor(Color.BLACK);
                break;
            case 5:
                twoSpeedButton.setTextColor(Color.BLACK);
                fiveSpeedButton.setTextColor(Color.WHITE);
                break;
        }

        terrainImageResourceNum = new int[NUM_TERRAINS];
        terrainImageView = findViewById(R.id.terrainImageView);

        terrainImageResourceNum[0] = R.drawable.field1_hd;
        terrainImageResourceNum[1] = R.drawable.field2_hd;
        terrainImageResourceNum[2] = R.drawable.field3_hd;
        terrainImageResourceNum[3] = R.drawable.field4_hd;

        terrainImageView.setImageResource(terrainImageResourceNum[currentImage % NUM_TERRAINS]);
    }

    public void onGoalButtonClick(View view) {
        switch (view.getId()) {
            case R.id.oneGoalButton:
                numGoalsChosen = MainActivity.MaxGoals.ONE_GOAL.getValue();
                oneGoalButton.setTextColor(Color.WHITE);
                threeGoalButton.setTextColor(Color.BLACK);
                sevenGoalButton.setTextColor(Color.BLACK);
                break;
            case R.id.threeGoalButton:
                numGoalsChosen = MainActivity.MaxGoals.THREE_GOALS.getValue();
                oneGoalButton.setTextColor(Color.BLACK);
                threeGoalButton.setTextColor(Color.WHITE);
                sevenGoalButton.setTextColor(Color.BLACK);
                break;
            case R.id.sevenGoalButton:
                numGoalsChosen = MainActivity.MaxGoals.SEVEN_GOALS.getValue();
                oneGoalButton.setTextColor(Color.BLACK);
                threeGoalButton.setTextColor(Color.BLACK);
                sevenGoalButton.setTextColor(Color.WHITE);
                break;
        }
    }

    public void onTerrainSelectButtonClick(View view) {
        switch (view.getId()) {
            case R.id.leftNavigateButton:
                terrainImageView.setImageResource(terrainImageResourceNum[currentImage = (currentImage + NUM_TERRAINS - 1) % NUM_TERRAINS]);
                break;
            case R.id.rightNavigateButton:
                terrainImageView.setImageResource(terrainImageResourceNum[currentImage = (currentImage + 1) % NUM_TERRAINS]);
                break;
        }
    }

    public void onMinuteButtonClick(View view) {
        switch (view.getId()) {
            case R.id.twoMinuteButton:
                numMinutesChosen = MainActivity.MaxMinutes.TWO_MIN.getValue();
                twoMinuteButton.setTextColor(Color.WHITE);
                fiveMinuteButton.setTextColor(Color.BLACK);
                tenMinuteButton.setTextColor(Color.BLACK);
                break;
            case R.id.fiveMinuteButton:
                numMinutesChosen = MainActivity.MaxMinutes.FIVE_MIN.getValue();
                twoMinuteButton.setTextColor(Color.BLACK);
                fiveMinuteButton.setTextColor(Color.WHITE);
                tenMinuteButton.setTextColor(Color.BLACK);
                break;
            case R.id.tenMinuteButton:
                numMinutesChosen = MainActivity.MaxMinutes.TEN_MIN.getValue();
                twoMinuteButton.setTextColor(Color.BLACK);
                fiveMinuteButton.setTextColor(Color.BLACK);
                tenMinuteButton.setTextColor(Color.WHITE);
                break;
        }
    }

    public void onSpeedSelectButtonClick(View view) {
        switch (view.getId()) {
            case R.id.twoSpeedSelect:
                speedChosen = MainActivity.Speed.TWO_SPEED.getValue();
                twoSpeedButton.setTextColor(Color.WHITE);
                fiveSpeedButton.setTextColor(Color.BLACK);
                break;
            case R.id.fiveSpeedSelect:
                speedChosen = MainActivity.Speed.FIVE_SPEED.getValue();
                twoSpeedButton.setTextColor(Color.BLACK);
                fiveSpeedButton.setTextColor(Color.WHITE);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SharedPreferences.Editor editor = getSharedPreferences("parameters_select", MODE_PRIVATE).edit();

        editor.putInt(MainActivity.MAX_GOALS, numGoalsChosen);
        editor.putInt(MainActivity.MAX_MINUTES, numMinutesChosen);
        editor.putInt(MainActivity.SPEED, speedChosen);
        editor.putInt(MainActivity.TERRAIN, currentImage);

        editor.apply();
        finish();
    }

    public void onClickBackButton(View view) {
        SharedPreferences.Editor editor = getSharedPreferences("parameters_select", MODE_PRIVATE).edit();

        editor.putInt(MainActivity.MAX_GOALS, numGoalsChosen);
        editor.putInt(MainActivity.MAX_MINUTES, numMinutesChosen);
        editor.putInt(MainActivity.SPEED, speedChosen);
        editor.putInt(MainActivity.TERRAIN, currentImage);

        editor.apply();
        finish();
    }
}
