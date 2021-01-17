package com.example.tasteit_alpha.DataBase.UploadStrategies;


import com.example.tasteit_alpha.Model.Data.DataClasses.UpdateAble;

public interface DbStrategy {
    <T extends UpdateAble> void uploadToDb(T t);
}
