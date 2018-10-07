package com.quartz.zielclient.channel;

import android.location.Location;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.quartz.zielclient.messages.Message;
import com.quartz.zielclient.messages.MessageService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * This Object abstracts away the communication with the database Channels It requires a
 * Chanlelistener to be able to pass updates to the user.
 *
 * @author Bilal Shehata
 */
public class ChannelData implements ValueEventListener {

  // contains current channel values in database
  private Map<String, Object> channelValues;

  private DatabaseReference channelReference;

  // the object that wants to listen to this channel
  private ChannelListener channelListener;
  private String channelKey;

  /**
   * @param channelReference - location in database where channel exists
   * @param channelListener - the object that wants to listen to the channel
   * @param channelKey
   */
  public ChannelData(
      DatabaseReference channelReference, ChannelListener channelListener, String channelKey) {
    this.channelReference = channelReference;
    this.channelListener = channelListener;
    this.channelKey = channelKey;
    channelReference.addValueEventListener(this);
  }

  /**
   * method returns a LatLng Object since this is more practical for the google maps API.
   *
   * @return A LatLng object representing the user's location.
   */
  @SuppressWarnings("unchecked")
  public LatLng getAssistedLocation() {
    if (channelValues != null) {
      Map<String, String> assistedLocationCordinates =
          (Map<String, String>) channelValues.get("assistedLocation");

      double xCoord = Double.parseDouble(assistedLocationCordinates.get("xCoord"));
      double yCoord = Double.parseDouble(assistedLocationCordinates.get("yCoord"));
      return new LatLng(xCoord, yCoord);
    }

    // Default to Null Island
    return new LatLng(0, 0);
  }

  public void setAssistedLocation(Location location) {
    final String xCoord = String.valueOf(location.getLatitude());
    final String yCoord = String.valueOf(location.getLongitude());

    channelReference.child("assistedLocation").child("xCoord").setValue(xCoord);
    channelReference.child("assistedLocation").child("yCoord").setValue(yCoord);
  }

  /**
   * Adding the message object into the channel Database as a JSON object.
   *
   * @param message The new message being sent in
   */
  public void sendMessage(Message message) {
    channelReference.child("messages").push().setValue(message);
  }

  /**
   * Retrieve all the messages from this channel.
   *
   * <p>Downcasting is required to serialise the JSON representaiton of the messages to a Java Map.
   *
   * @return A map of the messages.
   */
  public Map<String, Message> getMessages() {
    if (channelValues.get("messages") != null) {
      return MessageService.deserialiseMessages(channelValues.get("messages"));
    }

    Map<String, Message> messageObject = new HashMap<>();
    return messageObject;
  }

  public void setMessages(Map<String, String> messages) {
    channelReference.child("messages").setValue(messages);
  }

  /**
   * recieve update from database and update the listener that some data has changed
   *
   * @param dataSnapshot The datasnaphot to update the client based on.
   */
  @Override
  public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
    GenericTypeIndicator<Map<String, Object>> t =
        new GenericTypeIndicator<Map<String, Object>>() {};
    channelValues = dataSnapshot.getValue(t);
    channelListener.dataChanged();
  }

  @Override
  public void onCancelled(@NonNull DatabaseError databaseError) {
    // todo
  }

  public String getDirectionsURL() {
    return channelValues.get("directionsURL").toString();
  }

  public void setDirectionsURL(String directionsURL) {
    channelReference.child("directionsURL").setValue(directionsURL);
  }

  public String getAssisted() {
    return channelValues.get("assisted").toString();
  }

  public void setAssisted(String assisted) {
    channelReference.child("assisted").setValue(assisted);
  }

  public boolean getAssistedStatus() {
    return channelValues.get("assistedStatus").equals(true);
  }

  public void setAssistedStatus(boolean assistedStatus) {
    channelReference.child("assistedStatus").setValue(assistedStatus);
  }

  public String getCarer() {
    return channelValues.get("carer").toString();
  }

  public void setCarer(String carer) {
    this.channelReference.child("carer").setValue(carer);
  }

  public boolean getCarerStatus() {
    return channelValues.get("carerStatus").equals(true);
  }

  public void setCarerStatus(boolean carerStatus) {
    channelReference.child("carerStatus").setValue(carerStatus);
  }

  public Boolean getVideoCallStatus() {
    return channelValues.get("videoCallStatus").equals(true);
  }

  public void setVideoCallStatus(Boolean active) {
    channelReference.child("videoCallStatus").setValue(active);
  }

  public String getChannelKey() {
    return channelKey;
  }

  public void setChannelKey(String channelKey) {
    this.channelKey = channelKey;
  }

  public List<LatLng> getCarerMarkerList() {
    if (channelValues.get("carerMarkerList") != null) {
      ArrayList<LatLng> markerList = new ArrayList<>();
      try {
        Map<String, Map<String, String>> markerMap =
            (Map<String, Map<String, String>>) channelValues.get("carerMarkerList");
        markerMap
            .values()
            .forEach(
                value -> {
                  markerList.add(
                      new LatLng(
                          Double.valueOf(value.get("xCoord")),
                          Double.valueOf(value.get("yCoord"))));
                });
        return markerList;
      } catch (ClassCastException e) {
        return null;
      }
    }
    return null;
  }

  public void addMarker(LatLng coordinate) {
    String coordinateId = UUID.randomUUID().toString();
    // set x coordinate for marker
    channelReference
        .child("carerMarkerList")
        .child(coordinateId)
        .child("xCoord")
        .setValue(String.valueOf(coordinate.latitude));
    // set y coordinate for marker
    channelReference
            .child("carerMarkerList")
            .child(coordinateId)
            .child("yCoord")
            .setValue(String.valueOf(coordinate.longitude));

  }

  public  void clearMarkers(){
    channelReference
            .child("carerMarkerList")
            .setValue(null);
  }
}
