package com.example.tasteit_alpha.DataBase.UploadStrategies;

import android.net.Uri;

import com.example.tasteit_alpha.Model.Data.DataClasses.UpdateAble;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

public class FireBaseStorageStrategy implements DbStrategy {
    private final File dataFile;
    private final String path;

    public FireBaseStorageStrategy(File dataFile,String path) {
        this.dataFile =dataFile;
        this.path=path;
    }



    @Override
    public <T extends UpdateAble> void uploadToDb(T t) {
        StorageReference childRef = FirebaseStorage.getInstance().getReference().child(path);
        childRef.putFile(Uri.fromFile(dataFile));
    }

}
