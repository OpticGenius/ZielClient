package com.quartz.zielclient.utilities;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.quartz.zielclient.utilities.channel.Channel;
import com.quartz.zielclient.utilities.channel.ChannelHandler;
import com.quartz.zielclient.utilities.channel.ChannelListener;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class ChannelHandlerTest {


  @Before
  public void setUp(){
    ChannelHandler.staticInit();
  }


  /**
   * Test that when a channel is requested it is created.
   */
  @Test
  public void testCreateChannel() {
    FirebaseApp firebaseApp = Mockito.mock(FirebaseApp.class);
    ChannelListener channelListener =  Mockito.mock(ChannelListener.class);
    Mockito.when(channelListener.getAssistedId()).thenReturn("testAssistedId");
    Mockito.when(channelListener.getCarerId()).thenReturn("testCarerId");
    Channel testChannel = ChannelHandler.createChannel(channelListener);
    Mockito.verify(channelListener,VerificationModeFactory.atLeastOnce()).getAssistedId();
    Mockito.verify(channelListener, VerificationModeFactory.atLeastOnce()).getCarerId();
    assertNotNull(testChannel);
  }

  /**
   * Test to see whether channels can be retrieved
   */
  @Test
  public void retrieveChannel() {
    DatabaseReference testChannelsReference = Mockito.mock(DatabaseReference.class);
    DatabaseReference testChannelReference = Mockito.mock(DatabaseReference.class);
    ChannelListener channelListener =  Mockito.mock(ChannelListener.class);
    String testChannelID = "123456789";
    Mockito.when(testChannelsReference.child(testChannelID)).thenReturn(testChannelReference);
    Channel channel = ChannelHandler.retrieveChannel(testChannelID,channelListener);
    assertNotNull(channel);
  }
}