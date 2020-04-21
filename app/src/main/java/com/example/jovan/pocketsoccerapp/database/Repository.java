package com.example.jovan.pocketsoccerapp.database;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

class Repository {

    private static GameDatabase database;

    Repository(Application application) {
        database = GameDatabase.getInstance(application);
    }

    void insertGame(Game game) {
        new AsyncTaskInsertGame().execute(game);
    }

    LiveData<List<Game>> getPlayersStats(String firstPlayerName, String secondPlayerName) {
        return database.gameDAO().getPlayersStats(firstPlayerName, secondPlayerName);
    }

    LiveData<List<Game>> getLastPlayedGame() {
        return database.gameDAO().getLastPlayedGame();
    }

    LiveData<List<GameDAO.ResultClass>> getAllGames() {
        return database.gameDAO().getAllGames();
    }

    void deleteAllRecords() {
        new AsyncTaskDeleteAllGames().execute();
    }

    void deleteRecords(String firstPlayerName, String secondPlayerName) {
        new AsyncTaskDeleteGames(firstPlayerName, secondPlayerName).execute();
    }

    private static class AsyncTaskInsertGame extends AsyncTask<Game, Void, Void> {

        @Override
        protected Void doInBackground(Game... games) {
            database.gameDAO().insertGame(games[0]);
            return null;
        }
    }

    private static class AsyncTaskDeleteGames extends AsyncTask<Void, Void, Void> {

        private String firstPlayerName;
        private String secondPlayerName;

        AsyncTaskDeleteGames(String firstPlayerName, String secondPlayerName) {
            this.firstPlayerName = firstPlayerName;
            this.secondPlayerName = secondPlayerName;
        }

        @Override
        protected Void doInBackground(Void ... voids) {
            database.gameDAO().deleteRecord(firstPlayerName, secondPlayerName);
            return null;
        }
    }

    private static class AsyncTaskDeleteAllGames extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void ... voids) {
            database.gameDAO().deleteAllRecords();
            return null;
        }
    }
}
