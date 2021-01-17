package com.example.tasteit_alpha.Model.Data.DataClasses

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.example.tasteit_alpha.Model.Data.DataClasses.Retrofit.PlacesPOJO.PlaceDetails
import com.example.tasteit_alpha.Utils.AppUtils.*

@Entity(tableName = "images")
data class ImageDatum(
    @PrimaryKey var filePath: String="",
    @ColumnInfo(name = "user_id") var userID:String="",
    @ColumnInfo(name = "likes")  var likes: Int = 0,
    @ColumnInfo(name="longitude") var imageLong: Double = DEFAULT_LANG_LAT,
    @ColumnInfo(name="latitude") var imageLat: Double= DEFAULT_LANG_LAT,
    @ColumnInfo(name = "address") var address: String="",
    @ColumnInfo (name = "locality") var locality:String="",
    @ColumnInfo (name = "uri")  var uriString:String = "",
    @ColumnInfo (name = "image_height")  var imageHeight : Int = DEF_IMAGE_HW,
    @ColumnInfo (name = "image_width")  var imageWidth : Int = DEF_IMAGE_HW,
    @Ignore var geohash:String = "",
    @Ignore var relatedPlaceDetails: PlaceDetails? = null
): UpdateAble,Parcelable,PresentAble {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readParcelable(PlaceDetails::class.java.classLoader)
    )


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(filePath)
        parcel.writeString(userID)
        parcel.writeInt(likes)
        parcel.writeDouble(imageLong)
        parcel.writeDouble(imageLat)
        parcel.writeString(address)
        parcel.writeString(locality)
        parcel.writeString(uriString)
        parcel.writeInt(imageHeight)
        parcel.writeInt(imageWidth)
        parcel.writeString(geohash)
        parcel.writeParcelable(relatedPlaceDetails, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ImageDatum> {
        override fun createFromParcel(parcel: Parcel): ImageDatum {
            return ImageDatum(parcel)
        }

        override fun newArray(size: Int): Array<ImageDatum?> {
            return arrayOfNulls(size)
        }
    }

}
