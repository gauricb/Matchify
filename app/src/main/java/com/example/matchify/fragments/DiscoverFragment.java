package com.example.matchify.fragments;

import static com.example.matchify.MainActivity.currentSpotifyUser;
import static com.example.matchify.MainActivity.mSpotifyAppRemote;
import static com.example.matchify.MainActivity.spotifyService;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
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

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.models.Recommendations;
import kaaes.spotify.webapi.android.models.SeedsGenres;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class DiscoverFragment extends Fragment {

    private SongCardsAdapter adapter;
    Koloda koloda;

    private List<Song> songs;
    private List<Song> likedSongs;
    private List<Song> playingSongs;

    private ImageButton rejectButton;
    private ImageButton acceptButton;
    private ImageButton playButton;
    private boolean paused = true;

    String[] genres = new String[]{"acoustic", "afrobeat", "alt-rock", "alternative", "ambient", "anime", "black-metal", "bluegrass", "blues", "bossanova", "brazil", "breakbeat", "british", "cantopop", "chicago-house", "children", "chill", "classical", "club", "comedy", "country", "dance", "dancehall", "death-metal", "deep-house", "detroit-techno", "disco", "disney",
            "drum-and-bass", "dub", "dubstep", "edm", "electro", "electronic", "emo", "folk", "forro", "french", "funk", "garage", "german", "gospel", "goth",
            "grindcore", "groove", "grunge", "guitar", "happy", "hard-rock", "hardcore", "hardstyle", "heavy-metal", "hip-hop", "holidays", "honky-tonk", "house", "idm", "indian", "indie", "indie-pop", "industrial", "iranian", "j-dance", "j-idol", "j-pop", "j-rock", "jazz", "k-pop", "kids", "latin", "latino", "malay", "mandopop", "metal", "metal-misc",
            "metalcore", "minimal-techno", "movies", "mpb", "new-age", "new-release", "opera", "pagode", "party", "philippines-opm", "piano", "pop", "pop-film", "post-dubstep", "power-pop", "progressive-house", "psych-rock", "punk", "punk-rock", "r-n-b", "rainy-day", "reggae", "reggaeton", "road-trip", "rock", "rock-n-roll", "rockabilly", "romance", "sad",
            "salsa", "samba", "sertanejo", "show-tunes", "singer-songwriter", "ska", "sleep", "songwriter", "soul", "soundtracks", "spanish", "study", "summer", "swedish", "synth-pop", "tango", "techno", "trance", "trip-hop", "turkish", "work-out", "world-music"
    };

    List<String> chosenGenres = new ArrayList<String>();


    int position = -1;

    public static final String TAG = "DiscoverFragment";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        //inflate the fragment
        return inflater.inflate(R.layout.fragment_discover, container, false);
        
    }



    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem item1 = menu.findItem(R.id.buttonPrefs);
        if (item1 != null) {
            item1.setVisible(false);
        }

        MenuItem item2 = menu.findItem(R.id.buttonGenres);
        if (item2 != null) {
            item2.setVisible(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.buttonGenres) {
            showGenreDialog();
            //generateRecommendations();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        koloda = view.findViewById(R.id.stack_view);
        rejectButton = view.findViewById(R.id.buttonReject);
        acceptButton = view.findViewById(R.id.buttonAccept);
        playButton = view.findViewById(R.id.buttonPlay);

        songs = new ArrayList<>();
        likedSongs = new ArrayList<>();
        playingSongs = new ArrayList<>();

        showGenreDialog();

        try {
            generateRecommendations();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void generateRecommendations() throws JSONException {

        Log.e(TAG, "*********" + getGenres());
        spotifyService.getRecommendations(getGenres(), new Callback<Recommendations>() {
            @Override
            public void success(Recommendations recommendations, Response response) {
                for (int i = 0; i < recommendations.tracks.size(); i++) {
                    songs.add(new Song(recommendations.tracks.get(i).name, recommendations.tracks.get(i).artists.get(0).name,
                            recommendations.tracks.get(i).album.images.get(0).url, recommendations.tracks.get(i).uri, recommendations.tracks.get(i).external_urls.get("spotify")));
                }

                adapter = new SongCardsAdapter(getContext(), songs);

                koloda.setAdapter(adapter);

                rejectButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        koloda.onButtonClick(false);
                    }
                });

                acceptButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        koloda.onButtonClick(true);

                    }
                });

                playButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (paused) {
                            playCurrentSong(position);
                            playButton.setImageResource(R.drawable.ic_baseline_pause_60);
                            paused = false;
                        }
                        else {
                            pauseCurrentSong(position);
                            playButton.setImageResource(R.drawable.ic_baseline_play_arrow_30);
                            paused = true;
                        }

                    }
                });



                KolodaListener kolodaListener = new KolodaListener() {

                    @Override
                    public void onNewTopCard(int i) {
                        position = i;
                        pauseCurrentSong(position);
                        playButton.setImageResource(R.drawable.ic_baseline_play_arrow_30);
                        paused = true;
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
                        Toast.makeText(getContext(), "Added to liked songs!", Toast.LENGTH_SHORT).show();
                        try {
                            saveLikedSong(i);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                    @Override
                    public void onClickRight(int i) {
                        Toast.makeText(getContext(), "Added to liked songs!", Toast.LENGTH_SHORT).show();
                        try {
                            saveLikedSong(i);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onClickLeft(int i) {
                        Toast.makeText(getContext(), "Never mind", Toast.LENGTH_SHORT).show();
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

    public Map<String, Object> getGenres() throws JSONException {
        Map<String, Object> options = new HashMap<>(10);

        String g = "";

        JSONArray theGenres = currentSpotifyUser.getChosenGenres();

        for (int i = 0; i < theGenres.length(); i++) {
            if (theGenres.get(i) != theGenres.get(theGenres.length()-1)) {
                g += theGenres.get(i) + ", ";
            }
            else {
                g += theGenres.get(i);
            }
        }

        String gn = g;
        options.put("seed_genres", gn);
        return options;
    }

    private void playCurrentSong(int i) {

        if (i == -1) {
            mSpotifyAppRemote.getPlayerApi().play(songs.get(0).getSongUri());
        }
        else {
            mSpotifyAppRemote.getPlayerApi().play(songs.get(i+1).getSongUri());
        }

    }

    private void pauseCurrentSong(int i) {
        if (i == -1) {
            mSpotifyAppRemote.getPlayerApi().pause();
        }
        else {
            mSpotifyAppRemote.getPlayerApi().pause();
        }
    }

    private void showGenreDialog() {

        currentSpotifyUser.setChosenGenres(new String[0]);
                currentSpotifyUser.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Log.e(TAG, "genres cleared");
                    }
                });

        boolean[] checkedItems = new boolean[genres.length];
        List<String> selectedItems = Arrays.asList(genres);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Choose up to 5 genres");

        builder.setMultiChoiceItems(genres, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                checkedItems[which] = isChecked;
                String currentItem = selectedItems.get(which);
            }
        });

        builder.setCancelable(false);

        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (int i = 0; i < checkedItems.length; i++) {
                    if (checkedItems[i]) {
                        chosenGenres.add(selectedItems.get(i));
                    }
                }
                currentSpotifyUser.setChosenGenres(chosenGenres.toArray(new String[0]));
                currentSpotifyUser.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Log.e(TAG, "chosen genres are saved");
                    }
                });
                try {
                    generateRecommendations();
                } catch (JSONException e) {
                    e.printStackTrace();
                }




            }
        });

        builder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setNeutralButton("CLEAR ALL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (int i = 0; i < checkedItems.length; i++) {
                    checkedItems[i] = false;
                }
            }
        });

        builder.create();

        AlertDialog alertDialog = builder.create();
        alertDialog.show();


    }


}
