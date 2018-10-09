package com.quartz.zielclient.activities.common;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.quartz.zielclient.R;
import com.wonderkiln.camerakit.CameraKit;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

/**
 * This class is responsible for setting up a camera view to take photos of a location.
 * This class uses the CameraKit API.
 * <p>
 * Documentation: https://github.com/CameraKit/camerakit-android
 *
 * @author Armaan McLeod
 * @version 1.0
 * 9/10/2018
 */
public class TakePhotosActivity extends AppCompatActivity {
  private CameraView cameraView;

  private boolean canTakePicture;

  private final String activity = this.getClass().getSimpleName();

  /**
   * Camera event listener which listens for camera events.
   */
  private final CameraKitEventListener cameraListener = new CameraKitEventListener() {

    /**
     * Checks if camera is opened or closed.
     * @param cameraKitEvent The camera event kit.
     */
    @Override
    public void onEvent(CameraKitEvent cameraKitEvent) {
      String s = cameraKitEvent.getType();
      if (CameraKitEvent.TYPE_CAMERA_OPEN.equals(s)) {
        Log.d(activity, "Camera enabled");
        canTakePicture = true;

      } else if (CameraKitEvent.TYPE_CAMERA_CLOSE.equals(s)) {
        Log.d(activity, "Camera disabled");
        canTakePicture = false;
      }
    }

    /**
     * Checks if camera had any errors executing any tasks.
     * @param cameraKitError The camera event kit.
     */
    @Override
    public void onError(CameraKitError cameraKitError) {
      Log.d(activity, "Error opening camera");
    }

    /**
     * Extracts image taken into Bitmap form.
     * @param cameraKitImage The camera event kit.
     */
    @Override
    public void onImage(CameraKitImage cameraKitImage) {
      Log.d(activity, "Taking picture");
      byte[] picture = cameraKitImage.getJpeg();
      Bitmap result = BitmapFactory.decodeByteArray(picture, 0, picture.length);
    }

    /**
     * Event listen for videos, out of scope for this feature.
     * @param cameraKitVideo The camera event kit.
     */
    @Override
    public void onVideo(CameraKitVideo cameraKitVideo) {
      Log.d(activity, "Taking video");
    }
  };

  /**
   * Called when the activity is first created.
   * This is where you should do all of your normal static set up:
   * create views, bind data to lists, etc.
   * This method also provides you with a Bundle containing the activity's
   * previously frozen state, if there was one.
   * <p>
   * Documentation: https://developer.android.com/reference/android/app/Activity.html
   * #onCreate(android.os.Bundle)
   *
   * @param savedInstanceState This is the data it most recently supplied in
   *                           onSaveInstanceState(Bundle).
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.take_photos);

    // Create camera view
    cameraView = findViewById(R.id.camera);
    cameraView.addCameraKitListener(cameraListener);

    // Initialise preferred settings
    cameraView.setFlash(CameraKit.Constants.FLASH_OFF);
    cameraView.setFacing(CameraKit.Constants.FACING_BACK);
    cameraView.setFocus(CameraKit.Constants.FOCUS_CONTINUOUS);
    cameraView.setMethod(CameraKit.Constants.METHOD_STANDARD);
    cameraView.setZoom(CameraKit.Constants.ZOOM_PINCH);
    cameraView.setPermissions(CameraKit.Constants.PERMISSIONS_STRICT);
    cameraView.setJpegQuality(100);

    // button which captures photo
    Button takePhotoButton = findViewById(R.id.button);
    takePhotoButton.setOnClickListener(v -> {
      Log.d(activity, Boolean.toString(canTakePicture));
      Log.d(activity, "Clicked button");

      if (canTakePicture) {
        Log.d(activity, "Captured image");
        cameraView.captureImage();
      }
    });
  }

  /**
   * Called when the activity will start interacting with the user.
   * At this point your activity is at the top of the activity stack, with user input going to it.
   * <p>
   * Documentation: https://developer.android.com/reference/android/app/Activity.html#onResume()
   */
  @Override
  protected void onResume() {
    super.onResume();
    cameraView.start();
  }

  /**
   * Called when the system is about to start resuming a previous activity.
   * This is typically used to commit unsaved changes to persistent data,
   * stop animations and other things that may be consuming CPU, etc.
   * Implementations of this method must be very quick because the next activity will
   * not be resumed until this method returns.
   * <p>
   * Documentation: https://developer.android.com/reference/android/app/Activity.html#onPause()
   */
  @Override
  protected void onPause() {
    cameraView.stop();
    super.onPause();
  }
}
