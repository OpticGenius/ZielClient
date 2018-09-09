package com.quartz.zielclient.activities.common;

import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.quartz.zielclient.R;
import com.quartz.zielclient.utilities.map.DirectionsJSONParser;
import com.quartz.zielclient.utilities.map.FetchUrl;
import com.quartz.zielclient.utilities.map.ParserTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * This class is responsible for handling all map activities.
 *
 * <p>
 * Courtesy : https://stackoverflow.com/questions/44992014/
 * how-to-get-current-location-in-googlemap-using-fusedlocationproviderclient/44993694#44993694
 *
 * @author Armaan McLeod
 * @version 1.0- 1
 * 28/08/2018
 */
public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

  // Custom permissions request code
  private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

  private static final int DEFAULT_ZOOM = 11;

  private static final String API_URL = "https://maps.googleapis.com/maps/api/directions/json?";

  private final String TAG = this.getClass().getSimpleName();

  private GoogleMap mGoogleMap;

  private LocationRequest mLocationRequest;
  private FusedLocationProviderClient mFusedLocationClient;

  private LatLng source;
  private LatLng destination;

  public LatLng getDestination() {
    return destination;
  }

  public void setDestination(LatLng destination) {
    this.destination = destination;
  }

  public LatLng getSource() {
    return source;
  }

  public void setSource(LatLng source) {
    this.source = source;
  }

  private LocationCallback mLocationCallback = new LocationCallback() {

    /**
     * Moves camera to last known location of user.
     * @param locationResult location results fetched from API.
     */
    @Override
    public void onLocationResult(LocationResult locationResult) {

      // All previous locations
      List<Location> locationList = locationResult.getLocations();

      // If one location exists
      if (!locationList.isEmpty()) {

        // The last location in the list is the newest
        Location location = locationList.get(locationList.size() - 1);
        Log.i(TAG, "Location: " +
            location.getLatitude() + " "
            + location.getLongitude());

        // Update and draw source location
        setSource(new LatLng(location.getLatitude(), location.getLongitude()));
        drawMarker(source, BitmapDescriptorFactory.HUE_MAGENTA, "Current Location");
      }
    }
  };

  /**
   * Draws marker on the Google map.
   * @param location This is the location on the map.
   * @param colour This is the colour of the marker.
   * @param message This is the message to pass to the marker.
   */
  private void drawMarker(LatLng location, float colour, String message) {
    MarkerOptions markerOptions = new MarkerOptions();

    // Update marker options
    markerOptions.position(location);
    markerOptions.title(message);
    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(colour));

    // Add marker to the map
    mGoogleMap.addMarker(markerOptions);

    // Zoom in on map location
    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, DEFAULT_ZOOM));
  }

  /**
   * Creates map along with its attributes.
   *
   * <p>
   * Documentation : https://developer.android.com/reference/android/app/
   * Activity.html#onCreate(android.os.Bundle)
   *
   * @param savedInstanceState This is responsible for saving state of map activities.
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_maps2);

    // Create autocomplete bar
    PlaceAutocompleteFragment placeAutoComplete = (PlaceAutocompleteFragment)
        getFragmentManager().findFragmentById(R.id.place_autocomplete);

    // Listen for new places queried in search bar
    placeAutoComplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {

      @Override
      public void onPlaceSelected(Place place) {
        // Clear all previous points on map
        mGoogleMap.clear();

        Log.d(TAG, "Place selected: " + place.getLatLng());
        setDestination(place.getLatLng());

        // Compute path to destination
        String directionsURL = getDirectionsUrl();
        FetchUrl fetchUrl = new FetchUrl(mGoogleMap);
        fetchUrl.execute(directionsURL);

        // Redraw both source and destination markers to screen
        drawMarker(source, BitmapDescriptorFactory.HUE_MAGENTA, "Current Location");
        drawMarker(destination, BitmapDescriptorFactory.HUE_RED, "Destination Location");
      }

      @Override
      public void onError(Status status) {
        Log.d(TAG, "An error occurred: " + status);
      }
    });

    // Create fused location client to interact with API
    mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

    // Place map in application
    SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager()
        .findFragmentById(R.id.map);

    if (mapFrag != null) {
      mapFrag.getMapAsync(this);
    }
  }

  /**
   * This is called when user received an event call.
   *
   * <p>
   * Documentation : https://developers.google.com/android/reference/com/google/android/gms/maps/
   * OnMapReadyCallback.html#onMapReady(com.google.android.gms.maps.GoogleMap)
   */
  @Override
  public void onPause() {
    super.onPause();

    // Stop location updates when Activity is no longer active
    if (mFusedLocationClient != null) {
      mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }
  }

  /**
   * This initialises map activities and checks permissions.
   *
   * @param googleMap This is the Google Map
   */
  @Override
  public void onMapReady(GoogleMap googleMap) {

    // Initialise Google map
    mGoogleMap = googleMap;

    // Setup location request and intervals between requests
    mLocationRequest = new LocationRequest();
    mLocationRequest.setInterval(120000); // two minute interval
    mLocationRequest.setFastestInterval(120000);
    mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

    // Check permissions
    if (checkSelfPermission(ACCESS_FINE_LOCATION) == PERMISSION_GRANTED) {
      requestLocation();
    } else {
      //Request Location Permission
      requestLocationPermission();
    }
  }

  /**
   * Check location permissions before showing user location.
   */
  private void requestLocationPermission() {
    // If permission is not granted
    if (checkSelfPermission(ACCESS_FINE_LOCATION) != PERMISSION_GRANTED) {

      // Should we show an explanation?
      if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {

        // Show an explanation to the user *asynchronously* -- don't block
        // This thread waiting for the user's response! After the user
        // Sees the explanation, try again to request the permission.
        new AlertDialog.Builder(this)
            .setTitle("Location Permission Needed")
            .setMessage(
                "This app needs the Location permission, " +
                    "please accept to use location functionality")

            .setPositiveButton("OK",
                //Prompt the user once explanation has been shown
                (dialogInterface, i) ->
                    requestPermissions(new String[]{ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION))
            .create()
            .show();

      } else {
        // No explanation needed, we can request the permission.
        requestPermissions(new String[]{ACCESS_FINE_LOCATION},
            MY_PERMISSIONS_REQUEST_LOCATION);
      }
    }
  }

  /**
   * This is a callback for requesting and checking the result of a permission.
   *
   * <p>
   * Documentation : https://developer.android.com/reference/android/support/v4/app/
   * ActivityCompat.OnRequestPermissionsResultCallback#onRequestPermissionsResult
   *
   * @param requestCode  This is the request code passed to requestPermissions.
   * @param permissions  This is the permissions.
   * @param grantResults This is results for granted or un-granted permissions.
   */
  @Override
  public void onRequestPermissionsResult(int requestCode,
                                         @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
    if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
      handleLocationPermission(grantResults);
    }
  }

  /**
   * This is responsible for requesting a location permission from the user.
   * @param grantResults This is results for granted or un-granted permissions.
   */
  private void handleLocationPermission(@NonNull int[] grantResults) {
    // If request is cancelled, the result arrays are empty.
    if (grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED) {
      requestLocation();
    } else {
      // Permission denied
      Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
    }
  }

  /**
   * This enables the location to be shown on the map.
   */
  private void requestLocation() {
    // Permission was granted so we can enable user location
    if (checkSelfPermission(ACCESS_FINE_LOCATION) == PERMISSION_GRANTED) {
      mFusedLocationClient.requestLocationUpdates(mLocationRequest,
          mLocationCallback,
          Looper.myLooper());
      mGoogleMap.setMyLocationEnabled(true);
    }
  }

  /**
   * Builds URL from Directions API web service.
   * @return String This is the new url pointing to the API endpoint.
   */
  private String getDirectionsUrl() {

    // Source and destination formats
    String strSource = "origin=" + source.latitude + "," + source.longitude;
    String strDestination = "destination=" + destination.latitude + "," + destination.longitude;

    // Sensor initialisation
    String sensor = "sensor=false";

    // Building the parameters to the web service
    String parameters = strSource + "&" + strDestination + "&" + sensor;

    // Building the url to the web service
    return API_URL + parameters;
  }
}