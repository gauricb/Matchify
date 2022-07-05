package com.example.matchify;

import static com.example.matchify.MainActivity.spotifyService;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.matchify.databinding.FragmentProfileBinding;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.SavedTrack;
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

        FragmentProfileBinding binding = DataBindingUtil.inflate(inflater,R.layout.fragment_profile, container, false);

        //set the username
        spotifyService.getMe(new Callback<UserPrivate>() {
            @Override
            public void success(UserPrivate userPrivate, Response response) {
                binding.textViewUsername.setText(userPrivate.display_name);
            }
            @Override
            public void failure(RetrofitError error) {}
        });

        spotifyService.getMe(new Callback<UserPrivate>() {
            @Override
            public void success(UserPrivate userPrivate, Response response) {
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
        //test if the recycler view is working

//        spotifyService.getMySavedTracks(new Callback<Pager<SavedTrack>>() {
//            @Override
//            public void success(Pager<SavedTrack> savedTrackPager, Response response) {
//                for (int i = 0; i < savedTrackPager.items.size(); i++) {
//                    likedSongs.add(new Song(savedTrackPager.items.get(i).track.name, savedTrackPager.items.get(i).track.artists.get(0).name,
//                            savedTrackPager.items.get(i).track.album.images.get(0).url));
//
//                }
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//
//            }
//        });

        Bundle bundle = this.getArguments();

        if (bundle != null) {
            Song receivedSong = bundle.getParcelable("likedSongObject");
            likedSongs.add(new Song(receivedSong.getSongName(), receivedSong.getArtistName(), receivedSong.albumCoverUrl));

            //Log.d("this was received: ", likedSongs.get(0).getSongName());

        }
        adapter.notifyDataSetChanged();

    }
}
