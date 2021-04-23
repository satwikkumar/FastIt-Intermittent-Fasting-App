package edu.neu.madcourse.fastit;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.Collections;

public class LeaderboardFragment extends Fragment {
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

        initFBLoginCallbacks();

        if(preferenceManager.getStringPref(Constants.SP_LOGGED_IN_USER_TOKEN).length() > 0){
            fetchFriendList();
            fetchUserName();
            textViewLogin.setVisibility(View.INVISIBLE);
        }

        return root;

    }

    private void initFBLoginCallbacks(){
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                userName.setVisibility(View.VISIBLE);
                textViewLogin.setVisibility(View.INVISIBLE);
                preferenceManager.setStringPref(Constants.SP_LOGGED_IN_USER_TOKEN, loginResult.getAccessToken().getUserId());
            }

            @Override
            public void onCancel() { }

            @Override
            public void onError(FacebookException error) { }
        });

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if (currentAccessToken == null) {
                    myAdapter.clear();
                    textViewLogin.setVisibility(View.VISIBLE);
                    userName.setVisibility(View.INVISIBLE);
                    preferenceManager.removePref(Constants.SP_LOGGED_IN_USER_TOKEN);
                    preferenceManager.removePref(Constants.SP_LOGGED_IN_USER_NAME);
                    LoginManager.getInstance().logOut();
                }
            }
        };
    }

    private void fetchFriendList(){
        GraphRequest request = GraphRequest.newMyFriendsRequest(AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONArrayCallback() {

                    @Override
                    public void onCompleted(JSONArray objects, GraphResponse response) {
                        ArrayList<FbFriend> fbFriends = new ArrayList<>();
                        for(int i=0; i< objects.length();i++) {
                            try {
                                JSONObject object = objects.getJSONObject(i);
                                FbFriend f = new FbFriend(object.getString("name"),
                                        0,
                                        object.getString("id"));
                                fbFriends.add(f);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        int streak = preferenceManager.getIntPref(Constants.SP_CURRENT_STREAK);
                        FbFriend currentUser = new FbFriend(preferenceManager.getStringPref(Constants.SP_LOGGED_IN_USER_NAME),
                                Math.max(streak, 0),
                                preferenceManager.getStringPref(Constants.SP_LOGGED_IN_USER_TOKEN));
                        fbFriends.add(currentUser);

                        fetchScoresForUsers(fbFriends);
                    }
                });
        request.executeAsync();
    }

    private void fetchUserName(){
        GraphRequest graphRequest = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            String name= object.getString("name");
                            userName.setText(name);
                            userName.setVisibility(View.VISIBLE);
                            preferenceManager.setStringPref(Constants.SP_LOGGED_IN_USER_NAME, name);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        graphRequest.executeAsync();
    }

    private void updateLeaderboardUI(ArrayList<FbFriend> friendList){
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        myAdapter = new LeaderboardAdapter(friendList);
        recyclerView.setAdapter(myAdapter);
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

        fetchFriendList();

        fetchUserName();
    }

    private void fetchScoresForUsers(final ArrayList<FbFriend> friendList){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("users");
        final ArrayList<FbFriend> refList = new ArrayList<>(friendList); /// list for users who have
        // not yet started using the app but have logged in to the application

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
                            friendList.add(local);
                            refList.remove(local);
                        }
                    }
                }

                // adding the above said user to firebase
                for(FbFriend friend : refList){
                    friend.setScore(0);
                    myRef.child(friend.getUserId()).setValue(friend);
                }
                myRef.removeEventListener(this);
                Collections.sort(friendList);
                updateLeaderboardUI(friendList);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }
}