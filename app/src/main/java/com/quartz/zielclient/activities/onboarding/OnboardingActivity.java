package com.quartz.zielclient.activities.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.quartz.zielclient.R;
import com.quartz.zielclient.activities.signup.SignUpActivity;

public class OnboardingActivity extends AppCompatActivity implements View.OnClickListener {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_help_onboarding_page1);

    Button nextButton = findViewById(R.id.next1);
    nextButton.setOnClickListener(this);

    Button skipButton = findViewById(R.id.signup);
    skipButton.setOnClickListener(this);
  }

  @Override
  public void onClick(View v) {
    if (v.getId() == R.id.next1) {
      startActivity(new Intent(this, SecondOnboardingActivity.class));
    } else if (v.getId() == R.id.signup) {
      startActivity(new Intent(this, SignUpActivity.class));
    }
  }
}