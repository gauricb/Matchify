package com.example.matchify;

import static com.example.matchify.MainActivity.spotifyService;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.yalantis.library.Koloda;

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

        //sets up the parameters for the recommendations function
        Map<String, Object> options = new HashMap<>(5);
        options.put("seed_genres", "pop, rock, road-trip, new-release");
//        options.put("seed_artists", "5GnnSrwNCGyfAU4zuIytiS");
//        options.put("seed_tracks", "2yGk5A4oipC4jvDLIVlQIJ");

        spotifyService.getRecommendations(options, new Callback<Recommendations>() {
            @Override
            public void success(Recommendations recommendations, Response response) {

                for (int i = 0; i < 10; i++) {
                    songs.add(new Song(recommendations.tracks.get(i).name, recommendations.tracks.get(i).artists.get(0).name,
                            recommendations.tracks.get(i).album.images.get(0).url));
                }

                adapter = new SongCardsAdapter(getContext(), songs);

                koloda.setAdapter(adapter);
                /*
                TO DO: SET ON SWIPE RIGHT LISTENER

                */


            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("cannot produce recommendations", error.toString());
            }
        });



    }
}
