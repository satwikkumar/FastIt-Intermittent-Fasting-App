package edu.neu.madcourse.fastit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlanListAdapter extends RecyclerView.Adapter<PlanListAdapter.ViewHolder>{

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView getTextView() {
                return textView;
            }

        private final TextView textView;

            public ViewHolder(View view) {
                super(view);
                textView = view.findViewById(R.id.plan_list_item);
            }
    }

    private List<FastingCycle> fastingCycleArrayList;

    public PlanListAdapter() {
        fastingCycleArrayList =  new ArrayList<>(Arrays.asList(FastingCycle.values()));
        fastingCycleArrayList.remove(fastingCycleArrayList.size()-1);
    }

    @NonNull
    @Override
    public PlanListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.plan_row, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlanListAdapter.ViewHolder holder, int position) {
        holder.getTextView().setText(Helpers.getStringForFastingCycle(fastingCycleArrayList.get(position)));
    }

    @Override
    public int getItemCount() {
        return fastingCycleArrayList.size();
    }
}
