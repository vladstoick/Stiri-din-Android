package com.vladstoick.DataModel;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by vlad on 7/19/13.
 */
public class JSONParsing {
    public static ArrayList<NewsGroup> parseNewsDataSource(String response) {
        ArrayList<NewsGroup> newsDataSource = new ArrayList<NewsGroup>();

        try {
            JSONArray responseJA = new JSONArray(response);

            for (int groupIterator = 0; groupIterator < responseJA.length(); groupIterator++) {
                JSONObject groupJO = responseJA.getJSONObject(groupIterator);
                NewsGroup ng = new NewsGroup(groupJO.getString(NewsGroup.TAG_TITLE),
                        groupJO.getInt(NewsGroup.TAG_ID));
                JSONArray feedsJA = groupJO.getJSONArray(NewsGroup.TAG_NEWSOURCES);
                for (int feedIterator = 0; feedIterator < feedsJA.length(); feedIterator++) {
                    JSONObject feedJO = feedsJA.getJSONObject(feedIterator);
                    NewsSource ns = new NewsSource(feedJO.getString(NewsSource.TAG_RSSLINK),
                            feedJO.getString(NewsSource.TAG_TITLE),
                            feedJO.getString(NewsSource.TAG_DESCRIPTION),
                            feedJO.getInt(NewsSource.TAG_ID));

                    ng.newsSources.add(ns);
                }
                newsDataSource.add(ng);


            }
            return newsDataSource;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static int parseAddNewsGroupResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            return jsonObject.getInt(NewsGroup.TAG_ID);
        } catch (JSONException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
