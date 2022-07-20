package com.example.matchify.fragments;

import static com.example.matchify.MainActivity.spotifyService;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.matchify.adapters.ProfileAdapter;
import com.example.matchify.R;
import com.example.matchify.models.Song;
import com.example.matchify.models.SpotifyUser;
import com.example.matchify.databinding.FragmentProfileBinding;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.UserPrivate;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ProfileFragment extends Fragment {

    public static final String TAG = "Profile Fragment";
    private RecyclerView rvLikedSongs;
    protected List<Song> likedSongs;
    protected ProfileAdapter adapter;
    public ProfileFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        FragmentProfileBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);

        spotifyService.getMe(new Callback<UserPrivate>() {
            @Override
            public void success(UserPrivate userPrivate, Response response) {
                binding.textViewUsername.setText(userPrivate.display_name);
                Glide.with(ProfileFragment.this).load(userPrivate.images.get(0).url).into(binding.imageViewProfilePic);
            }
            @Override
            public void failure(RetrofitError error) {
            }
        });



        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvLikedSongs = view.findViewById(R.id.rvLikedSongs);



        likedSongs = new ArrayList<>();
        adapter = new ProfileAdapter(getContext(), likedSongs);
        rvLikedSongs.setLayoutManager(new LinearLayoutManager(getContext()));
        rvLikedSongs.setAdapter(adapter);

        ParseQuery<Song> query = ParseQuery.getQuery(Song.class);
        query.whereEqualTo("songUser", ParseUser.getCurrentUser());
        query.setLimit(20);
        query.addDescendingOrder("createdAt");

        ParseQuery<SpotifyUser> spotifyUserParseQuery = ParseQuery.getQuery("SpotifyUser");
        List<SpotifyUser> currentUser = new ArrayList<>();
        spotifyUserParseQuery.whereEqualTo("songUser", ParseUser.getCurrentUser());
        try {
            currentUser = spotifyUserParseQuery.find();
            Log.e(TAG, "!!!!" + currentUser.size());
        } catch (ParseException e) {
            e.printStackTrace();
        }


        List<SpotifyUser> finalCurrentUser = currentUser;
        query.findInBackground(new FindCallback<Song>() {
            @Override
            public void done(List<Song> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting liked songs", e);
                    return;
                }


                likedSongs.addAll(objects);
                //add these songs to likedSongs field in current user SpotifyUser
                String[] userLikedSongs = new String[likedSongs.size()];
                for (int i = 0; i < userLikedSongs.length; i++) {
                    userLikedSongs[i] = likedSongs.get(i).getParseSongName();
                }
                finalCurrentUser.get(0).setLikedSongs(userLikedSongs);
                //finalCurrentUser.get(0).setUserGenres();
                finalCurrentUser.get(0).saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Log.d("liked song saved! ", "******");
                    }
                });


                adapter.notifyDataSetChanged();

            }
        });



    }


}
