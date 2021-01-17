package com.example.tasteit_alpha.DataBase.Room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.tasteit_alpha.Model.Data.DataClasses.ImageDatum;

import java.util.List;

@Dao
interface ImageDao {
    @Query("SELECT * FROM images")
     List<ImageDatum> getAll();

    @Query("SELECT * FROM images Where user_id NOT LIKE :userID ")
    List<ImageDatum>getLikedImages(String userID);

    @Query("SELECT * FROM images Where user_id  LIKE :userID ")
    List<ImageDatum>getMyImages(String userID);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     void insertAll(ImageDatum... images);

    @Delete
    void delete(ImageDatum image);
}
