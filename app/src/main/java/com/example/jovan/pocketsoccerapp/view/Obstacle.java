package com.example.jovan.pocketsoccerapp.view;

import java.io.Serializable;

class Obstacle implements Serializable {

    static class Coef {
        float x;
        float y;

        Coef(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    Coef c1, c2;

    float factorA, factorB, factorC;

    Obstacle(float x1, float y1, float x2, float y2) {
        this.c1 = new Coef(x1, y1);
        this.c2 = new Coef(x2, y2);

        this.factorA = y1 - y2;
        this.factorB = x2 - x1;
        this.factorC = y1*(x1 - x2)  + x1 * (y2 - y1);
    }

    boolean isHorizontalObstacle() {
        return c1.y == c2.y;
    }
}
