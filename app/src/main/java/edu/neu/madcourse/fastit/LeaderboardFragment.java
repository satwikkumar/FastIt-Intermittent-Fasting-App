package edu.neu.madcourse.fastit;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LeaderboardFragment extends Fragment {
    private static final String EMAIL = "email";
    CallbackManager callbackManager;
    ArrayList<String> permissions = new ArrayList();


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
        permissions.add("email");
        permissions.add("user_friends");
    }

    public static LeaderboardFragment newInstance(String param1, String param2) {
        LeaderboardFragment fragment = new LeaderboardFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                                FbFriend f = new FbFriend(object.getString("name"), 60, "12312312");
                                f.setUserId(object.getString("id"));
                                fbFriends.add(f);
                                Log.v("navyasai", object.getString("id"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        FbFriend f=  new FbFriend("Ravi", 70, "abc");
                        f.setUserId("ddfd");
                        fbFriends.add(f);

                        FbFriend fb1 = new FbFriend("user 1",90, "user1ID");
                        FbFriend fb2 = new FbFriend("user 2",90, "user2ID");
                        fbFriends.add(fb1);
                        fbFriends.add(fb2);

                        layoutManager = new LinearLayoutManager(getActivity());
                        recyclerView.setLayoutManager(layoutManager);
                        myAdapter = new LeaderboardAdapter(fbFriends);
                        recyclerView.setAdapter(myAdapter);
                        fetchScoresForUsers(fbFriends);
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

    private void fetchScoresForUsers(final List<FbFriend> friendList){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("users");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()){
                    for (DataSnapshot data : dataSnapshot.getChildren()){
                        FbFriend friend = data.getValue(FbFriend.class);
                        if (friendList.contains(friend)){
                            int index = friendList.indexOf(friend);
                            FbFriend local = friendList.remove(index);
                            local.setScore(friend.getScore());
                            if(friend.getName().length() == 0){
                                friend.setName(local.getName());
                                myRef.removeEventListener(this);
                                myRef.child(friend.getUserId()).setValue(local);
                            }
                            friendList.add(local);
                        }
                    }
                }
                myRef.removeEventListener(this);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }
}