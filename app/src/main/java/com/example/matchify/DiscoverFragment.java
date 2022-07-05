package com.example.matchify;

import static com.example.matchify.MainActivity.spotifyService;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.yalantis.library.Koloda;
import com.yalantis.library.KolodaListener;

import org.parceler.Parcels;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import kaaes.spotify.webapi.android.models.Recommendations;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class DiscoverFragment extends Fragment {

    private SongCardsAdapter adapter;
    private List<Song> songs;
    Koloda koloda;
    private List<Song> likedSongs;
    public static final String TAG = "DiscoverFragment";


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
        likedSongs = new ArrayList<>();

        //sets up the parameters for the recommendations function
        Map<String, Object> options = new HashMap<>(5);
        options.put("seed_genres", "kids");
        // options.put("seed_artists", "5GnnSrwNCGyfAU4zuIytiS");
        // options.put("seed_tracks", "2yGk5A4oipC4jvDLIVlQIJ");

        spotifyService.getRecommendations(options, new Callback<Recommendations>() {
            @Override
            public void success(Recommendations recommendations, Response response) {
//                Log.d(TAG, recommendations.tracks.get(0).name);
                for (int i = 0; i < recommendations.tracks.size(); i++) {
                    songs.add(new Song(recommendations.tracks.get(i).name, recommendations.tracks.get(i).artists.get(0).name,
                            recommendations.tracks.get(i).album.images.get(0).url));

                }

                adapter = new SongCardsAdapter(getContext(), songs);

                koloda.setAdapter(adapter);

                KolodaListener kolodaListener = new KolodaListener() {
                    @Override
                    public void onNewTopCard(int i) {
                        return;
                    }

                    @Override
                    public void onCardDrag(int i, @NonNull View view, float v) {
                        return;
                    }

                    @Override
                    public void onCardSwipedLeft(int i) {
                        Toast.makeText(getContext(), "Never mind", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCardSwipedRight(int i) {
                        Toast.makeText(getContext(), "Added to liked songs!" + (i+1), Toast.LENGTH_SHORT).show();

                        likedSongs.add(songs.get(i+1));
                        Log.d("this was added: ", likedSongs.get(likedSongs.size()-1).getSongName());

                        //TODO SEND EACH LIKED SONG TO PROFILE FRAGMENT
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        ProfileFragment profileFragment = new ProfileFragment();

                        // using bundle to pass this liked song
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("likedSongObject", likedSongs.get(likedSongs.size()-1));
                        profileFragment.setArguments(bundle);
                        transaction.commit();
                    }
                    @Override
                    public void onClickRight(int i) {
                        return;
                    }

                    @Override
                    public void onClickLeft(int i) {
                        return;
                    }

                    @Override
                    public void onCardSingleTap(int i) {
                        return;
                    }

                    @Override
                    public void onCardDoubleTap(int i) {
                        return;
                    }

                    @Override
                    public void onCardLongPress(int i) {
                        return;
                    }

                    @Override
                    public void onEmptyDeck() {
                        return;
                    }

                };


                koloda.setKolodaListener(kolodaListener);




            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("cannot produce recommendations", error.toString());
            }
        });



    }
}
