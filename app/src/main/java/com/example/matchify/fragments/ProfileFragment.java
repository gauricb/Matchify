package com.example.matchify.fragments;

import static com.example.matchify.MainActivity.currentSpotifyUser;
import static com.example.matchify.MainActivity.spotifyService;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.matchify.adapters.ProfileAdapter;
import com.example.matchify.R;
import com.example.matchify.models.Song;
import com.example.matchify.models.SpotifyUser;
import com.example.matchify.databinding.FragmentProfileBinding;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import kaaes.spotify.webapi.android.models.UserPrivate;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ProfileFragment extends Fragment {

    public static final String TAG = "Profile Fragment";
    private RecyclerView rvLikedSongs;
    protected List<Song> likedSongs;
    protected ProfileAdapter adapter;
    int numSongs = 0;
    public ProfileFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        FragmentProfileBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);

        int numMatches = 0;


        if (currentSpotifyUser != null) {
            binding.textViewUsername.setText(currentSpotifyUser.getUserName());
            numMatches = currentSpotifyUser.getUserMatches().length();
            binding.tvNumMatches.setText(Integer.toString(numMatches) + " matches");
            Glide.with(ProfileFragment.this).load(currentSpotifyUser.getUserImage()).into(binding.imageViewProfilePic);
        }


        return binding.getRoot();

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
            item2.setVisible(false);
        }
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvNumSongs = getView().findViewById(R.id.tvNumSongs);


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


                numSongs = likedSongs.size();
                Log.e(TAG, "likes songs size: " + numSongs);
                tvNumSongs.setText(Integer.toString(numSongs) + " favorites");

                //add these songs to likedSongs field in current user SpotifyUser
                String[] userLikedSongs = new String[likedSongs.size()];
                for (int i = 0; i < userLikedSongs.length; i++) {
                    userLikedSongs[i] = likedSongs.get(i).getParseSongName();
                }
                if (ParseUser.getCurrentUser() != null) {
                    finalCurrentUser.get(0).setLikedSongs(userLikedSongs);
                    finalCurrentUser.get(0).saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            Log.d("liked song saved! ", "******");
                        }
                    });
                }


                adapter.notifyDataSetChanged();

            }
        });
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        ((DividerItemDecoration) itemDecoration).setDrawable(new ColorDrawable(getResources().getColor(R.color.grey)));
        rvLikedSongs.addItemDecoration(itemDecoration);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(rvLikedSongs);

    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            switch (direction) {
                case ItemTouchHelper.LEFT:

                    ParseQuery<Song> query = ParseQuery.getQuery(Song.class);
                    query.whereEqualTo("uri", likedSongs.get(position).getParseSongUri());
                    List<SpotifyUser> currentUser = new ArrayList<>();
                    try {
                        Song song = query.find().get(0);
                        song.deleteInBackground(new DeleteCallback() {
                            @Override
                            public void done(ParseException e) {
                                Toast.makeText(getContext(), "Song deleted from liked songs", Toast.LENGTH_LONG).show();
                            }
                        });
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    likedSongs.remove(position);
                    numSongs = likedSongs.size();
                    Log.e(TAG, "updated liked song size: " + numSongs);
                    //tvNumSongs.setText(Integer.toString(numSongs) + " favorites");
                    adapter.notifyItemRemoved(position);
                    break;
                case ItemTouchHelper.RIGHT:
                    break;
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(getContext(), R.color.red))
                    .addSwipeLeftActionIcon(R.drawable.ic_baseline_remove_circle_outline_24)
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };


}
