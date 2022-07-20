package com.example.matchify.fragments;

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

import com.example.matchify.R;
import com.example.matchify.models.Song;
import com.example.matchify.adapters.SongCardsAdapter;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.yalantis.library.Koloda;
import com.yalantis.library.KolodaListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        generateRecommendations();

    }

    private void generateRecommendations() {

        // TODO ASK USER FOR GENRES HERE

        spotifyService.getRecommendations(getGenres(), new Callback<Recommendations>() {
            @Override
            public void success(Recommendations recommendations, Response response) {
                for (int i = 0; i < recommendations.tracks.size(); i++) {
                    songs.add(new Song(recommendations.tracks.get(i).name, recommendations.tracks.get(i).artists.get(0).name,
                            recommendations.tracks.get(i).album.images.get(0).url, recommendations.tracks.get(i).uri, recommendations.tracks.get(i).external_urls.get("spotify")));
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


                        try {
                            saveLikedSong(i);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }


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

    private void saveLikedSong(int i) throws ParseException {
        likedSongs.add(songs.get(i+1));
        Song likedSong = new Song();

        likedSong.setSongName(likedSongs.get(likedSongs.size()-1).getSongName());
        likedSong.setArtistName(likedSongs.get(likedSongs.size()-1).getArtistName());
        likedSong.setAlbumCover(likedSongs.get(likedSongs.size()-1).getAlbumCoverUrl());
        likedSong.setSongUser(ParseUser.getCurrentUser());
        likedSong.setSongUri(likedSongs.get(likedSongs.size()-1).getSongUri());
        likedSong.setSongLink(likedSongs.get(likedSongs.size()-1).getSongLink());


        likedSong.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.d("liked song saved! ", "mememe");
            }
        });
    }

    public Map<String, Object> getGenres() {
        Map<String, Object> options = new HashMap<>(5);
        options.put("seed_genres", "indie-pop");
        return options;
    }

}
