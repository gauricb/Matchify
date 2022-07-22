package com.example.matchify.adapters;

import static com.example.matchify.models.Message.IncomingMessageSongWidget;
import static com.example.matchify.models.Message.MessageIncoming;
import static com.example.matchify.models.Message.MessageOutgoing;
import static com.example.matchify.models.Message.OutgoingMessageSongWidget;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.matchify.DoubleClick;
import com.example.matchify.R;
import com.example.matchify.models.Message;
import com.example.matchify.models.Song;
import com.example.matchify.models.SpotifyUser;
import com.parse.ParseException;
import com.parse.SaveCallback;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import java.util.List;

public class CopyChatAdapter extends RecyclerView.Adapter{

    private List<Message> messages;
    private Context mContext;
    private SpotifyUser sender;
    public static final String TAG = "CopyChatAdapter";
    private String username = "";


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

        if (message.getBody() == null  && message.getSender().getObjectId().equals(sender.getObjectId())) {
            return OutgoingMessageSongWidget;
        }
        else if(message.getBody() == null  && !message.getSender().getObjectId().equals(sender.getObjectId())) {
            return IncomingMessageSongWidget;

        }
        else if (message.getBody() != null && message.getSender().getObjectId().equals(sender.getObjectId())){
            Log.e(TAG, "current user: " +sender.getUserName());
            return MessageOutgoing;
        }
        else  {
            boolean cond = message.getBody() != null && message.getSender().getObjectId() == sender.getObjectId();
            Log.e(TAG, "!" + message.getSender().getObjectId() + "==" + sender.getObjectId() + "so " + cond );
            return MessageIncoming;
        }

    }

    public class IncomingMessageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageOther;
        TextView body;
        TextView name;

        public IncomingMessageViewHolder(View itemView) {
            super(itemView);
            imageOther = (ImageView) itemView.findViewById(R.id.ivProfileMe);
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

    public class IncomingMessageSongWidgetViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView trackName;
        private TextView artistName;
        private ImageView albumCover;
        private ImageButton addButton;
        ImageView imageOther;

        // button - add to like songs, only visible if you're not user who sent it
        // open song on spotify
        // if this song is not in your liked songs then add it

        public IncomingMessageSongWidgetViewHolder(@NonNull View itemView) {
            super(itemView);
            trackName = itemView.findViewById(R.id.likedTrackName);
            artistName = itemView.findViewById(R.id.outgoingMessage);
            albumCover = itemView.findViewById(R.id.likedAlbumCover);
            addButton = itemView.findViewById(R.id.buttonAdd);
            imageOther = itemView.findViewById(R.id.ivProfileMe);

        }
        public void bind(Message song)  {

            try {
                trackName.setText(song.getSongWidget().fetchIfNeeded().getString("songName"));
                artistName.setText(song.getSongWidget().fetchIfNeeded().getString("artistName"));
                Glide.with(mContext).load(song.getSongWidget().fetchIfNeeded().getString("albumCover")).into(albumCover);
                Glide.with(mContext)
                        .load(song.getSender().fetchIfNeeded().getString("profileImage"))
                        .circleCrop() // create an effect of a round profile picture
                        .into(imageOther);

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
                    addButton.setImageResource(R.drawable.ic_baseline_playlist_add_check_24);
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

    public class OutgoingMessageSongWidgetViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView trackName;
        private TextView artistName;
        private ImageView albumCover;
        private ImageButton playButton;
        ImageView imageMe;

        // button - add to like songs, only visible if you're not user who sent it
        // open song on spotify
        // if this song is not in your liked songs then add it

        public OutgoingMessageSongWidgetViewHolder(@NonNull View itemView) {
            super(itemView);
            trackName = itemView.findViewById(R.id.likedTrackName);
            artistName = itemView.findViewById(R.id.outgoingMessage);
            albumCover = itemView.findViewById(R.id.likedAlbumCover);
            imageMe = itemView.findViewById(R.id.ivProfileMe);

        }
        public void bind(Message song)  {

            try {
                trackName.setText(song.getSongWidget().fetchIfNeeded().getString("songName"));
                artistName.setText(song.getSongWidget().fetchIfNeeded().getString("artistName"));
                Glide.with(mContext).load(song.getSongWidget().fetchIfNeeded().getString("albumCover")).into(albumCover);
                Glide.with(mContext)
                        .load(song.getSender().fetchIfNeeded().getString("profileImage"))
                        .circleCrop() // create an effect of a round profile picture
                        .into(imageMe);

            } catch (ParseException e) {
                Log.v("LOG_TAG", e.toString());
                e.printStackTrace();
            }

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
            case IncomingMessageSongWidget:
                View messageSongWidgetIncoming = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_message_incoming, parent, false);
                return new IncomingMessageSongWidgetViewHolder(messageSongWidgetIncoming);
            case OutgoingMessageSongWidget:
                View messageSongWidgetOutgoing = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_message_outgoing, parent, false);
                return new OutgoingMessageSongWidgetViewHolder(messageSongWidgetOutgoing);
            default:
                return null;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);
        if (message.getBody() == null  && message.getSender().getObjectId().equals(sender.getObjectId())) {
            ((OutgoingMessageSongWidgetViewHolder)holder).bind(messages.get(position));
        }
        else if(message.getBody() == null  && !message.getSender().getObjectId().equals(sender.getObjectId())) {
            ((IncomingMessageSongWidgetViewHolder)holder).bind(messages.get(position));

        }
        else if (message.getBody() != null && message.getSender().getObjectId().equals(sender.getObjectId())){

            ((OutgoingMessageViewHolder)holder).bindMessage(messages.get(position));
        }
        else  {

            ((IncomingMessageViewHolder)holder).bindMessage(messages.get(position));
        }

    }


    @Override
    public int getItemCount() {
        return messages.size();
    }
}
