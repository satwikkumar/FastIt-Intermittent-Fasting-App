package edu.neu.madcourse.fastit.plan;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.neu.madcourse.fastit.Constants;
import edu.neu.madcourse.fastit.R;
import edu.neu.madcourse.fastit.RecyclerItemClickListener;
import edu.neu.madcourse.fastit.SharedPreferenceManager;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlanFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlanFragment extends Fragment {

    private SharedPreferenceManager sharedPreferenceManager;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PlanFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PlanFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PlanFragment newInstance(String param1, String param2) {
        PlanFragment fragment = new PlanFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        sharedPreferenceManager = new SharedPreferenceManager(getActivity());
        View view = inflater.inflate(R.layout.fragment_plan, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.plan_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        final PlanListAdapter listAdapter = new PlanListAdapter();
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(listAdapter);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), recyclerView,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        setCurrentFastingCycle(listAdapter.fastingCycleArrayList.get(position));
                        Intent intent = new Intent(getActivity(), PlanningInfoActivity.class);
                        String cycleText = Helpers.getStringForFastingCycle(listAdapter.fastingCycleArrayList.get(position));
                        intent.putExtra("Plan", cycleText);
                        startActivity(intent);
                        //((MainActivity)getActivity()).changeNavigationTab(R.id.action_fasting);
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );


        return view;
    }

    private void setCurrentFastingCycle(FastingCycle fastingCycle){
        sharedPreferenceManager.setIntPref(Constants.SP_CURRENT_FASTING_CYCLE, fastingCycle.getId());
    }

}