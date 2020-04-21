package com.example.jovan.pocketsoccerapp.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface GameDAO {

    class ResultClass {
        private String firstPlayerName;
        private String secondPlayerName;
        private int numWinsFirstPlayer;
        private int numWinsSecondPlayer;

        ResultClass(String firstPlayerName, String secondPlayerName, int numWinsFirstPlayer, int numWinsSecondPlayer) {
            this.firstPlayerName = firstPlayerName;
            this.secondPlayerName = secondPlayerName;
            this.numWinsFirstPlayer = numWinsFirstPlayer;
            this.numWinsSecondPlayer = numWinsSecondPlayer;
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

        public int getNumWinsFirstPlayer() {
            return numWinsFirstPlayer;
        }

        public void setNumWinsFirstPlayer(int numWinsFirstPlayer) {
            this.numWinsFirstPlayer = numWinsFirstPlayer;
        }

        public int getNumWinsSecondPlayer() {
            return numWinsSecondPlayer;
        }

        public void setNumWinsSecondPlayer(int numWinsSecondPlayer) {
            this.numWinsSecondPlayer = numWinsSecondPlayer;
        }
    }

    @Insert
    void insertGame(Game game);

    @Query("SELECT * FROM game_table WHERE firstPlayerName = :firstPlayerName AND secondPlayerName = :secondPlayerName")
    LiveData<List<Game>> getPlayersStats(String firstPlayerName, String secondPlayerName);

    @Query("SELECT * FROM game_table " +
            "WHERE id = (SELECT MAX(id) FROM game_table)")
    LiveData<List<Game>> getLastPlayedGame();

    @Query("SELECT firstPlayerName, secondPlayerName, SUM(firstPlayerWin) AS numWinsFirstPlayer, SUM(secondPlayerWin) AS numWinsSecondPlayer " +
            "FROM game_table " +
            "GROUP BY firstPlayerName, secondPlayerName")
    LiveData<List<ResultClass>> getAllGames();

    @Query("DELETE FROM game_table WHERE firstPlayerName = :firstPlayerName AND secondPlayerName = :secondPlayerName")
    void deleteRecord(String firstPlayerName, String secondPlayerName);

    @Query("DELETE FROM game_table")
    void deleteAllRecords();
}
