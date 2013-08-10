package com.vladstoick.Utils;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.JsonParseException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Vlad on 8/7/13.
 */
public class LoginVolleyRequest<T> extends Request<JSONObject> {
    private Map<String, String> params = new HashMap<String, String>();
    private Response.Listener listener;
    public static final String TAG_FB = "facebook";
    public static final String TAG_G = "google";
    public static final String url = "http://stiriromania.eu01.aws.af.cm/user/login";
    public LoginVolleyRequest(String type, String token, String account,
                              Response.Listener<JSONObject> listener,
                              Response.ErrorListener errorListener) {
        super(Method.POST, url , errorListener);
        this.listener = listener;
        if(type == TAG_FB) {
            params.put("fbaccount",account);
            params.put("fbtoken",token);
        } else {
            params.put("gaccount",account);
            params.put("gtoken", token);
        }
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return params;
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try{
            String json = new String(response.data, HttpHeaderParser.parseCharset
                     (response.headers));
            JSONObject jsonObject;
            jsonObject = new JSONObject(json);
            return Response.success(jsonObject, HttpHeaderParser.parseCacheHeaders(response));
        } catch (JSONException e){
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void deliverResponse(JSONObject jsonObject) {
        listener.onResponse(jsonObject);
    }
}
