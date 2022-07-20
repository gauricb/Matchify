package com.example.matchify.adapters;

import static com.example.matchify.MainActivity.currentSpotifyUser;
import static com.example.matchify.MainActivity.mSpotifyAppRemote;
import static com.example.matchify.models.Message.MessageIncoming;
import static com.example.matchify.models.Message.MessageOutgoing;
import static com.example.matchify.models.Message.MessageSongWidget;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.matchify.ChatActivity;
import com.example.matchify.DoubleClick;
import com.example.matchify.R;
import com.example.matchify.models.Message;
import com.example.matchify.models.Song;
import com.example.matchify.models.SpotifyUser;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import java.util.List;

public class CopyChatAdapter extends RecyclerView.Adapter{

    private List<Message> messages;
    private Context mContext;
    private SpotifyUser sender;
    public static final String TAG = "CopyChatAdapter";

    SpotifyAppRemote mSpotifyAppRemote;

    public CopyChatAdapter(Context context, SpotifyUser sender, List<Message> messages, SpotifyAppRemote mSpotifyAppRemote) {
        this.messages = messages;
        this.mContext = context;
        this.sender = sender;
        this.mSpotifyAppRemote = mSpotifyAppRemote;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);

        if (message.getBody() == null) {
            return MessageSongWidget;
        }
        else if (message.getBody() != null && message.getSender() == sender){
            return MessageOutgoing;
        }
        else  {
            return MessageIncoming;
        }

    }

    public class IncomingMessageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageOther;
        TextView body;
        TextView name;

        public IncomingMessageViewHolder(View itemView) {
            super(itemView);
            imageOther = (ImageView) itemView.findViewById(R.id.ivProfileOther);
            body = (TextView) itemView.findViewById(R.id.incomingMessage);
            name = (TextView) itemView.findViewById(R.id.tvName);
        }
        public void bindMessage(Message message) {
            try {
                Glide.with(mContext)
                        .load(message.getSender().fetchIfNeeded().getString("profileImage"))
                        .circleCrop() // create an effect of a round profile picture
                        .into(imageOther);
                name.setText(message.getSender().fetchIfNeeded().getString("username")); // in addition to message show user ID

            } catch (ParseException e) {
                Log.v("LOG_TAG", e.toString());
                e.printStackTrace();
            }
            body.setText(message.getBody());

        }

    }

    public class OutgoingMessageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageMe;
        TextView body;

        public OutgoingMessageViewHolder(View itemView) {
            super(itemView);
            imageMe = (ImageView)itemView.findViewById(R.id.ivProfileMe);
            body = (TextView)itemView.findViewById(R.id.outgoingMessage);
        }
        public void bindMessage(Message message) {
            try {
                Glide.with(mContext)
                        .load(message.getSender().fetchIfNeeded().getString("profileImage"))
                        .circleCrop() // create an effect of a round profile picture
                        .into(imageMe);
            } catch (ParseException e) {
                Log.v("LOG_TAG", e.toString());
                e.printStackTrace();
            }
            body.setText(message.getBody());
        }

    }

    public class MessageSongWidgetViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView trackName;
        private TextView artistName;
        private ImageView albumCover;
        private Button addButton;


        // button - add to like songs, only visible if you're not user who sent it
        // open song on spotify
        // if this song is not in your liked songs then add it

        public MessageSongWidgetViewHolder(@NonNull View itemView) {
            super(itemView);
            trackName = itemView.findViewById(R.id.likedTrackName);
            artistName = itemView.findViewById(R.id.outgoingMessage);
            albumCover = itemView.findViewById(R.id.likedAlbumCover);
            addButton = itemView.findViewById(R.id.buttonAdd);


        }
        public void bind(Message song)  {

            try {
                trackName.setText(song.getSongWidget().fetchIfNeeded().getString("songName"));
                artistName.setText(song.getSongWidget().fetchIfNeeded().getString("artistName"));
                Glide.with(mContext).load(song.getSongWidget().fetchIfNeeded().getString("albumCover")).into(albumCover);

            } catch (ParseException e) {
                Log.v("LOG_TAG", e.toString());
                e.printStackTrace();
            }

            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "Song added to your playlist!" + song.getMSongName(),
                            Toast.LENGTH_SHORT).show();
                    SpotifyUser match = song.getReceiver();
                    Song newLikedSongForMatch = new Song();
                    try {
                        newLikedSongForMatch.setSongName(song.getSongWidget().fetchIfNeeded().getString("songName"));
                        newLikedSongForMatch.setArtistName(song.getSongWidget().fetchIfNeeded().getString("artistName"));
                        newLikedSongForMatch.setAlbumCover(song.getSongWidget().fetchIfNeeded().getString("albumCover"));
                        newLikedSongForMatch.setSongLink(song.getSongWidget().fetchIfNeeded().getString("link"));
                        newLikedSongForMatch.setSongUri(song.getSongWidget().fetchIfNeeded().getString("uri"));
                        newLikedSongForMatch.setSongUser(match.fetchIfNeeded().getParseUser("songUser"));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    // TODO check if this particular song exists in a user's liked song playlist already!

                    newLikedSongForMatch.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                Log.d("liked song saved! ", "mememe");
                            }
                        });

                }
            });


            itemView.setOnClickListener(new DoubleClick() {
                @Override
                public void onDoubleClick() {
                    Toast.makeText(mContext, "Playing song!" + song.getMSongName(),
                            Toast.LENGTH_SHORT).show();
                    try {
                        mSpotifyAppRemote.getPlayerApi().play(song.getSongWidget().fetchIfNeeded().getString("uri"));
                    }
                    catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(mContext, "song widget clicked",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case MessageIncoming:
                View messageIncoming = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_incoming, parent, false);
                return new IncomingMessageViewHolder(messageIncoming);
            case MessageOutgoing:
                View messageOutgoing = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_outgoing, parent, false);
                return new OutgoingMessageViewHolder(messageOutgoing);
            case MessageSongWidget:
                View messageSongWidget = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_message_outgoing, parent, false);
                return new MessageSongWidgetViewHolder(messageSongWidget);
            default:
                return null;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);
        if (message.getBody() == null) {
            ((MessageSongWidgetViewHolder)holder).bind(messages.get(position));
        }
        if (message.getBody() != null && message.getSender() == sender){

            ((OutgoingMessageViewHolder)holder).bindMessage(messages.get(position));
        }
        if (message.getBody() != null && message.getSender() != sender) {

            ((IncomingMessageViewHolder)holder).bindMessage(messages.get(position));
        }

    }


    @Override
    public int getItemCount() {
        return messages.size();
    }
}
