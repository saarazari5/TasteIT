package com.example.tasteit_alpha.DataBase.Room;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.room.Room;

import com.example.tasteit_alpha.DataBase.UploadStrategies.DbStrategy;
import com.example.tasteit_alpha.Model.Data.DataClasses.ImageDatum;
import com.example.tasteit_alpha.Model.Data.DataClasses.UpdateAble;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ImageRoomRepo  implements DbStrategy {
    private ImagesRoomDataBase db;
    //used for DB Access:
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler uiThread = new Handler(Looper.getMainLooper());

    //constructor: init my properties
    private ImageRoomRepo(Context context) {
        //Room-> factory method (movies = sqlite fileName)
        db = Room.databaseBuilder(context, ImagesRoomDataBase.class, "images").build();
    }

    private static ImageRoomRepo sharedInstance;

    public synchronized static ImageRoomRepo getInstance(Context context) {
        if (sharedInstance == null) {
            sharedInstance = new ImageRoomRepo(context);
        }
        return sharedInstance;
    }


    @Override
    public <T extends UpdateAble> void uploadToDb(T t) {
        executor.execute(() -> db.getImageDao().insertAll((ImageDatum) t));
    }

    public  void removeFromDb(ImageDatum imageDatum){
        executor.execute(()->db.getImageDao().delete(imageDatum));
    }

}
