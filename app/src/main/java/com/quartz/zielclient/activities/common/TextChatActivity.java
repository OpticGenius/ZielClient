package com.quartz.zielclient.activities.common;

import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.quartz.zielclient.R;
import com.quartz.zielclient.adapters.MessageListAdapter;
import com.quartz.zielclient.channel.ChannelController;
import com.quartz.zielclient.channel.ChannelData;
import com.quartz.zielclient.channel.ChannelListener;
import com.quartz.zielclient.messages.Message;
import com.quartz.zielclient.messages.MessageFactory;

import java.util.List;
import java.util.Objects;

/**
 * Chat activity allows users to communicate with eachother through messaging
 * This activity is currently unstyled.
 */
public class TextChatActivity extends AppCompatActivity implements ChannelListener, View.OnClickListener {


  // temporary for debugging will become a dynamic channel
  private ChannelData channel;
  private TextView chatOutput;
  private EditText chatInput;

  // Recycler Views and Adapter for the text chat
  private RecyclerView mMessageRecycler;
  private MessageListAdapter mMessageListAdapter;
  private List<Message> messageList;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_text_chat_message_list);

    // Chat using RecyclerView
    mMessageRecycler = findViewById(R.id.message_recyclerview);
    mMessageListAdapter = new MessageListAdapter(this, messageList);
    mMessageRecycler.setLayoutManager(new LinearLayoutManager(this));

    // Fetching channel using handler
    String channelKey = getIntent().getStringExtra(getApplicationContext()
        .getString(R.string.channel_key));
    channel = ChannelController.retrieveChannel(channelKey, this);

    // Initialise the graphical elements
    chatInput = findViewById(R.id.enter_chat_box);
    Button sendMessage = findViewById(R.id.button_chatbox_send);
    sendMessage.setOnClickListener(this);

    /*
    // initialize graphical elements
    chatOutput = findViewById(R.id.chatOutput);
    chatInput = findViewById(R.id.chatInput);
    Button sendButton = findViewById(R.id.sendButton);
    sendButton.setOnClickListener(this);
    */
  }

  /**
   * Fetching the channel chat to be displayed
   * @return Respective ChannelData Object
   */
  public ChannelData getCurrentChannel() {


    return null;
  }

  @Override
  public void dataChanged() {
    if (channel.getMessages() != null) {
      // this is completely unstylised representation of the messages
      chatOutput.setText(channel.getMessages().toString());
    }
  }

  @Override
  public String getAssistedId() {
    return null;
  }

  @Override
  public String getCarerId() {
    return null;
  }

  /**
   * Send message located in the input view
   * TODO: Add the sender/ receiver's username into this instead of the hardcoded string
   * @param view
   */
  @Override
  public void onClick(View view) {
    Message messageToSend = MessageFactory.makeTextMessage(chatInput.getText().toString(), "Name");
    channel.sendMessage(messageToSend);
  }
}
