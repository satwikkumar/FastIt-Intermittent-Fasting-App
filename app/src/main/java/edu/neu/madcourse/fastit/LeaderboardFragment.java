package edu.neu.madcourse.fastit;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LeaderboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LeaderboardFragment extends Fragment {


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

    //changes here
    TextView userName;
    TextView textViewLogin;
    RecyclerView recyclerView;
    LeaderboardAdapter myAdapter;
    RecyclerView.LayoutManager layoutManager;
    AccessTokenTracker accessTokenTracker;
    SharedPreferenceManager preferenceManager;
    public LeaderboardFragment() {
        // Required empty public constructor

        permissions.add("email");
        permissions.add("user_friends");

        //initialize items



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
        preferenceManager = new SharedPreferenceManager(getActivity());
        textViewLogin = root.findViewById(R.id.txtViewLogin);
        recyclerView = root.findViewById(R.id.rv_friendsList);
        userName = root.findViewById(R.id.txt_userName);
        userName.setVisibility(View.INVISIBLE);
        loginButton = (LoginButton) root.findViewById(R.id.login_button);

        callbackManager = CallbackManager.Factory.create();
        loginButton.setPermissions(permissions);

        loginButton.setFragment(this);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                userName.setVisibility(View.VISIBLE);
                textViewLogin.setVisibility(View.INVISIBLE);
                //loginResult.getAccessToken().getUserId();
                //Log.v("navyasaiporanki", );
                preferenceManager.setStringPref(Constants.SP_LOGGED_IN_USER_TOKEN, loginResult.getAccessToken().getUserId());
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
        return root;

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        accessTokenTracker.stopTracking();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);

        GraphRequest request = GraphRequest.newMyFriendsRequest(AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONArrayCallback() {

                    @Override
                    public void onCompleted(JSONArray objects, GraphResponse response) {
                        ArrayList<FbFriend> fbFriends = new ArrayList<>();
                        for(int i=0; i< objects.length();i++) {
                            try {
                                JSONObject object = objects.getJSONObject(i);
                                FbFriend f = new FbFriend(object.getString("name"), 60);
                                f.setUserId(object.getString("id"));
                                fbFriends.add(f);
                                Log.v("navyasai", object.getString("id"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        FbFriend f=  new FbFriend("Ravi", 70);
                        f.setUserId("ddfd");
                        fbFriends.add(f);
                        layoutManager = new LinearLayoutManager(getActivity());
                        recyclerView.setLayoutManager(layoutManager);
                        myAdapter = new LeaderboardAdapter(fbFriends);
                        recyclerView.setAdapter(myAdapter);
                    }
                });
        request.executeAsync();

        GraphRequest graphRequest = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    String name= object.getString("name");
                    userName.setText(name);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        graphRequest.executeAsync();

            accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if (currentAccessToken == null) {
                    myAdapter.clear();
                    textViewLogin.setVisibility(View.VISIBLE);
                    userName.setVisibility(View.INVISIBLE);
                    LoginManager.getInstance().logOut();
                }
            }
        };


    }
}