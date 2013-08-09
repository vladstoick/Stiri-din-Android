package com.vladstoick.stiridinromania;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.android.volley.VolleyError;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.plus.PlusClient;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.vladstoick.DataModel.JSONParsing;
import com.vladstoick.DataModel.NewsDataSource;
import com.vladstoick.Utils.LoginRequest;
import com.vladstoick.Utils.Tags;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import butterknife.InjectView;
import butterknife.Views;

/**
 * Created by vlad on 7/17/13.
 */
public class LoginActivity extends SherlockFragmentActivity
        implements View.OnClickListener, Session.StatusCallback ,
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {
    private int userId = 0;
    public String token;
    ProgressDialog pd;
    private static final int REQUEST_CODE_RESOLVE_ERR = 9000;
    private PlusClient mPlusClient;
    private ConnectionResult mConnectionResult;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences settings = getSharedPreferences("appPref", Context.MODE_PRIVATE);
        if (settings.getInt("user_id", 0) != 0){
            ((StiriApp)getApplication()).newsDataSource =
                    new NewsDataSource(settings.getInt("user_id", 0),getApplication());
            gotoAllGroupsActivity();
            return;
        }
        ((StiriApp)getApplication()).newsDataSource = null;
        getSupportActionBar().hide();
        pd = new ProgressDialog(this);
        pd.setCancelable(false);
        pd.setMessage(getString(R.string.loading));
        setContentView(R.layout.activity_login);
        LoginButton fbLoginButton = (LoginButton) findViewById(R.id.login_facebook);
        fbLoginButton.setSessionStatusCallback(this);
        findViewById(R.id.login_google).setOnClickListener(this);
        mPlusClient = new PlusClient.Builder(this, this, this)
                .setVisibleActivities(null)
                .build();
        if(mConnectionResult !=null)
            mPlusClient.clearDefaultAccount();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_google:
                googleLogin();
                break;
        }
    }

    public void googleLogin() {
        if (mConnectionResult == null) {
            pd.show();
            mPlusClient.connect();
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data == null )
            return;
        if (requestCode == REQUEST_CODE_RESOLVE_ERR && resultCode == RESULT_OK) {
            mConnectionResult = null;
            mPlusClient.connect();
        } else {
            Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        final String gUserId= mPlusClient.getCurrentPerson().getId();
        final SharedPreferences.Editor editor =
                getSharedPreferences("appPref", Context.MODE_PRIVATE).edit();
        editor.putString("user_id_google", gUserId);
        editor.commit();
        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    token = GoogleAuthUtil.getToken(getApplicationContext(),
                            mPlusClient.getAccountName(),
                            "oauth2:" + Scopes.PLUS_LOGIN);
                    return token;
                } catch (Exception e){
                    e.printStackTrace();
                    return null;
                }
            }
            @Override
            protected void onPostExecute(String token) {
                super.onPostExecute(token);
                LoginRequest loginRequest =
                        new LoginRequest(LoginRequest.TAG_G, token,gUserId,
                                new com.android.volley.Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject jsonObject) {
                                        pd.dismiss();
                                        userId = JSONParsing.parseServerLogin(jsonObject, editor);
                                        ((StiriApp)getApplication()).newsDataSource =
                                                new NewsDataSource(userId,getApplication());
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
        task.execute();
    }
    @Override
    public void onDisconnected() {

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (pd.isShowing()) {
            // The user clicked the sign-in button already. Start to resolve
            // connection errors. Wait until onConnected() to dismiss the
            // connection dialog.
            if (result.hasResolution()) {
                try {
                    result.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
                } catch (IntentSender.SendIntentException e) {
                    mPlusClient.connect();
                }
            }
        }

        // Save the intent so that we can start an activity when the user clicks
        // the sign-in button.
        mConnectionResult = result;
    }

    //FACEBOOK LOGIN

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
                            pd.dismiss();
                            userId = JSONParsing.parseServerLogin(jsonObject, editor);
                            ((StiriApp)getApplication()).newsDataSource =
                                    new NewsDataSource(userId,getApplication());
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
            pd.show();
            final SharedPreferences.Editor editor =
                    getSharedPreferences("appPref", Context.MODE_PRIVATE).edit();
            token = session.getAccessToken();
            editor.putString("user_token_fb", token);
            editor.commit();
            Request.executeMeRequestAsync(session, graphUserCallback);
        }
    }


    //GENERAL
    private void gotoAllGroupsActivity() {
        Intent intent = new Intent(this, NewsGroupListActivity.class);
        startActivity(intent);
        finish();
    }
}