package com.example.tasteit_alpha.DataBase.Room;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.example.tasteit_alpha.Model.Data.DataClasses.ImageDatum;
//this dataBase will serve for user to see his own pictures and the pictured he liked
@Database(entities = {ImageDatum.class}, version = 1, exportSchema = false)
public abstract class ImagesRoomDataBase extends RoomDatabase {
    public abstract ImageDao getImageDao();
}
