package edu.neu.madcourse.fastit;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LeaderboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LeaderboardFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String EMAIL = "email";
    CallbackManager callbackManager;
    ArrayList<String> permissions = new ArrayList();


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    //textbox and login
    private TextView info;
    private LoginButton loginButton;
    public LeaderboardFragment() {
        // Required empty public constructor

        permissions.add("email");
        permissions.add("user_friends");
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LeaderboardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LeaderboardFragment newInstance(String param1, String param2) {
        LeaderboardFragment fragment = new LeaderboardFragment();
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


        final View root = inflater.inflate(R.layout.fragment_leaderboard, container, false);
        info = (TextView) root.findViewById(R.id.text);
        loginButton = (LoginButton) root.findViewById(R.id.login_button);
         callbackManager = CallbackManager.Factory.create();
         loginButton.setPermissions(permissions);

        loginButton.setFragment(this);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                Log.v("navyasai",loginResult.getAccessToken().getUserId());
                System.out.println(loginResult.getAccessToken().getUserId());
                Log.v("navyasai",loginResult.getAccessToken().getToken());

                String userId = loginResult.getAccessToken().getUserId();
                GraphRequest request = GraphRequest.newMyFriendsRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONArrayCallback() {
                    @Override
                    public void onCompleted(JSONArray objects, GraphResponse response) {
                        Log.d("navyasai", objects.toString());
                    }
                });
                request.executeAsync();
            }

            @Override
            public void onCancel() {
               Log.v("navyasai","came hre");
            }

            @Override
            public void onError(FacebookException error) {
                Log.v("navyasai","came here");
            }
        });


        //info.setText("hello");
        TextView x =  root.findViewById(R.id.textView4);
        x.setText("some messages");
        // Inflate the layout for this fragment

        //info.setText("hello");
        //return inflater.inflate(R.layout.fragment_leaderboard, container, false);
        return root;

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }
}