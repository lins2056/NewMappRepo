package com.example.lins2056.finalmapp;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.identity.intents.Address;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private boolean isGPSEnabled = false;
    private boolean isNetworkEnabled = false;
    private boolean canGetLocation = false;
    private static final long MIN_TIME_BTWN_UPDATES = 1000 * 15 * 1;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 5;
    private static final int MY_LOC_ZOOM_FACTOR = 20;
    private Location location;
    private Location myLocation;
    private LatLng userLocation;
    private String address;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ToggleButton toggle = (ToggleButton) findViewById(R.id.toggleButton);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    getLocation();
                }
                else{

                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        Log.d("MyMapsApp", "Failed permission 1");
                      }

                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        Log.d("MyMapsApp", "Failed permission 2");
                    }
                    locationManager.removeUpdates(locationListenerNetwork);
                    locationManager.removeUpdates(locationListenerGPS);
                }
            }
        });
        //GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(this).build();
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
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng indiana = new LatLng(40, -86);
        mMap.addMarker(new MarkerOptions().position(indiana).title("Born here"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(indiana));

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("MyMapsApp", "Failed permission 1");
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("MyMapsApp", "Failed permission 2");
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
        }

        //mMap.setMyLocationEnabled(true);
        //getLocation();

    }

    public void cleanthismess(View v){
        mMap.clear();
    }

    /*public void track(View v) {
        int taps = 0;
        if (taps % 2 == 1) {

            getLocation();
        } else {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d("MyMapsApp", "Failed permission 1");
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d("MyMapsApp", "Failed permission 2");
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
            }
            locationManager.removeUpdates(locationListenerNetwork);
            locationManager.removeUpdates(locationListenerGPS);
        }
        taps++;
    }*/




    public void getLocation() {
        try {

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d("MyMapsApp", "Failed permission 1");
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d("MyMapsApp", "Failed permission 2");
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
            }
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            //getGPSstatus
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (isGPSEnabled) Log.d("MyMapsApp", "getLocation: GPS is enabled");

            //getnetworkstatus
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (isNetworkEnabled) Log.d("MyMapsApp", "getLocation: NETWORK is enabled");

            if (!isGPSEnabled && !isNetworkEnabled) {
                Log.d("MyMapsApp", "getLocation: No provider is enabled!");
            } else {

                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    Log.d("MyMapsApp", " getLocation; Network enabled-requesting location updates");
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BTWN_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES,
                            locationListenerNetwork);
                    Log.d("MyMapsApp", "getLocation: NetworkLoc update request successful");
                    Toast.makeText(this, "Using Network", Toast.LENGTH_SHORT);

                }

                if (isGPSEnabled) {
                    Log.d("MyMapsApp", " getLocation; gps enabled-requesting location updates");
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
                        return;
                    }
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            MIN_TIME_BTWN_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES,
                            locationListenerGPS);
                    Log.d("MyMapsApp", "getLocation: GPSLoc update request successful");
                    Toast.makeText(this, "Using gps", Toast.LENGTH_SHORT);

                }
            }

        } catch (Exception e) {
            Log.d("MyMapsApp", "Caught exception in getLocation");
            e.printStackTrace();
        }
    }

    android.location.LocationListener locationListenerGPS = new android.location.LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            //output in Log.d and Toast that GPS is enabled and working
            Log.d("MyMapsApp", "GPS is enabled and working.");
            Toast.makeText(getApplicationContext(), "GPS is enabled and working.", Toast.LENGTH_SHORT);

            //drop a marker on map - create a method called dropMarker
            dropMarker(LocationManager.GPS_PROVIDER, -16711681);//cyan
            //remove network location updates. Hint see locationmanager for update removal method.
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) getApplicationContext(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                ActivityCompat.requestPermissions((Activity) getApplicationContext(), new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
                return;
            }

            locationManager.removeUpdates(locationListenerNetwork);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    MIN_TIME_BTWN_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES,
                    locationListenerGPS);


        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            //output in Log.d and Toast that GPS is enabled and working
            Log.d("MyMapsApp", "GPS is enabled and working.");
            Toast.makeText(getApplicationContext(), "GPS is enabled and working.", Toast.LENGTH_SHORT);

            //set up switch statement to check the status input parameter
            //case LocationProvider.AVAILABLE (2) --> output message to Log.d and Toast
            //case LocationProvider.OUT_OF_SERVICE (0) --> request updates from NETWORK_PROVIDER
            //case LocationProvider.TEMPORARILY_UNAVAILABLE (1) --> request updates from NETWORK_PROVIDER
            //case default--> request updates from NETWORK_PROVIDER

            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

            switch (status) {
                case 0:
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BTWN_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES,
                            locationListenerNetwork);
                    break;
                case 1:
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BTWN_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES,
                            locationListenerNetwork);
                    break;
                case 2:
                    Log.d("MyMapsApp", "available-GPS");
                    break;
                default:
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BTWN_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES,
                            locationListenerNetwork);
                    break;
            }


        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

    };

    android.location.LocationListener locationListenerNetwork = new android.location.LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            //output in Log.d and Toast that network is enabled and working
            Log.d("MyMapsApp", "Network is enabled and working.");
            Toast.makeText(getApplicationContext(), "Network is enabled and working.", Toast.LENGTH_SHORT);

            //drop a marker on map - create a method called dropMarker
            dropMarker(LocationManager.NETWORK_PROVIDER, -16777216); //black
            //relaunch the network provider request requestLocationUpdates (NETWORK_PROVIDER)
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    MIN_TIME_BTWN_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES,
                    locationListenerNetwork);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            //output message in log.d and toast
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            switch (status) {
                case 0:
                    Log.d("MyMapsApp", "network out of service");
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            MIN_TIME_BTWN_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES,
                            locationListenerNetwork);
                    break;
                case 1:
                    Log.d("MyMapsApp", "network temp unavailable");
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            MIN_TIME_BTWN_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES,
                            locationListenerNetwork);
                    break;
                case 2:
                    Log.d("MyMapsApp", "available-NETWORK");
                    break;
                default:
                    Log.d("MyMapsApp", "whattttt? default.");
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            MIN_TIME_BTWN_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES,
                            locationListenerNetwork);
                    break;
            }
        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    public void dropMarker(String provider, int color) {
        userLocation = new LatLng(0, 0);
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            myLocation = locationManager.getLastKnownLocation(provider);
        }

        if(myLocation == null){
            Toast.makeText(this, "null myLocation", Toast.LENGTH_SHORT).show();
            Log.d("self", "null mylocation");
        }

        else{

            userLocation = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());

            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(userLocation, MY_LOC_ZOOM_FACTOR);

            Circle circle = mMap.addCircle(new CircleOptions()
                    .center(userLocation)
                    .radius(1)
                    .strokeColor(Color.RED)
                    .strokeWidth(2)
                    .fillColor(color));
            mMap.animateCamera(update);
        }

    }

    public void changeView (View v){

        if(mMap.getMapType() == 1)
        {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        }
        else if(mMap.getMapType()== 2)
        {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
    }

    public void searchAddress(View v){

        address = ((EditText)findViewById(R.id.editText_search)).getText().toString();
        if(address != null && !address.equals("")){
            mMap.clear();
            Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
            try
            {
                Double nearlat = myLocation.getLatitude();
                Double nearlong = myLocation.getLongitude();
                List<android.location.Address> manyaddresses = geoCoder.getFromLocationName(address, 20,
                        nearlat - 0.04, nearlong - 0.04, nearlat + 0.04, nearlong + 0.04); //get 5 miles??

                //mMap.addMarker(new MarkerOptions().position(new LatLng(nearlat, nearlong)));
                Log.d("MyMapsApp", "lat, long, address" + nearlat+nearlong+ manyaddresses);
                if(manyaddresses.size() > 0){
                    for(int i = 0; i < manyaddresses.size(); i++){
                        Double lat = (double)(manyaddresses.get(i).getLatitude());
                        Double lon = (double)(manyaddresses.get(i).getLongitude());
                        LatLng mattch = new LatLng(lat,lon);
                        mMap.addMarker(new MarkerOptions().position(mattch).title(address));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mattch, MY_LOC_ZOOM_FACTOR));
                    }

                }
            }
            catch (IOException e)
            {
                Log.d("MyMapsApp", "YOU FAILED :(");
                e.printStackTrace();
            }
        }

    }
    public void onCheckedChanged(View v) {

    }
}
