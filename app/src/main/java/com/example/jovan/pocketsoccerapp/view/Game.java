package com.example.jovan.pocketsoccerapp.view;

import android.util.Pair;

import java.io.Serializable;
import java.util.Vector;

public class Game implements Serializable {

    private static final int CORRECTION_FACTOR_HEIGHT = -50;
    private static final int CORRECTION_FACTOR_WIDTH = -60;

    public static final int PLAYER_RADIUS = 60;
    public static final int BALL_RADIUS = 35;

    public static final int WIDTH = 1280;
    public static final int HEIGHT = 672;

    private Ball[] balls;
    private Ball[] players;

    private Obstacle[] obstacles;
    private Obstacle[] gameOverLines;

    private int[] score;
    private int maxGoalsChosen;

    private float width;
    private float height;
    private float ballRadius, playerRadius;

    private boolean gameOver, goalScored;
    private int playersTurn;

    public Game(float width, float height, float ballRadius, float playerRadius, int maxGoalsChosen) {
        this.width = width;
        this.height = height;

        this.ballRadius = ballRadius;
        this.playerRadius = playerRadius;

        this.score = new int[] {0, 0};
        balls = new Ball[7];
        players = new Ball[6];

        initBallParameters();

        obstacles = new Obstacle[8];
        obstacles[0] = new Obstacle(CORRECTION_FACTOR_WIDTH, CORRECTION_FACTOR_HEIGHT, CORRECTION_FACTOR_WIDTH, height + CORRECTION_FACTOR_HEIGHT);
        obstacles[1] = new Obstacle(width + CORRECTION_FACTOR_WIDTH, CORRECTION_FACTOR_HEIGHT, width + CORRECTION_FACTOR_WIDTH, height + CORRECTION_FACTOR_HEIGHT);
        obstacles[2] = new Obstacle(CORRECTION_FACTOR_WIDTH, CORRECTION_FACTOR_HEIGHT, width + CORRECTION_FACTOR_WIDTH, CORRECTION_FACTOR_HEIGHT);
        obstacles[3] = new Obstacle(CORRECTION_FACTOR_WIDTH, height + CORRECTION_FACTOR_HEIGHT, width + CORRECTION_FACTOR_WIDTH, height + CORRECTION_FACTOR_HEIGHT);

        obstacles[4] = new Obstacle(CORRECTION_FACTOR_WIDTH,height * 0.33f + CORRECTION_FACTOR_HEIGHT,width * 0.04f + CORRECTION_FACTOR_WIDTH,height * 0.33f + CORRECTION_FACTOR_HEIGHT);
        obstacles[5] = new Obstacle(CORRECTION_FACTOR_WIDTH,height * (1-0.33f) + CORRECTION_FACTOR_HEIGHT,width * 0.04f + CORRECTION_FACTOR_WIDTH,height * (1 - 0.33f) + CORRECTION_FACTOR_HEIGHT);
        obstacles[6] = new Obstacle(width * (1-0.04f) + CORRECTION_FACTOR_WIDTH,height * 0.33f + CORRECTION_FACTOR_HEIGHT, width + CORRECTION_FACTOR_WIDTH,height * 0.33f + CORRECTION_FACTOR_HEIGHT);
        obstacles[7] = new Obstacle(width * (1-0.04f) + CORRECTION_FACTOR_WIDTH,height * (1-0.33f) + CORRECTION_FACTOR_HEIGHT, width + CORRECTION_FACTOR_WIDTH,height * (1 - 0.33f) + CORRECTION_FACTOR_HEIGHT);

        gameOverLines = new Obstacle[2];
        gameOverLines[0] = new Obstacle(width * 0.02f + CORRECTION_FACTOR_WIDTH,height* 0.33f + CORRECTION_FACTOR_HEIGHT,0.02f + CORRECTION_FACTOR_WIDTH,height *(1 - 0.33f) + CORRECTION_FACTOR_HEIGHT);
        gameOverLines[1] = new Obstacle(width * (1-0.02f) + CORRECTION_FACTOR_WIDTH,height* 0.33f + CORRECTION_FACTOR_HEIGHT,width * (1-0.02f) + CORRECTION_FACTOR_WIDTH,height *(1 - 0.33f) + CORRECTION_FACTOR_HEIGHT);

        this.maxGoalsChosen = maxGoalsChosen;
    }

    private void initBallParameters() {
        for(int i = 0; i < 6; i++) {
            players[i] = new Ball(((i/3)*2+1)*width/4.0f + CORRECTION_FACTOR_WIDTH, ((i%3)+1)*height/4.0f + CORRECTION_FACTOR_HEIGHT, playerRadius);
            balls[i] = players[i];
        }

        Ball soccerBall = new Ball(width / 2.0f + CORRECTION_FACTOR_WIDTH, height / 2.0f + CORRECTION_FACTOR_HEIGHT, ballRadius);
        balls[6] = soccerBall;
    }

    public void update(float dx) {
        for(Ball ball: balls)
            ball.updatePosition(dx);

        for(Ball ball: balls){
            for(Obstacle obstacle : obstacles){
                if(ball.isBallHitObstacle(obstacle)){
                    while(ball.isBallHitObstacle(obstacle)) {
                        Ball.separateFromObstacle(ball, obstacle, dx);
                    }
                    ball.resolveCollisionWithObstacle(obstacle);
                }
            }
        }

        Vector<Pair<Ball,Ball>> collisions = new Vector<>();
        for(Ball b1: balls){
            for(Ball b2: balls){
                if(b1 == b2)
                    continue;
                if(b1.isBallsOverlaps(b2)){
                    collisions.add(new Pair<>(b1,b2));
                    Ball.separateBalls(b1,b2);
                }
            }
        }

        for(Pair<Ball, Ball> collision: collisions){
            Ball b1 = collision.first;
            Ball b2 = collision.second;
            Ball.resolveCollisionBetweenBalls(b1, b2);
        }

        checkPlayerScored();
    }

    private void checkPlayerScored() {
        if(isGameOver())
            return;
        for(Obstacle line : gameOverLines) {
            if(balls[6].isBallHitObstacle(line)) {
                goalScored = true;
                if(line == gameOverLines[0])
                    score[1]++;
                else
                    score[0]++;
                initBallParameters();

                if(score[0] == maxGoalsChosen || score[1] == maxGoalsChosen) {
                    gameOver = true;
                }

                return;
            }
        }
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver() {
        this.gameOver = true;
    }

    public int getActivePlayer() {
        return playersTurn;
    }

    public void endTurn() {
        playersTurn = (playersTurn + 1) % 2;
    }

    public boolean isGoalScored() {
        boolean previousGoalsScoredBoolean = goalScored;
        goalScored = false;
        return previousGoalsScoredBoolean;
    }

    public int getPlayersScore(int playerNum) {
        return score[playerNum];
    }

    public void setPlayerScore(int playerNumber, int goals) {
        score[playerNumber] = goals;
    }

    public void setPlayersTurn(int playersTurn) {
        this.playersTurn = playersTurn;
    }

    public Ball findClosestTeamBall(int teamNumber, float x, float y) {
        float bestDistance = 100000.0f;
        Ball closestBall = null;

        for(int i = teamNumber * 3; i < (teamNumber + 1) * 3; i++) {
            float distance = Ball.distanceBetweenBalls(balls[i], x, y);
            if(distance < bestDistance) {
                bestDistance = distance;
                closestBall = balls[i];
            }
        }
        return closestBall;
    }

    public Ball[] getAllBalls() {
        return balls;
    }
}
