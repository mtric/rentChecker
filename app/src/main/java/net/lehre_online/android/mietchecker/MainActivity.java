package net.lehre_online.android.mietchecker;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * This class represents the main activity of the RentChecker app, where the user
 * can search for apartments on a map.
 * After initialization the menus are created, a map fragment is added and
 * an sql connection established to load markers on the map. The user can
 * click on menu items, localize his position, click on marker to show
 * some real estate information and call the tenant with a long click on the marker.
 *
 * @author Michael Kaleve, Eric Walter
 * @version 1.2, 2019-07-07
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GoogleMap.OnMyLocationButtonClickListener, OnMapReadyCallback,
        GoogleMap.OnMyLocationClickListener, GoogleMap.OnMarkerClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback, SqlConnectorWithDelegate.AsyncResponse,
        GoogleMap.OnInfoWindowLongClickListener {

    /**
     * Debug variable. Set false to end debug mode.
     */
    private static final boolean DBG = true;
    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    @SuppressWarnings("JavadocReference")
    private static final int FINE_LOCATION_PERMISSION_REQUEST = 1;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final String TAG = ".MainActivity";
    private ArrayList m_objectList = new ArrayList();
    private static final int REQUEST_CODE_GET_PARAMETERS = 0;
    private static final int REQUEST_CODE_NEW_FLAT = 1;
    private double maxSquare = 100000;
    private double minSquare = 0;
    private double maxRent = 500000;
    private double minRent = 0;
    private String zipCode = "";

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    @SuppressWarnings("JavadocReference")
    private boolean mPermissionDenied = false;
    private GoogleMap mMap;

    /**
     * This method initializes the main activity
     *
     * @param savedInstanceState The Bundle to save & recover state information for the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(v -> {
                openEnter_Parameters();
            });
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        if (DBG) Log.d(TAG, "calling map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        //execute the async task to connect to database
        new SqlConnectorWithDelegate(this).execute();
    }

    /**
     * This method overrides the implemented method from AsyncResponse interface
     */
    @Override
    public void processFinish(ArrayList objectList) {
        //Here you will receive the result fired from async class
        //of onPostExecute(result) method
        m_objectList = objectList;
        setMarker(maxSquare, minSquare, maxRent, minRent, zipCode);
    }

    /**
     * This method handles when the user presses tha back button
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * This method specifies the options menu for the main activity
     * @param menu The menu
     * @return Returns true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * This method handles a click event on an item in the options menu
     * @param item The clicked menu item
     * @return Returns a superclass call of onOptionsItemSelected()
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_normal) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        } else if (id == R.id.action_terrain) {
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        } else if (id == R.id.action_hybrid) {
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        } else if (id == R.id.action_none) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
        } else if (id == R.id.action_satellite) {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        } else if (id == R.id.action_logout) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method handles a click event on a menu item in the navigation menu
     * @param item The clicked menu item
     * @return Returns true.
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.addFlat) {
            openAddFlat();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * This method opens new intent to add the users own object/marker to the map
     */
    private void openAddFlat() {
        // Launch Add_Flat activity
        Intent intent = new Intent(this, AddFlat.class);
        startActivityForResult(intent, REQUEST_CODE_NEW_FLAT);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (DBG) Log.i(TAG, "onMapReady() opened ...");
        mMap = googleMap;

        // disable maps toolbar at bottom right
        mMap.getUiSettings().setMapToolbarEnabled(false);

        setUpMap();
        if (DBG) Log.i(TAG, "onMapReady() done...");
    }

    /**
     * This is the callback method when the main activity receives results
     *
     * @param requestCode The transmitted request code
     * @param resultCode  Result code showing if the result is o.k. or cancelled
     * @param data        Intent object containing the data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (DBG) Log.i(TAG, "setUpMap() opened....");
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION}, requestCode);
        } else {
            // Android version < 6.0 or permission already granted
            setUpMap();
        }

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_GET_PARAMETERS && data != null) {

            // Get the Parameters and check, if parameters where set
            String zip_code = data.getStringExtra("zipCode");
            if (zip_code != null && !zip_code.trim().isEmpty()) {
                zipCode = zip_code;
                if (DBG) Log.i(TAG, "zip code from user input: " + zipCode);
            }
            String max_square = data.getStringExtra("max_square");
            if (max_square != null && !max_square.trim().isEmpty()) {
                maxSquare = Double.parseDouble(max_square);
            }
            String min_square = data.getStringExtra("min_square");
            if (min_square != null && !min_square.trim().isEmpty()) {
                minSquare = Double.parseDouble(min_square);
            }
            String max_rent = data.getStringExtra("max_rent");
            if (max_rent != null && !max_rent.trim().isEmpty()) {
                maxRent = Double.parseDouble(max_rent);
            }
            String min_rent = data.getStringExtra("min_rent");
            if (min_rent != null && !min_rent.trim().isEmpty()) {
                minRent = Double.parseDouble(min_rent);
            }
            // Relaunch map with new Parameters
            if (DBG) Log.i(TAG, "Daten = " + maxSquare);
            setMarker(maxSquare, minSquare, maxRent, minRent, zipCode);
            zipCode = "";
            maxSquare = 100000;
            minSquare = 0;
            maxRent = 500000;
            minRent = 0;
        } else if (requestCode == REQUEST_CODE_NEW_FLAT && resultCode == Activity.RESULT_OK && data != null) {
            new SqlConnectorWithDelegate(this).execute();
            String zip_code = data.getStringExtra("zipCode");
            String city = data.getStringExtra("city");
            LatLng newCoordinates = GeoCoderHelper
                    .getLocationFromAddress(this, zip_code + " " + city);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newCoordinates, 14));

        } else {
            if (DBG) Log.i(TAG, "Enter_Parameters cancelled");
        }
    }

    /**
     * This method defines the map properties and sets the position to the current location
     */
    public void setUpMap() {
        final String MNAME = "setUpMap()";
        // sets the map type
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        // checks permission to enable location service
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    FINE_LOCATION_PERMISSION_REQUEST);
            return;
        }

        mMap.setMyLocationEnabled(true);

        // Sets map to current location with zoom level 16
        Location currentLocation = getMyLocation();
        if (currentLocation != null) {
            if (DBG) Log.i("location", MNAME + "...got currentLocation");
            LatLng currentCoordinates = new LatLng(
                    currentLocation.getLatitude(),
                    currentLocation.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentCoordinates, 16));
        }

        // Verkehr
        mMap.setTrafficEnabled(false);
        // Innenräume
        mMap.setIndoorEnabled(false);
        // Gebäude
        mMap.setBuildingsEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        if (DBG) Log.d(TAG, "setUpMap() got map ready...");
    }

    /**
     * This method sets markers which are pulled from the database
     * m_objectList Contains an ArrayList with all the objects
     *
     * @param maSquare Maximum square meters
     * @param miSquare Minimum square meters
     * @param maRent   Maximum rent
     * @param miRent   Minimum rent
     * @param zipCode  Zip code of an area
     */
    private void setMarker(Double maSquare, Double miSquare, Double maRent, Double miRent,
                           String zipCode) {
        mMap.clear();
        //mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowLongClickListener(this);
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        ArrayList<String> zipList = new ArrayList<>();
        for (int i = 0; i < m_objectList.size(); i = i + 11) {
            Double lat = (Double) m_objectList.get(i + 1);
            Double lng = (Double) m_objectList.get(i + 2);
            String squareMeters = Double.toString((Double) m_objectList.get(i + 3));// + ' qm\n' + Double.toString((Double) objectList.get(i+9)) + ' Euro\nFrei ab: ' + ((Date) objectList.get(i+10));
            String monthlyRent = Double.toString((Double) m_objectList.get(i + 8));
            String freeFrom = formatter.format((Date) m_objectList.get(i + 9));
            String telNumber = (String) m_objectList.get(i + 10);
            LatLng marker = new LatLng(lat, lng);
            // transform marker with GeoCode to zip code
            String zipCodeGeo = GeoCoderHelper.getZipFromLocation(marker, this);

            if (DBG) Log.i(TAG, "transmitted zip code: " + zipCodeGeo);

            if (zipCodeGeo == null || zipCode.trim().isEmpty()) {
                // Sets all the markers which match the parameters
                if (maRent >= Double.parseDouble(monthlyRent)
                        && miRent <= Double.parseDouble(monthlyRent)
                        && maSquare >= Double.parseDouble(squareMeters)
                        && miSquare <= Double.parseDouble(squareMeters)) {
                    if (DBG) Log.i(TAG, "sqm: " + squareMeters);
                    mMap.addMarker(new MarkerOptions().position(marker)
                            .title(squareMeters + " qm, " + monthlyRent + " Euro, frei ab " + freeFrom)
                            .snippet(telNumber));
                }
            } else {
                if (maRent >= Double.parseDouble(monthlyRent)
                        && miRent <= Double.parseDouble(monthlyRent)
                        && maSquare >= Double.parseDouble(squareMeters)
                        && miSquare <= Double.parseDouble(squareMeters)
                        && zipCode.trim().substring(0, 5).equals(zipCodeGeo)) {
                    if (DBG) Log.i(TAG, "sqm: " + squareMeters);
                    mMap.addMarker(new MarkerOptions().position(marker)
                            .title(squareMeters + " qm, " + monthlyRent + " Euro, frei ab " + freeFrom)
                            .snippet(telNumber));
                }
            }
        }
        /*
        if (zipCode != 0 && !zipList.contains(zipCode.toString())){
            Toast.makeText(this, "Kein Angebot unter dieser PLZ", Toast.LENGTH_LONG).show();
        }
        */
        if (!zipCode.isEmpty()) {
            LatLng newCoordinates = GeoCoderHelper
                    .getLocationFromAddress(this, zipCode);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newCoordinates, 14));
        }
    }

    /**
     * A method to get the user location
     *
     * @return returns the users location
     */
    private Location getMyLocation() {
        final String MNAME = "getMyLocation()";
        if (DBG) Log.i(TAG, MNAME + "...entering");
        Location myLocation;
        myLocation = null;
        // Get location from GPS if it's available
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (DBG) Log.i(TAG, MNAME + "...access for location granted");
            myLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            // Location wasn't found, check the next most accurate place for the current location
            if (myLocation == null) {
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                // Finds a provider that matches the criteria
                String provider = lm.getBestProvider(criteria, true);
                // Use the provider to get the last known location
                myLocation = lm.getLastKnownLocation(provider);
            }
        }
        if (DBG) Log.i(TAG, MNAME + "...done");
        return myLocation;
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    /**
     * This method handles the click event on the location button
     * @return Returns false.
     */
    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    /**
     * This method shows a toast message, when the user clicks on the actual location
     * @param location The location.
     */
    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    /**
     * This method launches an activity to define the search parameters
     */
    public void openEnter_Parameters() {
        // Launch Enter_Parameters activity
        Intent intent = new Intent(this, Enter_Parameters.class);
        startActivityForResult(intent, REQUEST_CODE_GET_PARAMETERS);
    }

    /**
     * The method handles a marker click event
     * @param marker The clicked marker
     * @return Returns true
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
        Toast.makeText(this, marker.getTitle(), Toast.LENGTH_LONG).show();
        if (DBG) Log.i(TAG, "Marker clicked");

        return true;
    }

    /**
     * This method opens a new intent to call somebody with the action dial
     *
     * @param marker The marker object that got clicked
     */
    public void onClickCall(Marker marker) {
        String sTel = marker.getSnippet();
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + sTel)));
    }

    /**
     * This method opens onClickCall() when the info window is clicked for a long time
     *
     * @param marker The marker object that got clicked
     */
    @Override
    public void onInfoWindowLongClick(Marker marker) {
        onClickCall(marker);
    }
}
