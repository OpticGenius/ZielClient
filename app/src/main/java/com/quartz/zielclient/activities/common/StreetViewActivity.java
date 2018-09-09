package com.quartz.zielclient.activities.common;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;
import com.google.android.gms.maps.model.StreetViewPanoramaOrientation;
import com.quartz.zielclient.R;

/**
 * This class is responsible for showing a street view of a location
 *
 * @author Armaan McLeod
 * @version 1.0- 1
 * 9/09/2018
 */
public class StreetViewActivity extends AppCompatActivity implements
    OnStreetViewPanoramaReadyCallback {

  private LatLng destination;

  /**
   * Creates a street view of a map location.
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
    setContentView(R.layout.activity_street_view);

    // Get bundle of arguments passed to this activity
    Bundle bundle = getIntent().getExtras();
    if (bundle != null) {
      destination = bundle.getParcelable("destination");
    }

    // Create toolbar
    Toolbar tb = findViewById(R.id.toolbar);
    tb.setSubtitle("Street View");

    // Create street view fragment
    StreetViewPanoramaFragment streetViewPanoramaFragment =
        (StreetViewPanoramaFragment) getFragmentManager()
            .findFragmentById(R.id.streetview_panorama);
    streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);
  }

  /**
   * Called when the Street View panorama is ready to be used.
   *
   * Documentation : https://developers.google.com/android/reference/com/google/android/gms/maps/
   * OnStreetViewPanoramaReadyCallback
   *
   * @param streetViewPanorama This is the street view panorama
   */
  @Override
  public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
    streetViewPanorama.setPosition(destination);
    streetViewPanorama.setStreetNamesEnabled(true);
    streetViewPanorama.setPanningGesturesEnabled(true);
    streetViewPanorama.setZoomGesturesEnabled(true);

    // Animate street view on screen
    streetViewPanorama.animateTo(
        new StreetViewPanoramaCamera.Builder().
            orientation( new StreetViewPanoramaOrientation(20, 20))
            .zoom(streetViewPanorama.getPanoramaCamera().zoom)
            .build(), 2000);
  }
}
