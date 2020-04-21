package com.example.jovan.pocketsoccerapp;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jovan.pocketsoccerapp.database.Game;
import com.example.jovan.pocketsoccerapp.database.GameViewModel;

import java.util.List;

public class DetailViewActivity extends AppCompatActivity {

    public static final float FONT_SIZE = 22.0f;

    private GameViewModel gameViewModel;
    private LinearLayout mainLL;

    private String firstPlayerName;
    private String secondPlayerName;

    private Typeface typeface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_view);

        gameViewModel = ViewModelProviders.of(this).get(GameViewModel.class);

        Intent data = getIntent();
        firstPlayerName = data.getStringExtra(ShowStatisticsActivity.FIRST_PLAYER_NAME);
        secondPlayerName = data.getStringExtra(ShowStatisticsActivity.SECOND_PLAYER_NAME);

        typeface = Typeface.createFromAsset(getAssets(), "dancing_script.ttf");

        this.mainLL = findViewById(R.id.playerGameRow);

        gameViewModel.getPlayersStats(firstPlayerName, secondPlayerName).observe(this, new Observer<List<Game>>() {
            @Override
            public void onChanged(@Nullable List<Game> games) {
                if(games == null) return;

                int numWinsFP = 0;
                int numWinsSP = 0;

                for(Game game: games) {
                    LinearLayout linearLayout = new LinearLayout(DetailViewActivity.this);
                    linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                    linearLayout.setLayoutParams(
                            new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    linearLayout.setPadding(0, 0, 0, 20);

                    LinearLayout.LayoutParams layoutParamsW1 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
                    LinearLayout.LayoutParams layoutParamsW2 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);

                    TextView firstPlayerName = new TextView(DetailViewActivity.this);
                    firstPlayerName.setTypeface(typeface);
                    firstPlayerName.setTextSize(FONT_SIZE);
                    firstPlayerName.setText(game.getFirstPlayerName());
                    firstPlayerName.setGravity(Gravity.CENTER);

                    TextView secondPlayerName = new TextView(DetailViewActivity.this);
                    secondPlayerName.setTypeface(typeface);
                    secondPlayerName.setTextSize(FONT_SIZE);
                    secondPlayerName.setText(game.getSecondPlayerName());
                    secondPlayerName.setGravity(Gravity.CENTER);

                    TextView playerWinTW = new TextView(DetailViewActivity.this);
                    playerWinTW.setTypeface(typeface);
                    playerWinTW.setTextSize(FONT_SIZE);

                    int firstPlayerWin = game.getFirstPlayerWin();
                    int secondPlayerWin = game.getSecondPlayerWin();
                    if(firstPlayerWin == 0 && secondPlayerWin == 0) {
                        playerWinTW.setText("Draw");
                    } else {
                        if(firstPlayerWin == 0) {
                            numWinsSP++;
                            playerWinTW.setText("2nd");
                        } else {
                            numWinsFP++;
                            playerWinTW.setText("1th");
                        }
                    }

                    playerWinTW.setGravity(Gravity.CENTER);

                    TextView finishTime = new TextView(DetailViewActivity.this);
                    finishTime.setTypeface(typeface);
                    finishTime.setTextSize(FONT_SIZE);
                    finishTime.setText(game.getGameTimeFinished());
                    finishTime.setGravity(Gravity.CENTER);

                    linearLayout.addView(firstPlayerName, layoutParamsW2);
                    linearLayout.addView(secondPlayerName, layoutParamsW2);
                    linearLayout.addView(playerWinTW, layoutParamsW1);
                    linearLayout.addView(finishTime, layoutParamsW1);
                    mainLL.addView(linearLayout, 0);
                }

                TextView firstPlayerWins = findViewById(R.id.numWinsFP);
                firstPlayerWins.setText(Integer.toString(numWinsFP) + " win(s)");

                TextView secondPlayerWins = findViewById(R.id.numWinsSP);
                secondPlayerWins.setText(Integer.toString(numWinsSP) + " win(s)");
            }
        });

        int type = data.getIntExtra(ShowStatisticsActivity.TYPE_OF_DETAIL_VIEW,
                ShowStatisticsActivity.Type.FROM_SHOW_STAT.getOrdinary());

        if(type == ShowStatisticsActivity.Type.FROM_SHOW_STAT.getOrdinary()) {
            Button backButton = findViewById(R.id.backButton);
            backButton.setVisibility(View.VISIBLE);
        } else {
            Button continueButton = findViewById(R.id.continueButton);
            Button clearRecordsButton = findViewById(R.id.clearPlayersRecords);
            clearRecordsButton.setVisibility(View.GONE);
            continueButton.setVisibility(View.VISIBLE);
        }

    }

    private void deleteRecords() {
        gameViewModel.deleteRecords(firstPlayerName, secondPlayerName);

        int childCount = mainLL.getChildCount();
        if(childCount > 2) { // If we have only two views, that's means that we don't have any records. So no need to delete anything.
            mainLL.removeViews(0, childCount - 2);
            Toast.makeText(this, "Records are deleted!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No records to be deleted!", Toast.LENGTH_SHORT).show();
        }


    }

    public void onClickContinueButton(View view) {
        finish();
    }

    public void onClickBackButton(View view) {
        finish();
    }

    public void onClickDeleteRecordsButton(View view) {
        deleteRecords();
    }
}
