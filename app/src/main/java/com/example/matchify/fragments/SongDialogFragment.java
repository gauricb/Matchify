package com.example.matchify.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.matchify.ChatActivity;
import com.example.matchify.R;
import com.example.matchify.adapters.ChatSongAdapter;
import com.example.matchify.models.Song;
import com.example.matchify.models.SpotifyUser;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class SongDialogFragment extends DialogFragment {

    ChatSongAdapter chatSongAdapter;
    RecyclerView songRv;
    protected List<Song> likedSongs;


    public static final String TAG = "SongDialogFragment";


    public SongDialogFragment() {
        // Empty constructor required for DialogFragment
    }

    public static SongDialogFragment newInstance(String title) {
        SongDialogFragment songDialogFragment = new SongDialogFragment();
        Bundle args = new Bundle();
        args.putString("Select a song", title);
        songDialogFragment.setArguments(args);
        return songDialogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        return inflater.inflate(R.layout.song_dialog_box, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        likedSongs = new ArrayList<>();
        chatSongAdapter = new ChatSongAdapter(getContext(), likedSongs);
        songRv = view.findViewById(R.id.dialog_recycler_view);
        songRv.setLayoutManager(new LinearLayoutManager(getContext()));
        songRv.setAdapter(chatSongAdapter);

        fetchSongs(likedSongs);
        String title = getArguments().getString("title", "Select a Song");
        getDialog().setTitle(title);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

    }

    public void fetchSongs(List<Song> likedSongs) {

        ParseQuery<Song> query = ParseQuery.getQuery(Song.class);
        query.whereEqualTo("songUser", ParseUser.getCurrentUser());
        query.setLimit(20);
        query.addDescendingOrder("createdAt");

        query.findInBackground(new FindCallback<Song>() {
            @Override
            public void done(List<Song> objects, ParseException e) {
                likedSongs.addAll(objects);

                chatSongAdapter.notifyDataSetChanged();
            }
        });

    }

}
