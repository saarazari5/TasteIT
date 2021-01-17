package com.example.tasteit_alpha.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;

import androidx.fragment.app.Fragment;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;

import io.nlopez.smartlocation.SmartLocation;

import static io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesProvider.REQUEST_CHECK_SETTINGS;


public class AppUtils {
    public static String LINK_TAG = "link";
    public static String DISTANCE_TAG = "distance";
    public static String STATE_TAG = "state";
    public static final int RC_LOCATION = 200;
    public static final double DEFAULT_LANG_LAT = 0.0;
    public static final String LOC_KEY  = "loc";
    public static final String API_KEY="AIzaSyCKfZMIhmy-r6qvO-a1r89JM_o0YGVcPjg";
    public static final String IMAGE_ARGS="image_data";
    public static final String PHOTO_ARGS= "photo_args";
    public static final String LIST_STATE = "list_state_key";
    public static final String ACTION_GET_ADDRESS="get_address";
    public static final String PHOTO_CHILD_PATH="media/images/";
    public static final String ADDRESS_EXTRA="address";
    public static final String LAT_EXTRA="lat";
    public static final String LONG_EXTRA="long";
    public static final String CITY_EXTRA="locality";
    public static final String LOCALITY ="locality";
    public static final String GEO_HASH= "geohash";
    public static final String ADDRESS= "address";
    public static final int DEF_IMAGE_HW = 400;
    public static final String IMAGES_COLLECTION_PATH="media_data";
    public static final String USER_LIKES_PATH="userLikes";
    public static final String USERS_LIKES_ID="usersLikesID";
    public static final String LIKES_FIELD="likes";
    public static final String LIKES_DOC="Likes";
    public static final String ACTION_SEND_QUERY="send_query";
    public static final String SEARCH_QUERY="query";
    public static final String BASE_PHOTO_URL = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=800&photoreference=";



    public static float getDistanceBetweenTwoPoints(double lat1, double lon1, double lat2, double lon2) {
        float[] distance = new float[2];
        Location.distanceBetween( lat1, lon1,
                lat2, lon2, distance);
        return distance[0];
    }

    public static boolean isCameraAvailable(Context context){
        PackageManager pm = context.getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    //compare sdk with user sdk if sdk version larger than wanted version user may do some actions
    public static boolean compareSDK(int version){
        return (Build.VERSION.SDK_INT>=version);
    }

    public static void showLocationDialog(Fragment fragment){

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(createTempLocationRequest());
        SettingsClient client = LocationServices.getSettingsClient(fragment.requireActivity());
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnFailureListener(fragment.requireActivity(), e -> {
            if(fragment==null)return;
            if (e instanceof ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    fragment.startIntentSenderForResult
                            (resolvable.getResolution().getIntentSender() , REQUEST_CHECK_SETTINGS,
                                    null,0,0,0,null);
                } catch (IntentSender.SendIntentException sendEx) {
                    // Ignore the error.
                }
            }
        });

    }
    public static void showLocationDialog(Activity activity){
        if(activity==null)return;
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(createTempLocationRequest());
        SettingsClient client = LocationServices.getSettingsClient(activity);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnFailureListener(activity, e -> {
            if(activity==null)return;
            if (e instanceof ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    resolvable.startResolutionForResult(activity,REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException sendEx) {
                    // Ignore the error.
                }
            }
        });

    }
    private static LocationRequest createTempLocationRequest(){
        return LocationRequest.create()
                .setInterval(10000)
                .setFastestInterval(5000)
                .setPriority(LocationRequest.PRIORITY_LOW_POWER);
    }


    public static boolean checkLocSetting(Context context) {
        return SmartLocation.with(context).location().state().locationServicesEnabled();
    }

    public static CharSequence setLikesText(int likes) {
        if(likes>=1_000_000){
            return likes / 1_000_000 +"m";
        }else if(likes>=100_000){
            return likes/100+"k";
        }
        return String.valueOf(likes);
    }
}
