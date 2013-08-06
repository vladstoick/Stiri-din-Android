package com.vladstoick.stiridinromania;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.vladstoick.DataModel.JSONParsing;
import com.vladstoick.DataModel.NewsDataSource;

import org.json.JSONObject;

/**
 * Created by vlad on 7/17/13.
 */
public class LoginActivity extends SherlockFragmentActivity {
    private int userId = 0;
    private String token;
    public static final String TAG="LOGIN";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViewById(R.id.facebookLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                facebookLogin();
            }
        });
        SharedPreferences settings = getSharedPreferences("appPref",Context.MODE_PRIVATE);
        if (settings.getString("user_id_fb", null) != null) {
            userId = settings.getInt("user_id", 0);
            gotoAllGroupsActivity();
        }
    }

    public void facebookLogin() {
        Log.d(TAG, "Logging in");
        Session.openActiveSession(this, true,sessionCallback);
    }

    Request.GraphUserCallback graphUserCallback = new Request.GraphUserCallback() {
        @Override
        public void onCompleted(GraphUser user, Response response) {
            Log.d(TAG, "MADE REQUEST");
            if(user == null){
                throw new NullPointerException();
            }
            final SharedPreferences.Editor editor =
                    getSharedPreferences("appPref",Context.MODE_PRIVATE).edit();
            editor.putString("user_id_fb", user.getId());
            editor.commit();
            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            params.put("fbtoken", token);
            params.put("fbaccount", user.getId());
            client.post("http://stiriromania.eu01.aws.af.cm/user/login",
                    params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String s) {
                    super.onSuccess(s);
                    try {
                        userId = JSONParsing.parseServerLogin(s, editor);
                        gotoAllGroupsActivity();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    Session.StatusCallback sessionCallback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            if (session.isOpened()) {
                final SharedPreferences.Editor editor =
                        getSharedPreferences("appPref",Context.MODE_PRIVATE).edit();
                String token = session.getAccessToken();
                editor.putString("user_token_fb", token);
                editor.commit();
                Request.executeMeRequestAsync(session, graphUserCallback);
            }
        }
    };

    private void gotoAllGroupsActivity() {
        ((StiriApp) getApplication()).newsDataSource = new NewsDataSource(userId,
                (StiriApp) getApplication());
        Intent intent = new Intent(this, NewsGroupListActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }
}