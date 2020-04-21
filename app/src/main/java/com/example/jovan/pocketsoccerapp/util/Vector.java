package com.example.jovan.pocketsoccerapp.util;

public class Vector {

    public float x;
    public float y;

    public Vector(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void resetValues() {
        this.x = 0;
        this.y = 0;
    }
}
