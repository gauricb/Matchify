package com.example.matchify;

import static com.example.matchify.MainActivity.spotifyService;

import android.content.Context;
import android.os.Bundle;
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

import com.bumptech.glide.Glide;
import com.example.matchify.databinding.FragmentProfileBinding;

import kaaes.spotify.webapi.android.models.UserPrivate;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ProfileFragment extends Fragment {

    public static final String TAG = "Profile Fragment";

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
        // inflate the content view


    }
}
