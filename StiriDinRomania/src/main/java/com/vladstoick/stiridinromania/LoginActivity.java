package com.vladstoick.stiridinromania;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.android.volley.VolleyError;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.plus.PlusClient;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.vladstoick.DataModel.JSONParsing;
import com.vladstoick.DataModel.NewsDataSource;
import com.vladstoick.Utils.LoginRequest;

import org.json.JSONObject;

/**
 * Created by vlad on 7/17/13.
 */
public class LoginActivity extends SherlockFragmentActivity
        implements View.OnClickListener, Session.StatusCallback ,
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {
    private int userId = 0;
    public String token;

    public static final String TAG = "LOGIN";
    private static final int REQUEST_CODE_RESOLVE_ERR = 9000;
    private ProgressDialog mConnectionProgressDialog;
    private PlusClient mPlusClient;
    private ConnectionResult mConnectionResult;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        LoginButton fbLoginButton = (LoginButton) findViewById(R.id.login_facebook);
        fbLoginButton.setSessionStatusCallback(this);
        findViewById(R.id.login_google).setOnClickListener(this);
        mPlusClient = new PlusClient.Builder(this, this, this)
                .setVisibleActivities("http://schemas.google.com/AddActivity", "http://schemas.google.com/BuyActivity")
                .build();
        mConnectionProgressDialog = new ProgressDialog(this);
        SharedPreferences settings = getSharedPreferences("appPref", Context.MODE_PRIVATE);
        if (settings.getString("user_id", null) != null) {
            userId = settings.getInt("user_id", 0);
            gotoAllGroupsActivity();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.login_facebook:
//                facebookLogin();
//                break;
            case R.id.login_google:
                googleLogin();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_RESOLVE_ERR && resultCode == RESULT_OK) {
            mConnectionResult = null;
            mPlusClient.connect();
        } else {
            Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
        }
    }

    public void googleLogin() {
        if (mConnectionResult == null) {
            mConnectionProgressDialog.show();
        } else {
            try {
                mConnectionResult.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
            } catch (IntentSender.SendIntentException e) {
                // Try connecting again.
                mConnectionResult = null;
                mPlusClient.connect();
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        gotoAllGroupsActivity();
    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    //FACEBOOK LOGIN

    public void facebookLogin() {
        Session.openActiveSession(this, true, sessionCallback);
    }

    Request.GraphUserCallback graphUserCallback = new Request.GraphUserCallback() {
        @Override
        public void onCompleted(GraphUser user, Response response) {
            if (user == null) throw new RuntimeException();
            final SharedPreferences.Editor editor =
                    getSharedPreferences("appPref", Context.MODE_PRIVATE).edit();
            editor.putString("user_id_fb", user.getId());
            editor.commit();
            LoginRequest loginRequest = new LoginRequest(LoginRequest.TAG_FB, token, user.getId(),
                    new com.android.volley.Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            userId = JSONParsing.parseServerLogin(jsonObject, editor);
                            gotoAllGroupsActivity();
                        }
                    }, new com.android.volley.Response.ErrorListener() {
                        @Override
                            public void onErrorResponse(VolleyError volleyError) {
                            volleyError.printStackTrace();
                        }
                    }
             );
            StiriApp.queue.add(loginRequest);
        }
    };
    @Override
    public void call(Session session, SessionState state, Exception exception) {
        if (session.isOpened()) {
            final SharedPreferences.Editor editor =
                    getSharedPreferences("appPref", Context.MODE_PRIVATE).edit();
            token = session.getAccessToken();
            editor.putString("user_token_fb", token);
            editor.commit();
            Request.executeMeRequestAsync(session, graphUserCallback);
        }
    }
    Session.StatusCallback sessionCallback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {

        }
    };

    //GENERAL
    private void gotoAllGroupsActivity() {
        ((StiriApp) getApplication()).newsDataSource = new NewsDataSource(userId,
                (StiriApp) getApplication());
        Intent intent = new Intent(this, NewsGroupListActivity.class);
        startActivity(intent);
        finish();
    }
}