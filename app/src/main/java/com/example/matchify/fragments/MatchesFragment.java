package com.example.matchify.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.matchify.adapters.ChatSongAdapter;
import com.example.matchify.adapters.MatchAdapter;
import com.example.matchify.R;
import com.example.matchify.models.Match;
import com.example.matchify.models.SpotifyUser;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MatchesFragment extends Fragment {

    private List<SpotifyUser> matches;
    private List<Match> storedMatches;
    private RecyclerView rvMatches;
    protected MatchAdapter adapter;

    public static final String TAG = "MatchesFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_match, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvMatches = view.findViewById(R.id.rvMatches);

        matches = new ArrayList<>();
        adapter = new MatchAdapter(getContext(), matches);
        rvMatches.setLayoutManager(new LinearLayoutManager(getContext()));
        rvMatches.setAdapter(adapter);

        generateMatch();

    }

    public void generateMatch() {
        //pull all your matches from the Match class on Parse
        //loop through spotify users
        ParseQuery<SpotifyUser> query = ParseQuery.getQuery(SpotifyUser.class);
        query.findInBackground(new FindCallback<SpotifyUser>() {
            @Override
            public void done(List<SpotifyUser> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting users", e);
                    return;
                }
                // initializing parameters of the match algo
                JSONArray myFavoriteArtists = new JSONArray();
                JSONArray myFavoriteTracks = new JSONArray();
                JSONArray myLikedSongs = new JSONArray();

                ParseQuery<SpotifyUser> query = ParseQuery.getQuery("SpotifyUser");
                List<SpotifyUser> obj = new ArrayList<>();
                List<SpotifyUser> obj2 = new ArrayList<>();

                try {
                    query.whereEqualTo("songUser", ParseUser.getCurrentUser());
                    obj = query.find(); //the current user object size should be 1
                    myFavoriteArtists = obj.get(0).getTopArtists();
                    myFavoriteTracks = obj.get(0).getTopTracks();
                    myLikedSongs = obj.get(0).getLikedSongs();
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }

                query.whereNotEqualTo("songUser", ParseUser.getCurrentUser());
                try {
                    obj2 = query.find(); //has all the objects that are not the current user (potential matches)

                } catch (ParseException ex) {
                    ex.printStackTrace();
                }



                double compatibilityScore = 0.0;

                double likedSongScore = 0.0;
                double artistScore = 0.0;
                double trackScore = 0.0;
                double ageScore = 0.0;
                double locationScore = 0.0;


                for (int i = 0; i < obj2.size(); i++) {

                    try {
                        likedSongScore = getScore(myLikedSongs, obj2.get(i).getLikedSongs());
                        artistScore = getScore(myFavoriteArtists, obj2.get(i).getTopArtists());
                        trackScore = getScore(myFavoriteTracks, obj2.get(i).getTopTracks());
                        locationScore = getDistanceScore(obj.get(0), obj2.get(i), obj.get(0).getUserLocationRange()) ;
                        ageScore = getAgeScore(obj2.get(i).getUserAge(), obj.get(0).getUserAgeRange());


                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                    compatibilityScore = (likedSongScore * 0.1 + artistScore * 0.2 + trackScore * 0.1 + 0.2 * locationScore + ageScore * 0.2);
                    // send compatibility percent to adapter
                    Intent intent1 = new Intent(getContext(), MatchAdapter.class);
                    intent1.putExtra("compat", Double.toString(compatibilityScore));

                    Log.e(TAG, "##########" + compatibilityScore);
                    if (compatibilityScore < 50) {
                        matches.add(obj2.get(i));
                    }
                }

                adapter.notifyDataSetChanged();
            }
        });

    }

    public int numCommonItems(JSONArray arr1, JSONArray arr2) throws JSONException {
        // find the number of common items btw 2 arrays
        Set<String> set1 = new HashSet<>();
        Set<String> set2 = new HashSet<>();
        for (int i = 0; i < arr1.length(); i++) {
            set1.add(arr1.getString(i));
        }

        for (int i = 0; i < arr2.length(); i++) {
            set2.add(arr2.getString(i));
        }
        set1.retainAll(set2);
        return set1.size();
    }

    public double getScore(JSONArray arr1, JSONArray arr2) throws JSONException {
        int numCommonItems = numCommonItems(arr1, arr2);
        int actualNumItems = 0;

        if (arr1.length() > arr2.length()) {
            actualNumItems = arr1.length();
        }
        else {
            actualNumItems = arr2.length();
        }

        int score = numCommonItems /  actualNumItems;
        return score * 100;

    }

    public double getAgeScore(int matchAge, int ageRange) {

        if (matchAge < ageRange) {
            return 1.0 * 100;
        }

        return 0.0;
    }

    public double getDistanceScore(SpotifyUser me, SpotifyUser anotherUser, int locationRange) {

        double distance =  me.getUserLocation().distanceInMilesTo(anotherUser.getUserLocation());
        if (distance < locationRange) {
            return 1.0 * 100;
        }
        return 0.0;
    }

}
