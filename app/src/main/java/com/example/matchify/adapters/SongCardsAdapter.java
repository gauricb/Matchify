package com.example.matchify.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.matchify.R;
import com.example.matchify.models.Song;


import java.util.List;

public class SongCardsAdapter extends BaseAdapter {

    private Context context;
    private List<Song> songs;

    public SongCardsAdapter(Context context, List<Song> songs) {
        this.context = context;
        this.songs = songs;
    }

    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Object getItem(int position) {
        return songs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

         View view;
         if (convertView == null) {
             view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_card_two, parent, false);
         }
         else {
             view = convertView;
         }

        ((TextView) view.findViewById(R.id.trackName)).setText(songs.get(position).getSongName());
        ((TextView) view.findViewById(R.id.artistName)).setText(songs.get(position).getArtistName());
        ImageView albumCover = view.findViewById(R.id.albumCover);
        Glide.with(context).load(songs.get(position).albumCoverUrl).into(albumCover);

        return view;
    }
}
