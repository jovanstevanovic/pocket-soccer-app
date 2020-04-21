package com.example.jovan.pocketsoccerapp.database;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

public class GameViewModel extends AndroidViewModel {

    private Repository repository;

    public GameViewModel(@NonNull Application application) {
        super(application);
        repository = new Repository(application);
    }

    public void insertGame(Game game) {
        repository.insertGame(game);
    }

    public void deleteAllRecords() {
        repository.deleteAllRecords();
    }

    public void deleteRecords(String firstPlayerName, String secondPlayerName) {
        repository.deleteRecords(firstPlayerName, secondPlayerName);
    }

    public LiveData<List<Game>> getPlayersStats(String firstPlayerName, String secondPlayerName) {
        return repository.getPlayersStats(firstPlayerName, secondPlayerName);
    }

    public LiveData<List<Game>> getLastPlayedGame() {
        return repository.getLastPlayedGame();
    }

    public LiveData<List<GameDAO.ResultClass>> getAllGames() {
        return repository.getAllGames();
    }
}
