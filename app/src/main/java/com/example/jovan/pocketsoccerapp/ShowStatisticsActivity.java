package com.example.jovan.pocketsoccerapp;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jovan.pocketsoccerapp.database.GameDAO;
import com.example.jovan.pocketsoccerapp.database.GameViewModel;
import com.facebook.stetho.Stetho;

import java.util.List;

public class ShowStatisticsActivity extends AppCompatActivity {

    private LinearLayout mainLL;

    public static final String FIRST_PLAYER_NAME = "First player name";
    public static final String SECOND_PLAYER_NAME = "Second player name";
    public static final String TYPE_OF_DETAIL_VIEW = "Type of detail view";

    public enum Type {
        FROM_SHOW_STAT(1), FROM_MATCH_END(2);

        private int ordinary;

        Type(int ordinary) {
            this.ordinary = ordinary;
        }

        public int getOrdinary() {
            return ordinary;
        }
    }

    private GameViewModel gameViewModel;

    private Typeface typeface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_statistics);

        this.mainLL = findViewById(R.id.scoreTable);

        this.gameViewModel = ViewModelProviders.of(this).get(GameViewModel.class);

        typeface = Typeface.createFromAsset(getAssets(), "dancing_script.ttf");

        gameViewModel.getAllGames().observe(this, new Observer<List<GameDAO.ResultClass>>() {
            @Override
            public void onChanged(@Nullable List<GameDAO.ResultClass> resultClasses) {
                if (resultClasses == null)
                    return;

                int childCount = mainLL.getChildCount();
                if(childCount > 1) {
                    mainLL.removeViews(0, childCount - 1);
                }

                int numRows = resultClasses.size();
                for (int i = 0; i < numRows; i++) {
                    final GameDAO.ResultClass result = resultClasses.get(i);

                    LinearLayout linearLayout = new LinearLayout(ShowStatisticsActivity.this);
                    linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                    linearLayout.setLayoutParams(
                            new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    linearLayout.setPadding(0, 0, 0, 20);

                    LinearLayout.LayoutParams layoutParamsW1 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
                    LinearLayout.LayoutParams layoutParamsW2 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 2);

                    TextView firstPlayerName = new TextView(ShowStatisticsActivity.this);
                    firstPlayerName.setText(result.getFirstPlayerName());
                    firstPlayerName.setTypeface(typeface);
                    firstPlayerName.setTextSize(DetailViewActivity.FONT_SIZE);
                    firstPlayerName.setGravity(Gravity.CENTER);

                    TextView firstPlayerScore = new TextView(ShowStatisticsActivity.this);
                    firstPlayerScore.setText(Integer.toString(result.getNumWinsFirstPlayer()));
                    firstPlayerScore.setTypeface(typeface);
                    firstPlayerScore.setTextSize(DetailViewActivity.FONT_SIZE);
                    firstPlayerScore.setGravity(Gravity.CENTER);

                    TextView secondPlayerName = new TextView(ShowStatisticsActivity.this);
                    secondPlayerName.setText(result.getSecondPlayerName());
                    secondPlayerName.setTypeface(typeface);
                    secondPlayerName.setTextSize(DetailViewActivity.FONT_SIZE);
                    secondPlayerName.setGravity(Gravity.CENTER);

                    TextView secondPlayerScore = new TextView(ShowStatisticsActivity.this);
                    secondPlayerScore.setText(Integer.toString(result.getNumWinsSecondPlayer()));
                    secondPlayerScore.setTypeface(typeface);
                    secondPlayerScore.setTextSize(DetailViewActivity.FONT_SIZE);
                    secondPlayerScore.setGravity(Gravity.CENTER);

                    linearLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent detailViewIntent = new Intent(ShowStatisticsActivity.this, DetailViewActivity.class);

                            detailViewIntent.putExtra(FIRST_PLAYER_NAME, result.getFirstPlayerName());
                            detailViewIntent.putExtra(SECOND_PLAYER_NAME, result.getSecondPlayerName());
                            detailViewIntent.putExtra(TYPE_OF_DETAIL_VIEW, Type.FROM_SHOW_STAT.getOrdinary());

                            startActivity(detailViewIntent);
                        }
                    });

                    linearLayout.addView(firstPlayerName, layoutParamsW2);
                    linearLayout.addView(firstPlayerScore, layoutParamsW1);
                    linearLayout.addView(secondPlayerName, layoutParamsW2);
                    linearLayout.addView(secondPlayerScore, layoutParamsW1);
                    mainLL.addView(linearLayout, 0);
                }
            }
        });
    }

    private void deleteAllRecords() {
        gameViewModel.deleteAllRecords();

        int childCount = mainLL.getChildCount();
        if(childCount > 1) { // If we have only one view, that's means that we don't have any records. So no need to delete anything.
            mainLL.removeViews(0, childCount - 1);
            Toast.makeText(this, "All records deleted!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No records to be deleted!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void onClickBackButton(View view) {
        finish();
    }

    public void onClickDeleteAllRecordsButton(View view) {
        deleteAllRecords();
    }
}
