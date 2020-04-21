package com.example.jovan.pocketsoccerapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

public class NewGameActivity extends AppCompatActivity {

    private enum NumberOfRobots {
        NONE,
        ONE,
        TWO
    }

    public static final int NUM_CLUB_TEAMS = 32;

    public static final String FIRST_PLAYER_IMAGE_ID = "FIRST_PLAYER_IMAGE_ID";
    public static final String SECOND_PLAYER_IMAGE_ID = "SECOND_PLAYER_IMAGE_ID";
    public static final String FIRST_PLAYER_NAME = "FIRST_PLAYER_NAME";
    public static final String SECOND_PLAYER_NAME = "SECOND_PLAYER_NAME";
    public static final String NUMBER_OF_ROBOTS_STRING = "NUM_OF_ROBOTS";

    private int[] teamResourceIds;

    private int currentImageFirstPlayer;
    private int currentImageSecondPlayer;

    private ImageView firstPlayerCoA;
    private ImageView secondPlayerCoA;

    private NumberOfRobots numOfRobots;
    private EditText editText1;
    private EditText editText2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);

        teamResourceIds = new int[NUM_CLUB_TEAMS];

        teamResourceIds[0] = R.drawable.t0;
        teamResourceIds[1] = R.drawable.t1;
        teamResourceIds[2] = R.drawable.t2;
        teamResourceIds[3] = R.drawable.t3;
        teamResourceIds[4] = R.drawable.t4;
        teamResourceIds[5] = R.drawable.t5;
        teamResourceIds[6] = R.drawable.t6;
        teamResourceIds[7] = R.drawable.t7;
        teamResourceIds[8] = R.drawable.t8;
        teamResourceIds[9] = R.drawable.t9;
        teamResourceIds[10] = R.drawable.t10;
        teamResourceIds[11] = R.drawable.t11;
        teamResourceIds[12] = R.drawable.t12;
        teamResourceIds[13] = R.drawable.t13;
        teamResourceIds[14] = R.drawable.t14;
        teamResourceIds[15] = R.drawable.t15;
        teamResourceIds[16] = R.drawable.t16;
        teamResourceIds[17] = R.drawable.t17;
        teamResourceIds[18] = R.drawable.t18;
        teamResourceIds[19] = R.drawable.t19;
        teamResourceIds[20] = R.drawable.t20;
        teamResourceIds[21] = R.drawable.t21;
        teamResourceIds[22] = R.drawable.t22;
        teamResourceIds[23] = R.drawable.t23;
        teamResourceIds[24] = R.drawable.t24;
        teamResourceIds[25] = R.drawable.t25;
        teamResourceIds[26] = R.drawable.t26;
        teamResourceIds[27] = R.drawable.t27;
        teamResourceIds[28] = R.drawable.t28;
        teamResourceIds[29] = R.drawable.t29;
        teamResourceIds[30] = R.drawable.t30;
        teamResourceIds[31] = R.drawable.t31;

        firstPlayerCoA = findViewById(R.id.coatOfArmsImageViewFP);
        secondPlayerCoA = findViewById(R.id.coatOfArmsImageViewSP);

        numOfRobots = NumberOfRobots.NONE;

        editText1 = findViewById(R.id.firstPlayerNameEditText);
        editText2 = findViewById(R.id.secondPlayerNameEditText);
    }

    public void onChangeButtonClick(View view) {
        switch (view.getId()) {
            case R.id.leftNavigateButtonFP:
                firstPlayerCoA.setImageResource(teamResourceIds[currentImageFirstPlayer = (currentImageFirstPlayer + 1) % NUM_CLUB_TEAMS]);
                break;
            case R.id.rightNavigateButtonFP:
                firstPlayerCoA.setImageResource(teamResourceIds[currentImageFirstPlayer = (currentImageFirstPlayer + NUM_CLUB_TEAMS - 1) % NUM_CLUB_TEAMS]);
                break;
            case R.id.leftNavigateButtonSP:
                secondPlayerCoA.setImageResource(teamResourceIds[currentImageSecondPlayer = (currentImageSecondPlayer + 1) % NUM_CLUB_TEAMS]);
                break;
            case R.id.rightNavigateButtonSP:
                secondPlayerCoA.setImageResource(teamResourceIds[currentImageSecondPlayer = (currentImageSecondPlayer + NUM_CLUB_TEAMS - 1) % NUM_CLUB_TEAMS]);
                break;
        }
    }

    public void onClickKickOffButton(View view) {
       String firstPlayerName = editText1.getText().toString();
       String secondPlayerName = editText2.getText().toString();

       if(currentImageFirstPlayer == currentImageSecondPlayer) {
            Toast.makeText(this, "Teams are the same!", Toast.LENGTH_SHORT).show();
            return;
       }

       if(numOfRobots == NumberOfRobots.NONE && (firstPlayerName.isEmpty() || secondPlayerName.isEmpty())) {
            Toast.makeText(this, "Both players must have their names!", Toast.LENGTH_SHORT).show();
            return;
       }

       if(numOfRobots == NumberOfRobots.NONE && firstPlayerName.equals(secondPlayerName)) {
            Toast.makeText(this, "Players have same name!", Toast.LENGTH_SHORT).show();
            return;
       }

       if(numOfRobots == NumberOfRobots.ONE && firstPlayerName.isEmpty()) {
            Toast.makeText(this, "Second players don't have name!", Toast.LENGTH_SHORT).show();
            return;
       }

       Intent playGameIntent = new Intent(this, PlayGameActivity.class);

       playGameIntent.putExtra(FIRST_PLAYER_IMAGE_ID, teamResourceIds[currentImageFirstPlayer]);
       playGameIntent.putExtra(SECOND_PLAYER_IMAGE_ID, teamResourceIds[currentImageSecondPlayer]);
       playGameIntent.putExtra(FIRST_PLAYER_NAME, editText1.getText().toString());
       playGameIntent.putExtra(SECOND_PLAYER_NAME, editText2.getText().toString());
       playGameIntent.putExtra(NUMBER_OF_ROBOTS_STRING, numOfRobots.ordinal());
       playGameIntent.putExtra(MainActivity.RESUME_GAME_REQUESTED, false);

       startActivity(playGameIntent);
       finish();
    }

    public void onRadioButtonClick(View view) {
        RadioButton radioButton;

        switch (view.getId()) {
            case R.id.noneRobotsSelected:
                editText1.setEnabled(true);
                editText2.setEnabled(true);

                numOfRobots = NumberOfRobots.NONE;
                break;
            case R.id.oneRobotSelected:
                editText1.setEnabled(true);

                editText2.setText("");
                editText2.setEnabled(false);

                radioButton = findViewById(R.id.twoRobotsSelected);
                radioButton.setSelected(false);

                numOfRobots = NumberOfRobots.ONE;
                break;
            case R.id.twoRobotsSelected:
                editText1.setText("");
                editText1.setEnabled(false);

                editText2.setText("");
                editText2.setEnabled(false);

                radioButton = findViewById(R.id.oneRobotSelected);
                radioButton.setSelected(false);

                numOfRobots = NumberOfRobots.TWO;
                break;
        }
    }
}
