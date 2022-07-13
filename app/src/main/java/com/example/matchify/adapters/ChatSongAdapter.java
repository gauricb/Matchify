package com.example.matchify.adapters;

import static com.example.matchify.MainActivity.currentSpotifyUser;
import static com.example.matchify.MainActivity.mSpotifyAppRemote;
import static com.example.matchify.MainActivity.spotifyService;

import android.content.Context;
import android.content.Intent;
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
import com.example.matchify.ChatActivity;
import com.example.matchify.R;
import com.example.matchify.models.Message;
import com.example.matchify.models.Song;
import com.example.matchify.models.SpotifyUser;
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
            artistName = itemView.findViewById(R.id.likedArtistName);
            albumCover = itemView.findViewById(R.id.likedAlbumCover);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    //make sure the position actually exists in the view
                    if (position != RecyclerView.NO_POSITION) {
                        Song song = likedSongs.get(position);
                        //Log.e(TAG, "song position is " + position);
                        Toast.makeText(context, "song name is " + song.getParseSongName(), Toast.LENGTH_LONG).show();
                        //TODO PASS SELECTED TO CHAT ACTIVITY
                        Message message = new Message();
                        final SpotifyUser spotifySender = currentSpotifyUser;
                        message.setSender(spotifySender);
                        message.setBody(song.getParseSongName());


                        message.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Toast.makeText(context, "Successfully created message on Parse",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.e(TAG, "Failed to save message", e);
                                }
                            }
                        });

                        // PLAY THE SONG FROM SPOTIFY
                        mSpotifyAppRemote.getPlayerApi().play("spotify:playlist:37i9dQZF1DX7K31D69s4M1");

                        Log.e(TAG, "starting to play");

//                        Intent intent = new Intent(context, ChatActivity.class);
//                        String thisMatchName = song.getParseSongName();
//                        intent.putExtra("SongName", thisMatchName);
//                        intent.putExtra("SongObject", song);
                        //context.startActivity(intent);

                    }
                }
            });
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
                //Toast.makeText(context, "song position is " + song.getParseSongName(), Toast.LENGTH_LONG).show();
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

