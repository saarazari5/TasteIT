package com.example.tasteit_alpha.DataBase;

import com.example.tasteit_alpha.DataBase.UploadStrategies.DbStrategy;
import com.example.tasteit_alpha.Model.Data.DataClasses.UpdateAble;

public class UploadManager {
    private DbStrategy dbStrategy;
    public UploadManager(DbStrategy dbStrategy) {
        this.dbStrategy = dbStrategy;
    }

    public void uploadToDb(UpdateAble updateAble) {
        if(dbStrategy ==null)return;
        dbStrategy.uploadToDb(updateAble);
    }


    public void setDbStrategy(DbStrategy dbStrategy) {
        if(dbStrategy ==null)return;
        this.dbStrategy = dbStrategy;
    }


}
