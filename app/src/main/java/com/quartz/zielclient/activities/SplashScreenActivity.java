package com.quartz.zielclient.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.quartz.zielclient.activities.common.LaunchPadActivity;
import com.quartz.zielclient.activities.signup.SignUpActivity;
import com.quartz.zielclient.exceptions.AuthorisationException;
import com.quartz.zielclient.user.User;
import com.quartz.zielclient.user.UserController;
import com.quartz.zielclient.user.UserFactory;

import java.util.Optional;

public class SplashScreenActivity extends AppCompatActivity implements ValueEventListener {

  private static final String TAG = SplashScreenActivity.class.getSimpleName();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    try {
      UserController.fetchThisUser(this);
    } catch (AuthorisationException e) {
      Log.e(TAG, "Error when authorising user", e);
      goToSignin();
    }
  }

  @Override
  public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
    Optional<User> maybeUser = UserFactory.getUser(dataSnapshot);
    if (maybeUser.isPresent()) {
      User user = maybeUser.get();
      redirect(user);
    } else {
      goToSignin();
    }
  }

  @Override
  public void onCancelled(@NonNull DatabaseError databaseError) {
    Log.e(TAG, "Database error:", databaseError.toException());
    goToSignin();
  }

  private void goToSignin() {
    Intent intent = new Intent(this, SignUpActivity.class);
    startActivity(intent);
    finish();
  }

  private void redirect(User user) {
    if (user.isAssisted()) {
      // TODO implement assisted home page
      startActivity(new Intent(this, HomePageActivity.class));
    } else {
      startActivity(new Intent(this, CarerHomepageActivity.class));
    }
    finish();
  }
}