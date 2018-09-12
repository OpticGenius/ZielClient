package com.quartz.zielclient.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.quartz.zielclient.R;
import com.quartz.zielclient.adapters.ListAdapter;
import com.quartz.zielclient.models.ListItem;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Activity to display a carer's home page.
 *
 * @author wei how ng
 */
public class CarerHomepageActivity extends Activity implements ValueEventListener {

  private RecyclerView mRecyclerView;
  private RecyclerView.Adapter mAdapter;
  private RecyclerView.LayoutManager mLayoutManager;
  private List<ListItem> listItems;
  private DatabaseReference requestsReference;
  private String userID = "LglIRTsQqGUmpU16CuYJIxtS0S62";//getUserId

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_carer_homepage);

    // Getting requestsReference from FireBase
    requestsReference = FirebaseDatabase.getInstance().getReference("channelRequests/" + userID);
    requestsReference.addValueEventListener(this);

    // Initialising RecyclerView
    mRecyclerView = findViewById(R.id.my_recycler_view);

    // Each entry has fixed size.
    mRecyclerView.setHasFixedSize(true);

    // Use a linear layout manager
    mLayoutManager = new LinearLayoutManager(this);
    mRecyclerView.setLayoutManager(mLayoutManager);
  }

  // TODO Add the actual description, which would be the destination
  /**
   * Fetches the data as JSON files to
   *
   * @param channelRequestsData Collection of all appropriate channel requests.
   */
  private void initData(Map<String, Map<String, String>> channelRequestsData) {
    // Adding each user name and description to the listItem object and then appending them in
    listItems = channelRequestsData.values()
        .stream()
        .map(channel -> new ListItem(channel.get("name"), channel.get("channel-id")))
        .collect(Collectors.toList());

    // Using the Adapter to convert the data into the recycler view
    mAdapter = new ListAdapter(listItems, this);
    mRecyclerView.setAdapter(mAdapter);
  }

  @Override
  public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
    // Getting the channel data and calling the rendering method on it
    // Nasty generic types needed unfortunately
    GenericTypeIndicator<Map<String, Map<String, String>>> t =
        new GenericTypeIndicator<Map<String, Map<String, String>>>() {};
    Map<String, Map<String, String>> channelRequestsData = dataSnapshot.getValue(t);
    if (channelRequestsData != null) {
      initData(channelRequestsData);
    }
  }

  //TODO
  @Override
  public void onCancelled(@NonNull DatabaseError databaseError) {

  }
}