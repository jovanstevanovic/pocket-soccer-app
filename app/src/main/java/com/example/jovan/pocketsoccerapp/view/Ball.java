package com.example.jovan.pocketsoccerapp.view;

import com.example.jovan.pocketsoccerapp.util.Vector;

import java.io.Serializable;

public class Ball implements Serializable {

    private Vector speedVector;
    private Vector distanceVector;
    private Vector accVector;
    private float radius;

    Ball(float x, float y, float radius) {
        distanceVector = new Vector(x, y);
        speedVector = new Vector(0, 0);
        accVector = new Vector(0, 0);

        this.radius = radius;
    }

    void updatePosition(float dx) {
        accVector.x = -speedVector.x * 0.75f;
        accVector.y = -speedVector.y * 0.75f;

        speedVector.x += accVector.x * dx;
        speedVector.y += accVector.y * dx;

        distanceVector.x += speedVector.x * dx;
        distanceVector.y += speedVector.y * dx;

        if((speedVector.x * speedVector.x + speedVector.y * speedVector.y) < 1) {
            speedVector.resetValues();
            accVector.resetValues();
        }
    }

    static float distanceBetweenBalls(Ball b1, float x, float y) {
        return (float)(Math.sqrt((b1.distanceVector.x - x)*(b1.distanceVector.x - x)
                + (b1.distanceVector.y - y)*(b1.distanceVector.y - y)));
    }

    private static float distanceBetweenBalls(Ball b1, Ball b2) {
        return (float)(Math.sqrt((b1.distanceVector.x - b2.distanceVector.x)*(b1.distanceVector.x - b2.distanceVector.x)
                + (b1.distanceVector.y - b2.distanceVector.y)*(b1.distanceVector.y - b2.distanceVector.y)));
    }

    private static float distanceFromObstacle(Ball b1, Obstacle o) {
        return (float) (Math.abs(o.factorA * b1.distanceVector.x + o.factorB * b1.distanceVector.y  + o.factorC)
                / Math.sqrt(o.factorA * o.factorA + o.factorB * o.factorB));
    }

    boolean isBallsOverlaps(Ball ball) {
        return distanceBetweenBalls(this, ball) <= (radius + ball.radius);
    }

    boolean isBallHitObstacle(Obstacle o) {
        return distanceFromObstacle(this, o) < radius && distanceVector.x > (o.c1.x < o.c2.x ? o.c1.x :o.c2.x) - radius && distanceVector.x < (o.c1.x > o.c2.x ? o.c1.x : o.c2.x) + radius
                && distanceVector.y > (o.c1.y < o.c2.y ? o.c1.y : o.c2.y) - radius && distanceVector.y < (o.c1.y > o.c2.y ? o.c1.y : o.c2.y) + radius;
    }

    static void separateBalls(Ball ball1, Ball ball2) {
        if(!ball1.isBallsOverlaps(ball2))
            return;

        float distance = Ball.distanceBetweenBalls(ball1, ball2);
        float overlap = (distance - ball1.radius - ball2.radius) * 0.5f;

        ball1.distanceVector.x += overlap * (ball2.distanceVector.x - ball1.distanceVector.x) / distance;
        ball1.distanceVector.y += overlap * (ball2.distanceVector.y - ball1.distanceVector.y) / distance;

        ball2.distanceVector.x += overlap * (ball1.distanceVector.x - ball2.distanceVector.x) / distance;
        ball2.distanceVector.y += overlap * (ball1.distanceVector.y - ball2.distanceVector.y) / distance;
    }

    static void separateFromObstacle(Ball b1, Obstacle obstacle, float dx) {
        if(obstacle.isHorizontalObstacle()) {
            if(obstacle.c1.y > b1.distanceVector.y)
                b1.distanceVector.y = obstacle.c1.y - b1.radius;
            else
                b1.distanceVector.y = obstacle.c1.y + b1.radius;
        } else {
            if(obstacle.c1.x > b1.distanceVector.x)
                b1.distanceVector.x = obstacle.c1.x - b1.radius;
            else
                b1.distanceVector.x = obstacle.c1.x + b1.radius;
        }
    }

    static void resolveCollisionBetweenBalls(Ball b1, Ball b2) {
        float distance = Ball.distanceBetweenBalls(b1, b2);

        float normalX = (b2.distanceVector.x - b1.distanceVector.x) / distance;
        float normalY = (b2.distanceVector.y - b1.distanceVector.y) / distance;

        float tangentX = -normalY;
        float tangentY = normalX;

        float tangentDotProduct1 = b1.speedVector.x * tangentX + b1.speedVector.y * tangentY;
        float tangentDotProduct2 = b2.speedVector.x * tangentX + b2.speedVector.y * tangentY;

        float normalDotProduct1 = b1.speedVector.x * normalX + b1.speedVector.y * normalY;
        float normalDotProduct2 = b2.speedVector.x * normalX + b2.speedVector.y * normalY;

        float momentum1 = normalDotProduct2;
        float momentum2 = normalDotProduct1;

        b1.speedVector.x = tangentX * tangentDotProduct1 + normalX * momentum1;
        b1.speedVector.y = tangentY * tangentDotProduct1 + normalY * momentum1;

        b2.speedVector.x = tangentX * tangentDotProduct2 + normalX * momentum2;
        b2.speedVector.y = tangentY * tangentDotProduct2 + normalY * momentum2;
    }

    void resolveCollisionWithObstacle(Obstacle obstacle) {
        if(obstacle.isHorizontalObstacle())
            speedVector.y = -speedVector.y;
        else
            speedVector.x = -speedVector.x;
    }

    public void setVelocity(float x, float y) {
        speedVector.x = x;
        speedVector.y = y;
    }

    public Vector getBallPosition() {
        return distanceVector;
    }

    public Vector getVelocity() {
        return speedVector;
    }

    public Vector getAccVector() {
        return accVector;
    }
}
