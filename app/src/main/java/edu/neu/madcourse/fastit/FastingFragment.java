package edu.neu.madcourse.fastit;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class FastingFragment extends Fragment {

    private Button startFastingButton;
    private Button endFastingButton;

    public FastingFragment() {
        // Required empty public constructor
    }

    public static FastingFragment newInstance(String param1, String param2) {
        FastingFragment fragment = new FastingFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_fasting, container, false);
        endFastingButton = view.findViewById(R.id.end_fasting);
        endFastingButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                loadAdditionalActivity();
            }
        });

        return view;
    }

    private void loadAdditionalActivity(){
        // do some validations
        Intent intent = new Intent(getActivity(), AdditionalInfoActivity.class);
        startActivity(intent);
    }
}