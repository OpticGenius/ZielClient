package com.quartz.zielclient.activities;

import android.os.Bundle;
import android.app.Activity;

import com.quartz.zielclient.R;

public class HomePageActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    setTheme(R.style.HomeTheme);
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_home_page);
  }
}