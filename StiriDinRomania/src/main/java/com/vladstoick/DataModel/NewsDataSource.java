package com.vladstoick.DataModel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.otto.Subscribe;
import com.vladstoick.DialogFragment.RenameDialogFragment;
import com.vladstoick.OttoBus.BusProvider;
import com.vladstoick.OttoBus.DataLoadedEvent;
import com.vladstoick.OttoBus.NewsItemLoadedEvent;
import com.vladstoick.OttoBus.SearchResultsEvent;
import com.vladstoick.Utils.Utils;
import com.vladstoick.stiridinromania.StiriApp;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by vlad on 7/20/13.
 */
public class
        NewsDataSource {
    public boolean isDataLoaded = true;
    public AsyncHttpClient client;
    private AsyncHttpClient httpClient;
    private String BASE_URL = "http://37.139.26.80/user/";
    private int userId;
    private String token;
    private SqlHelper sqlHelper;

    //CONSTRUCTORS
    //0. constructor is called
    //1. if there is intenet laodDataFromInternet is called;
    //2. Once groups and sourcesa are added loadNewsItems in called
    //3. For each news source getNewsItems is being called
    //4. after they are done addNewsSourceInDb is called
    public NewsDataSource(Application app, SharedPreferences settings) {
        this.userId = settings.getInt("user_id",0);
        this.token = settings.getString("key","");
        if (Utils.isOnline(app)) {
            loadDataFromInternet();
        }
        sqlHelper = new SqlHelper(app);
        BusProvider.getInstance().register(this);
    }

    public void loadDataFromInternet() {
        isDataLoaded = false;
        httpClient = new AsyncHttpClient();
        StringRequest request = new StringRequest(Request.Method.GET,
                BASE_URL + userId+ Utils.tokenWithoutAnd(token), new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                ArrayList<NewsGroup> allNewsGroups = JSONParsing.parseNewsDataSource(s);
                sqlHelper.deleteOldNewsGroupsAndSources(allNewsGroups);
                for (int i = 0; i < allNewsGroups.size(); i++) {
                    for (int j = 0; j < allNewsGroups.get(i).newsSources.size(); j++)
                        sqlHelper.insertNewsSourceInDb(allNewsGroups.get(i).newsSources.get(j));
                    sqlHelper.insertNewsGroupInDb(allNewsGroups.get(i));
                }
                loadNewsItems();
                BusProvider.getInstance().post(new DataLoadedEvent(
                        DataLoadedEvent.TAG_NEWSDATASOURCE));
                isDataLoaded = true;

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
            }
        }
        );
        StiriApp.queue.add(request);
    }
    public void loadNewsItems(){
        ArrayList<NewsSource> newsSources = sqlHelper.getAllNewsSources();
        for(int j = 0; j < newsSources.size(); j++) {
            NewsSource ns = newsSources.get(j);
            getNewsItems(ns);
        }
    }
    public void getNewsItems(NewsSource ns) {
        String url = NewsSource.BASE_URL + ns.getId();
//                            +"&date="+dateString;
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                addNewsSourceInDb(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
            }
        }
        );
        StiriApp.queue.add(stringRequest);
    }

    public void addNewsSourceInDb(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            String sFeedId = jsonObject.getString("feedId");
            int feedId = Integer.parseInt(sFeedId);
            JSONArray feedArray = jsonObject.getJSONArray("articles");
            ArrayList<NewsItem> newsItems = JSONParsing.parseFeed(feedArray);
            NewsSource ns = getNewsSource(feedId);
            ns.news = newsItems;
            sqlHelper.insertNewsSourceInDb(ns);
            sqlHelper.insertNewsItemsInDb(ns);
            newsItems = sqlHelper.getNewsItems(ns.getId());
            for (int i = 0; i < newsItems.size(); i++)
                if (newsItems.get(i).getDescription() == "null")
                    paperizeNewsItem(newsItems.get(i));
            BusProvider.getInstance().post(new
                    DataLoadedEvent(DataLoadedEvent.TAG_NEWSDATASOURCE_MODIFIED));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //MULTIPLE

    @Subscribe
    public void renameElement(RenameDialogFragment.ElementRenamedEvent event) {
        String url;
        RequestParams requestParams = new RequestParams();
        requestParams.put("title", event.newName);
        if (event.type == RenameDialogFragment.GROUP_TAG) {
            sqlHelper.renameNewsGroup(event);
            url = BASE_URL + userId + "/" + event.id;
        } else {
            sqlHelper.renameNewsSource(event);
            NewsSource ns = getNewsSource(event.id);
            url = BASE_URL + userId + "/" + ns.getGroupId() + "/" + ns.getId();
        }
        httpClient.put(url, requestParams, new AsyncHttpResponseHandler() {
        });
        BusProvider.getInstance().post(new DataLoadedEvent(
                DataLoadedEvent.TAG_NEWSDATASOURCE_MODIFIED));
    }

    public void addNewsGroupAndNewsSource(final String groupTitle, final NewsSource ns) {
        RequestParams requestParams = new RequestParams();
        requestParams.put("title", groupTitle);
        httpClient.post(BASE_URL + userId, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String s) {
                NewsGroup ng = new NewsGroup(groupTitle, JSONParsing.parseAddNewsGroupResponse(s));
                sqlHelper.insertNewsGroupInDb(ng);
                addNewsSource(ns, ng.getId());
                BusProvider.getInstance().post(new
                        DataLoadedEvent(DataLoadedEvent.TAG_NEWSDATASOURCE_MODIFIED));
            }
        });
    }
    //NEWSGROUP

    public ArrayList<NewsGroup> getAllNewsGroups() {
        return sqlHelper.getAllNewsGroups();
    }

    public NewsGroup getNewsGroup(int id) {
        return sqlHelper.getNewsGroup(id);
    }

    public void deleteNewsGroup(final int id) {
        sqlHelper.deleteNewsGroup(id);
        BusProvider.getInstance().post(new DataLoadedEvent(
                DataLoadedEvent.TAG_NEWSDATASOURCE_MODIFIED));
        httpClient.delete(BASE_URL + userId + "/" + id,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String s) {
                    }
                });

    }

    //NewsSource

    public NewsSource getNewsSource(int id) {
        return sqlHelper.getNewsSource(id);
    }
    public void addNewsSource(final NewsSource newsSource, final int groupId) {
        RequestParams requestParams = new RequestParams();
        requestParams.put("url", newsSource.getRssLink());
        httpClient.post(BASE_URL + userId + "/" + groupId, requestParams,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String s) {
                        JSONParsing.parseAddNewsSource(newsSource, s);
                        newsSource.setGroupId(groupId);
                        sqlHelper.insertNewsSourceInDb(newsSource);
                        getNewsItems(newsSource);
                        sqlHelper.updateNewsGroupNoFeeds(groupId);
                    }
                });
    }

    public void deleteNewsSource(NewsSource ns) {
        sqlHelper.deleteNewsSource(ns.getId());
        sqlHelper.updateNewsGroupNoFeeds(ns.getGroupId());
        BusProvider.getInstance().post(new DataLoadedEvent(
                DataLoadedEvent.TAG_NEWSDATASOURCE_MODIFIED));
        String url = BASE_URL + userId + "/" + ns.getGroupId() + "/" + ns.getId();
        httpClient.delete(url, new AsyncHttpResponseHandler() {
        });
    }

    public int getNumberOfNewsForNewsSource(int id){
        return sqlHelper.getNumberOfNewsForNewsSource(id);
    }
    //NEWSITEM

    public void makeNewsRead(String url){
        sqlHelper.makeNewsRead(url);
    }



    public NewsItem getNewsItem(String url) {
        return sqlHelper.getNewsItem(url);
    }

    public ArrayList<NewsItem> searchNewsItemsLocal(String query) {
        return sqlHelper.searchNewsItem(query);
    }

    public void searchNewsItemOnline(final String query) {
        String url = "http://37.139.8.146:8983/solr/collection1/select?start=0&rows=20" +
                "&wt=json&indent=true&fl=title,content,url&q=description:" + query;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jobj) {
                        ArrayList<NewsItem> searchResults = JSONParsing.parseSearchResults(jobj);
                        BusProvider.getInstance()
                                .post(new SearchResultsEvent(searchResults,query));
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();
                    }
                }
        );
        StiriApp.queue.add(request);
    }

    public void paperizeNewsItem(NewsItem ni) {
        String url = "http://37.139.8.146:8080/?url=" + ni.getUrlLink();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            String url = jsonObject.getString("url");
                            addPaperizedStringToNewsItem(url, jsonObject.getString("response"));
                            BusProvider.getInstance().post(
                                    new NewsItemLoadedEvent(getNewsItem(url)));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
            }
        }
        );
        StiriApp.queue.add(request);
    }

    public void addPaperizedStringToNewsItem(String url, String paperized) {
        sqlHelper.updateNewsItem(url, paperized);
    }
}
