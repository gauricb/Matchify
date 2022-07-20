package com.example.matchify.adapters;

import static com.example.matchify.MainActivity.currentSpotifyUser;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.matchify.R;
import com.example.matchify.models.Match;
import com.example.matchify.models.Message;
import com.example.matchify.models.Song;
import com.example.matchify.models.SpotifyUser;
import com.klinker.android.link_builder.Link;
import com.klinker.android.link_builder.LinkBuilder;
import com.parse.ParseException;
import com.parse.SaveCallback;

import java.util.List;

public class ChatSongAdapter extends RecyclerView.Adapter<ChatSongAdapter.ViewHolder>{

    private Context context;
    private List<Song> likedSongs;



    public static final String TAG = "ChatSongAdapter";

    public ChatSongAdapter(Context context, List<Song> likedSongs) {
        this.context = context;
        this.likedSongs = likedSongs;

    }


    @NonNull
    @Override
    public ChatSongAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_liked_song, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatSongAdapter.ViewHolder holder, int position) {
        Song likedSong = likedSongs.get(position);
        holder.bind(likedSong);
    }

    @Override
    public int getItemCount() {
        return likedSongs.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView trackName;
        private TextView artistName;
        private ImageView albumCover;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            trackName = itemView.findViewById(R.id.likedTrackName);
            artistName = itemView.findViewById(R.id.outgoingMessage);
            albumCover = itemView.findViewById(R.id.likedAlbumCover);

            Activity activity = unwrap(itemView.getContext());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    // make sure the position actually exists in the view
                    if (position != RecyclerView.NO_POSITION) {
                        Song song = likedSongs.get(position);

                        Toast.makeText(context, "song name is " + song.getParseSongName(), Toast.LENGTH_LONG).show();

                        Message message = new Message();
                        message.setSongWidget(song);

                        final SpotifyUser spotifySender = currentSpotifyUser;

                        SpotifyUser receiver = activity.getIntent().getParcelableExtra("MatchObject");
                        try {
                            Log.e(TAG, "------!!!!!" + receiver.fetchIfNeeded().getString("username"));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        message.setSender(spotifySender);

                        message.setReceiver(receiver);

                        message.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {

                                } else {
                                    Log.e(TAG, "Failed to save message", e);
                                }
                            }
                        });


                    }
                }
            });
        }

        private Activity unwrap(Context context) {
            while (!(context instanceof Activity) && context instanceof ContextWrapper) {
                context = ((ContextWrapper) context).getBaseContext();
            }
            return (Activity) context;
        }

        public void bind(Song song) {
            trackName.setText(song.getParseSongName());
            artistName.setText(song.getParseArtistName());
            Glide.with(context).load(song.getParseAlbumCover()).into(albumCover);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            //make sure the position actually exists in the view
            if (position != RecyclerView.NO_POSITION) {
                Song song = likedSongs.get(position);
                Log.e(TAG, "song position is " + position);
            }
        }
    }

    public void clear() {
        likedSongs.clear();
        notifyDataSetChanged();
    }
    public void addAll(List<Song> songs) {
        likedSongs.addAll(songs);
        notifyDataSetChanged();
    }
}

