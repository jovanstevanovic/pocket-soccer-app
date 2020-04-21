package com.example.jovan.pocketsoccerapp.controller;

import android.view.View;

import com.example.jovan.pocketsoccerapp.R;
import com.example.jovan.pocketsoccerapp.view.Ball;
import com.example.jovan.pocketsoccerapp.view.Game;

public class Controller {

    public static final float HALF_VISIBILITY = 0.5f;
    public static final float FULL_VISIBILITY = 1f;
    public static final float FACTOR = 7.5f;

    public static final int FIRST_PLAYER = 0;
    public static final int SECOND_PLAYER = 1;

    private Game game;

    private float startXValue, startYValue;

    public Controller(Game game) {
        this.game = game;
    }

    public void onBallSelected(View view, float x, float y) {
        this.startXValue = x;
        this.startYValue = y;

        view.setAlpha(HALF_VISIBILITY);
    }

    public void onBallReleased(View view, float x, float y) {
        view.setAlpha(FULL_VISIBILITY);
        Ball[] balls = game.getAllBalls();

        float distanceX = x - startXValue;
        float distanceY = y - startYValue;

        switch (view.getId()) {
            case R.id.customImageViewBall1:
                if(canMoveBall(1)) {
                    balls[0].setVelocity(distanceX * FACTOR, distanceY * FACTOR);
                    game.endTurn();
                }
                break;
            case R.id.customImageViewBall2:
                if(canMoveBall(2)) {
                    balls[1].setVelocity(distanceX * FACTOR, distanceY * FACTOR);
                    game.endTurn();
                }
                break;
            case R.id.customImageViewBall3:
                if(canMoveBall(3)) {
                    balls[2].setVelocity(distanceX * FACTOR, distanceY * FACTOR);
                    game.endTurn();
                }

                break;
            case R.id.customImageViewBall4:
                if(canMoveBall(4)) {
                    balls[3].setVelocity(distanceX * FACTOR, distanceY * FACTOR);
                    game.endTurn();
                }
                break;
            case R.id.customImageViewBall5:
                if(canMoveBall(5)) {
                    balls[4].setVelocity(distanceX * FACTOR, distanceY * FACTOR);
                    game.endTurn();
                }
                break;
            case R.id.customImageViewBall6:
                if(canMoveBall(6)) {
                    balls[5].setVelocity(distanceX * FACTOR, distanceY * FACTOR);
                    game.endTurn();
                }
                break;
        }
    }

    private boolean canMoveBall(int ballId) {
        return (ballId < 4 && game.getActivePlayer() == FIRST_PLAYER) || (ballId >= 4 && game.getActivePlayer() == SECOND_PLAYER);
    }
}
