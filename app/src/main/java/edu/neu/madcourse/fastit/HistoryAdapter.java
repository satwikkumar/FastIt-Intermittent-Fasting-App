package edu.neu.madcourse.fastit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.neu.madcourse.fastit.R;
import edu.neu.madcourse.fastit.plan.Helpers;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder>{

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private CardView cardView;
        private final TextView startDate;
        private final TextView endDate;
        private final TextView sessionCompleted;
        private final TextView weight;

        public CardView getCardView() {
            return cardView;
        }

        public TextView getStartDate() {
            return startDate;
        }

        public TextView getEndDate() {
            return endDate;
        }

        public TextView getSessionCompleted() {
            return sessionCompleted;
        }

        public TextView getWeight() {
            return weight;
        }

        public ImageView getImageView() {
            return imageView;
        }

        private final ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            cardView = view.findViewById(R.id.history_cardView);
            startDate = view.findViewById(R.id.start_time);
            endDate = view.findViewById(R.id.end_time);
            sessionCompleted = view.findViewById(R.id.completed_status);
            weight = view.findViewById(R.id.weight_value);
            imageView = view.findViewById(R.id.session_thumbnail);
        }
    }

    public List<FastingSession> fastingSessions;

    public HistoryAdapter(List<FastingSession> sessions) {
        fastingSessions =  sessions;
    }

    @NonNull
    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_row, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.ViewHolder holder, int position) {
        FastingSession session = fastingSessions.get(position);
        holder.getStartDate().setText(Helpers.getFormattedDate(session.startTime));
        holder.getEndDate().setText(Helpers.getFormattedDate(session.endTime));
        holder.getSessionCompleted().setText(session.hasCompletedSession ? "Yes" : "No");
        holder.getWeight().setText(session.weight + " KG");
        Bitmap bitmap = BitmapFactory.decodeFile(session.progressImagePath);
        if (bitmap!=null) {
            holder.getImageView().setImageBitmap(bitmap);
        }
    }

    @Override
    public int getItemCount() {
        return fastingSessions.size();
    }
}
