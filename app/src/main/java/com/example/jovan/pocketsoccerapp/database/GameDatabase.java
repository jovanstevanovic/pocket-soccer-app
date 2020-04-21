package com.example.jovan.pocketsoccerapp.database;

import android.app.Application;
import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

@Database(entities = {Game.class}, version = 1, exportSchema = false)
abstract class GameDatabase extends RoomDatabase {

    abstract GameDAO gameDAO();

    private static GameDatabase instance;

    synchronized static GameDatabase getInstance(Application application) {
        if(instance == null) {
            instance = Room.databaseBuilder(application.getApplicationContext(), GameDatabase.class, "game_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(callback)
                    .build();
        }
        return instance;
    }

    private static RoomDatabase.Callback callback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(instance.gameDAO()).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {

        private GameDAO gameDAO;

        public PopulateDbAsyncTask(GameDAO gameDAO) {
            this.gameDAO = gameDAO;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Game game1 = new Game("Jovan", "Milan", 1, 0, "16:21");
            Game game2 = new Game("Jovan", "Djole", 0, 1, "17:21");
            Game game3 = new Game("Jovan", "Milan", 0, 1, "18:21");
            Game game4 = new Game("Jovan", "Robot_1", 1, 0, "19:21");
            Game game5 = new Game("Robot_1", "Robot_2", 1, 0, "20:21");
            Game game6 = new Game("Robot_1", "Robot_2", 1, 0, "21:21");

            gameDAO.insertGame(game1);
            gameDAO.insertGame(game2);
            gameDAO.insertGame(game3);
            gameDAO.insertGame(game4);
            gameDAO.insertGame(game5);
            gameDAO.insertGame(game6);

            return null;
        }
    }

}
