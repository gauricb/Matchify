package com.example.matchify;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.yalantis.library.Koloda;

import java.util.ArrayList;
import java.util.List;


public class DiscoverFragment extends Fragment {

    private SongCardsAdapter adapter;
    private List<Song> songs;
    Koloda koloda;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //inflate the fragment
        return inflater.inflate(R.layout.fragment_discover, container, false);
        
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        koloda = view.findViewById(R.id.stack_view);
        songs = new ArrayList<>();

        adapter = new SongCardsAdapter(getContext(), songs);
        koloda.setAdapter(adapter);


    }
}
