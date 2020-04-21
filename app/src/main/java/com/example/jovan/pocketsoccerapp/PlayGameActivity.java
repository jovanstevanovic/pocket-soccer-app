package com.example.jovan.pocketsoccerapp;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.jovan.pocketsoccerapp.controller.Controller;
import com.example.jovan.pocketsoccerapp.database.GameViewModel;
import com.example.jovan.pocketsoccerapp.util.Vector;
import com.example.jovan.pocketsoccerapp.view.Ball;
import com.example.jovan.pocketsoccerapp.view.CustomImageView;
import com.example.jovan.pocketsoccerapp.view.Game;

public class PlayGameActivity extends AppCompatActivity implements View.OnTouchListener {

    public static final int MAX_STREAMS = 2;

    private static final int REFRESH_RATE_TIME = 30;
    private static final float REFRESH_RATE_STEP = 0.01f;

    private static final float BALL_SPEED_LIMIT = 3.5f;

    private static final String CURRENT_ROBOT_NUMBER = "CURRENT_ROBOT_NUMBER";
    private static final String X_PLAYER_ = "X_PLAYER_";
    private static final String Y_PLAYER_ = "Y_PLAYER_";
    public static final String PARAMS_ARE_VALID = "PARAMS_ARE_VALID";

    private static final String FIRST_PLAYER_NAME = "FIRST_PLAYER_NAME";
    private static final String SECOND_PLAYER_NAME = "SECOND_PLAYER_NAME";
    private static final String FIRST_PLAYER_SCORE = "FIRST_PLAYER_SCORE";
    private static final String SECOND_PLAYER_SCORE = "SECOND_PLAYER_SCORE";
    private static final String SECONDS_PLAYED = "SECONDS_PLAYED";
    private static final String PLAYER_TURN = "PLAYER_TURN";

    private static final int ONE_SECOND = 1000; // 1s == 1000ms
    private static final int FIVE_SECOND = 5000; // 5s == 5000ms

    public static final int NUMBER_BALL_POSITIONS = 11;
    public static final int BALL_ID = 6;

    private CustomImageView[] customImageViews;
    private Controller controller;
    private Game game;
    private MainCalculatingThread mainCalculatingThread;

    private int numMinutesChosen;
    private int speedChosen;
    private int numOfRobots;

    private SoundPool soundPool;
    private int goalScoredSound, winnerSoundHomeTeam, winnerSoundAwayTeam, drawResultSound;

    private int[] differentBallPositions;
    private int currentBallPosition;

    private RobotPlayerThread robot1;
    private RobotPlayerThread robot2;

    private GameViewModel gameViewModel;

    private String firstPlayerName;
    private String secondPlayerName;

    private int firstPlayerTeamId;
    private int secondPlayerTeamId;

    private GameTimerThread gameTimerThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_game);

        Intent dataIntent = getIntent();

        SharedPreferences sharedPreferences = getSharedPreferences("parameters_select", MODE_PRIVATE);
        SharedPreferences.Editor editor = getSharedPreferences("parameters_select", MODE_PRIVATE).edit();

        SharedPreferences sharedPreferencesResume = getSharedPreferences("resume_game_parameters", MODE_PRIVATE);
        SharedPreferences.Editor editorResume = getSharedPreferences("resume_game_parameters", MODE_PRIVATE).edit();

        int numGoalsChosen = sharedPreferences.getInt(MainActivity.MAX_GOALS, MainActivity.MaxGoals.ONE_GOAL.getValue());
        numMinutesChosen = sharedPreferences.getInt(MainActivity.MAX_MINUTES, MainActivity.MaxMinutes.TWO_MIN.getValue());
        speedChosen = sharedPreferences.getInt(MainActivity.SPEED, MainActivity.Speed.TWO_SPEED.getValue());
        int currentImage = sharedPreferences.getInt(MainActivity.TERRAIN, 0);

        RelativeLayout activityLayout = findViewById(R.id.play_game_main);
        activityLayout.setBackgroundResource(R.drawable.field1_hd + currentImage);

        this.game = new Game(Game.WIDTH, Game.HEIGHT, Game.BALL_RADIUS, Game.PLAYER_RADIUS, numGoalsChosen);

        boolean isGameAlreadySaved = dataIntent.getBooleanExtra(MainActivity.RESUME_GAME_REQUESTED, false);
        if(isGameAlreadySaved) {
            this.firstPlayerTeamId = sharedPreferencesResume.getInt(NewGameActivity.FIRST_PLAYER_IMAGE_ID, 0);
            this.secondPlayerTeamId = sharedPreferencesResume.getInt(NewGameActivity.SECOND_PLAYER_IMAGE_ID, 0);
            this.numOfRobots = sharedPreferencesResume.getInt(NewGameActivity.NUMBER_OF_ROBOTS_STRING, 0);

            Ball[] balls = game.getAllBalls();
            int i = 0;

            for(Ball ball : balls) {
                float x = sharedPreferencesResume.getFloat(PlayGameActivity.X_PLAYER_ + i, 0);
                float y = sharedPreferencesResume.getFloat(PlayGameActivity.Y_PLAYER_ + i, 0);
                Vector position = ball.getBallPosition();
                position.x = x;
                position.y = y;
                i++;
            }

            TextView firstPlayerName = findViewById(R.id.firstPlayerNameTextView);
            TextView secondPlayerName = findViewById(R.id.secondPlayerNameTextView);
            TextView firstPlayerScore = findViewById(R.id.scoreTablePlayer1);
            TextView secondPlayerScore = findViewById(R.id.scoreTablePlayer2);

            String firstPlayerNameString =  sharedPreferencesResume.getString(PlayGameActivity.FIRST_PLAYER_NAME, "");
            String secondPlayerNameString = sharedPreferencesResume.getString(PlayGameActivity.SECOND_PLAYER_NAME, "");
            String firstPlayerScoreString = sharedPreferencesResume.getString(PlayGameActivity.FIRST_PLAYER_SCORE, "0");
            String secondPlayerScoreString = sharedPreferencesResume.getString(PlayGameActivity.SECOND_PLAYER_SCORE, "0");

            firstPlayerName.setText(firstPlayerNameString);
            secondPlayerName.setText(secondPlayerNameString);
            firstPlayerScore.setText(firstPlayerScoreString);
            secondPlayerScore.setText(secondPlayerScoreString);

            editorResume.putBoolean(PlayGameActivity.PARAMS_ARE_VALID, false);

            editorResume.apply();

            int seconds = sharedPreferencesResume.getInt(SECONDS_PLAYED, 0);

            this.game.setPlayerScore(Controller.FIRST_PLAYER, Integer.parseInt(firstPlayerScoreString));
            this.game.setPlayerScore(Controller.SECOND_PLAYER, Integer.parseInt(secondPlayerScoreString));

            this.gameTimerThread = this.new GameTimerThread(seconds);
            this.gameTimerThread.start();

            int activePlayer = sharedPreferencesResume.getInt(PLAYER_TURN, 0);
            this.game.setPlayersTurn(activePlayer);

            this.firstPlayerName = firstPlayerNameString;
            this.secondPlayerName = secondPlayerNameString;

            switch (numOfRobots) {
                case 1:
                    robot1 = new RobotPlayerThread();
                    robot1.start();
                    break;
                case 2:
                    robot1 = new RobotPlayerThread();
                    robot2 = new RobotPlayerThread();

                    robot1.setSecondRobot(robot2);
                    robot2.setSecondRobot(robot1);

                    robot1.start();
                    robot2.start();

                    synchronized (robot1) {
                        robot1.setCanPlay();
                    }
                    break;
            }

        } else {
            this.firstPlayerTeamId = dataIntent.getIntExtra(NewGameActivity.FIRST_PLAYER_IMAGE_ID, 0);
            this.secondPlayerTeamId = dataIntent.getIntExtra(NewGameActivity.SECOND_PLAYER_IMAGE_ID, 0);
            this.numOfRobots = dataIntent.getIntExtra(NewGameActivity.NUMBER_OF_ROBOTS_STRING, 0);

            this.gameTimerThread = this.new GameTimerThread();
            this.gameTimerThread.start();

            switch (numOfRobots) {
                case 0: {
                    firstPlayerName = dataIntent.getStringExtra(NewGameActivity.FIRST_PLAYER_NAME);
                    secondPlayerName = dataIntent.getStringExtra(NewGameActivity.SECOND_PLAYER_NAME);

                    TextView firstPlayerNameTextView = findViewById(R.id.firstPlayerNameTextView);
                    TextView secondPlayerNameTextView = findViewById(R.id.secondPlayerNameTextView);

                    firstPlayerNameTextView.setText(firstPlayerName);
                    secondPlayerNameTextView.setText(secondPlayerName);
                }
                break;
                case 1: {
                    firstPlayerName = dataIntent.getStringExtra(NewGameActivity.FIRST_PLAYER_NAME);

                    TextView firstPlayerNameTextView = findViewById(R.id.firstPlayerNameTextView);
                    TextView secondPlayerNameTextView = findViewById(R.id.secondPlayerNameTextView);

                    firstPlayerNameTextView.setText(firstPlayerName);

                    int currentRobotNumber = sharedPreferences.getInt(PlayGameActivity.CURRENT_ROBOT_NUMBER, 0);
                    editor.putInt(PlayGameActivity.CURRENT_ROBOT_NUMBER, currentRobotNumber + 1);
                    editor.apply();

                    secondPlayerName = "Robot_" + currentRobotNumber;
                    secondPlayerNameTextView.setText(secondPlayerName);

                    robot1 = new RobotPlayerThread();
                    robot1.start();
                }
                break;
                case 2: {
                    TextView firstPlayerNameTextView = findViewById(R.id.firstPlayerNameTextView);
                    TextView secondPlayerNameTextView = findViewById(R.id.secondPlayerNameTextView);

                    int currentRobotNumber = sharedPreferences.getInt(PlayGameActivity.CURRENT_ROBOT_NUMBER, 0);
                    editor.putInt(PlayGameActivity.CURRENT_ROBOT_NUMBER, currentRobotNumber + 2);
                    editor.apply();

                    firstPlayerName = "Robot_" + currentRobotNumber;
                    secondPlayerName = "Robot_" + (currentRobotNumber + 1);

                    firstPlayerNameTextView.setText(firstPlayerName);
                    secondPlayerNameTextView.setText(secondPlayerName);

                    robot1 = new RobotPlayerThread();
                    robot2 = new RobotPlayerThread();

                    robot1.setSecondRobot(robot2);
                    robot2.setSecondRobot(robot1);

                    robot1.start();
                    robot2.start();

                    synchronized (robot1) {
                        robot1.setCanPlay();
                    }
                }
                break;
            }
        }

        customImageViews = new CustomImageView[7];

        customImageViews[0] = findViewById(R.id.customImageViewBall1);
        customImageViews[1] = findViewById(R.id.customImageViewBall2);
        customImageViews[2] = findViewById(R.id.customImageViewBall3);
        customImageViews[3] = findViewById(R.id.customImageViewBall4);
        customImageViews[4] = findViewById(R.id.customImageViewBall5);
        customImageViews[5] = findViewById(R.id.customImageViewBall6);
        customImageViews[6] = findViewById(R.id.customImageViewBall7);

        for(int i = 0; i < 6; i++) {
            if(i < 3) {
                customImageViews[i].setBackgroundResource(firstPlayerTeamId);
            }
            else {
                customImageViews[i].setBackgroundResource(secondPlayerTeamId);
            }
            customImageViews[i].setOnTouchListener(this);
        }

        customImageViews[6].setBackgroundResource(R.drawable.ball0);
        customImageViews[6].setOnTouchListener(this);

        controller = new Controller(game);

        mainCalculatingThread = this.new MainCalculatingThread();
        mainCalculatingThread.start();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();

            soundPool = new SoundPool.Builder().setMaxStreams(PlayGameActivity.MAX_STREAMS)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
            soundPool = new SoundPool(PlayGameActivity.MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
        }

        goalScoredSound = soundPool.load(this, R.raw.goal_scored_sound,1);
        winnerSoundHomeTeam = soundPool.load(this, R.raw.winner_hometeam_sound,1);
        winnerSoundAwayTeam = soundPool.load(this, R.raw.winner_awayteam_sound, 1);
        drawResultSound = soundPool.load(this, R.raw.draw_result_sound, 1);

        this.differentBallPositions = new int[NUMBER_BALL_POSITIONS];

        differentBallPositions[0] = R.drawable.ball0;
        differentBallPositions[1] = R.drawable.ball1;
        differentBallPositions[2] = R.drawable.ball2;
        differentBallPositions[3] = R.drawable.ball3;
        differentBallPositions[4] = R.drawable.ball4;
        differentBallPositions[5] = R.drawable.ball5;
        differentBallPositions[6] = R.drawable.ball6;
        differentBallPositions[7] = R.drawable.ball7;
        differentBallPositions[8] = R.drawable.ball8;
        differentBallPositions[9] = R.drawable.ball9;
        differentBallPositions[10] = R.drawable.ball10;

        this.gameViewModel = ViewModelProviders.of(this).get(GameViewModel.class);

        shadeAllInactiveBalls();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float x;
        float y;

        if(game.isGameOver()) {
            return true;
        }

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                x = event.getX();
                y = event.getY();

                controller.onBallSelected(v, x, y);
                break;
            case MotionEvent.ACTION_UP:
                x = event.getX();
                y = event.getY();

                controller.onBallReleased(v, x, y);

                shadeAllInactiveBalls();

                if(numOfRobots > 0) {
                    synchronized (robot1) {
                        robot1.setCanPlay();
                    }
                }

                break;
        }
        return true;
    }

    public void shadeAllInactiveBalls() {
        int activePlayer = game.getActivePlayer();

        if(activePlayer == Controller.SECOND_PLAYER) {
            for(int i = 0; i < 3; i++) {
                customImageViews[i].setAlpha(Controller.HALF_VISIBILITY);
            }
            for(int i = 3; i < 6; i++) {
                customImageViews[i].setAlpha(Controller.FULL_VISIBILITY);
            }
        } else {
            for(int i = 0; i < 3; i++) {
                customImageViews[i].setAlpha(Controller.FULL_VISIBILITY);
            }
            for(int i = 3; i < 6; i++) {
                customImageViews[i].setAlpha(Controller.HALF_VISIBILITY);
            }
        }
    }

    @Override
    public void onBackPressed() {

        SharedPreferences.Editor editor = getSharedPreferences("resume_game_parameters", MODE_PRIVATE).edit();
        Ball[] balls = game.getAllBalls();
        int i = 0;

        for(Ball ball : balls) {
            Vector position = ball.getBallPosition();
            editor.putFloat(PlayGameActivity.X_PLAYER_ + i, position.x);
            editor.putFloat(PlayGameActivity.Y_PLAYER_ + i, position.y);
            i++;
        }

        TextView firstPlayerName = findViewById(R.id.firstPlayerNameTextView);
        TextView secondPlayerName = findViewById(R.id.secondPlayerNameTextView);
        TextView firstPlayerScore = findViewById(R.id.scoreTablePlayer1);
        TextView secondPlayerScore = findViewById(R.id.scoreTablePlayer2);

        editor.putString(PlayGameActivity.FIRST_PLAYER_NAME, firstPlayerName.getText().toString());
        editor.putString(PlayGameActivity.SECOND_PLAYER_NAME, secondPlayerName.getText().toString());
        editor.putString(PlayGameActivity.FIRST_PLAYER_SCORE, firstPlayerScore.getText().toString());
        editor.putString(PlayGameActivity.SECOND_PLAYER_SCORE, secondPlayerScore.getText().toString());

        editor.putInt(NewGameActivity.FIRST_PLAYER_IMAGE_ID, firstPlayerTeamId);
        editor.putInt(NewGameActivity.SECOND_PLAYER_IMAGE_ID, secondPlayerTeamId);
        editor.putInt(NewGameActivity.NUMBER_OF_ROBOTS_STRING, numOfRobots);

        editor.putBoolean(PlayGameActivity.PARAMS_ARE_VALID, true);

        editor.putInt(SECONDS_PLAYED, gameTimerThread.getSeconds());
        editor.putInt(PLAYER_TURN, game.getActivePlayer());

        shutdownAllThreads();

        editor.apply();

        super.onBackPressed();
    }

    public void onClickEndGameButton(View view) {
        int firstPlayerWin = game.getPlayersScore(Controller.FIRST_PLAYER);
        int secondPlayerWin = game.getPlayersScore(Controller.SECOND_PLAYER);

        if(firstPlayerWin > secondPlayerWin) {
            firstPlayerWin = 1;
            secondPlayerWin = 0;
        } else {
            if(firstPlayerWin < secondPlayerWin) {
                firstPlayerWin = 0;
                secondPlayerWin = 1;
            } else {
                firstPlayerWin = 0;
                secondPlayerWin = 0;
            }
        }

        TextView gameTimer = findViewById(R.id.gameTimer);
        com.example.jovan.pocketsoccerapp.database.Game newGame =
                new com.example.jovan.pocketsoccerapp.database.Game(firstPlayerName, secondPlayerName,
                        firstPlayerWin, secondPlayerWin, gameTimer.getText().toString());
        this.gameViewModel.insertGame(newGame);

        Intent displayMatchInfo = new Intent(this, DetailViewActivity.class);

        displayMatchInfo.putExtra(ShowStatisticsActivity.FIRST_PLAYER_NAME, firstPlayerName);
        displayMatchInfo.putExtra(ShowStatisticsActivity.SECOND_PLAYER_NAME, secondPlayerName);
        displayMatchInfo.putExtra(ShowStatisticsActivity.TYPE_OF_DETAIL_VIEW, ShowStatisticsActivity.Type.FROM_MATCH_END.getOrdinary());

        startActivity(displayMatchInfo);
        finish();
    }

    private class MainCalculatingThread extends Thread {

        private TextView firstPlayerScore;
        private TextView secondPlayerScore;
        private Button gameWinnerButton;

        MainCalculatingThread() {
            this.firstPlayerScore = findViewById(R.id.scoreTablePlayer1);
            this.secondPlayerScore = findViewById(R.id.scoreTablePlayer2);
            this.gameWinnerButton = findViewById(R.id.gameWinnerButton);
        }

        public void run() {
            while (!Thread.interrupted()) {
                game.update(REFRESH_RATE_STEP * speedChosen);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int i = 0;
                        Ball[] balls = game.getAllBalls();
                        for(Ball ball : balls) {
                            Vector position = ball.getBallPosition();
                            customImageViews[i].setX(position.x);
                            customImageViews[i].setY(position.y);

                            if(i == BALL_ID) {
                                Vector velocity = ball.getAccVector();
                                if(velocity.x >= BALL_SPEED_LIMIT || velocity.y >= BALL_SPEED_LIMIT) {
                                    customImageViews[i].setBackgroundResource(differentBallPositions[currentBallPosition]);
                                    currentBallPosition = (currentBallPosition + 1) % NUMBER_BALL_POSITIONS;
                                }
                            }
                            i++;

                            boolean isGoalScored = game.isGoalScored();
                            if(isGoalScored) {
                                int numberOfGoalsFP = game.getPlayersScore(Controller.FIRST_PLAYER);
                                int numberOfGoalsSP = game.getPlayersScore(Controller.SECOND_PLAYER);

                                firstPlayerScore.setText(Integer.toString(numberOfGoalsFP));
                                secondPlayerScore.setText(Integer.toString(numberOfGoalsSP));

                                boolean isGameOver = game.isGameOver();
                                if(isGameOver) {
                                    gameWinnerButton.setVisibility(View.VISIBLE);

                                    shutdownAllThreads();

                                    if(numberOfGoalsFP > numberOfGoalsSP) {
                                        gameWinnerButton.setText(R.string.player_1_wins);
                                        soundPool.play(winnerSoundHomeTeam, 1, 1, 0, 0, 1);
                                    } else {
                                        gameWinnerButton.setText(R.string.player_2_wins);
                                        soundPool.play(winnerSoundAwayTeam, 1, 1, 0, 0, 1);
                                    }
                                } else {
                                    soundPool.play(goalScoredSound, 1, 1, 0, 0, 1);
                                }
                            }
                        }
                    }
                });

                try {
                    Thread.sleep(REFRESH_RATE_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    private class GameTimerThread extends Thread {

        private Button gameWinnerButton;
        private TextView gameTimer;
        private int seconds;

        GameTimerThread(){
            this.gameWinnerButton = findViewById(R.id.gameWinnerButton);
            this.gameTimer = findViewById(R.id.gameTimer);
            this.seconds = numMinutesChosen * 60;
        }

        GameTimerThread(int seconds) {
            this.gameWinnerButton = findViewById(R.id.gameWinnerButton);
            this.gameTimer = findViewById(R.id.gameTimer);
            this.seconds = seconds;
        }

        synchronized int getSeconds() {
            return seconds;
        }

        @Override
        public void run() {
            while(seconds > 0 && !Thread.interrupted()) {
                try {
                    Thread.sleep(ONE_SECOND); // Waiting for one second an then refresh UI timer.
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                seconds--;
                publishProgress();
            }
        }

        private void publishProgress() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int minutes = seconds / 60;
                    String time = minutes + "m    " + (seconds%60) + "s";
                    gameTimer.setText(time);

                    if(seconds == 0) {
                        game.setGameOver();
                        gameWinnerButton.setVisibility(View.VISIBLE);

                        shutdownAllThreads();

                        int goalsHomeTeam = game.getPlayersScore(Controller.FIRST_PLAYER);
                        int goalsAwayTeam = game.getPlayersScore(Controller.SECOND_PLAYER);
                        if(goalsHomeTeam > goalsAwayTeam) {
                            soundPool.play(winnerSoundHomeTeam, 1, 1, 0, 0, 1);
                            gameWinnerButton.setText(R.string.player_1_wins);
                        } else {
                            if(goalsHomeTeam < goalsAwayTeam) {
                                soundPool.play(winnerSoundAwayTeam, 1, 1, 0, 0, 1);
                                gameWinnerButton.setText(R.string.player_2_wins);
                            } else {
                                soundPool.play(drawResultSound, 1, 1, 0, 0, 1);
                            }
                        }
                    }
                }
            });
        }
    }

    private void shutdownAllThreads() {
        if(robot1 != null) {
            robot1.interrupt();
        }

        if(robot2 != null) {
            robot2.interrupt();
        }

        if(mainCalculatingThread != null) {
            mainCalculatingThread.interrupt();
        }

        if(gameTimerThread != null) {
            gameTimerThread.interrupt();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        soundPool.release();

        shutdownAllThreads();
    }

    public class RobotPlayerThread extends Thread {

        private boolean canPlay;

        private RobotPlayerThread secondRobot;

        void setSecondRobot(RobotPlayerThread secondRobot) {
            this.secondRobot = secondRobot;
        }

        @Override
        public void run() {
            while(!Thread.interrupted()) {
                synchronized (this) {
                    while(!canPlay) {
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    canPlay = false;
                }

                try {
                    Thread.sleep(FIVE_SECOND); // Sleep one second, and then let another player or robot to move its figures.
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                calculateNewVelocity();

                game.endTurn();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        shadeAllInactiveBalls();
                    }
                });

                if(numOfRobots == 2) {
                    synchronized (secondRobot) {
                        secondRobot.setCanPlay();
                    }
                }
            }
        }

        void setCanPlay() {
            this.canPlay = true;
            this.notifyAll();
        }

        private void calculateNewVelocity() {
            Ball[] balls = game.getAllBalls();
            int activePlayer = game.getActivePlayer();
            Vector soccerBallPosition = balls[6].getBallPosition();

            Ball closestBall = game.findClosestTeamBall(activePlayer, soccerBallPosition.x, soccerBallPosition.y);

            if(closestBall != null) {
                Vector closestTeamBallVectorPosition = closestBall.getBallPosition();

                float distanceX = soccerBallPosition.x - closestTeamBallVectorPosition.x;
                float distanceY = soccerBallPosition.y - closestTeamBallVectorPosition.y;
                closestBall.setVelocity(distanceX * Controller.FACTOR, distanceY * Controller.FACTOR);
            }
        }
    }
}
