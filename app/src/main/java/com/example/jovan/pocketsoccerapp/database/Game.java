package com.example.jovan.pocketsoccerapp.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "game_table")
public class Game {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String firstPlayerName;
    private String secondPlayerName;

    private int firstPlayerWin;
    private int secondPlayerWin;

    private String gameTimeFinished;

    public Game(String firstPlayerName, String secondPlayerName, int firstPlayerWin, int secondPlayerWin, String gameTimeFinished) {
        this.firstPlayerName = firstPlayerName;
        this.secondPlayerName = secondPlayerName;
        this.firstPlayerWin = firstPlayerWin;
        this.secondPlayerWin = secondPlayerWin;
        this.gameTimeFinished = gameTimeFinished;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstPlayerName() {
        return firstPlayerName;
    }

    public void setFirstPlayerName(String firstPlayerName) {
        this.firstPlayerName = firstPlayerName;
    }

    public String getSecondPlayerName() {
        return secondPlayerName;
    }

    public void setSecondPlayerName(String secondPlayerName) {
        this.secondPlayerName = secondPlayerName;
    }

    public int getFirstPlayerWin() {
        return firstPlayerWin;
    }

    public void setFirstPlayerWin(int firstPlayerWin) {
        this.firstPlayerWin = firstPlayerWin;
    }

    public int getSecondPlayerWin() {
        return secondPlayerWin;
    }

    public void setSecondPlayerWin(int secondPlayerWin) {
        this.secondPlayerWin = secondPlayerWin;
    }

    public String getGameTimeFinished() {
        return gameTimeFinished;
    }

    public void setGameTimeFinished(String gameTimeFinished) {
        this.gameTimeFinished = gameTimeFinished;
    }
}
