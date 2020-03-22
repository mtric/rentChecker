package net.lehre_online.android.mietchecker;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static androidx.constraintlayout.widget.Constraints.TAG;

/**
 * This class represents a helper to transform gps/address data with the Google GeoCoder API
 */
class GeoCoderHelper {

    /**
     * Debug variable. Set false to end debug mode.
     */
    private static final boolean DBG = true;

    /**
     * This method transforms a LatLng coordinate into a zip code
     *
     * @param location Latitude and longitude parameters
     * @param context The context
     * @return Returns a string containing a zip code
     */
    static String getZipFromLocation(final LatLng location, final Context context) {

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        String result = null;
        try {
            List<Address> list = geocoder.getFromLocation(
                    location.latitude, location.longitude, 1);
            if (list != null && list.size() > 0) {
                Address address = list.get(0);
                // sending back first address line and locality
                result = address.getPostalCode(); // getAddressLine(0) + ", " + address.getLocality();
                if (DBG) Log.i(TAG, "Zip code from LatLng: " + result);
            }
        } catch (IOException e) {
            if (DBG) Log.e(TAG, "Impossible to connect to Geocoder", e);
        }
        return result;

    }

    /**
     * This method transforms an address string into LatLng coordinates.
     *
     * @param context  The context
     * @param sAddress An address string
     * @return Returns a LatLng object with latitude and longitude parameters
     */
    public static LatLng getLocationFromAddress(Context context, String sAddress) {

        Geocoder coder = new Geocoder(context, Locale.getDefault());
        List<Address> address;
        LatLng position = null;

        try {
            address = coder.getFromLocationName(sAddress, 5);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            position = new LatLng(location.getLatitude(), location.getLongitude());

        } catch (IOException ex) {

            if (DBG) Log.e(TAG, "Impossible to connect to Geocoder", ex);
        }

        return position;
    }
}