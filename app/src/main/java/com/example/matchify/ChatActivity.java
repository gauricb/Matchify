package com.example.matchify;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.matchify.models.Message;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.livequery.ParseLiveQueryClient;
import com.parse.livequery.SubscriptionHandling;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    static final String TAG = ChatActivity.class.getSimpleName();
    static final String USER_ID_KEY = "userId";
    static final String BODY_KEY = "body";
    static final int MAX_CHAT_MESSAGES_TO_SHOW = 50;

    EditText etMessage;
    ImageButton ibSend;
    RecyclerView rvChat;
    List<Message> mMessages;
    Boolean mFirstLoad;
    ChatAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        // User login
        if (ParseUser.getCurrentUser() != null) { // start with existing user
            startWithCurrentUser();
        }
        else { // If not logged in, login as a new anonymous user
            login();
        }

        // Load existing messages to begin with
        refreshMessages();

        // Make sure the Parse server is setup to configured for live queries
        // Enter the websocket URL of your Parse server
        String websocketUrl = "wss://matchify.b4a.io/"; // ⚠️ TYPE IN A VALID WSS:// URL HERE

        ParseLiveQueryClient parseLiveQueryClient = null;
        try {
            parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient(new URI(websocketUrl));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        ParseQuery<Message> parseQuery = ParseQuery.getQuery(Message.class);
        // This query can even be more granular (i.e. only refresh if the entry was added by some other user)
        // parseQuery.whereNotEqualTo(USER_ID_KEY, ParseUser.getCurrentUser().getObjectId());

        // Connect to Parse server
        SubscriptionHandling<Message> subscriptionHandling = parseLiveQueryClient.subscribe(parseQuery);

        // Listen for CREATE events on the Message class
        subscriptionHandling.handleEvent(SubscriptionHandling.Event.CREATE, (query, object) -> {
            mMessages.add(0, object);

            // RecyclerView updates need to be run on the UI thread
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.notifyDataSetChanged();
                    rvChat.scrollToPosition(0);
                }
            });
        });
    }

    // Get the userId from the cached currentUser object
    void startWithCurrentUser() {
        setupMessagePosting();
    }

    // Create an anonymous user using ParseAnonymousUtils and set sUserId
    void login() {
        ParseAnonymousUtils.logIn(new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Anonymous login failed: ", e);
                } else {
                    startWithCurrentUser();
                }
            }
        });
    }

    // Set up button event handler which posts the entered message to Parse
    void setupMessagePosting() {
        // Find the text field and button
        etMessage = (EditText) findViewById(R.id.etMessage);
        ibSend = (ImageButton) findViewById(R.id.ibSend);
        rvChat = (RecyclerView) findViewById(R.id.rvChat);
        mMessages = new ArrayList<>();
        mFirstLoad = true;
        final String userId = ParseUser.getCurrentUser().getObjectId();
        mAdapter = new ChatAdapter(ChatActivity.this, userId, mMessages);
        rvChat.setAdapter(mAdapter);

        // associate the LayoutManager with the RecylcerView
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatActivity.this);
        rvChat.setLayoutManager(linearLayoutManager);

        // When send button is clicked, create message object on Parse
        ibSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = etMessage.getText().toString();

                Message message = new Message();
                message.setUserId(ParseUser.getCurrentUser().getObjectId());
                message.setBody(data);

                message.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(ChatActivity.this, "Successfully created message on Parse",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "Failed to save message", e);
                        }
                    }
                });
                etMessage.setText(null);
            }
        });
    }

    void refreshMessages() {
        // Construct query to execute
        ParseQuery<Message> query = ParseQuery.getQuery(Message.class);
        // Configure limit and sort order
        query.setLimit(MAX_CHAT_MESSAGES_TO_SHOW);

        // get the latest 50 messages, order will show up newest to oldest of this group
        query.orderByDescending("createdAt");
        // Execute query to fetch all messages from Parse asynchronously
        // This is equivalent to a SELECT query with SQL
        query.findInBackground(new FindCallback<Message>() {
            public void done(List<Message> messages, ParseException e) {
                if (e == null) {
                    mMessages.clear();
                    mMessages.addAll(messages);
                    mAdapter.notifyDataSetChanged(); // update adapter
                    // Scroll to the bottom of the list on initial load
                    if (mFirstLoad) {
                        rvChat.scrollToPosition(0);
                        mFirstLoad = false;
                    }
                } else {
                    Log.e("message", "Error Loading Messages" + e);
                }
            }
        });
    }
}