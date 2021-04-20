package edu.neu.madcourse.fastit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder>{


    private ArrayList<FbFriend> fbFriends;

    public LeaderboardAdapter(ArrayList<FbFriend> fbFriends) {
        this.fbFriends = fbFriends;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.friendName.setText(fbFriends.get(position).getName());
        String value = String.valueOf(fbFriends.get(position).getScore());
        holder.friendScore.setText(value);
    }

    @Override
    public int getItemCount() {
        return fbFriends.size();
    }


    public void clear() {
        fbFriends.clear();
        notifyDataSetChanged();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView friendName;
        TextView friendScore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            friendName = itemView.findViewById(R.id.tvFriendName);
            friendScore = itemView.findViewById(R.id.tvFriendScore);
        }
    }
}
