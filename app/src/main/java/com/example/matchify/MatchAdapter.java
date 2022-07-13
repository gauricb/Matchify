package com.example.matchify;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.matchify.models.Match;
import com.example.matchify.models.Song;
import com.example.matchify.models.SpotifyUser;

import java.util.List;

public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.ViewHolder> {

    private Context context;
    private List<SpotifyUser> matches;

    public MatchAdapter(Context context, List<SpotifyUser> matches) {
        this.context = context;
        this.matches = matches;
    }

    @NonNull
    @Override
    public MatchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_match, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SpotifyUser match = matches.get(position);
        holder.bind(match);
    }

    @Override
    public int getItemCount() {
        return matches.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView matchPicture;
        private TextView matchName;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            matchName = itemView.findViewById(R.id.matchName);
            matchPicture = itemView.findViewById(R.id.matchProfilePic);
        }

        public void bind(SpotifyUser match) {
            matchName.setText(match.getUserName());
            Glide.with(context).load(match.getUserImage()).into(matchPicture);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                SpotifyUser user = matches.get(position);
            }
        }
    }
}
