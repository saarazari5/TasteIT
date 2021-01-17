package com.example.tasteit_alpha.DataBase.UploadStrategies;

import com.example.tasteit_alpha.Model.Data.DataClasses.ImageDatum;
import com.example.tasteit_alpha.Model.Data.DataClasses.UpdateAble;
import com.example.tasteit_alpha.Model.Data.DataClasses.UserLikes;
import com.example.tasteit_alpha.Utils.AppUtils;
import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


public class FireBasefireStoreStrategy implements DbStrategy {
    private final String path;
    private final FirebaseFirestore db ;
    public FireBasefireStoreStrategy(String path) {
        db=FirebaseFirestore.getInstance();
        this.path=path;
    }


    @Override
    public <T extends UpdateAble> void  uploadToDb(T t) {
        ImageDatum img= (ImageDatum) t;
        CollectionReference media_data = db.collection(path);
        img.setGeohash(GeoFireUtils.getGeoHashForLocation(new GeoLocation(img.getImageLat(),img.getImageLong())));
        DocumentReference documentReference=media_data.document((img).getFilePath());
        documentReference.set(img).addOnSuccessListener(aVoid -> {
            documentReference.collection(AppUtils.USER_LIKES_PATH).document(AppUtils.LIKES_DOC).set(new UserLikes());
        });

    }

}
