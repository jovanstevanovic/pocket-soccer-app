package com.example.jovan.pocketsoccerapp;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.jovan.pocketsoccerapp.database.Game;
import com.example.jovan.pocketsoccerapp.database.GameViewModel;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String MAX_GOALS = "MAX_GOALS";
    public static final String MAX_MINUTES = "MAX_MINUTES";
    public static final String SPEED = "SPEED";
    public static final String TERRAIN = "TERRAIN";

    public static final String RESUME_GAME_REQUESTED = "RESUME_GAME_REQUESTED";

    private Button resumeGameButton;

    public enum Speed {
        TWO_SPEED(2), FIVE_SPEED(5);

        private int value;

        Speed(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public enum MaxGoals {
        ONE_GOAL(1), THREE_GOALS(3), SEVEN_GOALS(7);

        private int value;

        MaxGoals(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public enum MaxMinutes {
        TWO_MIN(2), FIVE_MIN(5), TEN_MIN(10);

        private int value;

        MaxMinutes(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resumeGameButton = findViewById(R.id.resumeGameButton);

        GameViewModel gameViewModel = ViewModelProviders.of(this).get(GameViewModel.class);
        gameViewModel.getLastPlayedGame().observe(this, new Observer<List<Game>>() {
            @Override
            public void onChanged(@Nullable List<Game> games) {
                if(games != null) {
                    resumeGameButton.setEnabled(true);
                } else {
                    resumeGameButton.setEnabled(false);
                }
            }
        });
    }

    public void onClickNewGame(View view) {
        Intent newGameIntent = new Intent(this, NewGameActivity.class);

        SharedPreferences.Editor editor = getSharedPreferences("resume_game_parameters", MODE_PRIVATE).edit();
        editor.putBoolean(PlayGameActivity.PARAMS_ARE_VALID, false);
        editor.apply();

        startActivity(newGameIntent);
    }

    public void onClickGameResume(View view) {
        Intent resumeGameIntent = new Intent(this, PlayGameActivity.class);

        SharedPreferences sharedPreferences = getSharedPreferences("resume_game_parameters", MODE_PRIVATE);
        boolean isGameSaved = sharedPreferences.getBoolean(PlayGameActivity.PARAMS_ARE_VALID, false);

        if(!isGameSaved) {
            Toast.makeText(this, "No saved game!", Toast.LENGTH_SHORT).show();
            return;
        }

        resumeGameIntent.putExtra(MainActivity.RESUME_GAME_REQUESTED, true);
        startActivity(resumeGameIntent);
    }

    public void onClickShowStatistic(View view) {
        Intent showStatisticsIntent = new Intent(this, ShowStatisticsActivity.class);
        startActivity(showStatisticsIntent);
    }

    public void onClickSetParameters(View view) {
        Intent setParametersIntent = new Intent(this, SetParametersActivity.class);
        startActivity(setParametersIntent);
    }
}
