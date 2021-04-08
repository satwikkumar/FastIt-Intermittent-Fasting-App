package edu.neu.madcourse.fastit.plan;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.neu.madcourse.fastit.R;

public class PlanListAdapter extends RecyclerView.Adapter<PlanListAdapter.ViewHolder>{

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView getTextView1() {
                return textView1;
            }
        public TextView getTextView2() {
            return textView2;
        }
        public TextView getTextView3() {
            return textView3;
        }

        private final TextView textView1;
        private final TextView textView2;
        private final TextView textView3;

            public ViewHolder(View view) {
                super(view);
                textView1 = view.findViewById(R.id.plan_list_item_1);
                textView2 = view.findViewById(R.id.plan_list_item_2);
                textView3 = view.findViewById(R.id.plan_list_item_3);
            }
    }

    public List<FastingCycle> fastingCycleArrayList;

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
        String cycle = Helpers.getStringForFastingCycle(fastingCycleArrayList.get(position));
        holder.getTextView1().setText(cycle);
        holder.getTextView2().setText("\u2022 " + cycle.split("-")[0] + " hours fasting");
        holder.getTextView3().setText("\u2022 " + cycle.split("-")[1] + " hours eating period");
    }

    @Override
    public int getItemCount() {
        return fastingCycleArrayList.size();
    }
}
