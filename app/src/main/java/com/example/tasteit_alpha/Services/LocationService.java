package com.example.tasteit_alpha.Services;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;

import androidx.annotation.Nullable;

import com.example.tasteit_alpha.Utils.AppUtils;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static com.example.tasteit_alpha.Utils.AppUtils.LOC_KEY;

public class LocationService extends IntentService {

    public LocationService() {
        super("LocationService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null || !intent.hasExtra(LOC_KEY)) return;
        LatLng latLng = intent.getParcelableExtra(LOC_KEY);
        if (latLng == null )return;
        Geocoder coder = new Geocoder(this,Locale.ENGLISH);
        try {
            List<Address> addressList = coder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addressList.size() == 0)return;
            Address address = addressList.get(0);
            //action is the name of the broadcast-> it's the identifier.
            Intent result = new Intent(AppUtils.ACTION_GET_ADDRESS);
            result.putExtra(AppUtils.ADDRESS_EXTRA, address.getAddressLine(0));
            result.putExtra(AppUtils.CITY_EXTRA,address.getLocality());
            result.putExtra(AppUtils.LAT_EXTRA,latLng.latitude);
            result.putExtra(AppUtils.LONG_EXTRA,latLng.longitude);
            sendBroadcast(result);//context.


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
