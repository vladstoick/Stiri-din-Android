package com.vladstoick.stiridinromania;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.vladstoick.DataModel.NewsDataSource;

/**
 * Created by vlad on 7/17/13.
 */
public class LoginActivity extends SherlockFragmentActivity {
    private int userId = 0;
    private static String TAG = "LOGINACTIVITY";
    private String USER_ID_TAG = "userId";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViewById(R.id.facebookLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                facebookLogin();
            }
        });
        SharedPreferences settings = getSharedPreferences("appPref", Context.MODE_PRIVATE);
        if (settings.getString("user_id_fb", null) != null) {
            userId = settings.getInt("user_id", 0);
            gotoAllGroupsActivity();
        }
    }

    public void facebookLogin() {
        Log.d(TAG, "Logging in");
        Session.openActiveSession(this, true, new Session.StatusCallback() {

            // callback when session changes state
            @Override
            public void call(Session session, SessionState state, Exception exception) {
                if (session.isOpened()) {
                    Log.d(TAG, "SESSION IS OPEN");
                    // make request to the /me API
                    Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

                        // callback after Graph API response with user object
                        @Override
                        public void onCompleted(GraphUser user, Response response) {
                            {
                                Log.d(TAG, "MADE REQUEST");
                                if (user != null) {
                                    SharedPreferences settings = getSharedPreferences("appPref",
                                            Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = settings.edit();
                                    editor.putString("user_id_fb", "928bf8a68ad76c1f");
                                    System.out.println(user.getName());
                                    editor.putInt("user_id", 4);
                                    //TODO FACEBOOK
                                    userId = 4;
                                    editor.commit();
                                    gotoAllGroupsActivity();

                                }
                            }
                        }
                    });
                }
            }
        });
    }

    public void microsoftLogin(View view) {
    }

    private void gotoAllGroupsActivity() {
        ((StiriApp)getApplication()).newsDataSource = new NewsDataSource(userId,
                (StiriApp)getApplication());
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