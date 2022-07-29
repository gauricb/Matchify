package com.example.matchify.adapters;

import static com.example.matchify.MainActivity.mSpotifyAppRemote;

import android.content.Context;
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
import com.example.matchify.R;
import com.example.matchify.models.Song;

import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder>{

    private Context context;
    private List<Song> likedSongs;

    public static final String TAG = "ProfileAdapter";

    public ProfileAdapter(Context context, List<Song> likedSongs) {
        this.context = context;
        this.likedSongs = likedSongs;
    }

    @NonNull
    @Override
    public ProfileAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_liked_song, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileAdapter.ViewHolder holder, int position) {
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
        private ImageButton playButton;
        private boolean paused = true;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            trackName = itemView.findViewById(R.id.likedTrackName);
            artistName = itemView.findViewById(R.id.artistName);
            albumCover = itemView.findViewById(R.id.likedAlbumCover);
            playButton = itemView.findViewById(R.id.rvPlaySong);

            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Song song = likedSongs.get(position);
                    if (paused) {
                        playButton.setImageResource(R.drawable.ic_baseline_pause_24);
                        mSpotifyAppRemote.getPlayerApi().play(song.getParseSongUri());
                        paused = false;
                    }
                    else {
                        playButton.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                        mSpotifyAppRemote.getPlayerApi().pause();
                        paused = true;
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

