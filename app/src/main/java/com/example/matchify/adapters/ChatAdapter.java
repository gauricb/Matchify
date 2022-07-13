package com.example.matchify.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.matchify.R;
import com.example.matchify.models.Message;
import com.example.matchify.models.SpotifyUser;
import com.parse.ParseException;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {

    private List<Message> mMessages;
    private Context mContext;
    private SpotifyUser sender;

    private static final int MESSAGE_OUTGOING = 234;
    private static final int MESSAGE_INCOMING = 156;


    public ChatAdapter(Context context, SpotifyUser sender, List<Message> messages) {
        mMessages = messages;
        this.sender = sender;
        mContext = context;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        if (viewType == MESSAGE_INCOMING) {
            View contactView = inflater.inflate(R.layout.message_incoming, parent, false);
            return new IncomingMessageViewHolder(contactView);
        } else if (viewType == MESSAGE_OUTGOING) {
            View contactView = inflater.inflate(R.layout.message_outgoing, parent, false);
            return new OutgoingMessageViewHolder(contactView);
        } else {
            throw new IllegalArgumentException("Unknown view type");
        }    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.MessageViewHolder holder, int position) {
        Message message = mMessages.get(position);
        holder.bindMessage(message);
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isMe(position)) {
            return MESSAGE_OUTGOING;
        } else {
            return MESSAGE_INCOMING;
        }
    }

    private boolean isMe(int position) {
        Message message = mMessages.get(position);
        //return message.getSender() != null && message.getUserId().equals(mUserId);
        return message.getSender() != null && message.getSender().equals(sender);
    }

    public abstract class MessageViewHolder extends RecyclerView.ViewHolder {

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        abstract void bindMessage(Message message);
    }

    public class IncomingMessageViewHolder extends MessageViewHolder {
        ImageView imageOther;
        TextView body;
        TextView name;

        public IncomingMessageViewHolder(View itemView) {
            super(itemView);
            imageOther = (ImageView)itemView.findViewById(R.id.ivProfileOther);
            body = (TextView)itemView.findViewById(R.id.likedArtistName);
            name = (TextView)itemView.findViewById(R.id.tvName);
        }

        @Override
        public void bindMessage(Message message) {
            try {
                Glide.with(mContext)
                        .load(message.getSender().fetchIfNeeded().getString("profileImage"))
                        .circleCrop() // create an effect of a round profile picture
                        .into(imageOther);
                name.setText(message.getSender().fetchIfNeeded().getString("username")); // in addition to message show user ID

            } catch (ParseException e) {
                Log.v("LOG_TAG", e.toString());
                e.printStackTrace();
            }
            body.setText(message.getBody());
        }
    }

    public class OutgoingMessageViewHolder extends MessageViewHolder {
        ImageView imageMe;
        TextView body;

        public OutgoingMessageViewHolder(View itemView) {
            super(itemView);
            imageMe = (ImageView)itemView.findViewById(R.id.ivProfileMe);
            body = (TextView)itemView.findViewById(R.id.likedArtistName);
        }

        @Override
        public void bindMessage(Message message) {
            try {
                Glide.with(mContext)
                        .load(message.getSender().fetchIfNeeded().getString("profileImage"))
                        .circleCrop() // create an effect of a round profile picture
                        .into(imageMe);
            } catch (ParseException e) {
                Log.v("LOG_TAG", e.toString());
                e.printStackTrace();
            }
            body.setText(message.getBody());
        }
    }


}