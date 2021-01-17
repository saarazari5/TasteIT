@file:Suppress("NAME_SHADOWING")

package com.example.tasteit_alpha.Model


import android.content.Context
import com.example.tasteit_alpha.DataBase.Room.ImageRoomRepo
import com.example.tasteit_alpha.Model.Data.Callbacks.ImagesCallback
import com.example.tasteit_alpha.Model.Data.Callbacks.LikedCallback
import com.example.tasteit_alpha.Model.Data.Callbacks.UriDownloadedCallback
import com.example.tasteit_alpha.Model.Data.DataClasses.ImageDatum
import com.example.tasteit_alpha.Model.Data.DataClasses.PresentAble
import com.example.tasteit_alpha.Model.Data.DataClasses.Retrofit.PlacesDataSource
import com.example.tasteit_alpha.Model.Data.DataClasses.Retrofit.PlacesPOJO.Photo
import com.example.tasteit_alpha.Model.Data.DataClasses.Retrofit.PlacesPOJO.PlaceDetails
import com.example.tasteit_alpha.Model.Data.DataClasses.Retrofit.PlacesPOJO.PlaceSearch
import com.example.tasteit_alpha.Utils.AppUtils.*
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await





object DataRepository {
//since all Firebase API is Async we don't need to use any handler/coroutine/executors
// for the record: i used a callback in this case to show my understanding of the design Pattern

    val listMock= listOf("https://upload.wikimedia.org/wikipedia/commons/thumb/0/0b/RedDot_Burger.jpg/1200px-RedDot_Burger.jpg",
        "https://media.reshet.tv/image/upload/t_main_image_article_mobile,f_auto,q_auto/v1490721987/9736_1459263201_1459263202-2_psfonp.jpg",
        "https://www.thespruceeats.com/thmb/uacAFXnScHsefqv2KvSUZ9mHbWQ=/1885x1414/smart/filters:no_upscale()/Fresh-sashimi-GettyImages-86057409-58a0f05e3df78c475826b7de.jpg",
        "https://www.thespruceeats.com/thmb/_9P_SMHL4nuwqkt7bRK4RlYBNNc=/2001x1126/smart/filters:no_upscale()/454629837-58a4b2de5f9b58819cf851f5.jpg",
        "https://www.carolinescooking.com/wp-content/uploads/2019/03/eggs-Benedict-photo.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/thumb/0/0b/RedDot_Burger.jpg/1200px-RedDot_Burger.jpg",
        "https://media.reshet.tv/image/upload/t_main_image_article_mobile,f_auto,q_auto/v1490721987/9736_1459263201_1459263202-2_psfonp.jpg",
        "https://www.thespruceeats.com/thmb/uacAFXnScHsefqv2KvSUZ9mHbWQ=/1885x1414/smart/filters:no_upscale()/Fresh-sashimi-GettyImages-86057409-58a0f05e3df78c475826b7de.jpg",
        "https://www.thespruceeats.com/thmb/_9P_SMHL4nuwqkt7bRK4RlYBNNc=/2001x1126/smart/filters:no_upscale()/454629837-58a4b2de5f9b58819cf851f5.jpg",
        "https://www.carolinescooking.com/wp-content/uploads/2019/03/eggs-Benedict-photo.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/thumb/0/0b/RedDot_Burger.jpg/1200px-RedDot_Burger.jpg",
        "https://media.reshet.tv/image/upload/t_main_image_article_mobile,f_auto,q_auto/v1490721987/9736_1459263201_1459263202-2_psfonp.jpg",
        "https://www.thespruceeats.com/thmb/uacAFXnScHsefqv2KvSUZ9mHbWQ=/1885x1414/smart/filters:no_upscale()/Fresh-sashimi-GettyImages-86057409-58a0f05e3df78c475826b7de.jpg",
        "https://www.carolinescooking.com/wp-content/uploads/2019/03/eggs-Benedict-photo.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/thumb/0/0b/RedDot_Burger.jpg/1200px-RedDot_Burger.jpg",
        "https://www.thespruceeats.com/thmb/uacAFXnScHsefqv2KvSUZ9mHbWQ=/1885x1414/smart/filters:no_upscale()/Fresh-sashimi-GettyImages-86057409-58a0f05e3df78c475826b7de.jpg"
    )


    @Suppress("NON_EXHAUSTIVE_WHEN")
     fun  fetchDataByLocality(locality: String, callback: ImagesCallback){
        if(locality=="")return
        val imageData= ArrayList<ImageDatum>()
        val db = FirebaseFirestore.getInstance()
        val mediaData = db.collection(IMAGES_COLLECTION_PATH)
        val query=mediaData.whereEqualTo(LOCALITY, locality)
        query.get()
            .addOnSuccessListener {
                imageData.addAll(it.toObjects(ImageDatum::class.java))
                callback.onResponse(imageData, null)
                /**
                if(it.isEmpty) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val placesDetails = fetchPlaceDetailsByText(locality , "" , ArrayList())
                        if(placesDetails.isNullOrEmpty()){
                            callback.onResponse(null , null)
                        }else{
                           val photosList = ArrayList<Photo>()
                            convertDetailsToPhotos(placesDetails , photosList)
                            callback.onResponse(photosList , null)
                        }
                    }
                    return@addOnSuccessListener
                }
                **/

            }.addOnFailureListener{
                callback.onResponse(null, it)
            }
    }



    fun fetchDataByGeoQuery(distance: Int, lat: Double, long: Double, callback: ImagesCallback) {
        val db = FirebaseFirestore.getInstance()
        val fixedDistance : Double = if(distance==0) 1.0 else distance.toDouble()
        val center = GeoLocation(lat, long)
        val radiusInM  = fixedDistance * 1000
        val bounds = GeoFireUtils.getGeoHashQueryBounds(center, radiusInM)
        val tasks:MutableList<Task<QuerySnapshot>> = ArrayList()
        for (bound in bounds) {
            val q: Query = db.collection(IMAGES_COLLECTION_PATH)
                .orderBy(GEO_HASH)
                .startAt(bound.startHash)
                .endAt(bound.endHash)
                tasks.add(q.get())
        }

        Tasks.whenAllComplete(tasks).addOnSuccessListener {
            val matchingDocs : MutableList<ImageDatum> = ArrayList()
            tasks.forEach {
                val snap = it.result
                if (snap != null) {
                    for (document in snap.documents) {
                        if(document==null) continue
                        val lat: Double = document.getDouble("imageLat")!!
                        val lng: Double = document.getDouble("imageLong")!!
                        val docLocation = GeoLocation(lat, lng)
                        val distanceInM = GeoFireUtils.getDistanceBetween(docLocation, center)
                        if (distanceInM <= radiusInM) {
                            matchingDocs.add(document.toObject(ImageDatum::class.java)!!)
                        }
                    }
                }
            }
            if(matchingDocs.isNullOrEmpty()){
                //todo: fetch data from places api
                return@addOnSuccessListener
            }
          callback.onResponse(matchingDocs , null)
        }.addOnFailureListener { callback.onResponse(null,it) }

    }


    fun isUserLiked(userID: String, imagePath: String, likedCallback: LikedCallback){
    val db = FirebaseFirestore.getInstance()
         db.collection(IMAGES_COLLECTION_PATH).document(imagePath)
        .collection(USER_LIKES_PATH).whereArrayContains(USERS_LIKES_ID, userID)
             .get()
             .addOnSuccessListener {
                if (it.documents.isEmpty()){
                    likedCallback.updateUserLike(false)
                }
                 else {
                    likedCallback.updateUserLike(true)
                }
             }

    }


    fun addLike(userID: String, image: ImageDatum, context: Context){
        val db = FirebaseFirestore.getInstance()
        val imgDoc=db.collection(IMAGES_COLLECTION_PATH).document(image.filePath)
        val imageLikesDocRef = imgDoc
            .collection(USER_LIKES_PATH).document(LIKES_DOC)
        imageLikesDocRef.update(USERS_LIKES_ID, FieldValue.arrayUnion(userID)).addOnSuccessListener {
            imgDoc.update(LIKES_FIELD, FieldValue.increment(+1))
            ImageRoomRepo.getInstance(context).uploadToDb<ImageDatum>(image)

        }.addOnFailureListener {
            //todo: handle exceptions with MutableLiveData so we can toast to the user he have no internet connection
        }

    }
    fun removeLike(
        userID: String,
        image: ImageDatum,
        context: Context
    ){
        val db = FirebaseFirestore.getInstance()
        val imgDoc=db.collection(IMAGES_COLLECTION_PATH).document(image.filePath)
        val imageLikesDocRef = imgDoc.collection(USER_LIKES_PATH).document(LIKES_DOC)
        imageLikesDocRef.update(USERS_LIKES_ID, FieldValue.arrayRemove(userID)).addOnSuccessListener {
            imgDoc.update(LIKES_FIELD, FieldValue.increment(-1))
            ImageRoomRepo.getInstance(context).removeFromDb(image)
        }.addOnFailureListener {
            //todo: handle exceptions with MutableLiveData so we can toast to the user he have no internet connection
        }
    }


    fun updateImageUri(imagePath: String, uriString: String){
        FirebaseFirestore.getInstance().collection(IMAGES_COLLECTION_PATH).document(
            imagePath
        )
            .update("uriString", uriString)
    }

    private fun updateImageHeight(imagePath: String, height: Int){
        FirebaseFirestore.getInstance().collection(IMAGES_COLLECTION_PATH).document(
            imagePath
        )
            .update("imageHeight", height)

    }
    private fun updateImageWidth(imagePath: String, width: Int){
        FirebaseFirestore.getInstance().collection(IMAGES_COLLECTION_PATH).document(
            imagePath
        )
            .update("imageWidth", width)
    }
    fun updateImageScale(imagePath: String, width: Int, height: Int){
        updateImageWidth(imagePath, width)
        updateImageHeight(imagePath, height)
    }

    fun insertImageFromStorage(filePath: String, callback: UriDownloadedCallback) {
        val storageRef = FirebaseStorage.getInstance().reference
        val pathReference = storageRef.child(PHOTO_CHILD_PATH + filePath)
         pathReference.downloadUrl.addOnSuccessListener { callback.onUriDownloaded(it)}
    }

   suspend fun fetchDetails(address: String): PlaceDetails? {
        if(address=="")return null
        val response=PlacesDataSource.getPlaceSearch(address)
        if(response.isSuccessful&&response.body()!=null){
            val data= response.body() ?: return null
            if (data.placeSearches?.size ==0)return null
            data.placeSearches[0].placeId?.let {
                return fetchDetailsFromID(it)
            }
        }
        return null
    }

     private suspend fun fetchDetailsFromID(id: String): PlaceDetails? {
        if(id=="")return null
         val response=PlacesDataSource.getPlaceDetails(id)
         if(response.isSuccessful&&response.body()!=null){
                response.body()?.let {
                 return it.result
             }
         }
         return null
     }

    private suspend fun fetchPlaceDetailsByText(
        query: String,
        nextPageToken: String,
        dataList: ArrayList<PlaceDetails?>
    ):ArrayList<PlaceDetails?>?{
        if(query.isEmpty()&&nextPageToken.isEmpty())return null
        val response = PlacesDataSource.getPlacesByText(nextPageToken, query)
        val data=response.body()
        if(data==null||data.placeSearches.isNullOrEmpty())return null
        convertPlaceSearchToPlaceDetails(data.placeSearches).let { dataList.addAll(it) }
        //if(!data.nextPageToken.isNullOrEmpty()){
           //fetchPlaceDetailsByText("", nextPageToken, dataList)?.let { dataList.addAll(it) }
         //}
        return dataList
     }

    private suspend fun convertPlaceSearchToPlaceDetails(placeSearches: List<PlaceSearch>): ArrayList<PlaceDetails?> {
        val resList : ArrayList<PlaceDetails?> = ArrayList()
        for (placeSearch in placeSearches) {
            resList.add(fetchDetailsFromID(placeSearch.placeId))
        }
        return resList
    }

    /***
     * this method send a query to places api and use the list result of placeDetails to send the address query to fire base
     * and check if any photos related to the address are in there
     * the method also filter any null placeDetails objects and return a not null list of presentable either instance of image datum
     * or instance of google api photo which both implement the presentAble interface
     */


    suspend fun fetchPhotosFromQuery(query: String) : List<PresentAble>?{
        val dataList = ArrayList<PresentAble>()
        val photosList = ArrayList<Photo>()
        val response= fetchPlaceDetailsByText(query, "", ArrayList()) ?: return null
        val db = FirebaseFirestore.getInstance()
            for (res in response) {
                if (res==null) continue
                if(!res.photos.isNullOrEmpty())
                    res.photos.forEach {
                        it.relatedPlaceDetails = res
                        photosList.add(it)
                    }
            try {
                val documentData = db.collection(IMAGES_COLLECTION_PATH).whereEqualTo(
                    ADDRESS,
                    res.formattedAddress
                ).get().await()
                if (documentData.isEmpty) continue
                 dataList.addAll(documentData.toObjects(ImageDatum::class.java))
            }catch (e: Exception){
                continue
            }
        }
        if (dataList.isEmpty())
            dataList.addAll(photosList)

        return  dataList
    }

  suspend  fun fetchLikedImages() : List<String> {
        //todo: fetch data from room

      return listMock.shuffled()
    }

    suspend  fun fetchMyPhotos() : List<String> {
        //todo: fetch data from room

        return listMock.shuffled()
    }
}





