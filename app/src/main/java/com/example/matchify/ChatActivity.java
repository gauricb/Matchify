package com.example.matchify;

import static com.example.matchify.MainActivity.getCurrentSpotifyUser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.matchify.adapters.CopyChatAdapter;
import com.example.matchify.fragments.SongDialogFragment;
import com.example.matchify.models.Message;
import com.example.matchify.models.SpotifyUser;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.livequery.ParseLiveQueryClient;
import com.parse.livequery.SubscriptionHandling;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    static final String TAG = ChatActivity.class.getSimpleName();

    static final int MAX_CHAT_MESSAGES_TO_SHOW = 50;


    EditText etMessage;
    ImageButton ibSend;
    ImageButton ibAttach;
    RecyclerView rvChat;
    List<Message> mMessages;
    Boolean mFirstLoad;
    //ChatAdapter mAdapter;
    CopyChatAdapter mAdapter;
    SpotifyUser matchObject;

    public static SpotifyAppRemote mSpotifyAppRemote;


    private static final String CLIENT_ID = "33a4d498feb4475c902c47154d370dc2";
    private static final String REDIRECT_URI = "capstone-app-login://callback";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        SpotifyAppRemote.connect(this, connectionParams,
                new Connector.ConnectionListener() {

                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        Log.e(TAG, "Connected! Yay!");
                        // Now you can start interacting with App Remote

                        try {
                            startWithCurrentUser();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        // Load existing messages to begin with
                        try {
                            refreshMessages();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        //mSpotifyAppRemote.getPlayerApi().play("spotify:playlist:37i9dQZF1DX2sUQwD7tbmL");

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

                    public void onFailure(Throwable throwable) {
                        Log.e("MyActivity", throwable.getMessage(), throwable);
                    }
                });
    }

    // Get the userId from the cached currentUser object
    void startWithCurrentUser() throws ParseException {
        setupMessagePosting();
    }

    // Set up button event handler which posts the entered message to Parse
    void setupMessagePosting() throws ParseException {

        ibSend = (ImageButton) findViewById(R.id.ibSend);
        rvChat = (RecyclerView) findViewById(R.id.rvChat);

        etMessage = (EditText) findViewById(R.id.etMessage);

        ibAttach = findViewById(R.id.ibAttach);
        mMessages = new ArrayList<>();
        mFirstLoad = true;

        final SpotifyUser spotifySender = getCurrentSpotifyUser().get(0);
        matchObject = getIntent().getParcelableExtra("MatchObject");

        final SpotifyUser spotifyReceiver = matchObject;

        mAdapter = new CopyChatAdapter(ChatActivity.this, spotifySender, mMessages, mSpotifyAppRemote);
        rvChat.setAdapter(mAdapter);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatActivity.this);
        linearLayoutManager.setReverseLayout(true);
        rvChat.setLayoutManager(linearLayoutManager);

        ibSend.setBackgroundColor(getResources().getColor(R.color.teal_700));
        ibAttach.setBackgroundColor(getResources().getColor(R.color.teal_700));

        // When send button is clicked, create message object on Parse
        ibSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = etMessage.getText().toString();

                Message message = new Message();
                message.setSender(spotifySender);
                message.setReceiver(spotifyReceiver);


                message.setBody(data);

                message.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.d(TAG, "saved message", e);
                        } else {
                            Log.e(TAG, "Failed to save message", e);
                        }
                    }
                });
                etMessage.setText(null);
            }
        });

        ibAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSongDialog();
            }
        });

    }
    private void showSongDialog() {
        FragmentManager fm = getSupportFragmentManager();
        SongDialogFragment songDialogFragment = SongDialogFragment.newInstance("Select Song");
        songDialogFragment.show(fm, "song_dialog_box");

    }

    void refreshMessages() throws ParseException {
        final SpotifyUser spotifySender = getCurrentSpotifyUser().get(0);
        matchObject = getIntent().getParcelableExtra("MatchObject");
        final SpotifyUser spotifyReceiver = matchObject;
        // Construct query to execute
        ParseQuery<Message> query = ParseQuery.getQuery(Message.class);
        // Configure limit and sort order
        query.setLimit(MAX_CHAT_MESSAGES_TO_SHOW);

        // get the latest 50 messages, order will show up newest to oldest of this group
        query.orderByDescending("createdAt");
        // TODO QUERY ONLY SENDER AND RECEIVER'S MESSAGES
//        query.whereEqualTo("spotifyUserSender", spotifySender);
//        query.whereEqualTo("spotifyUserReceiver", spotifyReceiver);
//        query.whereEqualTo()

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